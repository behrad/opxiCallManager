package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;

import javax.servlet.sip.SipServletRequest;
import java.io.IOException;

/**
 * @author Jrad
 *         Date: Jul 8, 2008
 *         Time: 6:13:29 PM
 */
public class BadNoChangeEvent extends NoBodyRePublishEvent {

    public BadNoChangeEvent( PublishContext ctx, SipServletRequest publish ) {
        super( ctx, publish );
    }

    public void handleActiveEvent() throws OpxiException, IOException {
        getPresence().setComment( "Re-Publish Not Acceptable" );
        getContext().sendConditionalErrorResponse();
    }
}
