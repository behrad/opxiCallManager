package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;

import javax.servlet.sip.SipServletRequest;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Jul 8, 2008
 *         Time: 7:14:44 PM
 */
public class TimeoutPresenceEvent extends PresenceEvent {

    private static final Logger logger = Logger.getLogger( TimeoutPresenceEvent.class.getName() );


    public static final String PRESENCE_TIMER_EXCEEDED = "Activity Timer Exceeded";


    public TimeoutPresenceEvent( PublishContext presenceContext, SipServletRequest publish ) {
        super( presenceContext, publish );
    }

    public void handleActiveEvent() throws OpxiException, IOException {
        getPresence().setComment( "Publish Timeout" );
        logger.finest( "Trying to close presence context " + getContext() + " since no re-publish received..." );
        setActive();
        setStatus();
        try {
            getPresenceService().getServiceFactory().getAgentService().unassignPool( getPresence().getUserAgent() );
            getPresenceService().notifyPresence( getPresence() );
        } finally {
//            getContext().getHandler().removePresenceContext( getContext().getPublishRequest() );
        }
    }

    protected void passiveEventAction() throws OpxiException {
        // Do Nothing (Not to send any responses to this event!
        getPresence().setComment( "Inactive Publish Timeout" );
        getContext().getHandler().removePresenceContext( getContext().getPublishRequest() );
    }

    public void setActive() {
        getPresence().setActive( false );
    }

    public void setStatus() {
        getPresence().setBasic( BASIC_STATUS_CLOSED );
        getPresence().setNote( PRESENCE_TIMER_EXCEEDED );
    }
}
