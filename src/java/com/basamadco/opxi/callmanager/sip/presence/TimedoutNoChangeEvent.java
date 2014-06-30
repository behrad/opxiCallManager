package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import java.io.IOException;

/**
 * @author Jrad
 *         Date: Jul 8, 2008
 *         Time: 6:12:24 PM
 */
public class TimedoutNoChangeEvent extends NoBodyRePublishEvent {

    public TimedoutNoChangeEvent( PublishContext ctx, SipServletRequest publish ) {
        super( ctx, publish );
    }

    public void handleActiveEvent() throws OpxiException, IOException {
        getPresence().setComment( "Activity Timer Exceeded" );
        SipServletResponse res = getContext().getPublishRequest().createResponse( 412, "Activity Timer Exceeded" );
        res.setExpires( getContext().getPublishRequest().getExpires() );
        try {
            res.send();
        } catch ( IOException e ) {
            throw new OpxiException( e );
        }
    }
}
