package com.basamadco.opxi.callmanager.rule;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.call.AbstractTimerContext;
import com.basamadco.opxi.callmanager.call.TimerException;
import com.basamadco.opxi.callmanager.entity.Presence;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.DAOFactoryException;
import com.basamadco.opxi.callmanager.sip.presence.NoChangeRefreshEvent;
import com.basamadco.opxi.callmanager.sip.presence.PresenceEvent;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import com.basamadco.opxi.callmanager.util.ResourceBundleUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Sep 18, 2008
 *         Time: 1:10:33 PM
 */
public class AgentPresenceUsage extends AbstractTimerContext implements RuleUsage {

    private static final Logger logger = Logger.getLogger( AgentPresenceUsage.class.getName() );


    private PresenceEvent event;

    private WorkgroupPresencePlan plan;

    private int usageCount;

    private long totalUsageTime;

    private String comment;


    private long startTime;


    public AgentPresenceUsage( PresenceEvent event, WorkgroupPresencePlan plan ) {
        super( event.getPresenceService().getServiceFactory(), event.getPresence().getId() );
        this.event = event;
        this.plan = plan;
    }

    public long getTotalUsageTime() {
        return totalUsageTime;
    }

    public void setTotalUsageTime( long totalUsageTime ) {
        this.totalUsageTime = totalUsageTime;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount( int usageCount ) {
        this.usageCount = usageCount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment( String comment ) {
        this.comment = comment;
    }

    public UserAgent getUserAgent() {
        return event.getPresence().getUserAgent();
    }

    public boolean approves() {
        boolean hasTimeLimit = getTotalUsageTime() < plan.getUsageTimeLimit();
        boolean hasUsageCount = getUsageCount() < plan.getUsageCountLimit();
        if ( !hasUsageCount ) {
            setComment( ResourceBundleUtil.getMessage( "callmanager.presence.notAllowed.usageCount" ) );
            return false;
        }
        if ( !hasTimeLimit ) {
            setComment( ResourceBundleUtil.getMessage( "callmanager.presence.notAllowed.usageTime" ) );
            return false;
        }
        return true;
    }

    public void approveUsage() {
        usageCount++;
        long now = System.currentTimeMillis();
        long duration = plan.getDuration();
        logger.finest( "Duration: " + OpxiToolBox.duration( duration ) );
        long remainingCredit = plan.getUsageTimeLimit() - getTotalUsageTime();
        if ( remainingCredit < 0 ) {
            remainingCredit = 0;
        }
        logger.finest( "Remaining Credit: " + OpxiToolBox.duration( remainingCredit ) );
        long remainingPlanTime = plan.getTo().getTimeInMillis() - now;
        logger.finest( "Remaining PlanTime: " + OpxiToolBox.duration( remainingPlanTime ) );
        duration = OpxiToolBox.min( plan.getDuration(), remainingCredit, remainingPlanTime );
        if ( duration > 0 ) {
            setTimer( getServiceFactory().getSipService().getTimerService().createTimer(
                    getServiceFactory().getSipService().getApplicationSession(),
                    duration, false, this
            )
            );
        }

        event.getContext().registerUsageCtx( this );
        startTime = now;
        logger.finest( "Start USAGE for " + event.getPresence().getUserAgent() );


        sendIM( false );

    }

    public void endUsage() {
        logger.finest( "End USAGE for " + OpxiToolBox.duration( System.currentTimeMillis() - startTime ) + ": "
                + event.getPresence().getUserAgent() );
        event.getContext().registerUsageCtx( null );
        totalUsageTime += System.currentTimeMillis() - startTime;
        startTime = 0;
        plan.endAgentUsage( event.getPresence().getUserAgent() );
        sendIM( true );
    }

    public long getCurrentInUseTime() {
        if ( startTime == 0 ) {
            return 0;
        } else {
            return System.currentTimeMillis() - startTime;
        }
    }

    public boolean subsequentEventReceived( PresenceEvent event ) {
        if ( plan.getOnEvent().isInstance( event ) || event instanceof NoChangeRefreshEvent ) {
            logger.finest( "Received Event is an instance of plan.onEvent!" );
            return true;
        } else {
            logger.finest( "Received Event is not an instance of plan.onEvent" );
            cancel();
            return false;
        }
    }

    public void sendIM( boolean sendToAdmin ) {
        try {
            Presence contact = getServiceFactory().getPresenceService().getPresence( event.getPresence().getUserAgent() );

            long remaining = plan.getUsageTimeLimit() - getTotalUsageTime();
            /*long remainingPlanTime = plan.getTo().getTimeInMillis() - System.currentTimeMillis();
            remaining = OpxiToolBox.min( plan.getDuration(), remaining, remainingPlanTime );*/

            if ( remaining < 0 ) {
                remaining = 0;
            }

            event.getPresenceService().getServiceFactory().getSipService().sendIM( contact,
                    "-----------------------------------------------------", 3 );

            event.getPresenceService().getServiceFactory().getSipService().sendIM( contact,
                    ResourceBundleUtil.getMessage( "callmanager.service.usageCountLog.agent",
                            "" + (plan.getUsageCountLimit() - getUsageCount()) ), 3
            );
            event.getPresenceService().getServiceFactory().getSipService().sendIM( contact,
                    ResourceBundleUtil.getMessage( "callmanager.service.usageTimeLog.agent",
                            OpxiToolBox.duration(
                                    remaining
                            ) ), 3
            );
            event.getPresenceService().getServiceFactory().getSipService().sendIM( contact,
                    "-----------------------------------------------------", 3 );

            if ( sendToAdmin ) {
                try {
                    String manager = BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO().getManagerNameFor(
                            event.getPresence().getUserAgent().getName() );
                    UserAgent managerUA = new UserAgent( manager, OpxiToolBox.getLocalDomain() );
                    event.getPresenceService().getServiceFactory().getSipService().sendPresenceIM( managerUA,
                            "-----------------------------------------------------" );
                    event.getPresenceService().getServiceFactory().getSipService().sendPresenceIM( managerUA,
                            ResourceBundleUtil.getMessage( "callmanager.service.usageCountLog.agent",
                                    "" + (plan.getUsageCountLimit() - getUsageCount()) )
                    );
                    event.getPresenceService().getServiceFactory().getSipService().sendPresenceIM( managerUA,
                            ResourceBundleUtil.getMessage( "callmanager.service.usageTimeLog.agent",
                                    OpxiToolBox.duration(
                                            remaining
                                    ) )
                    );
                    event.getPresenceService().getServiceFactory().getSipService().sendPresenceIM( managerUA,
                            "-----------------------------------------------------" );

                } catch ( OpxiException e ) {
                    logger.log( Level.SEVERE, e.getMessage(), e );
                }
            }


        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    public void timeout() throws TimerException {
        try {
//            Presence agentCurrentStatus = event.getPresenceService().getPresence(event.getPresence().getUserAgent());

            if ( event.getContext().isActive() ) {
                String manager = BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO().getManagerNameFor(
                        event.getPresence().getUserAgent().getName() );
                UserAgent managerUA = new UserAgent( manager, OpxiToolBox.getLocalDomain() );

                event.getPresenceService().getServiceFactory().getSipService().sendPresenceIM( managerUA,
                        ResourceBundleUtil.getMessage( "callmanager.presence.usageLimitReached.admin",
                                event.getPresence().getUserAgent().getName() ) );

                event.getPresenceService().getServiceFactory().getSipService().sendIM( event.getPresence(),
                        ResourceBundleUtil.getMessage( "callmanager.presence.usageLimitReached.agent" ) );
                event.getPresenceService().getServiceFactory().getSipService().sendIM( event.getPresence(),
                        ResourceBundleUtil.getMessage( "callmanager.presence.setOnline.agent" ) );


                String aalId = event.getPresenceService().getServiceFactory().getAgentService().getAgentForUA(
                        event.getPresence().getUserAgent() ).getActivityLogId();
                event.getPresenceService().getServiceFactory().getLogService().getAgentActivityLogger( aalId ).
                        addPresenceComment( event.getPresence(),
                                ResourceBundleUtil.getMessage( "callmanager.presence.usageLimitReached.agent" )
                        );
            }

//            endUsage();

        } catch ( OpxiException e ) {
            throw new TimerException( e );
        }
    }

    public void cancel() {
        logger.finest( "Canceling busy agent timer for " + event.getPresence().getUserAgent() );
        endUsage();
        super.cancel();
    }

    public String toString() {
        return getClass().getName() + "[UA='"
                + event.getPresence().getUserAgent().getAORString() + "', pId='"
                + event.getPresence().getId() + "', totalUsageCount='"
                + usageCount + "', totalUsageTime='"
                + OpxiToolBox.duration( totalUsageTime ) + "', comment='"
                + getComment() + "', currentInUseTime='"
                + OpxiToolBox.duration( getCurrentInUseTime() )
                + "']";
    }
}
