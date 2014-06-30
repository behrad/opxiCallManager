package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;

import javax.servlet.sip.SipServletRequest;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Jul 8, 2008
 *         Time: 6:03:32 PM
 */
public class BusyBusyEvent extends BusyEvent {

    private static final Logger logger = Logger.getLogger(BusyBusyEvent.class.getName());

    public BusyBusyEvent( PublishContext ctx, SipServletRequest publish ) {
        super( ctx, publish );
    }

    public void handleActiveEvent() throws OpxiException, IOException {
        logger.warning( "Agent was open, busy already, do nothing!" );
        getPresence().setComment( "Busy Publish on Busy Status" );
        getPresenceService().notifyPresenceFromPublish( getPresence(), getContext().getPublishRequest() );
        getContext().sendOK();
    }
}
