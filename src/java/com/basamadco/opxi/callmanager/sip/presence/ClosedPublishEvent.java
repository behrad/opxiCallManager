package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.util.ResourceBundleUtil;

import javax.servlet.sip.SipServletRequest;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Nov 1, 2007
 *         Time: 11:54:34 AM
 */
public class ClosedPublishEvent extends PresenceEvent {

    private static final Logger logger = Logger.getLogger( ClosedPublishEvent.class.getName() );

    public ClosedPublishEvent( PublishContext ctx, SipServletRequest publish ) {
        super( ctx, publish );
    }


    public void setActive() {
        getPresence().setActive( false );
    }

    public void setStatus() {
        getPresence().setBasic( BASIC_STATUS_CLOSED );
        getPresence().setNote( NOTE_STATUS_OFFLINE );
    }

    public void handleActiveEvent() throws OpxiException, IOException {
        getPresenceService().getServiceFactory().getAgentService().unassignPool( getPresence().getUserAgent() );
        getContext().sendOK();
        getPresenceService().notifyPresenceFromPublish( getPresence(), getContext().getPublishRequest() );
        getPresenceService().getServiceFactory().getSipService().sendIM( getContext().getContactAddress().getURI(),
                ResourceBundleUtil.getMessage( "callmanager.presence.offlineMsg" )
        );
    }

}
