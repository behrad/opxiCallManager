package com.basamadco.opxi.callmanager.rule;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.call.CallTarget;
import com.basamadco.opxi.callmanager.entity.Presence;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.pool.WorkgroupAgentPool;
import com.basamadco.opxi.callmanager.sip.im.ConfigRobot;
import com.basamadco.opxi.callmanager.sip.presence.PresenceEvent;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import com.basamadco.opxi.callmanager.util.ResourceBundleUtil;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Sep 15, 2008
 *         Time: 3:31:29 PM
 */
public class WorkgroupPresencePlan extends AbstractRule {

    private static final Logger logger = Logger.getLogger( WorkgroupPresencePlan.class.getName() );

    private Calendar from;

    private Calendar to;

    private long usageTimeLimit;

    private int usageCountLimit;

    private long duration;

    private float usageAgentRatio;


    private volatile int totalAgentsInUse;

    private PresencePlanHistory usageHistory;

    private SimpleDateFormat sdf = new SimpleDateFormat( "H:m" );

    private Map<UserAgent, Boolean> adminAuthentications = new HashMap<UserAgent, Boolean>();


    private static final Object MUTEX = new Object();


    public long getUsageTimeLimit() {
        return usageTimeLimit;
    }

    public void setUsageTimeLimit( long usageTimeLimit ) {
        this.usageTimeLimit = usageTimeLimit;
    }

    public int getUsageCountLimit() {
        return usageCountLimit;
    }

    public void setUsageCountLimit( int usageCountLimit ) {
        this.usageCountLimit = usageCountLimit;
    }

    public float getUsageAgentRatio() {
        return usageAgentRatio;
    }

    public void setUsageAgentRatio( float usageAgentRatio ) {
        this.usageAgentRatio = usageAgentRatio;
    }

    public long getDuration() {
        return duration;
    }

    public Calendar getTo() {
        return to;
    }

    public Calendar getFrom() {
        return from;
    }

    public void setDuration( long duration ) {
        this.duration = duration;
    }

    public void setFrom( Calendar from ) {
        this.from = from;
    }

    public void setTo( Calendar to ) {
        this.to = to;
    }


    public void setUsageTimeLimit( String limit ) {
        this.usageTimeLimit = Long.parseLong( limit ) * 60 * 1000;
    }

    public void setUsageCountLimit( String limit ) {
        this.usageCountLimit = Integer.parseInt( limit );
    }

    public void setUsageAgentRatio( String limit ) {
        this.usageAgentRatio = Float.parseFloat( limit );
    }

    public void setDuration( String duration ) {
        this.duration = Long.parseLong( duration ) * 60 * 1000;
    }

    public void setFrom( String from ) throws ParseException {
        this.from = Calendar.getInstance();
        this.from.setTime( sdf.parse( from ) );
    }

    public void setTo( String to ) throws ParseException {
        this.to = Calendar.getInstance();
        this.to.setTime( sdf.parse( to ) );
    }


    public boolean evaluate( Object event ) throws OpxiException {
        logger.finest( "Evaluate '" + event + "' against '" + getRuleInfo() + "'" );
        if ( getOnEvent().isInstance( event ) ) {
            logger.finest( "Event matched rule.onEvent= " + getOnEvent() );
            PresenceEvent pe = (PresenceEvent) event;
            if ( timeIsInRange() ) {
                if ( getAdminAuthentications().containsKey( pe.getPresence().getUserAgent() ) ) {
                    if ( getAdminAuthentications().get( pe.getPresence().getUserAgent() ) ) {
                        getAdminAuthentications().put( pe.getPresence().getUserAgent(), false );
                        logger.finest( "Event has been authorized by agent's administrator" );
                        getUsageHistory( pe ).setComment( "Event has been authorized by agent's administrator" );
                        return satisfied( pe );
                    }
                }
                if ( isAllowed() ) {
                    synchronized ( MUTEX ) {
                        return envolveRule( pe );
                    }
                } else {
                    pe.getPresenceService().getServiceFactory().getSipService().sendIM( pe.getPresence(),
                            ResourceBundleUtil.getMessage( "callmanager.service.notAllowed.msg.agent" ) + " " +
                                    ResourceBundleUtil.getMessage( "callmanager.presence.setOnline.agent" )
                    );
                    logNotSatisfied( pe,
                            ResourceBundleUtil.getMessage( "callmanager.service.notAllowedIsSet.msg.admin", getRuleId() )
                    );
                    logger.finest( "Rule is not allowing the event" );
                    return false;
                }
            } else {
                logger.finest( "Time is not in range..." );
            }
        }
        throw new RuleNotInvolvedException( getRuleInfo() );
    }

    private void logNotSatisfied( PresenceEvent pe, String msg ) throws OpxiException {
        String logId = pe.getPresenceService().getServiceFactory().getAgentService().getAgentForUA(
                pe.getPresence().getUserAgent()
        ).getActivityLogId();
        Presence thisState = new Presence( pe.getPresence() );
        thisState.setBasic( pe.getContext().getStateBasic( pe.getPublish() ) );
        thisState.setNote( pe.getContext().getStateNote( pe.getPublish(), thisState.getBasic() ) );
        pe.getPresenceService().getServiceFactory().getLogService().getAgentActivityLogger( logId ).addPresence( thisState );
        pe.getPresenceService().getServiceFactory().getLogService().getAgentActivityLogger( logId ).addPresenceComment( thisState, msg );


        String adminAuthorizeRule = ConfigRobot.COMMANDS[2] + getTargetEntity().getName() + "." + getRuleId() + "."
                + ConfigRobot.AUTHORIZE + "(" + pe.getPresence().getUserAgent().getName() + ")";

        String manager = BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO().getManagerNameFor(
                pe.getPresence().getUserAgent().getName() );
        UserAgent managerUA = new UserAgent( manager, OpxiToolBox.getLocalDomain() );

        pe.getPresenceService().getServiceFactory().getSipService().sendPresenceIM( managerUA,
                ResourceBundleUtil.getMessage( "callmanager.presence.notAllowed.msg.admin",
                        thisState.getNote(), pe.getPresence().getUserAgent().getName() ) );

        pe.getPresenceService().getServiceFactory().getSipService().sendPresenceIM( managerUA, msg );

        pe.getPresenceService().getServiceFactory().getSipService().sendPresenceIM( managerUA, adminAuthorizeRule );
    }

    private boolean satisfied( PresenceEvent pe ) {
        getUsageHistory( pe ).approveUsage();
        totalAgentsInUse++;
        logger.finest( "Rule is satisfied for " + pe.getPresence().getUserAgent() + ": " + totalAgentsInUse );
        return true;
    }

    private boolean envolveRule( PresenceEvent pe ) throws OpxiException {
        String msg;
        AgentPresenceUsage apu = getUsageHistory( pe );
        if ( apu.approves() ) {
            logger.finest( "AgentPresenceUsage approves this event" );
            if ( (totalAgentsInUse + 1) <= Math.ceil( (usageAgentRatio * ((WorkgroupAgentPool) getTargetEntity()).size()) ) ) {
                apu.setComment( "Approved" );
                return satisfied( pe );
            } else {
                logger.finest( "Exceeding total current number of agents using this service: " + totalAgentsInUse );
                apu.setComment( "Maximum Total Agents Already Using: " + totalAgentsInUse );
                msg = ResourceBundleUtil.getMessage( "callmanager.presence.notAllowed.maximum" );

                pe.getPresenceService().getServiceFactory().getSipService().sendIM( pe.getPresence(),
                        ResourceBundleUtil.getMessage( "callmanager.service.notAllowed.msg.agent" ) + "  " +
                                msg
                );
            }
        } else {
            logger.finest( "AgentPresenceUsage did not approve this event: " + apu.getComment() );
            pe.getPresenceService().getServiceFactory().getSipService().sendIM( pe.getPresence(),
                    ResourceBundleUtil.getMessage( "callmanager.service.notAllowed.msg.agent" ) + "  " +
                            apu.getComment()
            );
            msg = apu.getComment();
        }

        pe.getPresenceService().getServiceFactory().getSipService().sendIM( pe.getPresence(),
                ResourceBundleUtil.getMessage( "callmanager.presence.setOnline.agent" ) );

        logNotSatisfied( pe, msg );

        logger.finest( "Rule is not satisfied " );
        return false;

    }


    public boolean isActive() {
        return timeIsInRange();
    }

    public RuleUsage getHistory( UserAgent userAgent ) {
        if ( usageHistory != null ) {
            return usageHistory.getUsageHistory( userAgent );
        }
        return null;
    }

    public synchronized void authorizeNextUse( UserAgent ua ) {
        /*if( usageHistory == null ) {
            return false;
        }*/
        /*AgentPresenceUsage apu = usageHistory.getUsageHistory( ua );
        if( apu == null ) {
            return false;
        }*/
        getAdminAuthentications().put( ua, true );
    }

    public void endAgentUsage( UserAgent ua ) {
        totalAgentsInUse--;
        logger.finest( "Decreased agent usage counter for " + ua + ": " + totalAgentsInUse );
    }

    private synchronized AgentPresenceUsage getUsageHistory( PresenceEvent event ) {
        if ( usageHistory == null ) {
            usageHistory = new PresencePlanHistory( event.getPresenceService().getServiceFactory(), getRuleInfo(), this );
        }
        AgentPresenceUsage apu = usageHistory.getOrAddUsageHistory( event );
        return apu;
    }

    private boolean timeIsInRange() {
        Calendar now = Calendar.getInstance();
        from.set( now.get( Calendar.YEAR ), now.get( Calendar.MONTH ), now.get( Calendar.DATE ) );
        to.set( now.get( Calendar.YEAR ), now.get( Calendar.MONTH ), now.get( Calendar.DATE ) );
        if ( now.after( from ) && now.before( to ) ) {
            return true;
        }
        return false;
    }

    Map<UserAgent, Boolean> getAdminAuthentications() {
        return adminAuthentications;
    }

    public List getEventContexts() {
        List usages = new ArrayList();
        if ( usageHistory != null ) {
            usages.addAll( usageHistory.getAll() );
        } else {
            usages.add( "History Empty" );
        }
        return usages;
    }

    @Override
    public void setTargetEntity( CallTarget targetEntity ) {
        super.setTargetEntity( targetEntity );
        if ( getTargetEntity() instanceof WorkgroupAgentPool ) {
            // TODo use WorkgroupAgentPool.evaluate to decide if this plan is in shift
//            ((WorkgroupAgentPool)getTargetEntity()).getProfile()
        }
    }

    public String getRuleInfo() {
        return "Rule[ruleId='"
                + getRuleId()
//                + ", from='"
//                + from.getTime() + "', to='"
//                + to.getTime()
                + "', duration='"
                + OpxiToolBox.duration( duration ) + "', usageCountLimit='"
                + usageCountLimit + "', usageTimeLimit='"
                + OpxiToolBox.duration( usageTimeLimit ) + "', usageAgentRatio='"
                + usageAgentRatio + ", allowed='"
                + isAllowed() + ", totalAgentsInUse='"
                + totalAgentsInUse
                + "']";
    }
}
