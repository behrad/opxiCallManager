package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.*;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.pool.AgentNotAvailableException;
import com.basamadco.opxi.callmanager.sip.OpxiSipServlet;
import com.basamadco.opxi.callmanager.sip.util.SipUtil;
import com.basamadco.opxi.callmanager.util.LockManager;
import com.basamadco.opxi.callmanager.util.PropertyUtil;
import com.basamadco.opxi.callmanager.util.ResourceBundleUtil;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.URI;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * OpxiCallManager Presence Server
 *
 * @author Jrad
 */
public class PresenceServlet extends OpxiSipServlet {

    private static final Logger logger = Logger.getLogger( PresenceServlet.class.getName() );

    public static final String PRESENCE_EVENT = "presence";



    protected boolean toValidate( SipServletRequest request ) throws ServletException {
        return ( request.getMethod().equals( PUBLISH ) && request.getExpires() > 0 );
    }

    protected void doPublish( SipServletRequest publish ) throws ServletException, IOException {
        
        try {
            getServiceFactory().getPresenceService().servicePublish( publish );
        } catch( AgentNotAvailableException e ) {
            logger.finer( "PUBLISH request not approved: " + publish.getRequestURI() );
            logger.log( Level.SEVERE, e.getMessage(), e );
            SipServletResponse res = publish.createResponse( 503, e.getMessage() );
            res.setHeader( "Retry-After", "10" );
            res.setExpires( publish.getExpires() );
            res.setHeader( SIP_ETAG, ETAG_VALUE );
            res.send();
            getServiceFactory().getSipService().sendIM( publish.getAddressHeader( CONTACT ).getURI(),
                    ResourceBundleUtil.getMessage( "callmanager.presence.restartSoftPhone" )
            );
        } catch ( OpxiException e ) {
            logger.finer( "PUBLISH request not approved: " + publish.getRequestURI() );
            logger.log( Level.SEVERE, e.getMessage(), e );
            SipServletResponse res = publish.createResponse( 503, e.getMessage() );
            res.setHeader( "Retry-After", "10" );
            res.setExpires( publish.getExpires() );
            res.setHeader( SIP_ETAG, ETAG_VALUE );
            res.send();
            getServiceFactory().getSipService().sendIM( publish.getAddressHeader( CONTACT ).getURI(), e.getMessage() );
        }
    }



    protected void doSubscribe( SipServletRequest request ) throws ServletException, IOException {
        String event = request.getHeader( EVENT );
        if (!event.equalsIgnoreCase( PRESENCE_EVENT )) {
            request.createResponse( 489, "Event Package Not Supported" ).send(); // Bad Event! RFC 3265
            return;
        }
        try {
            // Subscriber should be registered already but notifier may not be so!
            UserAgent subscriber = new UserAgent(
                    SipUtil.getName( request.getFrom().getURI() ), SipUtil.getDomain( request.getFrom().getURI() )
            );

            UserAgent notifier = new UserAgent(
                    SipUtil.getName( request.getTo().getURI() ), SipUtil.getDomain( request.getTo().getURI() ) );

            String name = notifier.getName();
            if( notifier.getName().indexOf( "Skill" ) > 0 &&
                    (notifier.getName().indexOf( "Skill" ) + 5 == notifier.getName().length()) 
                    ) {
                name = name.substring( 0, notifier.getName().indexOf( "Skill" ) );
            }
            BaseDAOFactory.getDirectoryDAOFactory().getCallTargetDAO().getCallTargetByName( name );

            synchronized (LockManager.getSubscriptionLock( subscriber, notifier ) ) {
                if (request.getExpires() == 0) {
                    handleUnsubscribe( request, subscriber, notifier );
                    logger.finer( "Unsubscribe request approved: " + request.getRequestURI() );
                } else {
                    handleSubscribe( request, subscriber, notifier );
                    Presence notifierPresence = new Presence( notifier );
                    try {
                        notifierPresence = getServiceFactory().getPresenceService().getPresence( notifier );
                    } catch ( UserNotAvailableException e ) {
                        logger.warning( "Notifier not available: " + request.getRequestURI() + e );
                    } catch ( NoActivePublishContextException e ) {
                        logger.warning( "Notifier has no active presence context: " + request.getRequestURI() + e );
                    } finally {
                        doNotifyOnSubscription( request, notifierPresence );
                    }
                }
            }
        } catch ( EntityNotExistsException e ) {
//            checkEntitySubscription( request );
            logger.finer( "Notifier not found for subscription request: " + request.getRequestURI() + e );
            SipServletResponse res = request.createResponse( SipServletResponse.SC_NOT_FOUND );
            res.send();
        } catch ( OpxiException e ) {
            logger.severe( "Error completing subscribtion request for: " + request.getRequestURI() + e );
            sendErrorResponse( request, e );
        }
    }

    private Subscription handleSubscribe( SipServletRequest subscribe, UserAgent subscriber, UserAgent notifier )
            throws OpxiException, ServletException, IOException {
        Subscription subscription;
        try {
            Subscription oldSubscription = getServiceFactory().getPresenceService().findSubscription(
                    subscribe.getCallId()
            );
            oldSubscription.setExpiry( oldSubscription.getExpiryDate( subscribe.getExpires() ) );
            oldSubscription.setNotifier( notifier );
            oldSubscription.setSubscriber( subscriber );
            getServiceFactory().getPresenceService().refresh( oldSubscription, subscribe );
            subscription = oldSubscription;
            logger.finer( "Resubscribe request approved: " + subscribe.getRequestURI() );
        } catch ( SubscriptionNotAvailableException e ) {
            subscription = new Subscription(
                    subscriber, notifier,
                    subscribe.getHeader( EVENT ), subscribe.getExpires(), subscribe.getCallId()
            );
            getServiceFactory().getPresenceService().subscribe( subscription, subscribe );
            logger.finer( "Subscribe request approved: " + subscribe.getRequestURI() );
        }        
        SipServletResponse res = subscribe.createResponse( SipServletResponse.SC_OK );
        if (subscribe.getExpires() >= 0) {
            res.setExpires( subscribe.getExpires() );
        }
        res.send();
        return subscription;
    }

    private Subscription handleUnsubscribe( SipServletRequest request, UserAgent subscriber, UserAgent notifier )
            throws OpxiException, ServletException, IOException {
        Subscription oldSub = getServiceFactory().getPresenceService().findSubscription(
                request.getCallId()
        );
        getServiceFactory().getPresenceService().unsubscribe( oldSub );
        SipServletResponse res = request.createResponse( SipServletResponse.SC_OK );
        if (request.getExpires() >= 0)
            res.setExpires( request.getExpires() );
        res.send();
        return oldSub;
    }

    protected void doNotifyOnSubscription( SipServletRequest subscribe, Presence notifier )
            throws ServletException, IOException, OpxiException {

        URI contact = subscribe.getAddressHeader( CONTACT ).getURI();
        getServiceFactory().getPresenceService().createNotifyRequest( subscribe, notifier, null, contact ).send();
    }

}
