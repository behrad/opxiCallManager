package com.basamadco.opxi.callmanager.queue;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;
import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.call.*;
import com.basamadco.opxi.callmanager.logging.ActivityLogNotExistsException;
import com.basamadco.opxi.callmanager.pool.*;
import com.basamadco.opxi.callmanager.sip.CallServlet;
import com.basamadco.opxi.callmanager.util.LockManager;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;

import javax.servlet.sip.ServletTimer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a container to handle incomming calls which target queue-able
 * entities. Queue is responsible to handle calls by routing them to Agents
 * (if any matches to handle) who are idle otherwise will queue calls till
 * they can be scheduled at a later time. If this waiting time for any call
 * exceeds, Queue will try to tear it down.
 * Queue has two internal structures to temporarly store incomming calls which
 * are inhandle calls list and pending calls list.
 *
 * @author Jrad
 * @see com.basamadco.opxi.callmanager.sip.queue.QueueServlet
 */
public class Queue extends QueueTarget {

    private static final Logger logger = Logger.getLogger( Queue.class.getName() );

//    private QueueTarget target;

//    private ServiceFactory serviceFactory;

//    private final Map inhandleCalls = Collections.synchronizedMap( new HashMap() );

    private final Map inhandleCalls = new ConcurrentHashMap();

    private final CallQueue pendingCalls = new CallQueue();

    private final Object CALL_SCHEDULE_LOCK = new Object();

    private final Object PENDING_CALLS_LOCK = new Object();

    private final Object HANDLED_CALLS_LOCK = new Object();

    private Timer supportGroupsTimer;

//    private QueueActivityLogger queueLogger;

    private String activityLogId;

//    private Buffer pendingCalls;

//    private final LinkedQueue pendingCalls = new LinkedQueue();

    Queue( QueueTarget target ) {
        super( target );
    }

    public String getActivityLogId() {
        return activityLogId;
    }

    public void setActivityLogId( String activityLogId ) {
        this.activityLogId = activityLogId;
    }

    public int getAgentsOnline() {
        try {
            return getServiceFactory().getPoolService().getPool( getName() ).agentView().size();
        } catch ( NoPoolAvailableException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            return 0;
        }
    }

    public void startMonitor() {
        if ( supportGroups.keySet().size() > 0 ) {
            logger.finest( "Start queue group monitor..." );
            supportGroupsTimer = new Timer( getName() + "-SupportGroupsMonitor", true );
            supportGroupsTimer.schedule( new SupportGroupMonitor( this ), 0, 1000 );
        }
    }

    public long getMaxCallWaitTime() {
        return (long) getMaxWaitTime().intValue() * 1000;
    }

    /**
     * Checks if this Queue has any call pending to be scheduled
     *
     * @return true if there's any call in the pending queue
     */
    public boolean hasPendingCalls() {
        synchronized ( PENDING_CALLS_LOCK ) {
            return pendingCalls.size() != 0;
        }
    }

    /**
     * Checks if pending calls queue if full
     *
     * @return true if pending calls queue has reached queueDepth property
     */
    public boolean isCallOverflowed() {
        synchronized ( PENDING_CALLS_LOCK ) {
            //        return pendingCalls.size() == target.getQueueDepth().intValue();
//            logger.finer( "Pending Calls Size: " + pendingCalls.size() );
//            logger.finer( "Queue capacity: " + target.getQueueDepth().intValue() );
            return pendingCalls.size() >= getQueueDepth().intValue();
        }
    }

    /**
     * Queues are in updatable state when there's no pending call available
     *
     * @return !hasPendingCalls()
     */
    protected boolean hasUpdatableState() {
        return !hasPendingCalls();
    }

    /**
     * Trys to hunt an Agent to handle the call.
     *
     * @param call CallService may have some requestics to the hunting policy
     * @throws NoIdleAgentException     if there has been no idle Agent to handle the call
     * @throws NoPoolAvailableException if there's no pool available to assign the call to
     */
    public void assignAgent( CallService call ) throws OpxiException {
        Agent agent = getServiceFactory().getPoolService().huntAgent( this, call );
        logger.finer( "agent hunted from pool: " + agent );
        assign( call );
    }

    public void handleSupportGroup( String groupName ) {
        getServiceFactory().getPoolService().addSupportGroup( getName(), groupName );
        try {
            int agents = getServiceFactory().getPoolService().getPool( groupName ).size();
            for ( int i = 0; i < agents; i++ ) {
                CallService longestCall, dCall;
                synchronized ( CALL_SCHEDULE_LOCK ) {
                    longestCall = longestWaitingCall();
                    assignAgent( longestCall );
                    dCall = dequeue();
                }
                if ( !longestCall.equals( dCall ) ) {
                    logger.warning( "LongestWaitingCall: " + longestCall );
                    logger.warning( "DequeuedCall: " + dCall );
                    logger.log( Level.SEVERE, "Assertion failed, Dequeue!=longestWaitingCall"
                            , new IllegalStateException()
                    );
                }
                transferFromWaiting( longestCall );
            }
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }


    /**
     * Queues the specified call to be later scheduled (i.e. adds it to waiting calls)
     *
     * @param call CallService to be waited
     */
    public void pend( CallService call ) {
        synchronized ( PENDING_CALLS_LOCK ) {
//            if( front.getWaitTimer() == null ) {
//                throw new IllegalStateException( "Wait timer should be set before adding to pending calls." );
//            }
            //        pendingCalls.add( front );
//            front.createWaitTimer( getTimerService(), getMaxCallWaitTime() );
            call.setWaitingRoom( getWaitingMsgURI() );
            call.setState( CallService.QUEUED );
            call.createWaitTimer( getServiceFactory().getSipService().getTimerService(), getMaxCallWaitTime() );
            pendingCalls.queue( call );
            logger.finer( "PendAdd " + call );
            try {
                getServiceFactory().getLogService().getServiceActivityLogger().
                        getOrAddQueueActivityLogger( getName() ).updateCallsWaiting( pendingCalls.size() );
            } catch ( ActivityLogNotExistsException e ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
            }
        }
    }

    /**
     * Selects, among the waiting calls, the latest call waiting to be handled and
     * assigns it to the Agent specified by agentAOR
     *
     * @param agentAOR Agent's address-of-record
     * @return The selected call from waiting queue
     * @throws AgentNotIdleException if specified Agent is not ready to accept calls
     * @throws NoPendingCallInQueue  if no call is available in waiting queue
     * @throws QueueException        if sth. goes wrong
     */
    private CallService _scheduleCallFor( String agentAOR ) throws AgentNotIdleException, NoPendingCallInQueue, QueueException {
        try {
            Agent agent = getServiceFactory().getAgentService().getAgentByAOR( agentAOR );
            if ( getServiceFactory().getPresenceService().isActive( agent.getAOR() ) ) {
                while ( hasPendingCalls() ) {
                    CallService call;
                    synchronized ( CALL_SCHEDULE_LOCK ) {
                        call = dequeue();
                    }
                    synchronized ( LockManager.getRejectScheduleLock( call ) ) {
                        synchronized ( LockManager.getReleaseScheduleLock( call ) ) {
                            if ( !(call.getState() == CallService.DISPOSING || call.getState() == CallService.TEAR_DOWN) ) {
                                agent.assignCall( call );
                                assign( call );
                                return call;
                            }
                        }
                    }
                }
                throw new NoPendingCallInQueue( getName() );
            } else {
                throw new AgentNotIdleException( agentAOR + " is not active." );
            }
        } catch ( AgentNotAvailableException e ) {
            throw new QueueException( e );
        }
    }

    /*private CallService scheduleCall() throws NoPendingCallInQueue, QueueException {
        synchronized ( CALL_SCHEDULE_LOCK ) {
            while ( hasPendingCalls() ) {
                CallService call = dequeue();
                synchronized ( LockManager.getRejectScheduleLock( call ) ) {
                    synchronized ( LockManager.getReleaseScheduleLock( call ) ) {
                        if ( !(call.getState() == CallService.DISPOSING || call.getState() == CallService.TEAR_DOWN) ) {
                            return call;
                        }
                    }
                }
            }
            throw new NoPendingCallInQueue( getName() );
        }
    }*/

    public void scheduleCallFor( String agentAOR ) {

        try {
            CallService waitingCall = _scheduleCallFor( agentAOR );
            transferFromWaiting( waitingCall );
        } catch ( NoPendingCallInQueue e ) {
            logger.warning( "Unable to schedule call... " + e.getMessage() );
        } catch ( AgentNotIdleException e ) {
            logger.warning( "Unable to schedule call... " + e.getMessage() );
        } catch ( QueueException e ) {
            logger.severe( "Unable to schedule call..." );
            logger.log( Level.SEVERE, e.getMessage(), e );
        } catch ( CallTransferException e ) {
            // schedule has won, but caller is disposed!
            // TODO better to define a more specific Exception type
            logger.warning( "Unable to schedule call..." );
            logger.log( Level.WARNING, e.getMessage(), e );
        }
    }

    private void transferFromWaiting( CallService waitingCall ) throws CallTransferException {
        if ( waitingCall.getState() == CallService.ASSIGNED ) { // front is not in WAITING (stable) status yet
            waitingCall.transfer( Leg.CALLER,
                    getServiceFactory().getSipService().createSipURIForName( CallServlet.GREETING_URI_USER ) );
        }
    }


    /**
     * Removes the call from waiting queue and cancels it waiting timer
     *
     * @param call CallService to be canceled for later schedules
     */
    public void cancel( CallService call ) {
        logger.finer( "Canceling call from pending list: '" + call.getId() + "'" );
        boolean dequeued = false;
        synchronized ( PENDING_CALLS_LOCK ) {
            dequeued = pendingCalls.dequeue( call );
        }
        if ( dequeued ) {
            if ( call.getWaitTimer() != null ) {
                //TODO should be canceled just when in not DISPOSING status
                call.getWaitTimer().cancel();
            }
            try {
                getServiceFactory().getLogService().getServiceActivityLogger()
                        .getOrAddQueueActivityLogger( getName() ).updateCallsWaiting( pendingCalls.size() );
            } catch ( ActivityLogNotExistsException e ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
            }
            logCallWaitTime( call.waitTime() );
            checkApplyProfileUpdate();
        }
    }

    /**
     * Returns the most waiting (oldest) call in waiting queue
     *
     * @return The oldest call in pending calls
     */
    private CallService dequeue() {
        CallService call = null;
        try {
            synchronized ( PENDING_CALLS_LOCK ) {
                call = pendingCalls.dequeue();
            }
            logger.finer( "PendRemove " + call );
            if ( call != null ) {
                ServletTimer timer = call.getWaitTimer();
                if ( timer != null ) {
                    timer.cancel();
                }
                try {
                    getServiceFactory().getLogService().getServiceActivityLogger()
                            .getOrAddQueueActivityLogger( getName() ).updateCallsWaiting( pendingCalls.size() );
                } catch ( ActivityLogNotExistsException e ) {
                    logger.log( Level.SEVERE, e.getMessage(), e );
                }
                logCallWaitTime( call.waitTime() );
                checkApplyProfileUpdate();
            }
            return call;
        } catch ( CallAlreadyDisposedError e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            return dequeue();
        }
    }

    /**
     * Mark the call to be handled by Agent specified with agentAOR
     *
     * @param call The call wishing to be handled
     */
    private void assign( CallService call ) {
        synchronized ( HANDLED_CALLS_LOCK ) {
            if ( addToHandledCalls( call ) ) {
                if ( call.getState() != CallService.QUEUED ) { // it should be waiting
                    call.setState( CallService.ASSIGNED );
                } // if Queued then caller is not stable yet, so stay Queued!
            }
        }
    }

    private boolean addToHandledCalls( CallService call ) {
//         synchronized( HANDLED_CALLS_LOCK ) {
        if ( !inhandleCalls.containsKey( call.getId() ) ) {
            inhandleCalls.put( call.getId(), call );
//                queueLogger.incCallsHandled( getActivityLogId() );
            logger.finer( "HandleAdd " + call );
            return true;
        }
        return false;
//        }
    }

    /**
     * Returns the time oldest call's been in wait
     *
     * @return longest waiting time in milliseconds
     */
    public long longestWaitTime() {
        synchronized ( PENDING_CALLS_LOCK ) {
            if ( pendingCalls.size() == 0 ) {
                return 0;
            }
//            logger.finer( "we have pending calls: " + pendingCalls.peek() + " with wt: "
//                    + OpxiToolBox.duration( pendingCalls.peek().waitTime() )
//            );
            return (pendingCalls.peek()).waitTime();
        }
    }

    private CallService longestWaitingCall() throws NoPendingCallInQueue {
        synchronized ( PENDING_CALLS_LOCK ) {
            if ( pendingCalls.size() == 0 ) {
                throw new NoPendingCallInQueue( getName() );
            }
            return pendingCalls.peek();
        }
    }

    /**
     * Notifies Queue object when a call is in waiting queue for more than
     * the Queues maximum wait time. This method will then tear the call down.
     *
     * @param call The call to be teared down since no scheduling happend in
     *             a constant limited period of time (maximum wait time)
     */
    public void maxCallWaitTimeReached( CallService call ) {
        synchronized ( LockManager.getRejectScheduleLock( call ) ) {
            if ( call.getState() == CallService.WAITING ) { // if not scheduled
                try {
                    call.teardown( "Wait-time exceeded" );
                } catch ( Throwable e ) {
                    logger.log( Level.SEVERE, e.getMessage(), e );
                }
            }
        }
    }

    private void logCallWaitTime( long time ) {
        try {
            getServiceFactory().getLogService().getServiceActivityLogger()
                    .getOrAddQueueActivityLogger( getName() ).addCallWaitTime( (int) time );
        } catch ( ActivityLogNotExistsException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    /**
     * When a call which is handled with some Queue terminates, it
     * should also trigger it's Queue object of its termination. This
     * method is responsible to release the call from its handler Agent
     * and update the queue internal call containers.
     *
     * @param call The call which is gonna terminate
     * @return Address-of-record of the Agent who was handling the call
     */
    public void release( CallService call ) {
        if ( call == null ) {
            throw new IllegalArgumentException( "CallService is null." );
        }
        try {
            if ( call.getHandlerAgent() != null ) {
                Agent freedAgent = getServiceFactory().getAgentService().getAgentByAOR( call.getHandlerAgent() );
                freedAgent.releaseCall( call );
            } else {
                logger.warning( "No HandlerAgent is set for the call " + this );
            }
        } catch ( AgentNotAvailableException e ) {
            logger.log( Level.WARNING, e.getMessage(), e );
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }

        /*String agentAor = call.getHandlerAgent();
        try {
            if (agentAor != null) {
                Agent freedAgent;
                // No need for state check! settingHandlerAgent == incOpenCalls
//                    if( front.getState() == CallService.IN_CALL || front.getState() == CallService.MAKE_CALL ||
//                            front.getState() == CallService.WAITING_MEDIA || front.getState() == CallService.IN_GREETING ) {
                freedAgent = getServiceFactory().getAgentService().getAgentByAOR(agentAor);
                freedAgent.releaseCall(call);

            }
        } catch (AgentNotAvailableException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        } catch (IllegalStateException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }*/
        if ( call.getHandlerAgent() != null ) {
            call.setDisconnectTime( System.currentTimeMillis() );
            try {
                getServiceFactory().getLogService().getServiceActivityLogger()
                        .getOrAddQueueActivityLogger( getName() ).addCallServiceTime( call.duration() );
            } catch ( ActivityLogNotExistsException e ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
            }
        }

        logger.finer( "Terminate call from queue: " + call );
        Object o;
        synchronized ( HANDLED_CALLS_LOCK ) {
            o = inhandleCalls.remove( call.getId() );
        }
        if ( o != null ) {
            logger.finer( "Remove call from handle list: '" + call.getId() + "'" );
        } else {
            cancel( call );
        }
        /*if( agentAor != null ) {
            getServiceFactory().getQueueManagementService().serviceAgentIdleEvent( agentAor );
        }*/
    }

//    public void cancelSchedule( String callId, String agentAOR ) {
//        try {
//            serviceFactory.getPoolService().getAgentByAOR( agentAOR ).decOpenCalls();
//        } catch ( AgentNotAvailableException e ) {
//            logger.log( Level.SEVERE, e.getMessage(), e );
//            e.printStackTrace();
//        }
//        inhandleCalls.remove( callId );

    //    }

    /**
     * Returns list of all calls which are routed to this Queue
     *
     * @return a collection of CallService objects
     */
    public Collection totalCallList() {
        Collection all = new ArrayList( inhandleCalls.values() );
        Collections.addAll( all, pendingCalls.readOnlyCallList() );
        return Collections.unmodifiableCollection( all );
    }


    public int getPendingCallIndex( String callId ) {
        return pendingCalls.callIndex( callId );
    }

    public int getPendingCallsSize() {
        return pendingCalls.size();
    }

    public int getInHandleCallsSize() {
        return inhandleCalls.size();
    }

    void dispose() {
        logger.finer( "Queue.dispose() " );
        if ( supportGroupsTimer != null ) {
            supportGroupsTimer.cancel();
        }
        Collection calls = inhandleCalls.values();
//        synchronized( inhandleCalls ) {
        Iterator i = calls.iterator();
        while ( i.hasNext() ) {
            ((CallService) i.next()).teardown( "QMS restart" );
//                release( (CallService)i.next() );
        }
//        }
        inhandleCalls.clear();
//        synchronized( pendingCalls ) {
        while ( pendingCalls.size() > 0 ) {
            pendingCalls.dequeue().teardown( "QMS restart" );
        }
//        }
        pendingCalls.clear();
        logger.info( "Queue '" + getName() + "' destroyed successfully." );
    }

}