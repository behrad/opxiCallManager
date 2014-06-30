package com.basamadco.opxi.callmanager.queue;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;
import com.basamadco.opxi.callmanager.DirectoryCallManagerService;
import com.basamadco.opxi.callmanager.profile.ProfileException;
import com.basamadco.opxi.callmanager.pool.AgentNotAvailableException;
import com.basamadco.opxi.callmanager.entity.EntityNotExistsException;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOFactoryException;
import com.basamadco.opxi.callmanager.call.CallService;
import com.basamadco.opxi.callmanager.call.QueueTarget;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.util.*;

public final class QueueManagementService extends DirectoryCallManagerService {

    private static final Logger logger = Logger.getLogger( QueueManagementService.class.getName() );


    private final Map queueMap = new ConcurrentHashMap();

    private final Map inTransferCallMap = new ConcurrentHashMap();


    private Queue getQueue( String queueId ) throws QueueNotExistsException {
        Queue queue = lookInLoadedQueues( queueId );
        if ( queue != null ) {
            return queue;
        }
        throw new QueueNotExistsException( queueId );
    }

    private Queue _queueForName( String queueId ) throws QueueManagerException, QueueNotExistsException {
        // see if queue is loaded before
        Queue queue = lookInLoadedQueues( queueId );
        if ( queue != null ) {
            return queue;
        }
        Queue new_queue = createQueue( queueId );
        queueMap.put( queueId, new_queue );
        return new_queue;
    }

    public Queue queueForName( String queueName ) throws QueueManagerException, QueueNotExistsException {
//        String queueId = null;
//        try {
//            queueId = getDAOFactory().getQueueTargetDAO().getCNForName( queueName );
        return _queueForName( queueName );
//        } catch ( DAOException e ) {
//            throw new QueueManagerException( queueName, e );
//        }
    }

    public void serviceAgentIdleEvent( String agentAOR ) {
        logger.finest( "********** Service Agent idle event: " + agentAOR );
//        synchronized (LockManager.getLockById(agentAOR)) {
        try {
            scheduleQueue( agentAOR ).scheduleCallFor( agentAOR );
        } catch ( NoPendingQueueToSchedule e ) {
            logger.warning( "No queue with waiting calls found... " + e.getMessage() );
        } catch ( Throwable e ) {
            logger.severe( "Unable to schedule call: " + e.getMessage() );
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
//        }
    }

    private Queue scheduleQueue( String agentAOR ) throws NoPendingQueueToSchedule, QueueManagerException {
        try {
            List poolIdList = getServiceFactory().getPoolService().getPools( agentAOR );
            long max = 0;
            String scheduledQueueName = null;
            for ( int i = 0; i < poolIdList.size(); i++ ) {
                String poolId = (String) poolIdList.get( i );
                logger.finer( "Checking QueueTarget " + poolId + " to schedule." );
                try {
                    long wait = getQueue( poolId ).longestWaitTime();
                    if ( wait > max ) {
                        scheduledQueueName = poolId;
                        max = wait;
                    }
                } catch ( QueueNotExistsException e ) {
                    logger.warning( e.getMessage() );
                    // ignore, look at the next queue to see if exists
                }
            }
            if ( max > 0 ) { // or scheduledQueueName != null
                return getQueue( scheduledQueueName );
            }
            throw new NoPendingQueueToSchedule( agentAOR );
        } catch ( AgentNotAvailableException e ) {
            throw new QueueManagerException( e.getMessage(), e );
        } catch ( QueueNotExistsException e ) {
            throw new QueueManagerException( e.getMessage(), e );
        }
    }

    private Queue lookInLoadedQueues( String queueId ) {
        if ( queueMap.containsKey( queueId ) )
            return (Queue) queueMap.get( queueId );
        //throw new NoQueueAvailableException( queueId );
        return null;
    }

    /**
     * Creates a new Queue object for specified queueId
     * This method currently works only for Groups & Skills
     *
     * @param queueId
     * @return A new instantiated Queue object whose properties are populated from directory
     * @throws QueueManagerException
     * @throws QueueNotExistsException
     */
    private Queue createQueue( String queueId ) throws QueueManagerException, QueueNotExistsException {
        try {
            QueueTarget queueTarget = (QueueTarget) getDAOFactory().getQueueTargetDAO().read( queueId );
            queueTarget.setServiceFactory( getServiceFactory() );
            Queue newQueue = new Queue( queueTarget );
            newQueue.assignProfile(
                    BaseDAOFactory.getConfigStorageDAOFactory().getPoolTargetProfileDAO( queueId ).readProfile()
            );
            newQueue.startMonitor();
            return newQueue;
        } catch ( EntityNotExistsException e ) {
            throw new QueueNotExistsException( e.getMessage() );
        } catch ( DAOException e ) {
            throw new QueueManagerException( e.getMessage() );
        } catch ( DAOFactoryException e ) {
            throw new QueueManagerException( e.getMessage() );
        } catch ( ProfileException e ) {
            throw new QueueManagerException( e.getMessage() );
        }
    }

    public void setExpectedCallForTransfer( com.basamadco.opxi.callmanager.call.CallService call ) {
        synchronized ( inTransferCallMap ) {
            inTransferCallMap.put( call.getId(), call );
        }
    }

    public CallService getExpectedCallForTransfer( String callId ) {
        synchronized ( inTransferCallMap ) {
            if ( inTransferCallMap.containsKey( callId ) ) {
                return (CallService) inTransferCallMap.remove( callId );
            } else {
                return null;
            }
        }
    }

    public Collection queueList() {
        Collection col = new ArrayList( queueMap.values() );
        return Collections.unmodifiableCollection( col );
    }

    public List listObjects() {
        List expTransferCalls = new ArrayList();
        expTransferCalls.add( "Expected Calls For Transfer" );
        expTransferCalls.addAll( Arrays.asList( inTransferCallMap.values().toArray() ) );
        return expTransferCalls;
    }

    public void destroy() {
//        synchronized( queueMap ) {
        Iterator queues = queueMap.values().iterator();
        while ( queues.hasNext() ) {
            ((Queue) queues.next()).dispose();
        }
//        }
        queueMap.clear();
//		queueMap = null;
        logger.info( "QueueManagementService destroyed successfully." );
    }

}