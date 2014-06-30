package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.util.ResourceBundleUtil;

import javax.servlet.sip.SipServletRequest;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Nov 1, 2007
 *         Time: 10:00:37 AM
 */
public class UnPublishEvent extends ClosedPublishEvent {

    private static final Logger logger = Logger.getLogger( UnPublishEvent.class.getName() );

    public UnPublishEvent( PublishContext ctx, SipServletRequest publish ) {
        super( ctx, publish );
    }

    public void handleActiveEvent() throws OpxiException, IOException {
        getPresence().setComment( "Un-Publish Received" );
        super.handleActiveEvent();
//        getContext().getHandler().removePresenceContext( getContext().getPublishRequest() );
        logger.finer( "Service Un-PUBLISH event: '" + publish.getRequestURI() + "'" );
    }
}
