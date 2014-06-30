package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.sip.registrar.RegistrationNotFoundException;
import com.basamadco.opxi.callmanager.util.ResourceBundleUtil;

import javax.servlet.sip.SipServletRequest;
import java.io.IOException;

/**
 * @author Jrad
 *         Date: Jul 8, 2008
 *         Time: 5:56:53 PM
 */
public class ReadyPublishEvent extends AvailablePublishEvent {

    public ReadyPublishEvent( PublishContext ctx, SipServletRequest publish ) {
        super( ctx, publish );
    }

    public void handleActiveEvent() throws OpxiException, IOException {
        getPresence().setComment( "Ready Publish Received" );
        try {
            getPresenceService().getServiceFactory().getLocationService().findRegistrations( getPresence().getUserAgent() );
        } catch ( RegistrationNotFoundException e ) {
            getPresenceService().getServiceFactory().getLocationService().handleContactRegistration( getPresence() );
        }
        getContext().sendOK();
        getPresenceService().getServiceFactory().getAgentService().assignToPool( getPresence().getUserAgent() );
        getPresenceService().getServiceFactory().getAgentService().notifyReadyForCall( getPresence().getUserAgent() );
        if ( !getPresenceService().getServiceFactory().getAgentService().getAgentForUA(
                getPresence().getUserAgent() ).isResting() ) {

            getPresenceService().notifyPresenceFromPublish( getPresence(), getContext().getPublishRequest() );
        }
        if ( !getContext().getOldPresence().isOnThePhone() ) {
            getPresenceService().getServiceFactory().getSipService().sendIM( getContext().getContactAddress().getURI(),
                    ResourceBundleUtil.getMessage( "callmanager.presence.isActiveMsg" )
            );
        }
    }
}
