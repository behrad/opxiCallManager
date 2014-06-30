package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.sip.CallServlet;
import com.basamadco.opxi.callmanager.sip.SipCallController;
import com.basamadco.opxi.callmanager.sip.util.ApplicationConstants;
import com.basamadco.opxi.callmanager.sip.util.SIPConstants;
import com.basamadco.opxi.callmanager.sip.util.SipUtil;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import com.basamadco.opxi.callmanager.util.PropertyUtil;
import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.SipService;

import javax.servlet.ServletException;
import javax.servlet.sip.*;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Jul 16, 2006
 *         Time: 3:48:04 PM
 */
public class Leg implements SIPConstants {

    private static final Logger logger = Logger.getLogger( Leg.class.getName() );


    /*
     * Some handy String based leg role name constants used in Call Manager B2BUAs
     */
    public static final String CALLER = "Caller";

    public static final String VOICE_APP = "Voice Application";

    public static final String AGENT = "Agent";

    public static final String WAITING_MEDIA = "Waiting Media";

    public static final String GREETING_MEDIA = "Greeting Media";

    public static final String IVR_TRANSFER_AGENT = "IVR Transferer to Agent";

    public static final String TRANSFERED_TO_GREETING = "Transferee to Greeting";

    public static final String TRANSFERED_LEG = "Transferee Leg";

    public static final String UNILEG = "Unileg";
    /*
     * End of Some handy String based leg role name constants used in Call Manager B2BUAs
     */

//    public static final String SESSION_LEG_ATTRIBUTE_NAME = "com.basamadco.opxi.callmanager.call.Leg";

    protected SipService sipService;

    private SipSession session;

    private String roleName; // one of the above!

    private SipServletRequest initialRequest;

    private SipServletRequest savedRequest;

    private SipServletResponse successResponse;

    private CallService belongingCall;

    protected LegState state;

    private TimerContext legTimer;


    private static final long ALIVE_CALL_LIMIT = Integer.parseInt(
            PropertyUtil.getProperty( "opxi.callmanager.aliveCallDuration" )
    ) * 1000;

    private final static String L_LABEL = "State-Change-Lock-";

    private final Object STATE_CHANGE_LOCK = L_LABEL;

    /**
     * Introduction of such property makes our B2BUA implementations as binary-leg-b2bua.
     * We're not supporting multi-leg b2bua yet!
     */
    private Leg peer;

    private URI markForTransfer;


    public int reInviteCount = 1;


    protected Leg( SipService sipFactory, CallService call, SipServletRequest req, String roleName ) {
        if ( roleName == null ) {
            throw new IllegalStateException( "Role name is null for '" + req.getCallId() + "'" );
        }
        this.roleName = roleName;
        this.sipService = sipFactory;
        if ( req != null ) { // UniLegPlaceHolder passes null parameters!
            this.session = req.getSession();
            setInitialRequest( req );
            // bind this Leg instance to corresponding sipsession
            logger.finest( "Creating Leg[" + session.getCallId() + "] for SipSession: " + session );
            logger.finest( "AppSession: " + session.getApplicationSession() );
            session.setAttribute( Leg.class.getName(), this );
        }
        this.belongingCall = call;
        this.state = LegState.TRYING;
        legStateUpdated();
    }

    public SipSession getSession() {
        return session;
    }

    public void setSession( SipSession session ) {
        this.session = session;
    }

    public Leg getPeer() {
        if ( peer == null ) {
            throw new NoPeerLegBoundException( "No peer leg is set for '" + session.getCallId() + "'" );
        }
        return peer;
    }

    protected void setPeer( Leg peer ) {
        clearPeerLeg();
        this.peer = peer;
    }

    public void bind( Leg peer ) {
        // Set up a bidirectional association between the two legs in one step
        logger.finer( "Bind pair('" + session.getCallId() + "', '" + peer.session.getCallId() + "')" );
        this.setPeer( peer );
        peer.setPeer( this );
    }

    private void clearPeerLeg() {
        Leg toClear = peer;
        if ( toClear != null && toClear != UniLegPlaceHolder.INSTANCE ) {
            toClear.peer = UniLegPlaceHolder.INSTANCE;
            peer = UniLegPlaceHolder.INSTANCE;
        }
    }

    public SipServletRequest getInitialRequest() {
        return initialRequest;
    }

    public void setInitialRequest( SipServletRequest initialRequest ) {
        this.initialRequest = initialRequest;
        setSavedRequest( initialRequest );
    }

    public CallService getCallService() {
        return belongingCall;
    }

    public void setCallService( CallService belongingCall ) {
        this.belongingCall = belongingCall;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName( String roleName ) {
        this.roleName = roleName;
    }

    public SipServletResponse getSuccessResponse() {
        return successResponse;
    }

    public void setSuccessResponse( SipServletResponse successResponse ) {
        this.successResponse = successResponse;
    }


    public TimerContext getLegTimer() {
        return legTimer;
    }

    public void setLegTimer( TimerContext legTimer ) {
        this.legTimer = legTimer;
    }

    public boolean hasSetTimer() {
        return legTimer != null;
    }

    public LegState getState() {
        synchronized ( STATE_CHANGE_LOCK ) {
            return state;
        }
    }

    public void setState( LegState state ) {
        synchronized ( STATE_CHANGE_LOCK ) {
            if ( state.equals( getState() ) ) {
                logger.warning( "State already is set to '" + state + "'." );
                return;
            }
            logger.finer( "Leg state update '" + this.state + "'-->'" + state + "' for " + session.getCallId() );
            this.state = state;
            legStateUpdated();
        }
        if ( this.state.equals( LegState.IN_CALL ) ) {
            URI transferTarget = getMarkForTransfer();
            if ( transferTarget != null ) {
                logger.finest( "This leg is marked to be transfered to '" + transferTarget + "'" );
                try {
                    transfer( transferTarget );
                } catch ( CallTransferException e ) {
                    logger.log( Level.SEVERE, e.getMessage(), e );
                }
                setMarkForTransfer( null );
            }
        }
        // postpone disposing for when associated sipSession is timed-out
        if ( this.state.equals( LegState.IDLE ) ) {
            dispose();
        }
    }

    /**
     * To update registered UAS leg's status
     */
    protected void legStateUpdated() {

    }

    public boolean isIdle() {
        return getState() == LegState.IDLE;
    }

    public boolean isAlive() {
//        logger.finest( "Call connect time: " + new Date( getCallService().getConnectTime() ) );
//        logger.finest( "Call aliveness limit time: " + new Date( getCallService().getConnectTime() + ALIVE_CALL_LIMIT ) );
        return (getCallService().getState() == CallService.IN_CALL || getCallService().getState() == CallService.CONNECTED)
                &&
                ((getCallService().getConnectTime() + ALIVE_CALL_LIMIT) > System.currentTimeMillis());
    }

    public void handleBye( SipServletRequest bye ) throws IOException {
        if ( !bye.isCommitted() ) {
            bye.createResponse( SipServletResponse.SC_OK ).send();
        }
        setState( LegState.IDLE );
    }

    /**
     * Establishes a connection between this leg and the specified target URI
     * <p/>
     * NOTE: This method should go to UACLeg!
     *
     * @param application the callee URI
     * @throws CallServiceException
     */
    public void involve( Application application ) throws CallServiceException {
        logger.finest( "involve '" + application.getName() + "' in '" + this + "'" );
        try {
            if ( state == LegState.TRYING ) {
                String appRole = application.getParameterMap().get( "asRole" );
                SipServletRequest peerInvite = createRequest( getInitialRequest(), (URI) application.getTargetURIs().get( 0 ) );

                if ( AGENT.equalsIgnoreCase( appRole ) ) {
                    getSession().setHandler( SipCallController.POST_GREETING_MACHINE );
                    peerInvite.getSession().setHandler( SipCallController.POST_GREETING_MACHINE );
                } else {  // Other or null
                    getSession().setHandler( SipCallController.APPLICATION_MACHINE );
                    peerInvite.getSession().setHandler( SipCallController.APPLICATION_MACHINE );
                }

                if ( OpxiToolBox.isEmpty( appRole ) ) {
                    createPeerLeg( peerInvite, Leg.VOICE_APP );
                } else {
                    createPeerLeg( peerInvite, appRole );
                }
                peerInvite.send();
            } else if ( state == LegState.IN_CALL ) {
                /*getSession().setHandler( SipCallController.B2B_APPLICATION_MACHINE );
                SipServletRequest peerInvite = createRequest( getInitialRequest(), getInitialRequest().getFrom(),
                        getInitialRequest().getTo() );
                peerInvite.setRequestURI( targetURI );
                peerInvite.getSession().setHandler( SipCallController.B2B_APPLICATION_MACHINE );
                createPeerLeg( peerInvite, Leg.VOICE_APP );
                peerInvite.send();*/

                try {
                    if ( application.getTelephoneNumber() == null ) {
                        logger.warning( "No telephone number is set for the application '" + application.getName() + "'" );
                        logger.warning( "Cannot involve application, trying to tear down this leg" );
                        terminate();
                        return;
                    }
                    SipURI targetURI = (SipURI) application.getTargetURIs().get( 0 );
                    targetURI.setUser( application.getTelephoneNumber() );
                    targetURI.setHost( OpxiToolBox.getLocalDomain() );
                    targetURI.setPort( -1 );
                    getCallService().transfer( getRoleName(), targetURI );
                } catch ( OpxiException e ) {
                    logger.log( Level.SEVERE, e.getMessage(), e );
                }

            } else if ( state == LegState.RINGING ) {
                // INVITE application party!
                // receive ringing!
                // receive 200
                // send a 200 (from received 200 SDP) to caller...
                getSession().setHandler( SipCallController.RINGING_APPLICATION_MACHINE );
                SipServletRequest peerInvite = createRequest( getInitialRequest(), (URI) application.getTargetURIs().get( 0 ) );
                peerInvite.getSession().setHandler( SipCallController.RINGING_APPLICATION_MACHINE );
                createPeerLeg( peerInvite, Leg.VOICE_APP );
                peerInvite.send();
            } else {
                throw new IllegalStateException( "Couldn't recognize correct action for leg '" + getRoleName() + "' in state '" + state + "'" );
            }
        } catch ( Throwable e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            setState( LegState.IDLE );
//            throw new CallServiceException(e);
        }
    }

    public void transfer( URI target ) throws CallTransferException {
        if ( getState().equals( LegState.IN_CALL ) ) {
            try {
                sipService.getServiceFactory().getQueueManagementService().setExpectedCallForTransfer(
                        getCallService()
                );
                Address referredBy = sipService.getSipFactory().createAddress( sipService.getLocalURI() );
                referredBy.setParameter( CallServlet.TRANSFER_CALLID_PARAM, getSession().getCallId() );
                Address referTo = sipService.getSipFactory().createAddress( target );
                //            Address referTo = ctx.getSipFactory().createAddress();
                Iterator iterator = ((SipURI) target).getParameterNames();
                while ( iterator.hasNext() ) {
                    String paramName = (String) iterator.next();
                    referredBy.setParameter( paramName, ((SipURI) target).getParameter( paramName ) );
                }
                SipServletRequest refer = createRefer( referTo, referredBy );
                refer.send();
            } catch ( Throwable t ) {
                logger.log( Level.WARNING, "Unable to transfer '" + this.getRoleName() + "': " + t.getMessage(), t );
                throw new CallTransferException( t.getMessage(), t );
            }
        } else {
            logger.warning( "Could not transfer leg '" + getRoleName() + "' in state '" + getState() + "'" );
            setMarkForTransfer( target );
        }
    }

    private void setMarkForTransfer( URI target ) {
        markForTransfer = target;
    }

    private URI getMarkForTransfer() {
        return markForTransfer;
    }

    public void terminate() throws IOException {
        if ( getState().equals( LegState.ACK_PENDING ) ) {
            getSuccessResponse().createAck().send();
            setState( LegState.IN_CALL );
            // send corrupt ACK, this forces them to BYE!?
        } else if ( getState().equals( LegState.IN_CALL ) || getState().equals( LegState.FAILURE ) ) {
            setState( LegState.END );
            SipServletRequest bye = session.createRequest( BYE );
            bye.send();
        } else if ( getState().equals( LegState.END ) ) {
            setState( LegState.IDLE );
        } else {
            logger.warning( "Couldn't find terminate action in state: " + this );
//            setState( LegState.IDLE );
        }
    }

    public SipServletRequest createRequest( SipServletRequest request ) throws ServletException, IOException {
        if ( !request.isInitial() ) {
            SipServletRequest newReq = session.createRequest( request.getMethod() );
            copyHeaders( request, newReq );
            copyContent( request, newReq );
            return newReq;
        }
        throw new IllegalStateException( "Use this method when creating subsequent requests: " + request );
    }

    public SipServletRequest createRequest( String method ) throws ServletException, IOException {
        SipServletRequest newReq = session.createRequest( method );
        return newReq;
    }

    public SipServletRequest createRefer( SipServletRequest refer ) throws ServletException, IOException {
        if ( !refer.isInitial() && refer.getMethod().equals( REFER ) ) { // just support indialog REFERs
            Address referredBy = refer.getAddressHeader( REFERRED_BY );
            referredBy.setParameter( CallServlet.TRANSFER_CALLID_PARAM, getSession().getCallId() );
            SipServletRequest newReq = createRefer( refer.getAddressHeader( REFER_TO ), referredBy );
            copyContent( refer, newReq );
            return newReq;
        }
        throw new IllegalStateException( "Use this method when creating indialog REFERs: " + refer );
    }

    public SipServletRequest createRefer( Address referTo, Address referredBy ) throws ServletException, IOException {
        SipServletRequest newReq = createRequest( REFER );
        // Added new refer-to header with canonical host name, to work with new version of Cisco gateway type1 dns lookups!
        Address newReferTo = sipService.getSipFactory().createAddress(
                sipService.getSipFactory().createSipURI(
                        ((SipURI)referTo.getURI()).getUser(),
                        OpxiToolBox.getLocalHostName()
                )
        );
        newReq.setAddressHeader( REFER_TO, newReferTo );
//        newReq.setAddressHeader( REFER_TO, referTo );
        newReq.setAddressHeader( REFERRED_BY, referredBy );
        ((SipURI) newReq.getRequestURI()).setParameter( ApplicationConstants.OUTBOUND_B2BUA_LEG, "true" );
        return newReq;
    }

    public SipServletRequest createNotify( SipServletRequest notify ) throws ServletException, IOException {
        if ( !notify.isInitial() && notify.getMethod().equals( NOTIFY ) ) { // just support indialog REFERs
            if ( session != null ) {
                SipServletRequest newReq = session.createRequest( notify.getMethod() );
                if ( notify.getHeader( EVENT ) != null ) {
                    newReq.setHeader( EVENT, notify.getHeader( EVENT ) );
                }
                if ( notify.getHeader( SUBSCRIPTION_STATE ) != null ) {
                    newReq.setHeader( SUBSCRIPTION_STATE, notify.getHeader( SUBSCRIPTION_STATE ) );
                } else {
                    newReq.setHeader( SUBSCRIPTION_STATE, "active;expires=60" );
                }
                //            if( this instanceof UASLeg ) {
                //                newReq.setRequestURI( getInitialRequest().getAddressHeader( CONTACT ).getURI() );
                //            } else {
                //                newReq.setRequestURI( getInitialRequest().getRequestURI() );
                //            }
                ((SipURI) newReq.getRequestURI()).setParameter( ApplicationConstants.OUTBOUND_B2BUA_LEG, "true" );
                copyContent( notify, newReq );
                return newReq;
            }
            throw new IllegalStateException( "Session is null: " + getInitialRequest().getCallId() );
        }
        throw new IllegalStateException( "Use this method when creating indialog NOTIFYs: " + notify );
    }

    public SipServletRequest createRequest( SipServletRequest request, Address from, Address to ) throws ServletException {
        SipServletRequest newRequest;
        newRequest = sipService.getSipFactory().createRequest( request.getApplicationSession(),
                request.getMethod(), from, to );
        newRequest.setHeader( "X-opxi-peer-callId", request.getCallId() );
        ((SipURI) newRequest.getRequestURI()).setParameter( ApplicationConstants.OUTBOUND_B2BUA_LEG, "true" );
        return newRequest;
    }

    public SipServletRequest createRequest( SipServletRequest request, URI requestURI ) throws ServletException {
        SipServletRequest newRequest;
        newRequest = sipService.getSipFactory().createRequest( request, false );
        newRequest.setRequestURI( requestURI );
        newRequest.setHeader( "X-opxi-peer-callId", request.getCallId() );
        ((SipURI) newRequest.getRequestURI()).setParameter( ApplicationConstants.OUTBOUND_B2BUA_LEG, "true" );
        return newRequest;
    }

    public Leg createPeerLeg( SipServletRequest initialReq, String role ) {
        Leg peerLeg = belongingCall.addUACLeg( initialReq, role );
        peerLeg.setInitialRequest( initialReq );
//        peerLeg.setState( LegState.TRYING );
        bind( peerLeg );
        return peerLeg;
    }

    public void copyContent( SipServletMessage fromMsg, SipServletMessage toMsg ) throws IOException {
        if ( fromMsg.getContentLength() > 0 && fromMsg.getContentType() != null ) {
            toMsg.setContent( fromMsg.getRawContent(), fromMsg.getContentType() );
        }
    }

    public void copyHeaders( SipServletMessage fromMsg, SipServletMessage toMsg ) throws IOException {
        Iterator<String> headerNames = fromMsg.getHeaderNames();
        while ( headerNames.hasNext() ) {
            String headerName = headerNames.next();
            //TODO check for case-insensitive header names
            if ( !SYSTEM_HEADERS.contains( headerName ) ) {
                ListIterator<String> headers = fromMsg.getHeaders( headerName );
                while ( headers.hasNext() ) {
                    toMsg.addHeader( headerName, headers.next() );
                }
            }
        }
    }

//    private static final Object PENDING_REQ_LOCK = "Pending-Request-Lock";

    public SipServletRequest getSavedRequest() {
//        synchronized( PENDING_REQ_LOCK ) {
//            if( savedRequest == null ) {
//                throw new IllegalStateException( "No pending request is set in leg: '" + session.getCallId() + "'" );
//            }
        SipServletRequest copyRef = savedRequest;
//            savedRequest = null;
//            logger.finer( "Clean saved request: " + copyRef );
        return copyRef;
//        }
    }

    public void setSavedRequest( SipServletRequest requestToSave ) {
//        synchronized( PENDING_REQ_LOCK ) {
//            if( savedRequest != null ) {
//                throw new IllegalStateException( "Another request is already pending: " + savedRequest );
//            }
        savedRequest = requestToSave;
//            logger.finer( "Set saved request: " + requestToSave );
//        }
    }

    private static final Set<String> SYSTEM_HEADERS;

    static {
        final Set<String> temp = new HashSet<String>();
        temp.add( CALL_ID );
        temp.add( FROM );
        temp.add( TO );
        temp.add( CSEQ );
        temp.add( VIA );
        temp.add( RECORD_ROUTE );
        temp.add( ROUTE );
        temp.add( CONTACT );
        temp.add( MAX_FORWARDS );
        SYSTEM_HEADERS = Collections.unmodifiableSet( temp );
    }

    public String toString() {
        return OpxiToolBox.unqualifiedClassName( getClass() ) + "[" + (session == null ? "null" : session.getCallId())
                + "] as role '" + roleName + "' in state '" + state + "'";
    }

    public void dispose() {
        //TODO should be tested for synchronization! (see setState())
        // update peer leg
        setPeer( UniLegPlaceHolder.INSTANCE );

        // update CallService
        if ( hasSetTimer() ) {
            logger.finest( "Canceling leg timer: " + getLegTimer() );
            getLegTimer().cancel();
        }
        belongingCall.removeLeg( session );

        // Do not remove the binding with SipSession so late SIP messages can be processed within this leg object 
//        session.removeAttribute( Leg.class.getName() );

//        logger.fine( "Invalidating SipSession '" + session.getCallId() + "'" );
//        session.invalidate();
        logger.fine( "Leg with callId '" + session.getCallId() + "' is disposed" );
    }


}