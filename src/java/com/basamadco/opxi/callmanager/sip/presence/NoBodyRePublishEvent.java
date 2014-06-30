package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;

import javax.servlet.sip.SipServletRequest;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Nov 1, 2007
 *         Time: 10:01:06 AM
 */
public abstract class NoBodyRePublishEvent extends PresenceEvent {

    private static final Logger logger = Logger.getLogger(NoBodyRePublishEvent.class.getName());

    public NoBodyRePublishEvent( PublishContext ctx, SipServletRequest publish ) {
        super( ctx, publish );
    }

    public void setActive() {
        // Do nothing
    }

    public void setStatus() throws OpxiException {
        // Do nothing
    }

    public static NoBodyRePublishEvent createSpecificEvent( PublishContext ctx, SipServletRequest newPublish ) {
        if ( isPresenceTimerAlreadyExceeded( ctx ) ) {
            // just reject publish
            return new TimedoutNoChangeEvent( ctx, newPublish );            
        } else if( ctx.isNew() ) {
            // New Publish with no body! (i.e. server is restarted without client notification... )
            return new BadNoChangeEvent( ctx, newPublish );
        } else {
            return new NoChangeRefreshEvent( ctx, newPublish );
        }
    }

    private static boolean isPresenceTimerAlreadyExceeded( PublishContext ctx ) {
        return ctx.getPresence().getBasic().equalsIgnoreCase(BASIC_STATUS_CLOSED) &&
                ctx.getPresence().getNote().equalsIgnoreCase(TimeoutPresenceEvent.PRESENCE_TIMER_EXCEEDED);
    }


}
