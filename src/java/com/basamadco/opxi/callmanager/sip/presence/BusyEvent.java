package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.Presence;
import com.basamadco.opxi.callmanager.util.ResourceBundleUtil;

import javax.servlet.sip.SipServletRequest;
import java.io.IOException;

/**
 * @author Jrad
 *         Date: Jul 8, 2008
 *         Time: 6:02:48 PM
 */
public class BusyEvent extends BusyPublishEvent {

    public BusyEvent( PublishContext ctx, SipServletRequest publish ) {
        super( ctx, publish );
    }

    public void handleActiveEvent() throws OpxiException, IOException {
        getPresence().setComment( "Busy Publish Received" );
        getContext().sendOK();
        getPresenceService().notifyPresenceFromPublish( getPresence(), getContext().getPublishRequest() );
        getPresenceService().getServiceFactory().getSipService().sendIM( getContext().getContactAddress().getURI(),
                ResourceBundleUtil.getMessage( "callmanager.presence.nonActiveMsg" )
        );
    }

}
