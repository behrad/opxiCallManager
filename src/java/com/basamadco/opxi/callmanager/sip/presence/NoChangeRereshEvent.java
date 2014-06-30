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
public class NoChangeRereshEvent extends NoBodyRePublishEvent {

    private static final Logger logger = Logger.getLogger(NoChangeRereshEvent.class.getName());


    public NoChangeRereshEvent( PublishContext ctx, SipServletRequest publish ) {
        super( ctx, publish );
    }

    public void handleActiveEvent() throws OpxiException, IOException {
        getContext().sendOK();
        logger.finer( "Service Re-PUBLISH(no-change) in context: " + getContext() );
    }
}
