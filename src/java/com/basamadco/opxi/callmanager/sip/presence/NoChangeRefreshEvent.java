package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;

import javax.servlet.sip.SipServletRequest;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Jul 8, 2008
 *         Time: 6:14:28 PM
 */
public class NoChangeRefreshEvent extends NoBodyRePublishEvent {

    private static final Logger logger = Logger.getLogger( NoChangeRefreshEvent.class.getName());


    public NoChangeRefreshEvent( PublishContext ctx, SipServletRequest publish ) {
        super( ctx, publish );
    }

    public void handleActiveEvent() throws OpxiException, IOException {
        getPresence().setComment( "Re-Publish Received" );
        getContext().sendOK();
        logger.finer( "Service Re-PUBLISH(no-change) in context: " + getContext() );
    }
}
