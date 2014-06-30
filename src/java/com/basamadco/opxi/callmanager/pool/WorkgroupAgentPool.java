package com.basamadco.opxi.callmanager.pool;

import com.basamadco.opxi.callmanager.call.CallService;
import com.basamadco.opxi.callmanager.profile.ProfileException;
import com.basamadco.opxi.callmanager.entity.profile.types.RepeatType;
import com.basamadco.opxi.callmanager.entity.profile.Parameter;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.sip.presence.PresenceEvent;
import com.basamadco.opxi.callmanager.util.ResourceBundleUtil;
import com.basamadco.opxi.callmanager.rule.Rule;
import com.basamadco.opxi.callmanager.rule.WorkgroupPresencePlan;

import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Sep 3, 2006
 *         Time: 3:33:35 PM
 */
public abstract class WorkgroupAgentPool extends AgentPool {

    private static final Logger logger = Logger.getLogger( WorkgroupAgentPool.class.getName() );


    private Calendar from;


    private Calendar to;


    private RepeatType repeat;


    private ShiftTimer timer;


    protected WorkgroupAgentPool( PoolTarget poolTarget, PoolAgentContainer agentContainer )
            throws PoolInitializationException, ProfileException {
        super( poolTarget, agentContainer );
        if ( getProfile().getOpxiCMEntityProfileChoice().getPoolTargetProfile().getPoolTargetProfileChoice()
                .getGroupProfile().getFrom() != null ) {
            from = Calendar.getInstance();
            to = Calendar.getInstance();
            from.setTime( getProfile().getOpxiCMEntityProfileChoice().getPoolTargetProfile().getPoolTargetProfileChoice()
                    .getGroupProfile().getFrom()
            );
            to.setTime( getProfile().getOpxiCMEntityProfileChoice().getPoolTargetProfile().getPoolTargetProfileChoice()
                    .getGroupProfile().getTo()
            );
            repeat = getProfile().getOpxiCMEntityProfileChoice().getPoolTargetProfile().getPoolTargetProfileChoice()
                    .getGroupProfile().getRepeat();
            if ( repeat == RepeatType.DAILY ) {
                Calendar todayFinish = Calendar.getInstance();
                todayFinish.set( Calendar.HOUR_OF_DAY, to.get( Calendar.HOUR_OF_DAY ) );
                todayFinish.set( Calendar.MINUTE, to.get( Calendar.MINUTE ) );
                Date now = new Date();
                if ( todayFinish.after( now ) ) {
                    timer = new ShiftTimer( getServiceFactory(), "ShiftTimer-" + this.getName(), this, todayFinish );
                }
            }
        }


    }

    public AgentSelection select( CallService call ) {
        List currentAgents = agentView();
        SelectionEvent[] selections = new SelectionEvent[currentAgents.size()];
        for ( int i = 0; i < currentAgents.size(); i++ ) {
            Agent agent = (Agent) currentAgents.get( i );
            selections[i] = new SelectionEvent( agent, 0 );
        }
        return new AgentSelection( getName(), call, selections );
    }

    public AgentSelection select( AgentSelection selection ) {
        // Workgroup has no effect as secondary matches!
        return selection;
    }

    public Agent hunt( AgentSelection selection ) throws NoIdleAgentException {
        for ( int i = 0; i < selection.selections().length; i++ ) {
            try {
                Agent thisOne = selection.selections()[i].getAgent();
                thisOne.assignCall( selection.getCall() );
                return thisOne;
            } catch ( AgentNotIdleException e ) {
                //try next agent...
            }
        }
        throw new NoIdleAgentException( getName() + ": " + selection.selections() );
    }

    @Override
    public boolean approves( Object event ) throws OpxiException {
        logger.finest( "Check shift " + this + " on " + event );
        if ( shiftMatched() ) {
            logger.info( "'" + this + "' is matched as a shift for event: " + event );
            return super.approves( event );
        } else {
            logger.info( "'" + this + "' is not matched as a shift for event: " + event );
            getServiceFactory().getSipService().sendIM( ((PresenceEvent) event).getPresence(),
                    ResourceBundleUtil.getMessage( "callmanager.shifts.agent.notApproved" )
            );
            return false;
        }
    }

    private boolean shiftMatched() {
        if ( from == null ) { // This is not a shift group
            return true;
        }
        Calendar now = Calendar.getInstance();
        // check for date interval validity
        if ( now.after( from ) && now.before( to ) ) {
            if ( repeat == RepeatType.DAILY ) {
                return checkAsDaily( now );
            }
            if ( repeat == RepeatType.WEEKLY ) {
                return checkAsWeekly( now );
            }
            throw new IllegalStateException( "Invalid repeat type: " + repeat );
        } else {
            logger.finest( "from time: " + from.getTime() );
            logger.finest( "to time: " + to.getTime() );
            logger.finest( "now: " + now.getTime() );
            logger.finest( "We are not in date range of this shift" );
            return false;
        }
    }


    private boolean checkAsDaily( Calendar now ) {
        return isInRangeInDay( now );
    }

    private boolean checkAsWeekly( Calendar now ) {
        if ( getProfile().getOpxiCMEntityProfileChoice().getPoolTargetProfile().
                getPoolTargetProfileChoice().getGroupProfile().getParameterCount() == 0 ) {
            if ( from.get( Calendar.DAY_OF_WEEK ) == now.get( Calendar.DAY_OF_WEEK ) ) {
                return isInRangeInDay( now );
            } else {
                return false;
            }
        }
        for ( Parameter parameter : getProfile().getOpxiCMEntityProfileChoice().getPoolTargetProfile().
                getPoolTargetProfileChoice().getGroupProfile().getParameter() ) {

            logger.finest( "++++++++++++++++++++ " + parameter.getName() );
            Calendar calendar = Calendar.getInstance();
            calendar.set( Calendar.DAY_OF_WEEK, Integer.parseInt( parameter.getValue() ) );
            logger.finest( "++++++++++++++++++++ " + calendar );
            if ( now.get( Calendar.DAY_OF_WEEK ) == calendar.get( Calendar.DAY_OF_WEEK ) ) {
                // today is the day!
                return isInRangeInDay( now );
            }
        }
        return false;
    }

    private boolean isInRangeInDay( Calendar now ) {
        Calendar from = (Calendar) this.from.clone();
        Calendar to = (Calendar) this.to.clone();
        from.set( now.get( Calendar.YEAR ), now.get( Calendar.MONTH ), now.get( Calendar.DATE ) );
        to.set( now.get( Calendar.YEAR ), now.get( Calendar.MONTH ), now.get( Calendar.DATE ) );
        // Check for hours in day
        if ( now.after( from ) && now.before( to ) ) {
            return true;
        } else {
            logger.finest( "from time: " + from.getTime() );
            logger.finest( "to time: " + to.getTime() );
            logger.finest( "now: " + now.getTime() );
            logger.finest( "Day time is not between shift time range" );
            return false;
        }
    }

    public void authorizeNextUse( String ruleId, UserAgent ua ) throws OpxiException {
        for ( Rule rule : ruleSet ) {
            if ( rule.getRuleId().equalsIgnoreCase( ruleId ) ) {
                ((WorkgroupPresencePlan) rule).authorizeNextUse( ua );
                getServiceFactory().getSipService().sendPresenceIM( ua,
                        ResourceBundleUtil.getMessage( "callmanager.presence.usageCredit.agent" )
                );
                return;
            }
        }
        throw new OpxiException( "No Rule available with Id '" + ruleId + "'" );
    }

    public Calendar getFrom() {
        return from;
    }

    public Calendar getTo() {
        return to;
    }

    public RepeatType getRepeat() {
        return repeat;
    }

    @Override
    public void dispose() {
        if ( timer != null )
            timer.cancel();
        super.dispose();
    }
}
