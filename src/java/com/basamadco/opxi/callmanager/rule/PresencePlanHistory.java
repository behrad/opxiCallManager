package com.basamadco.opxi.callmanager.rule;

import com.basamadco.opxi.callmanager.ServiceFactory;
import com.basamadco.opxi.callmanager.call.AbstractTimerContext;
import com.basamadco.opxi.callmanager.call.TimerException;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.sip.presence.PresenceEvent;

import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Sep 18, 2008
 *         Time: 12:44:12 PM
 */
public class PresencePlanHistory extends AbstractTimerContext {

    private static final Logger logger = Logger.getLogger( PresencePlanHistory.class.getName() );


    private WorkgroupPresencePlan presencePlan;

    private Map<UserAgent, AgentPresenceUsage> history = new ConcurrentHashMap<UserAgent, AgentPresenceUsage>();


    public PresencePlanHistory( ServiceFactory serviceFactory, String id, WorkgroupPresencePlan presencePlan ) {
        super( serviceFactory, id );
        this.presencePlan = presencePlan;
    }

    public synchronized AgentPresenceUsage getOrAddUsageHistory( PresenceEvent event ) {
        if ( getTimer() == null ) {
            Calendar timeout = Calendar.getInstance();
            timeout.set( Calendar.HOUR_OF_DAY, presencePlan.getTo().get( Calendar.HOUR_OF_DAY ) );
            timeout.set( Calendar.MINUTE, presencePlan.getTo().get( Calendar.MINUTE ) );
            long totimeoutmillis = timeout.getTime().getTime() - Calendar.getInstance().getTime().getTime();
//            logger.finest( "++++++++++++++++++ " + OpxiToolBox.duration( totimeoutmillis ) + " to timeout" );
            setTimer( getServiceFactory().getSipService().getTimerService().createTimer(
                    getServiceFactory().getSipService().getApplicationSession(),
                    totimeoutmillis, false, this
            )
            );
        }
        if ( history.containsKey( event.getPresence().getUserAgent() ) ) {
            logger.finest( "Retreiving Presence Usage: " + history.get( event.getPresence().getUserAgent() ) );

            return history.get( event.getPresence().getUserAgent() );
        }
        AgentPresenceUsage apu = new AgentPresenceUsage( event, presencePlan );
        logger.finest( "Created Presence Usage: " + apu );
        history.put( event.getPresence().getUserAgent(), apu );
        return apu;
    }

    public AgentPresenceUsage getUsageHistory( UserAgent ua ) {
//        AgentPresenceUsage lastHistory = null;
        for ( AgentPresenceUsage apu : history.values() ) {
            if ( apu.getUserAgent().equals( ua ) ) {
                return apu;
            }
        }
        return null;
    }

    public Collection getAll() {
        return history.values();
    }

    public void timeout() throws TimerException {
        synchronized ( this ) {
            history.clear();
            presencePlan.getAdminAuthentications().clear();
            setTimer( null );
            logger.finest( "Rule memory cleared..." );
        }
    }
}
