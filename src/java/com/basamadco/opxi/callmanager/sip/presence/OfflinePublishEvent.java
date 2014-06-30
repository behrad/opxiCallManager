package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.util.ResourceBundleUtil;

import javax.servlet.sip.SipServletRequest;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Nov 1, 2007
 *         Time: 10:10:03 AM
 */
public class OfflinePublishEvent extends ClosedPublishEvent {

    private static final Logger logger = Logger.getLogger(OfflinePublishEvent.class.getName());

    public OfflinePublishEvent( PublishContext ctx, SipServletRequest publish) {
        super( ctx, publish );
    }

    public void handleActiveEvent() throws OpxiException, IOException {
        super.handleActiveEvent();
        logger.finer("Service Offline PUBLISH event: '" + publish.getRequestURI() + "'");
    }
}