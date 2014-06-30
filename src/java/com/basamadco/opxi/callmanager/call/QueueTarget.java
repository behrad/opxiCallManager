package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.DAOFactoryException;
import com.basamadco.opxi.callmanager.entity.profile.OpxiCMEntityProfile;
import com.basamadco.opxi.callmanager.entity.profile.QueueProfile;
import com.basamadco.opxi.callmanager.logging.ActivityLogNotExistsException;
import com.basamadco.opxi.callmanager.pool.HuntingException;
import com.basamadco.opxi.callmanager.pool.NoIdleAgentException;
import com.basamadco.opxi.callmanager.pool.NoPoolAvailableException;
import com.basamadco.opxi.callmanager.profile.ProfileException;
import com.basamadco.opxi.callmanager.queue.Queue;
import com.basamadco.opxi.callmanager.queue.QueueNotExistsException;
import com.basamadco.opxi.callmanager.sip.queue.QueueServlet;

import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.URI;
import javax.servlet.sip.ServletParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;

/**
 * Any callable entity which want to handle calls with Call Manager Queuing
 * mechanisms should provide this type.
 *
 * @author Jrad
 *         Date: Nov 15, 2007
 *         Time: 1:45:04 PM
 */
public class QueueTarget extends CallTarget {

    private static final Logger logger = Logger.getLogger( QueueTarget.class.getName() );


    protected String id;

    protected String waitingMsgURI;

    protected Integer queueDepth;

    protected Integer maxWaitTime;

    protected Integer idleTimeToSchedule;

    protected Integer maxCallDuration;


    protected Map<Integer, String> supportGroups = new HashMap<Integer, String>();


    public QueueTarget() {
    }

    public QueueTarget( QueueTarget target ) {
        super( target );
        setId( target.getId() );
        setQueueDepth( target.getQueueDepth() );
        setWaitingMsgURI( target.getWaitingMsgURI() );
        setMaxWaitTime( target.getMaxWaitTime() );
        setMaxCallDuration( target.getMaxCallDuration() );
        setIdleTimeToSchedule( target.getIdleTimeToSchedule() );
    }

    public void service( CallService call ) throws OpxiException {
        call.setHandlerQueueName( getName() );
        call.setState( CallService.QUEUE );

        Queue queue = null;
        try {
            try {
                getServiceFactory().getLogService().getServiceActivityLogger()
                        .getOrAddQueueActivityLogger( call.getHandlerQueueName() ).incCallAttempts();
            } catch ( ActivityLogNotExistsException e ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
            }

            queue = getServiceFactory().getQueueManagementService().queueForName( getName() );
            queue.assignAgent( call );
            if ( !queue.doesApplicationInvolve( call ) ) { // check for ASSIGNED state applications
                call.playGreeting();
            }
        } catch ( HuntingException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            call.setState( CallService.REJECTED );
            call.reject( SipServletResponse.SC_SERVER_INTERNAL_ERROR, e.getMessage() );
        } catch ( NoIdleAgentException e ) {
            if ( queue.isCallOverflowed() ) { // queueDepth==0
                logger.finer( queue.getName() + " is rejecting overflowed call for NoIdleAgent." );
                call.setState( CallService.REJECTED );
                call.reject( SipServletResponse.SC_BUSY_HERE, queue.getName() + " is rejecting overflowed call for NoIdleAgent." );
            } else {
                logger.finer( queue.getName() + " is pending call for NoIdeAgent." );
                queue.pend( call );
                if ( queue.doesApplicationInvolve( call ) ) { // check for QUEUED state applications!
                    call.setState( CallService.WAITING );
                } else {
                    call.playWaitingMsg();
                }
            }
        } catch ( NoPoolAvailableException e ) {
            logger.warning( e.getMessage() );
            handleNoPoolAvailable( call );
        } catch ( QueueNotExistsException e ) {
            try {
                getServiceFactory().getLogService().getServiceActivityLogger()
                        .addUnsuccessfulHandling( "Queue Not Found", "Please check opxi.log for callee information." );
            } catch ( ActivityLogNotExistsException e1 ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
            }
            logger.log( Level.SEVERE, e.getMessage(), e );
            call.setState( CallService.REJECTED );
            call.reject( SipServletResponse.SC_NOT_FOUND, call.getHandlerQueueName() + " Not Found" );
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            call.setState( CallService.REJECTED );
            call.reject( SipServletResponse.SC_SERVER_INTERNAL_ERROR, e.getMessage() );
        }
    }

    private void handleNoPoolAvailable( CallService call ) {
        try {
            CallTarget target = BaseDAOFactory.getDirectoryDAOFactory().
                    getCallTargetDAO().getCallTargetByPhoneNumber( QueueServlet.NO_AGENT_APPLICATION );
            target.setServiceFactory( getServiceFactory() );
            target.setRequest( call.getInitialRequest() );
            target.service( call );
        } catch ( OpxiException e ) {
            call.reject( SipServletResponse.SC_BUSY_HERE, e.getMessage() );
            logger.log( Level.SEVERE, e.getMessage(), e );
        }

        try {
            getServiceFactory().getLogService().getServiceActivityLogger().getOrAddQueueActivityLogger( getName() )
                    .addUnsuccessfulService( "No Agent Available", "No agent available to handle calls." );
        } catch ( ActivityLogNotExistsException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public void setCN( String CN ) {
        super.setCN( CN );
        setId( CN );
    }

    public Integer getQueueDepth() {
        return queueDepth;
    }

    public void setQueueDepth( Integer queueDepth ) {
        this.queueDepth = queueDepth;
    }

    public void setQueueDepth( String queueDepth ) {
        this.queueDepth = new Integer( queueDepth );
    }

    public String getWaitingMsgURI() {
        return waitingMsgURI;
    }

    public void setWaitingMsgURI( String waitingMsgURI ) {
        this.waitingMsgURI = waitingMsgURI;
    }

    public Integer getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime( Integer maxWaitTime ) {
        this.maxWaitTime = maxWaitTime;
    }

    public void setMaxWaitTime( String waitTime ) {
        this.maxWaitTime = new Integer( waitTime );
    }

    public Integer getIdleTimeToSchedule() {
        return idleTimeToSchedule;
    }

    public void setIdleTimeToSchedule( Integer idleTimeToSchedule ) {
        this.idleTimeToSchedule = idleTimeToSchedule;
    }

    public void setIdleTimeToSchedule( String idleTimeToSchedule ) {
        this.idleTimeToSchedule = new Integer( idleTimeToSchedule );
    }

    public Integer getMaxCallDuration() {
        return maxCallDuration;
    }

    public void setMaxCallDuration( Integer maxCallDuration ) {
        this.maxCallDuration = maxCallDuration;
    }

    public Map<Integer, String> getSupportGroups() {
        return supportGroups;
    }

    public boolean isQueueable() {
        return true;
    }


    protected void applyProfile( OpxiCMEntityProfile profile ) throws ProfileException {
        QueueProfile[] qps = profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getQueueProfile();
        if ( qps.length > 0 ) {
            setMaxWaitTime( qps[0].getMaxWaitingTime() );
            setQueueDepth( qps[0].getMaxDepth() );
            setWaitingMsgURI( qps[0].getWaitingAudio() );
            setIdleTimeToSchedule( qps[0].getIdleTimeToSchedule() );
            for ( int i = 0; i < qps[0].getSupportGroupCount(); i++ ) {

                try {
                    supportGroups.put( qps[0].getSupportGroup( i ).getDelay(),
                            BaseDAOFactory.getDirectoryDAOFactory().getPoolTargetDAO().getCNForDN(
                                    qps[0].getSupportGroup( i ).getName() )
                    );
                } catch ( OpxiException e ) {
                    throw new ProfileException( e );
                }
            }
        }
        super.applyProfile( profile );
    }

    /**
     * Simply returns false. Any queue implementation (like Queue) overrides this
     * by the available runtime information from it's properties.
     *
     * @return always false
     * @see com.basamadco.opxi.callmanager.queue.Queue
     */
    protected boolean hasUpdatableState() {
        return false;
    }


    public String toString() {
        return "QueueTarget[id=" + id +
                ", name=" + getName() +
                ", tel=" + getTelephoneNumber() +
                ", waitMsgURI=" + waitingMsgURI +
                ", queueDepth=" + queueDepth +
                ", maxWaitTime=" + maxWaitTime +
                ", idleTimeToSchedule=" + idleTimeToSchedule +
                "]";
    }
}
