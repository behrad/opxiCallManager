package com.basamadco.opxi.callmanager;

import com.basamadco.opxi.callmanager.entity.Registration;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.sip.AbstractContext;
import com.basamadco.opxi.callmanager.sip.presence.CallManagerPresence;
import com.basamadco.opxi.callmanager.sip.presence.SubscriptionContext;
import com.basamadco.opxi.callmanager.sip.listener.SipSessionManager;
import com.basamadco.opxi.callmanager.sip.util.ApplicationConstants;

import javax.servlet.ServletException;
import javax.servlet.sip.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * More is like a SIP utility service. This service gains access to the SipFactory
 * object in use by application's host container.
 *
 * @author Jrad
 *         Date: Jun 30, 2006
 *         Time: 6:02:09 PM
 */
public class SipService extends AbstractCallManagerService implements ApplicationConstants, AbstractContext {

    private static final Logger logger = Logger.getLogger( SipService.class.getName() );


    private SipFactory sipFactory;

    private String servletContextName;

    private String localMachineName;

    private TimerService timerService;

    /**
     * General purpose appSession which is used in GLOBAL timers. GLOBAL timers are durable
     * and invalidation of appSessions should not deactivate them! so we need an always ACTIVE
     * appSession.
     */
    private SipApplicationSession applicationSession;

//    private ServiceFactory serviceFactory;

    public SipService( String servletContextName, SipFactory factory2, TimerService timerService ) {
//        super( factory1 );
        try {
            this.localMachineName = InetAddress.getLocalHost().getHostName();
            this.servletContextName = servletContextName;
            this.sipFactory = factory2;
            this.timerService = timerService;
            this.applicationSession = factory2.createApplicationSession();
            // make the appSession always ACTIVE
            //Eftekhari
            this.applicationSession.setAttribute( AbstractContext.class.getName(), this );
        } catch ( UnknownHostException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    public SipFactory getSipFactory() {
        return sipFactory;
    }

    public TimerService getTimerService() {
        return timerService;
    }

    public SipApplicationSession getApplicationSession() {
        return applicationSession;
    }

//    public ServiceFactory getServiceFactory() {
//        return serviceFactory;
//    }

    private SipServletRequest createIM( URI to, String bodyText ) throws Exception {
        return createIM( to, bodyText, MIME_TEXT_PLAIN );
    }

    private SipServletRequest createIM( URI to, String body, String contentType ) throws Exception {
        SipFactory sf = getServiceFactory().getSipService().getSipFactory();
        URI me = getServiceFactory().getSipService().getLocalURI();
        SipServletRequest im = sf.createRequest( sf.createApplicationSession(), MESSAGE, me, to );
        im.setContent( body, contentType );
//        im.setContentLanguage( new Locale( "fa", "IR" ) );
        return im;
    }

    private SipServletRequest createIM( UserAgent to, String bodyText, String contentType ) throws Exception {
        return createIM( getServiceFactory().getSipService().getSipFactory().createURI( to.getSipURIString() ), bodyText, contentType );
    }

    private SipServletRequest createIM( UserAgent to, String bodyText ) throws Exception {
        return createIM( getServiceFactory().getSipService().getSipFactory().createURI( to.getSipURIString() ), bodyText, MIME_TEXT_PLAIN );
    }

    /**
     * Sends an instant message to the user presence location with the specified username.
     *
     * @param userAgent UserAgent
     * @param text      The message's text body
     */
    public void sendPresenceIM( UserAgent userAgent, String text ) {
        try {
//            Set<Registration> locations = getServiceFactory().getLocationService().findRegistrations( userAgent );
            Registration presence_based_reg = getServiceFactory().getPresenceService().getPresence( userAgent );
//            if (locations.size() > 0) {
//                for ( Registration registration : locations ) {
            send( createIM( userAgent, text ), presence_based_reg.getLocation().getURI() );
//                }
//            } else {
//                logger.warning( "No destinations found for '" + userAgent + "'" );
//            }
        } catch ( Exception e ) {
            logger.log( Level.WARNING, "Unable to send IM: " + e.getMessage() );
        }
    }

    public void sendIM( final URI contact, final String text, int secs ) {
        TimerTask t = new TimerTask() {
            public void run() {
                sendIM( contact, text );
            }
        };
        Timer sendTimer = new Timer( "Im-Timer" );
        sendTimer.schedule( t, secs * 1000 );
    }

    public void sendIM( final Registration contact, final String text, int secs ) {
        TimerTask t = new TimerTask() {
            public void run() {
                sendIM( contact, text );
            }
        };
        Timer sendTimer = new Timer( "Im-Timer" );
        sendTimer.schedule( t, secs * 1000 );
    }

    /**
     * Sends an instant message to the user with the specified username and contact address
     *
     * @param contact Address URI of the user
     * @param text    The message's text body
     */
    public void sendIM( Registration contact, String text, String contentType ) {
        try {
            getServiceFactory().getSipService().send(
                    createIM( contact.getUserAgent(), text, contentType )
                    , contact.getLocation().getURI() );
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }


    public void sendIM( Registration contact, String text ) {
        sendIM( contact, text, MIME_TEXT_PLAIN );
    }

    public void sendIM( URI contactURI, String text, String contentType ) {
        try {
            getServiceFactory().getSipService().send(
                    createIM( contactURI, text, contentType )
                    , contactURI );
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    public void sendIM( URI contactURI, String text ) {
        sendIM( contactURI, text, MIME_TEXT_PLAIN );
    }

    /**
     * Sends an administrative instant message to Call Manager <i>admins</i>.
     * NOTE: A call manager admin user is a useragent which has subscribed to
     * the system useragent specified by sip:%SYSTEM_NAME%@%LOCALDOMAIN% where
     * SYSTEM_NAME is specified in application properties file by name:
     * "opxi.callmanager.user"
     * AND
     * LOCALDOMAIN is the application host server domain. A typical sample
     * system useragent AOR is "sip:opxiCallManager@cc.basamad.acc".
     *
     * @param text
     */
    public void sendAdminIM( String text ) {
        try {
            List<SubscriptionContext> adminSubs =
                    getServiceFactory().getPresenceService().findSubscriptionsFor( CallManagerPresence.CALL_MANAGER_UA );
            for ( int i = 0; i < adminSubs.size(); i++ ) {
                sendPresenceIM( adminSubs.get( i ).getSubscription().getSubscriber(), text );
            }
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    /**
     * Returns OPXi Call Manager's SIP AOR in form of sip:%SYSTEM_USER%@%LOCAL_DOMAIN%
     *
     * @return System's local SIP URI
     * @throws OpxiException
     */
    public SipURI getLocalURI() throws OpxiException {
        try {
            return (SipURI) sipFactory.createURI( ApplicationConstants.CALL_MANAGER_URI );
        } catch ( ServletParseException e ) {
            throw new OpxiException( e.getMessage(), e );
        }
    }

    /**
     * Returns the HTTP URI for specified path relative to Call Manager's
     * application context.
     *
     * @param relativePath
     * @return Http URI
     * @throws OpxiException
     */
    public URI getLocalURL( String relativePath ) {
        try {
            if ( relativePath.startsWith( "/" ) ) {
                String url = "http://" + localMachineName + "/" + "opxiCallManager" + relativePath;
                logger.finer( "Generated url: " + url );
                return sipFactory.createURI( url );
            } else {
                String url = "http://" + localMachineName + "/" + "opxiCallManager" + "/" + relativePath;
                logger.finer( "Generated url: " + url );
                return sipFactory.createURI( url );
            }
        } catch ( ServletParseException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new IllegalArgumentException( e.getMessage(), e );
        }
    }

    /**
     * Creates a SIP URI with specified user name and local domain.
     *
     * @param user SIP URI user part
     * @return SIP URI
     */
    public URI createSipURIForName( String user ) {
        return sipFactory.createSipURI( user, ApplicationConstants.DOMAIN );
    }

    public List toURIList( URI uri ) throws OpxiException {
        List uris = null;
        uris = new ArrayList();
        uris.add( uri );
        return uris;
    }

    public List toURIList( String contact ) throws OpxiException {
        try {
            return toURIList( getSipFactory().createURI( contact ) );
        } catch ( ServletParseException e ) {
            logger.severe( e.getMessage() );
            throw new OpxiException( e );
        }
    }

    public List toURIList( Collection<Registration> registrations ) throws OpxiException {
        List uris = new ArrayList();
        if ( registrations != null ) {
            Iterator list = registrations.iterator();
            for ( Registration reg : registrations ) {
                SipURI dest = (SipURI) reg.getLocation().getURI();
                uris.add( dest );
            }
        }
        return uris;
    }

    /**
     * Sends the specified SIP request to contact address specified by the input contact URI
     * NOTE: This method also adds a specific request-uri parameter
     * to mark outbound messages.
     *
     * @param request
     * @param contact
     * @throws ServletException
     * @throws IOException
     */
    public void send( SipServletRequest request, URI contact ) throws ServletException, IOException {
        ((SipURI) contact).removeParameter( "rinstance" );
        request.setRequestURI( contact );
//        ( (SipURI) request.getRequestURI() ).setParameter( OUTBOUND_B2BUA_LEG, "true" );
        request.send();
    }

    public void destroy() {
        SipSessionManager.clearSessions();
        applicationSession.invalidate();
        logger.info( "SipService destroyed successfully." );
    }

}
