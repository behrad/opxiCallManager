package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.ServiceObject;
import com.basamadco.opxi.callmanager.SipService;
import com.basamadco.opxi.callmanager.entity.Presence;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.entity.UserNotAvailableException;
import com.basamadco.opxi.callmanager.logging.ActivityLogNotExistsException;
import com.basamadco.opxi.callmanager.logging.AgentActivityLogger;
import com.basamadco.opxi.callmanager.pool.Agent;
import com.basamadco.opxi.callmanager.pool.AgentNotAvailableException;
import com.basamadco.opxi.callmanager.queue.Queue;
import com.basamadco.opxi.callmanager.queue.QueueManagementService;
import com.basamadco.opxi.callmanager.queue.QueueManagerException;
import com.basamadco.opxi.callmanager.queue.QueueNotExistsException;
import com.basamadco.opxi.callmanager.sip.CallServlet;
import com.basamadco.opxi.callmanager.sip.SipCallController;
import com.basamadco.opxi.callmanager.sip.util.SIPConstants;
import com.basamadco.opxi.callmanager.sip.presence.NoActivePublishContextException;
import com.basamadco.opxi.callmanager.sip.b2bua.ReferTransferTimerContext;
import com.basamadco.opxi.callmanager.sip.b2bua.greeting.PostGreetingLegTerminationTimer;
import com.basamadco.opxi.callmanager.sip.b2bua.greeting.PostGreetingRequestModifier;
import com.basamadco.opxi.callmanager.sip.b2bua.waiting.WaitingTimerContext;
import com.basamadco.opxi.callmanager.sip.listener.SipSessionManager;
import com.basamadco.opxi.callmanager.util.LockManager;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import org.castor.util.concurrent.ConcurrentHashMap;

import javax.servlet.sip.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CallService implements ServiceObject {

    private static final Logger logger = Logger.getLogger( CallService.class.getName() );

    public static final String CALLER_INFO_BASE_URI = "http://opxiMasterServer/opxiCallManager/showProfile.jsp?customerId=";

    public static final int INITIALIZED = 1;

    public static final int WAITING = 2;

    public static final int TRANSFERING = 3;

    public static final int TERMINATED = 4;

    public static final int IN_CALL = 5;

    public static final int QUEUE = 6;

    public static final int PROXY = 7;

    public static final int REJECTED = 8;

    public static final int MAKE_CALL = 9;

    public static final int CONNECTED = 10;

    public static final int TEAR_DOWN = 11;

    public static final int DISPOSING = 12;

    public static final int QUEUED = 13;

    public static final int IN_GREETING = 14;

    public static final int ASSIGNED = 15;

    public static final int CANCELED = 16;

    public static final int RINGING = 17;

    public static final int CALLEE_TEARDOWN = 18;


    private static final String[] STATE =
            {"", "Initialized", "Waiting", "Transfering", "Terminated", "In Call", "Queue", "Proxy",
                    "Rejected", "Make Call", "Connected", "Tear Down", "Disposed", "Queued", "In Greeting",
                    "Assigned", "Canceled", "Ringing", "Callee Teardown"
            };

    protected SipService ctx;

    private SipServletRequest initialRequest;

    private String requestURI;

    private Address callerAddress;

//    private URI callerInfo;

    private Address targetAddress;

    private ServletTimer waitTimer;

    private ServletTimer transferTimer;

    private boolean transferTimerAlreadyRead = false;

    private String id;

    private int state;

    private String handlerAgent;

    private List proxyTargets = new ArrayList( 3 );

    private String handlerQueueName;

    private String waitingRoom;

    private long arrival;

    private long connectTime = 0;

    private long disconnectTime = 0;

    private long ringingTime = 0;

    private volatile boolean disposed;

    private Map legMap = new ConcurrentHashMap();

    private Leg transferLeg;

    private CallTarget target;

    private boolean abandoned = false;

    private ApplicationIntegrationContext applicationContext;

    private String IM;

    private final Object LEG_MAP_UPDATE_LOCK = new Object();


    public CallService( SipServletRequest request, String role, SipService ctx ) {
        this.ctx = ctx;
        setInitialRequest( request );
        setRequestURI( request.getRequestURI().toString() );
        setId( request.getCallId() );
        setCallerAddress( request.getFrom() );
//        setCallerInfo( request.getFrom().getDisplayName() );
        setTargetAddress( request.getTo() );
        setState( INITIALIZED );
        arrival = System.currentTimeMillis();
        setTransferLeg( addUASLeg( request, role ) );
        CallServiceFactory.createCallIndex( getId(), this );
        logger.finer( toString() );

    }

    public UASLeg addUASLeg( SipServletRequest request, String roleName ) {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        synchronized ( LEG_MAP_UPDATE_LOCK ) {
            logger.finer( "Add UAS leg '" + request.getCallId() + "' as '" + roleName + "' to CS " + getId() );
            UASLeg leg = new UASLeg( ctx, this, request, roleName );
            legMap.put( request.getSession().getCallId(), leg );

            SipSessionManager.add( request.getSession() );
//            CallServiceFactory.createCallIndex( request.getCallId(), this );
            return leg;
        }
    }

    public UACLeg addUACLeg( SipServletRequest req, String roleName ) {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        synchronized ( LEG_MAP_UPDATE_LOCK ) {
            logger.finer( "Add UAC leg '" + req.getSession().getCallId() + "' as '" + roleName + "' to CS " + getId() );
            UACLeg leg;
            if ( roleName.equalsIgnoreCase( Leg.AGENT ) ) {
                leg = new AgentLeg( ctx, this, req, roleName );
            } else {
                leg = new UACLeg( ctx, this, req, roleName );
            }
            legMap.put( req.getSession().getCallId(), leg );


            SipSessionManager.add( req.getSession() );
//            CallServiceFactory.createCallIndex( session.getCallId(), this );
            return leg;
        }
    }

    public void removeLeg( SipSession session ) {
        synchronized ( LEG_MAP_UPDATE_LOCK ) {
            if ( !legMap.containsKey( session.getCallId() ) ) {
                //            logger.severe( "No leg for session " + session.getCallId() + " is assigned in CS " + getId() );
                throw new IllegalStateException( "No leg for session " + session.getCallId() + " is assigned in CS " + getId() );
            }
            Leg leg = (Leg) legMap.remove( session.getCallId() );
//            if( !getId().equals( session.getCallId() ) ) { // let the front owner index still exist
//                CallServiceFactory.removeCallIndex( session.getCallId() );
//            }
            SipSessionManager.remove( session );
            logger.finer( "Remove call leg '" + session.getCallId() + "' as '" + leg.getRoleName() + "' from CS " + getId() );
//            return leg;
        }
        checkIsDisposable();
    }

    public Leg getLeg( SipSession session ) {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        if ( !legMap.containsKey( session.getCallId() ) ) {
            throw new IllegalStateException( "No leg for session " + session.getCallId() + " is assigned in CS " + getId() );
        }
        return (Leg) legMap.get( session.getCallId() );
    }

    public Leg getLeg( String roleName ) {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        Iterator legs = legMap.values().iterator();
        synchronized ( legs ) {
            while ( legs.hasNext() ) {
                Leg leg = (Leg) legs.next();
                if ( leg.getRoleName().equalsIgnoreCase( roleName ) ) {
                    return leg;
                }
            }
        }
        throw new IllegalStateException( "No leg for role name '" + roleName + "' is assigned in " + this );
    }

    public Collection allLegs() {
        Collection legs = new ArrayList( legMap.values() );
        return Collections.unmodifiableCollection( legs );
    }

    public boolean isInRole( SipSession leg, String roleName ) {
        return getLeg( leg ).getRoleName().equals( roleName );
    }

    public Leg getTransferLeg() {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        return transferLeg;
    }

    public void setTransferLeg( Leg transferOriginator ) {
        getLeg( transferOriginator.getSession() ); // to check if Leg already exists!
        this.transferLeg = transferOriginator;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI( String requestURI ) {
        this.requestURI = requestURI;
    }

    public Address getCallerAddress() {
        return callerAddress;
    }

    public void setCallerAddress( Address callerAddress ) {
        this.callerAddress = callerAddress;
    }

    public Address getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress( Address targetAddress ) {
        this.targetAddress = targetAddress;
    }

//    public void mergeTransferDialog( CallService toBeMerged ) {
//        if( disposed ) {
//            throw new CallAlreadyDisposedError( id );
//        }
//        arrival = toBeMerged.arrival;
//        setHandlerQueueName( toBeMerged.getHandlerQueueName() );
//        setHandlerAgent( toBeMerged.getHandlerAgent() );
//        toBeMerged.setState( TRANSFERING );
//        setState( toBeMerged.getState() );
//    }

    public SipServletRequest getInitialRequest() {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        return initialRequest;
    }

    private void setInitialRequest( SipServletRequest request ) {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        this.initialRequest = request;
    }

    public long getArrival() {
        return arrival;
    }

    public void setArrival( long arrival ) {
        this.arrival = arrival;
    }

    public ServletTimer getWaitTimer() {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        return waitTimer;
    }

    private void setWaitTimer( ServletTimer waitTimer ) {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        this.waitTimer = waitTimer;
    }

    /**
     * You may think of a lock between getTransferTimer and createTransferTimer...
     *
     * @return
     */
    public ServletTimer getTransferTimer() {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        transferTimerAlreadyRead = true;
        return transferTimer;
    }

    private void setTransferTimer( ServletTimer transferTimer ) {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        this.transferTimer = transferTimer;
    }

    public void createWaitTimer( TimerService timerService, long waitTime ) {
        WaitingTimerContext timerCtx = new WaitingTimerContext( ctx.getServiceFactory(), this );
        setWaitTimer( createTimer( waitTime, timerCtx ) );
    }

    protected ServletTimer createTimer( long timout, TimerContext timerCtx ) {
        return ctx.getTimerService().createTimer( getInitialRequest().getApplicationSession(), timout, false, timerCtx );
    }

    public void createTransferTimer( long transferTimeout ) {
        if ( !transferTimerAlreadyRead ) {
            ReferTransferTimerContext timerCtx = new ReferTransferTimerContext( ctx.getServiceFactory(), this );
            setTransferTimer( createTimer( transferTimeout, timerCtx ) );
        }
    }

    /*public void proxy( URI targetURI ) throws CallServiceException {
        if( getState() == INITIALIZED ) {
            setState( CallService.PROXY );
            try {
                Proxy p = getInitialRequest().getProxy();
                p.setRecordRoute( OpxiSipServlet.RECORD_ROUTE );
                p.setSupervised( OpxiSipServlet.SUPERVISED );
                p.proxyTo( targetURI );
                getInitialRequest().getSession().setHandler( ApplicationConstants.PROXY_SERVLET );
            } catch ( Exception e ) {
                logger.severe( e.getMessage() );
                throw new CallServiceException( e );
            }
        } else {
            throw new IllegalStateException( "Can't proxy the call in state '" + getStateString() + "' to: " + target );
        }
    }

    public void queue( URI targetURI ) throws CallServiceException {

    }*/

    public int getState() {
        return state;
    }

    public String getStateString() {
//        if( disposed ) {
//            throw new CallAlreadyDisposedError( id );
//        }
        return STATE[state];
    }

    public void setState( int state ) throws IllegalStateException {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        if ( state != 0 ) {
            logger.finer( "State change [" + STATE[this.state] + "-->" + STATE[state] + "] for call " + this );
        }
        switch ( state ) {
            case QUEUE:
                if ( getHandlerQueueName() == null )
                    throw new IllegalStateException( "Queue Id not set. Please set a queueName before." );
                break;
            case PROXY:
                /*if( getProxyTargets().size() == 0 )
                    throw new IllegalStateException( "Proxy Targets not set. Please set proxyTargets before." );*/
                break;
            case MAKE_CALL:
            case IN_GREETING:
            case IN_CALL:
                if ( getHandlerAgent() == null )
                    throw new IllegalStateException( "Handler Agent not set. Please set handlerAgent before." );
                break;
            case QUEUED:
                if ( getWaitingRoom() == null /*|| getWaitTimer() == null*/ )
                    throw new IllegalStateException( "WaitingRoom/WaitTimer not set. Please set them before." );
                break;
            default:
                if ( state > STATE.length - 1 )
                    throw new IllegalStateException( "State not defined: " + state );
        }
        this.state = state;
        if ( getTarget() != null ) {
            getTarget().callStateUpdated( state );
        }
    }

    public String getId() {
//        if( disposed ) {
//            throw new CallAlreadyDisposedError( id );
//        }
        return id;
    }

    private void setId( String id ) {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        this.id = id;
    }

    public String getHandlerAgent() {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        return handlerAgent;
    }

    public void setHandlerAgent( String handlerAgent ) {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        this.handlerAgent = handlerAgent;
    }

    public List getProxyTargets() {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        return proxyTargets;
    }

    public void addProxyTarget( List proxyTarget ) {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        this.proxyTargets.addAll( proxyTarget );
    }

    public void addProxyTarget( URI contact ) {
        this.proxyTargets.add( contact );
    }

    public void setTarget( CallTarget target ) {
        this.target = target;
    }

    public CallTarget getTarget() {
        return target;
    }

    public String getWaitingRoom() {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        return waitingRoom;
    }

    public void setWaitingRoom( String waitingRoom ) {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        this.waitingRoom = waitingRoom;
    }

    public long waitTime() {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        if ( connectTime != 0 ) {
            return connectTime - arrival;
        } else {
            return System.currentTimeMillis() - arrival;
        }
    }

    public String getHandlerQueueName() {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        return handlerQueueName;
    }

    public void setHandlerQueueName( String queueId ) {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        handlerQueueName = queueId;
    }

    public String getIM() {
        return IM;
    }

    public void setIM( String IM ) {
        this.IM = IM;
    }

    public long getConnectTime() {
        return connectTime;
    }

    public void setConnectTime( long connectTime ) {
        this.connectTime = connectTime;
    }

    public long getDisconnectTime() {
        return disconnectTime;
    }

    public void setDisconnectTime( long disconnectTime ) {
        this.disconnectTime = disconnectTime;
    }

    public void setRingingTime( long ringingTime ) {
        this.ringingTime = ringingTime;
    }

    public boolean isAbandoned() {
        return abandoned;
    }

    public ApplicationIntegrationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext( ApplicationIntegrationContext applicationContext ) {
        this.applicationContext = applicationContext;
    }

    public int getAgentAnswerTime() {
        if ( ringingTime == 0 ) {
            return 0;
        }
        if ( connectTime == 0 ) {
            return (int) (System.currentTimeMillis() - ringingTime);
        }
        return (int) (connectTime - ringingTime);
    }

    public int getAnswerTime() {
        if ( connectTime == 0 ) {
            return (int) (System.currentTimeMillis() - arrival);
        }
        return (int) (connectTime - arrival);
    }

    public long duration() {
        if ( connectTime == 0 ) {
            return 0;
        }
        if ( disconnectTime == 0 ) {
            return (System.currentTimeMillis() - connectTime);
        }
        return (disconnectTime - connectTime);
    }

    public void ringing() {
        setState( CallService.RINGING );
        setRingingTime( System.currentTimeMillis() );
        try {
            ctx.getServiceFactory().getAgentService().getAgentByAOR( getHandlerAgent() ).ringing();
        } catch ( AgentNotAvailableException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    public void inGreeting() {
        setState( CallService.IN_GREETING );
        setRingingTime( System.currentTimeMillis() );
    }


    public void answered() {
        setState( CallService.IN_CALL );
        setConnectTime( System.currentTimeMillis() );

        try {

            ctx.getServiceFactory().getAgentService().getAgentByAOR( getHandlerAgent() ).answered( this );

            if ( getHandlerQueueName() != null ) {
                ctx.getServiceFactory().getQueueManagementService().queueForName( getHandlerQueueName() )
                        .doesApplicationInvolve( this );
            }

        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }

    }

    public void reject( int statusCode, String causeMsg ) {
        try {
            ((UASLeg) getLeg( getInitialRequest().getSession() )).reject( statusCode, causeMsg );
        } catch ( IOException e ) {
            logger.log( Level.SEVERE, e.getMessage() );
        }
    }

    public void transfer( String transferLegRoleName, URI target ) throws CallTransferException {
        getLeg( transferLegRoleName ).transfer( target );
    }

    public void playGreeting() throws OpxiException {
        CallController cc = new SipCallController( this );
        cc.setCalleeRoleName( Leg.GREETING_MEDIA );
        cc.setMachineType( SipCallController.GREETING_MACHINE );
//        cc.setUASLeg( (UASLeg)getTransferLeg() );
        Agent agent = ctx.getServiceFactory().getAgentService().getAgentByAOR( getHandlerAgent() );
        cc.setCalleeURI( ctx.getServiceFactory().getMediaService().getGreetingMediaURI( getId(), agent ) );
        cc.connect();
    }

    public void playWaitingMsg() throws OpxiException {
        CallController cc = new SipCallController( this );
        cc.setCalleeRoleName( Leg.WAITING_MEDIA );
        cc.setMachineType( SipCallController.WAIT_MACHINE );
        cc.setCalleeURI( ctx.getServiceFactory().getMediaService().getWaitingRoomMediaURI( getWaitingRoom() ) );
        cc.connect();
    }

    public void addPostGreetingLeg( SipServletRequest request ) throws OpxiException, IOException {
        try {
            if ( getState() == CallService.IN_GREETING ) {
                addUASLeg( request, Leg.IVR_TRANSFER_AGENT );
                playPostGreeting();
            } else {
                logger.warning( "Call Gone!" );
                request.createResponse( SipServletResponse.SC_GONE, "Call Gone" ).send();
            }
        } catch ( CallAlreadyDisposedError e ) {
            request.createResponse( SipServletResponse.SC_GONE, "Call Already Disposed" ).send();
            logger.log( Level.WARNING, e.getMessage(), e );
        }
    }

    private void playPostGreeting() throws IOException, OpxiException {
        try {
            UASLeg IVRTransfer2Agentleg = (UASLeg) getLeg( Leg.IVR_TRANSFER_AGENT );
            SipSession IVRT2ASession = IVRTransfer2Agentleg.getSession();
            Leg callInitiator = getTransferLeg();
            logger.finer( "call initiator state " + callInitiator );
            if ( callInitiator.getState().equals( LegState.IN_CALL ) ) {

                try {
                    Agent agent = ctx.getServiceFactory().getAgentService().getAgentByAOR( getHandlerAgent() );
                    Address agentAddress = this.ctx.getSipFactory().createAddress(
                            this.ctx.getSipFactory().createURI( agent.getAOR() ), agent.getName()
                    );
                    SipCallController cc = new SipCallController( this );
                    cc.setCalleeAddress( agentAddress );
                    Presence p = ctx.getServiceFactory().getPresenceService().getPresence( new UserAgent( agent.getAOR() ) );
                    cc.setCalleeURI( p.getLocation().getURI() );
                    cc.setMachineType( SipCallController.POST_GREETING_MACHINE );
                    cc.setCalleeRoleName( Leg.AGENT );
                    cc.setUASLeg( IVRTransfer2Agentleg );
                    cc.setCallerAddress( getCallerAddress() );
                    cc.setRequestModifier( new PostGreetingRequestModifier( ctx, getCallerAddress(), getInitialRequest().getTo() ) );
                    cc.connect();
                    getLeg( IVRT2ASession ).setLegTimer(
                            new PostGreetingLegTerminationTimer( this.ctx.getServiceFactory(), this )
                    );
                    if ( getIM() != null ) {
                        ctx.getServiceFactory().getSipService().sendIM( p, getIM(), SIPConstants.MIME_TEXT_HTML );
                    }

                    setState( CallService.MAKE_CALL );
                } catch ( AgentNotAvailableException e ) {
                    logger.log( Level.SEVERE, "Transfer to agent failed: " + e, e );
                    IVRTransfer2Agentleg.reject( SipServletResponse.SC_NOT_FOUND, e.getMessage() );
                } catch ( NoActivePublishContextException e ) {
                    logger.log( Level.SEVERE, "Transfer to agent failed: " + e, e );
                    IVRTransfer2Agentleg.reject( SipServletResponse.SC_NOT_FOUND, "Agent Has no Active Presence Context" );
                }
            } else {
                IVRTransfer2Agentleg.reject( SipServletResponse.SC_TEMPORARLY_UNAVAILABLE, "Caller Not Available" );
            }
        } catch ( ServletParseException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new OpxiException( e );
        }
    }

    public void disconnected( Leg disconnectingLeg ) {
        if ( disconnectingLeg.getRoleName().equals( Leg.CALLER )
                || disconnectingLeg.getRoleName().equals( Leg.TRANSFERED_TO_GREETING ) ) {
            if ( getHandlerAgent() == null || getState() != IN_CALL ) {
                // front is abandoned by caller!
                if ( !abandoned ) { // above condition becomes true for 2 legs of each front in some cases!
                    abandoned = true;
//                    try {
//                        int abandonTime = (int)(System.currentTimeMillis() - arrival);
//                        String queueALId = ctx.getServiceFactory().getQueueManagementService().queueForName( getHandlerQueueName() ).getActivityLogId();
//                        QueueActivityLogger queueL = ( (QueueActivityLogger)ctx.getServiceFactory().getLogService().getActivityLoggerByType( QueueActivityLogger.class.getName() ) );
//                        queueL.addCallAbandonTime( queueALId, abandonTime );
//                        queueL.incCallsAbandoned( queueALId );
//                    } catch ( OpxiException e ) {
//                        logger.log( Level.SEVERE, e.getMessage(), e );
//                    }
                }
            }
        }
    }

    public synchronized void teardown( String reason ) {
        synchronized ( LEG_MAP_UPDATE_LOCK ) {
            if ( legMap.size() > 0 ) {
                setState( TEAR_DOWN );
                Iterator legs = legMap.values().iterator();
                synchronized ( legs ) {
                    while ( legs.hasNext() ) {
                        Leg leg = (Leg) legs.next();
                        try {
                            leg.terminate();
                        } catch ( IOException e ) {
                            logger.log( Level.SEVERE, e.getMessage(), e );
                            leg.setState( LegState.IDLE );
                        } catch ( IllegalStateException e ) {
                            logger.log( Level.SEVERE, e.getMessage(), e );
                            leg.setState( LegState.IDLE );
                        }
                    }
                }
            }
        }
    }

    // TODO this method should have been placed in UACLeg (or AgentLeg class which does not exist)
    public void releaseAgent() throws OpxiException {
        try {
            if ( getHandlerAgent() != null ) {
                Agent freedAgent = ctx.getServiceFactory().getAgentService().getAgentByAOR( getHandlerAgent() );
                freedAgent.releaseCall( this );
            } else {
                logger.warning( "No HandlerAgent is set for the call " + this );
            }
        } catch ( AgentNotAvailableException e ) {
            logger.log( Level.WARNING, e.getMessage(), e );
        }
    }

    protected void release() {
        synchronized ( LockManager.getReleaseScheduleLock( this ) ) {
            logger.finer( "Release call '" + getId() + "'." );
            try {
                String queueName = getHandlerQueueName();
                if ( queueName != null ) {
                    QueueManagementService qms = ctx.getServiceFactory().getQueueManagementService();
                    Queue queue = qms.queueForName( queueName );
                    queue.release( this );
                }
            } catch ( QueueManagerException e ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
            } catch ( QueueNotExistsException e ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
            } catch ( Throwable t ) {
                logger.log( Level.SEVERE, t.getMessage(), t );
            }
        }
    }

    public boolean equals( Object that ) {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        if ( that == null )
            return false;

        if ( this == that )
            return true;

        if ( !(that instanceof CallService) )
            return false;

        CallService thatCall = (CallService) that;
        return this.getId().equals( thatCall.getId() );
    }

    public int hashCode() {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        return (id != null ? id.hashCode() : 0);
    }

    private void checkIsDisposable() {
        if ( legMap.size() == 0 ) {
            logger.finer( "No leg left to monitor. Ready to dispose myself..." );
            dispose();
        } else {
            logger.finer( "CallService still has '" + legMap.size() + "' leg(s)." );
        }
    }

    private synchronized void dispose() {
        if ( disposed ) {
            throw new CallAlreadyDisposedError( id );
        }
        logger.finer( toString() );
        setState( DISPOSING );
        release();
        CallServiceFactory.removeCallIndex( getId() );
//        getInitialRequest().getApplicationSession().invalidate();
        handlerAgent = null;
        handlerQueueName = null;
        waitingRoom = null;
        proxyTargets.clear();
        legMap.clear();
        disposed = true;
    }

    public String toString() {
        try {
            return OpxiToolBox.unqualifiedClassName( getClass() ) + "[" + getId() +
                    "]: arrival: " + new Timestamp( arrival ) + ", state: " + getStateString() +
                    ", caller=" + getInitialRequest().getFrom().getURI() +
                    ", queue type: " + getHandlerQueueName() +
                    ", handlerAgent: " + getHandlerAgent() +
                    ", requestURI: " + getRequestURI() +
                    ", duration: " + OpxiToolBox.duration( duration() );
        }
        catch ( CallAlreadyDisposedError e ) {
            return "[" + getId() + "]";
        }

    }
}