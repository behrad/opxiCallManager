package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;

import javax.servlet.sip.SipServletRequest;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Nov 1, 2007
 *         Time: 10:10:03 AM
 */
public class OfflineEvent extends ClosedPublishEvent {

    private static final Logger logger = Logger.getLogger( OfflineEvent.class.getName() );

    public OfflineEvent( PublishContext ctx, SipServletRequest publish ) {
        super( ctx, publish );
    }

    public void handleActiveEvent() throws OpxiException, IOException {
        getPresence().setComment( "Offline Publish Received" );
        super.handleActiveEvent();
        getPresenceService().getServiceFactory().getAgentService().getAgentForUA(
                getPresence().getUserAgent()
        ).clearServiceStatus();
        logger.finer( "Service Offline PUBLISH event: '" + publish.getRequestURI() + "'" );
    }
}