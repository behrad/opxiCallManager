package com.basamadco.opxi.callmanager.pool;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;
import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.util.ResourceBundleUtil;
import com.basamadco.opxi.callmanager.util.PropertyUtil;
import com.basamadco.opxi.callmanager.call.ApplicationIntegrationException;
import com.basamadco.opxi.callmanager.call.CallService;
import com.basamadco.opxi.callmanager.call.ProxyTarget;
import com.basamadco.opxi.callmanager.entity.profile.OpxiCMEntityProfile;
import com.basamadco.opxi.callmanager.entity.profile.SkillScore;
import com.basamadco.opxi.callmanager.entity.profile.TrunkAccess;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.entity.Presence;
import com.basamadco.opxi.callmanager.logging.ActivityLogNotExistsException;
import com.basamadco.opxi.callmanager.logging.AgentActivityLogger;
import com.basamadco.opxi.callmanager.profile.ProfileException;
import com.basamadco.opxi.callmanager.queue.AgentScheduleTimer;
import com.basamadco.opxi.callmanager.queue.Queue;
import com.basamadco.opxi.callmanager.sip.util.SIPConstants;
import com.basamadco.opxi.callmanager.sip.util.SipUtil;

import javax.servlet.sip.Address;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents an agent in OPXi Call Manager for handling calls.
 * Agents are also callable entities which adds direct agent
 * calling mechanism. This service is currently implemented via
 * proxy.
 * For all UserAgents which are system listAgents, one and only one
 * Agent object will be loaded into system memory.
 * Agent object are accessible through AgentPools to system components
 *
 * @author Jrad
 * @see com.basamadco.opxi.callmanager.pool.AgentPool
 * @since 1.2
 */
public class Agent extends ProxyTarget {

    private final static Logger logger = Logger.getLogger( Agent.class.getName() );

    private String AOR;

    private Integer maxOpenCalls;

    private TrunkAccess[] accessList;

    protected String greetingMsgURI;

    private List<String> poolMemberships;

    private volatile AgentState state = AgentState.IDLE;

    private volatile int notAnsweredCalls;

    private volatile int openCalls;

    private volatile long lastCallClosed = System.currentTimeMillis();

    private transient CallService myCall;

//    private volatile boolean active = false;

    private Map skillProfile = new ConcurrentHashMap();

    private String activityLogId;

    private final static int NOT_ANSWERED_CALLS_LIMIT = Integer.parseInt(
            PropertyUtil.getProperty( "opxi.callmanager.agent.notAnsweredCallsLimit" ) );


    public Agent() {
//        super( name );
    }

    /**
     * The time agent has been idle (i.e. handling no calls)
     *
     * @return The duration this Agent has not handled any calls
     */
    public long idleTime() {
        if ( openCalls() > 0 ) {
            return 0;
        }
        return System.currentTimeMillis() - lastCallClosed;
    }

    private void decOpenCalls() {
        if ( openCalls > 0 ) {
            logger.finer( "dec OpenCalls[" + AOR + "]: " + openCalls() + " -> " + (openCalls - 1) );
            openCalls--;
            if ( openCalls == 0 ) { // was agent's last call in hand, IDLE time starts
                lastCallClosed = System.currentTimeMillis();
                checkApplyProfileUpdate();
            }
//                return true;
        } else {
            throw new IllegalStateException( "OpenCalls '" + AOR + "' is already 0!" );
        }
    }


    private void incOpenCalls() throws AgentNotIdleException {
        if ( !hasMaximumCallsReached() ) {
            logger.finer( "inc OpenCalls[" + AOR + "]: " + openCalls() + " -> " + (openCalls + 1) );
            openCalls++;
            lastCallClosed = 0;
        } else {
            throw new AgentNotIdleException( "Maximum calls reached for '" + getAOR() + "'" );
        }
    }

    /**
     * Returns how many open/concurrent calls this Agent has in handle
     *
     * @return
     */
    public synchronized int openCalls() {
        return openCalls;
    }

    public boolean isIdle() {
        return state == AgentState.IDLE;
    }

    /**
     * Assigns the call to this Agent and marks that as his open calls.
     * Also logs this to the Agent's Activity Log
     *
     * @param call The CallService object to handle
     * @throws AgentNotIdleException if this Agent has no free slots to accept the call
     */
    public synchronized void assignCall( CallService call ) throws AgentNotIdleException {
        if ( getServiceFactory().getPresenceService().isActive( getAOR() ) ) {
            if ( isIdle() ) {
                incOpenCalls();
                notifyHunt();
                call.setHandlerAgent( getAOR() );
                myCall = call;
//                logger.finest( "****************** Set MyCall to " + call );
                logger.finer( "Assign " + call + " to " + this );
                try {
                    getServiceFactory().getLogService().getAgentActivityLogger( getActivityLogId() ).incAssignedCalls();
                } catch ( OpxiException e ) {
                    logger.log( Level.SEVERE, e.getMessage(), e );
                }
                try {
                    doesApplicationInvolve( call );
                } catch ( ApplicationIntegrationException e ) {
                    logger.log( Level.SEVERE, e.getMessage(), e );
                }
            } else {
                throw new AgentNotIdleException( "Agent's not idle '" + getAOR() + "': " + state );
            }
        } else {
            throw new AgentNotIdleException( "Agent's presence is not active '" + getAOR() + "'" );
        }
    }

    /**
     * Releases the call from this Agent and removes that from his open calls
     *
     * @param call The CallService object to remove from this Agent's in handle calls
     */
    public synchronized void releaseCall( CallService call ) throws OpxiException {
        if ( !call.equals( myCall ) ) {
//            logger.finest( "****************** MyCall is: " + myCall );
//            logger.finest( "****************** ParameterCall is: " + call );
            logger.finest( "Call " + call + " should be already released from " + this );
            return;
        }
        logger.finer( "Release " + call + " from " + this );
        decOpenCalls();
        myCall = null;
//        logger.finest( "****************** Set MyCall to NULL" );

        if ( call.duration() > 0 ) { // call was answered by this agent
            Queue queue = getServiceFactory().getQueueManagementService().queueForName( call.getHandlerQueueName() );
            if ( queue.getIdleTimeToSchedule() > 0 ) {
                rest( queue.getIdleTimeToSchedule() );
            } else {
                idle();
            }
        } else {
            idle();
        }
        try {
            getServiceFactory().getLogService().getAgentActivityLogger( getActivityLogId() )
                    .addIncomingCallTime( call.duration() );
        } catch ( ActivityLogNotExistsException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    private void rest( long restTime ) throws OpxiException {
        setState( AgentState.RESTING );
        new AgentScheduleTimer( this, restTime );

    }

    public void finishRest() {
        lastCallClosed = System.currentTimeMillis();
        idle();
    }

    private synchronized boolean setState( AgentState state ) {
        logger.fine( "Try to set agent[" + getAOR() + "] state to: '" + state + "'" );
        if ( this.state != AgentState.OUT_OF_SERVICE ) {
            changeState( state );
            return true;
        }
        return false;
    }

    private synchronized void changeState( AgentState state ) {
        logger.fine( "Agent[" + getAOR() + "] state changed: '" + this.state + "'->'" + state + "'" );
        AgentState oldState = this.state;
        this.state = state;
        if ( oldState == AgentState.RESTING ) {

            try {
                Presence copy = new Presence( getServiceFactory().getPresenceService().getPresence(
                        new UserAgent( getAOR() )
                ) );
                getServiceFactory().getPresenceService().notifyPresence( copy );
            } catch ( OpxiException e ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
            }

        } else {
            notifyServiceState( state );
        }
    }

    private void notifyServiceState( AgentState state ) {
        try {
            Presence copy = new Presence( getServiceFactory().getPresenceService().getPresence(
                    new UserAgent( getAOR() )
            ) );
            copy.setNote( state.name() );
            if ( state == AgentState.IDLE ) {
                copy.setActivity( Presence.ACTIVITY_UNKNOWN );
            } else if ( state == AgentState.RESTING ) {
                copy.setActivity( Presence.ACTIVITY_AWAY );
            } else {
                copy.setActivity( Presence.ACTIVITY_ON_THE_PHONE );
            }
            getServiceFactory().getPresenceService().notifyPresence( copy );
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    @Override
    public void callStateUpdated( int callState ) {
        logger.warning( "======================== Should we change Agent state based on call state: "
                + callState + "?" );
        if ( callState == CallService.RINGING ) {
//            setState( AgentState.RINGING );
            /*try {
                Presence cmAgentPresence = getServiceFactory().getPresenceService().getPresence(
                        new UserAgent( getAOR() )
                );
                cmAgentPresence.setNote( this.state.name() );
                cmAgentPresence.setActivity( Presence.ACTIVITY_ON_THE_PHONE );
                getServiceFactory().getPresenceService().notifyPresence( cmAgentPresence );
            } catch ( OpxiException e ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
            }*/
        } else if ( callState == CallService.DISPOSING ) {
            logger.warning( "=========================== Should we change Agent state to IDLE?" );
//            setState( AgentState.IDLE );
        }
    }

    private void idle() {
        if ( setState( AgentState.IDLE ) ) {
            getServiceFactory().getQueueManagementService().serviceAgentIdleEvent( getAOR() );
        }
    }

    public void clearServiceStatus() {
        changeState( AgentState.IDLE );
    }

    private boolean hasMaximumCallsReached() {
        if ( getMaxOpenCalls() == null ) {
            return false;
        }
        return getMaxOpenCalls() == openCalls;
    }

    public boolean isResting() {
        return state == AgentState.RESTING;
    }

    public void setCN( String CN ) {
        super.setCN( CN );
        setAOR( SipUtil.toSipURIString( CN ) );
    }

    public void setAOR( String AOR ) {
        this.AOR = AOR;
    }

    public String getAOR() {
        return AOR;
    }

    public Integer getMaxOpenCalls() {
        return maxOpenCalls;
    }

    public void setMaxOpenCalls( Integer maxOpenCalls ) {
        this.maxOpenCalls = maxOpenCalls;
    }

    public void setMaxOpenCalls( String maxConcurrentCalls ) {
        this.maxOpenCalls = new Integer( maxConcurrentCalls );
    }

    public String getGreetingMsgURI() {
        return greetingMsgURI;
    }

    public void setGreetingMsgURI( String greetingMsgURI ) {
        this.greetingMsgURI = greetingMsgURI;
    }

    public boolean hasTrunkAccess( String trunkName ) {
        for ( TrunkAccess trunk : accessList ) {
            if ( trunk.getName().equalsIgnoreCase( trunkName ) ) {
                return true;
            }
        }
        return false;
    }

//    public void setTrunkDefaultAccess(boolean trunkDefaultAccess) {
//        this.trunkDefaultAccess = trunkDefaultAccess;
//    }

    /**
     * This can be used for observers to observe this Agent's hunt event.
     *
     * @return The <i>hunting</i> observable role object
     */
    public Observable hunting() {
        return huntNotifier;
    }

    public Observable selection() {
        return selectNotifier;
    }

    /**
     * Notifies observers of this Agent for his hunt event
     */
    public void notifyHunt() {
        huntNotifier.notifyHunt( this );
    }

    public void notifySelection( SelectionEvent event ) {
        selectNotifier.notifySelection( event );
    }

    /**
     * Returns the skill efficiency score for this Agent in skillName membership
     *
     * @param skillName The skill name this Agent is a member of
     * @return membership efficiency of the Agent in skillName
     */
    public float skillEfficiency( String skillName ) {
        Float score = (Float) skillProfile.get( skillName );
        if ( score == null ) {
            return 0.0f;
        }
        logger.finest( "Skill score for '" + skillName + "'=" + score );
        return score.floatValue();
    }

    /**
     * Returns all entities this Agent is a member of. These can be
     * workgroups, skills, ... generally called Pools.
     *
     * @return A String list containing pool names (CNs)
     */
    public List getPoolMemberships() {
        return poolMemberships;
    }

    public void setPoolMemberships( List<String> poolMemberships ) {
        this.poolMemberships = poolMemberships;
    }

    public boolean isMemeber( String poolId ) {
        for ( String poolName : poolMemberships ) {
            if ( poolId.equals( poolName ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Call queues are not supported for now. Agents are called throght proxy.
     *
     * @return false everytime
     */
    public boolean isQueueable() {
        return false;
    }

    public String stateString() {
        return state.name();
    }

    /**
     * Returns the Agent Activiry Log id associated to this Agent
     *
     * @return
     */
    public String getActivityLogId() {
        return activityLogId;
    }

    public void setActivityLogId( String activityLogId ) {
        this.activityLogId = activityLogId;
    }

    protected void applyProfile( OpxiCMEntityProfile profile ) throws ProfileException {
//        try {
//            OpxiCMEntityProfile profile = BaseDAOFactory.getWebdavDAOFactory().getAgentProfileDAO( getName() ).readProfile();
        setMaxOpenCalls( new Integer( profile.getOpxiCMEntityProfileChoice().getAgentProfile().getMaxOpenCalls() ) );
//        setTrunkDefaultAccess(profile.getOpxiCMEntityProfileChoice().getAgentProfile().getTrunkDefaultAccess());
        if ( profile.getOpxiCMEntityProfileChoice().getAgentProfile().getGreetingAudioCount() > 0 ) {
            setGreetingMsgURI( profile.getOpxiCMEntityProfileChoice().getAgentProfile().getGreetingAudio( 0 ).getSrc() );
        }
        SkillScore[] scores = profile.getOpxiCMEntityProfileChoice().getAgentProfile().getSkillScore();
        for ( int i = 0; i < scores.length; i++ ) {
            skillProfile.put( scores[i].getSkillName(), new Float( scores[i].getScore() ) );
        }

        accessList = profile.getOpxiCMEntityProfileChoice().getAgentProfile().getTrunkAccess();

        logger.finest( "Profile for agent '" + getName() + "' successfully applied." );
        super.applyProfile( profile );
//        } catch ( DAOFactoryException e ) {
//            throw new DAOException( e.getMessage() );
//        }
    }

    public boolean approves( Object event ) throws OpxiException {
        if ( ruleSet.size() == 0 ) {
            logger.finest( "Agent rules are empty. Checking pool profile rules..." );
            return checkPoolRules( event );
        }
        if ( super.approves( event ) ) {
            logger.finest( "Agent rules approved. Checking pool profile rules..." );
            return checkPoolRules( event );
        } else {
            logger.finest( "Agent rules not approved" );
            return false;
        }
    }

    private boolean checkPoolRules( Object event ) {
        boolean result = true;
        List<String> pools = getPoolMemberships();
        for ( String poolName : pools ) {
            try {
                result &= getServiceFactory().getPoolService().getOrLoadPool( poolName ).approves( event );
                if ( !result ) { // support for short-circuting
                    return false;
                }
            } catch ( OpxiException e ) {
                logger.log( Level.SEVERE, "Couldn't check against pool rules: ", e );
            }
        }
        return result;
    }

    public void service( CallService call ) throws OpxiException {
        try {
            try {
                Address referedBy = getRequest().getAddressHeader( SIPConstants.REFERRED_BY );
                if ( referedBy != null ) {
                    setActivityLogId( getServiceFactory().getAgentService().getAgentByAOR( getAOR() ).getActivityLogId() );
                    getServiceFactory().getLogService().getAgentActivityLogger( getActivityLogId() ).addTransferTarget( call.getId(), referedBy.getURI().toString() );
                }
            } catch ( ServletParseException e ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
            }
            super.service( call );
        } catch ( AgentNotAvailableException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            call.reject( SipServletResponse.SC_NOT_FOUND, e.getMessage() );
        }
    }

    public void answered( CallService call ) {
        setState( AgentState.TALKING );
    }

    public void ringing() {
        setState( AgentState.RINGING );
        callStateUpdated( CallService.RINGING );
    }

    /**
     * This method is currently called by PostGreeting machine in below cases:</br>
     * 1) If caller canceles a call</br>
     * 2) If agent doesn't answer an incomming call (408)</br>
     * 3) If agent generates 302 response</br>
     */
    public void notAnswered(/* CallService call */ ) {
        try {
            checkOutOfService();
            getServiceFactory().getLogService().getAgentActivityLogger( getActivityLogId() ).incNotAnsweredCalls();
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    public void rejected( CallService call ) {
        checkOutOfService();
        try {
            String id = getServiceFactory().getAgentService().getAgentByAOR( call.getHandlerAgent() ).getActivityLogId();
            getServiceFactory().getLogService().getAgentActivityLogger( id ).incRejectedCalls();
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    private void checkOutOfService() {
        try {
            if ( ++notAnsweredCalls == NOT_ANSWERED_CALLS_LIMIT ) {
                setState( AgentState.OUT_OF_SERVICE );
                UserAgent ua = new UserAgent( getAOR() );
                getServiceFactory().getAgentService().unassignPool( ua );
                Presence myP = getServiceFactory().getPresenceService().getPresence( ua );
                getServiceFactory().getSipService().sendIM( myP,
                        ResourceBundleUtil.getMessage( "callmanager.agent.outOfService" )
                );
                getServiceFactory().getSipService().sendAdminIM(
                        ResourceBundleUtil.getMessage( "callmanager.admin.agent.outOfService", getName() )
                );
            }
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    public void readyStateReceived() {
        if ( !isResting() ) {
            idle();
        } else {
            logger.warning( "Ready state not serviceable in state '" + state + "'" );
        }
    }

    protected boolean hasUpdatableState() {
        return openCalls() == 0;
    }

    public boolean equals( Object to ) {
        if ( to == null ) {
            return false;
        }
        if ( this == to ) {
            return true;
        }
        if ( (to instanceof String) ) {
            return this.getAOR().equals( to );
        }
        Agent agent = (Agent) to;
        return this.getAOR().equals( agent.getAOR() );
    }

    public int hashCode() {
        return (getAOR() != null ? getAOR().hashCode() : 0);
    }

    public String toString() {
//        boolean huntable = !hasMaximumCallsReached() && isActive();
        return "Agent[" + getAOR() +
                ", maxOpenCalls: " + maxOpenCalls +
                ", greetingMsgURl: " + greetingMsgURI +
                ", openCalls: " + openCalls +
                ", hashCode: " + super.hashCode() +
                /* TODO read this information
                ", active: " + active +
                ", contact: " + registration +
                */
                "]";
    }

    public void dispose() {
        skillProfile.clear();
        poolMemberships.clear();
    }

    private HuntingNotifier huntNotifier = new HuntingNotifier();


    private class HuntingNotifier extends Observable {

        public void notifyHunt( Agent agent ) {
            setChanged();
            notifyObservers( agent );
        }

    }

    private SelectionNotifier selectNotifier = new SelectionNotifier();

    private class SelectionNotifier extends Observable {

        public void notifySelection( SelectionEvent selection ) {
            setChanged();
            notifyObservers( selection );
        }

    }

    public enum AgentState {
        IDLE,
        RESTING,
        TALKING,
        RINGING,
        OUT_OF_SERVICE
    }


}

