package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.util.ResourceBundleUtil;

import javax.servlet.sip.SipServletRequest;
import java.io.IOException;

/**
 * @author Jrad
 *         Date: Jul 8, 2008
 *         Time: 5:54:31 PM
 */
public class LoginPublishEvent extends AvailablePublishEvent {

    public LoginPublishEvent( PublishContext ctx, SipServletRequest publish ) {
        super( ctx, publish );
    }

    public void handleActiveEvent() throws OpxiException, IOException {
        getPresence().setComment( "Available Publish Received" );
        getPresenceService().getServiceFactory().getLocationService().handleContactRegistration( getPresence() );
        getContext().sendOK();
        getPresenceService().getServiceFactory().getAgentService().assignToPool( getPresence().getUserAgent() );
        getPresenceService().getServiceFactory().getAgentService().notifyReadyForCall( getPresence().getUserAgent() );
        getPresenceService().notifyPresenceFromPublish( getPresence(), getContext().getPublishRequest() );
        getPresenceService().getServiceFactory().getSipService().sendIM( getContext().getContactAddress().getURI(),
                ResourceBundleUtil.getMessage( "callmanager.presence.isActiveMsg" ), 3
        );
        if ( getContext().isNew() ) {
            String logId = getPresenceService().getServiceFactory().getAgentService().getAgentForUA(
                    getPresence().getUserAgent() ).getActivityLogId();
            getPresenceService().getServiceFactory().getLogService().getAgentActivityLogger( logId ).addRegistration( getPresence() );
        } else {
            String logId = getPresenceService().getServiceFactory().getAgentService().getAgentForUA(
                    getPresence().getUserAgent() ).getActivityLogId();
            getPresenceService().getServiceFactory().getLogService().getAgentActivityLogger( logId ).addPresence( getPresence() );
        }
    }
}
