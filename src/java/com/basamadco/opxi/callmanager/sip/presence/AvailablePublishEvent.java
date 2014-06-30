package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.sip.util.SIPConstants;

import javax.servlet.sip.SipServletRequest;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Nov 1, 2007
 *         Time: 10:47:13 AM
 */
public abstract class AvailablePublishEvent extends OpenPublishEvent {

    private static final Logger logger = Logger.getLogger(AvailablePublishEvent.class.getName());

    public AvailablePublishEvent( PublishContext ctx, SipServletRequest publish) {
        super( ctx, publish);
    }


    public void setActive() {
        getPresence().setActive( true );
    }

    public static AvailablePublishEvent createSpecificEvent( PublishContext ctx, SipServletRequest newPublish ) {
        if( !ctx.isNew() ) { // open as a re-publish
            if( ctx.getPresence().getBasic().equalsIgnoreCase( SIPConstants.BASIC_STATUS_CLOSED ) ) {
                // agent was closed already
                return new LoginPublishEvent( ctx, newPublish );
            } else {
                if( !ctx.getPresence().isActive() ) { // was open but busy
                    return new ReadyPublishEvent( ctx, newPublish );
                } else {
                    return new ReadyReadyPublishEvent( ctx, newPublish );
                }
            }
        } else { // open as a fresh new publish
            logger.finest( "This is an Active event in context: " + ctx );
            ctx.setActive( true );
            return new LoginPublishEvent( ctx, newPublish );
        }
    }
}
