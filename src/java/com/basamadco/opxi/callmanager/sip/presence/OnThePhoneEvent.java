package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;

import javax.servlet.sip.SipServletRequest;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Apr 16, 2010
 *         Time: 1:54:52 PM
 */
public class OnThePhoneEvent extends OpenPublishEvent {

    private static final Logger logger = Logger.getLogger( OnThePhoneEvent.class.getName() );


    public OnThePhoneEvent( PublishContext ctx, SipServletRequest newPublish ) {
        super( ctx, newPublish );
    }

    public void handleActiveEvent() throws OpxiException, IOException {
        getPresence().setComment( "On-The-Phone Publish Received" );
        logger.finest( "*****************************1 " + getContext() );
        getPresence().setActive( false );
        logger.finest( "*****************************2 " + getContext() );
        getContext().sendOK();
        getPresenceService().notifyPresenceFromPublish( getPresence(), getContext().getPublishRequest() );
    }


    public void setActive() {
        getPresence().setActive( false );
        logger.finest( "*****************************3 " + getContext() );
    }

}
