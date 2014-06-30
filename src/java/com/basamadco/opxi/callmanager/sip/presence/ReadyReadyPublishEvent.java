package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;

import javax.servlet.sip.SipServletRequest;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Jul 8, 2008
 *         Time: 5:57:40 PM
 */
public class ReadyReadyPublishEvent extends AvailablePublishEvent {

    private static final Logger logger = Logger.getLogger( ReadyReadyPublishEvent.class.getName() );

    public ReadyReadyPublishEvent( PublishContext ctx, SipServletRequest publish ) {
        super( ctx, publish );
    }

    public void handleActiveEvent() throws OpxiException, IOException {
        getPresence().setComment( "Ready Publish on Ready Status" );
        logger.warning( "was open and was available already, do nothing!" );
        getContext().sendOK();
        getPresenceService().notifyPresenceFromPublish( getPresence(), getContext().getPublishRequest() );
    }
}
