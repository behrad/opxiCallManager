package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.Presence;
import com.basamadco.opxi.callmanager.entity.Subscription;
import com.basamadco.opxi.callmanager.entity.UserAgent;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Apr 17, 2006
 *         Time: 11:39:51 AM
 */
public class CallManagerPresence extends PresenceServlet {

    private static final Logger logger = Logger.getLogger( CallManagerPresence.class.getName() );

    public static final UserAgent CALL_MANAGER_UA;

    public static final Presence OPXI_CALL_MANAGER_P;

    static {
        CALL_MANAGER_UA = new UserAgent( CALL_MANAGER_URI );
        OPXI_CALL_MANAGER_P = new Presence( CALL_MANAGER_UA );
        OPXI_CALL_MANAGER_P.setBasic( BASIC_STATUS_OPEN );
        OPXI_CALL_MANAGER_P.setNote( NOTE_STATUS_ONLINE );
    }



    protected void doSubscribe( SipServletRequest request ) throws ServletException, IOException {
//        if( new UserAgent( request.getTo().getURI() ).equals( CALL_MANAGER_UA ) ) {
        /*SipServletResponse res = request.createResponse( SipServletResponse.SC_OK );
        if( request.getExpires() >= 0 ) {
            res.setExpires( request.getExpires() );
        }
        res.send();
        if( request.getExpires() > 0 ) {
            Subscription subscription = new Subscription();
            subscription.setSubscriber( new UserAgent( request.getFrom().getURI() ) );
            subscription.setNotifier( CALL_MANAGER_UA );
            try {
                getServiceFactory().getPresenceService().subscribe( subscription, request );
                doNotifyOnSubscription( request, CALL_MANAGER_UA );
            } catch( OpxiException e ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
            }
        }*/
//        } else {
//            request.createResponse( SipServletResponse.SC_NOT_FOUND ).send();
//        }
    }

}
