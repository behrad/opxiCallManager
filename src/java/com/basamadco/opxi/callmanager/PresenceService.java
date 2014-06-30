package com.basamadco.opxi.callmanager;

import com.basamadco.opxi.callmanager.entity.*;
import com.basamadco.opxi.callmanager.sip.presence.*;
import com.basamadco.opxi.callmanager.sip.util.SIPConstants;
import com.basamadco.opxi.callmanager.sip.util.SipUtil;
import com.basamadco.opxi.callmanager.sip.util.ApplicationConstants;
import com.basamadco.opxi.callmanager.sip.registrar.RegistrationNotFoundException;
import com.basamadco.opxi.callmanager.util.LockManager;
import com.basamadco.opxi.callmanager.pool.AgentNotAvailableException;
import com.basamadco.opxi.callmanager.logging.ActivityLogNotExistsException;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.URI;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * OPXi Call Manager core presence service implementation.
 *
 * @author Jrad
 */
public class PresenceService extends AbstractCallManagerService implements SIPConstants {

    private static final Logger logger = Logger.getLogger( PresenceService.class.getName() );


    private final Map<UserAgent, PublishHandler> publisheHandlers = new ConcurrentHashMap<UserAgent, PublishHandler>();

    private final Map<String, SubscriptionContext> subscriptions = new ConcurrentHashMap<String, SubscriptionContext>();

//    private final Map<UserAgent, UserAgent> presenceTable = new ConcurrentHashMap<UserAgent, UserAgent>();


    private static final String PRESENCE_EVENT = "presence";

    private static final String XML_CONTENT_TYPE = "application/pidf+xml";

    private static final String AOR_URI = "AOR_URI";

    private static final String STATUS_BASIC = "STATUS_BASIC";

    private static final String STATUS_NOTE = "STATUS_NOTE";

    private static final String ACTIVITY = "ACTIVITY";


    private static final String NOTIFY_XML_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<presence xmlns=\"urn:ietf:params:xml:ns:pidf\" " +
            "xmlns:pp=\"urn:ietf:params:xml:ns:pidf:person\" " +
            "xmlns:et=\"urn:ietf:params:xml:ns:pidf:rpid:rpid-tuple\" " +
            "xmlns:ep=\"urn:ietf:params:xml:ns:pidf:rpid:rpid-person\" " +
            "xmlns:es=\"urn:ietf:params:xml:ns:pidf:rpid:status:rpid-status\" " +
            "xmlns:ci=\"urn:ietf:params:xml:ns:pidf:cipid\" " +
            "entity=\"" + AOR_URI + "\">" +
            "<pp:person>" +
            "<status>" +
            "<ep:activities>" +
            "<ep:" + ACTIVITY + "/>" +
            "</ep:activities>" +
            "</status>" +
            "</pp:person>" +
            "<note>" + STATUS_NOTE + "</note>" +
            "<tuple id=\"t1\">" +
            "<status>" +
            "<basic>" + STATUS_BASIC + "</basic>" +
            "</status>" +
            "<contact priority=\"1\">" + AOR_URI + "</contact>" +
            "</tuple>" +

            "</presence>";

    private static final String PUBLISH_PIDF_CONTENT_XLITE = "<?xml version='1.0' encoding='UTF-8'?>" +
            "<presence xmlns='urn:ietf:params:xml:ns:pidf' " +
            "xmlns:dm='urn:ietf:params:xml:ns:pidf:data-model' " +
            "xmlns:rpid='urn:ietf:params:xml:ns:pidf:rpid' " +
            "xmlns:c='urn:ietf:params:xml:ns:pidf:cipid' " +
            "entity='" + AOR_URI + "'>" +
            "<tuple id='tab7dda412b'>" +
            "<status><basic>" + STATUS_BASIC + "</basic></status>" +
            "</tuple>" +
            "<dm:person id='p6sa3f363d'>" +
            "<rpid:activities><rpid:" + ACTIVITY + "/></rpid:activities>" +
            "<dm:note>" + STATUS_NOTE + "</dm:note>" +
            "</dm:person>" +
            "</presence>";

    /**
     * Enables Call Manager's SYSTEM user presence as if it's an online user.
     */
    public PresenceService() {
        getOrAddPublishHandler( CallManagerPresence.OPXI_CALL_MANAGER_P );
    }

    /**
     * Subscribes a subscription binding in the presence service
     *
     * @param subscription Subscription information
     * @throws com.basamadco.opxi.callmanager.OpxiException
     *
     */
    public void subscribe( Subscription subscription, SipServletRequest subscribe ) throws OpxiException {
        registerSubscriptionContext( subscribe, subscription ).createTimer();
    }

    /*public UserAgent getUserAgentPresence( String name, String domain ) {
        UserAgent fakeAgent = new UserAgent( name, domain );
        if( presenceTable.containsKey( fakeAgent ) ) {
            return presenceTable.get( fakeAgent );
        } else { // if not! return the fake presence
            return fakeAgent;
        }
    }*/

    private void add( SubscriptionContext subscription ) throws EntityAlreadyExistsException {
        if ( !subscriptions.containsKey( subscription.getSubscription().getSessionId() ) ) {
            logger.finest( "Storing Subscription Context: " + subscription );
            subscriptions.put( subscription.getSubscription().getSessionId(), subscription );

        } else {
            throw new EntityAlreadyExistsException( subscription.getSubscription() );
        }
    }

    /**
     * Unsubscribes a previously submitted subscription
     *
     * @param subscription Subscription should have
     *                     Subscriber and Notifier populated
     * @throws com.basamadco.opxi.callmanager.OpxiException
     *
     */
    public void unsubscribe( Subscription subscription ) throws OpxiException {
        removeSubscriptionContext( subscription ).destroy();
    }

    /**
     * Renews a Subscription binding in the presence server
     *
     * @param subscription Subscription binding
     * @throws com.basamadco.opxi.callmanager.OpxiException
     *
     */
    public void refresh( Subscription subscription, SipServletRequest subscribe ) throws OpxiException {
        logger.finest( "Update Subscription: " + subscription );
//        getDAOFactory().getSubscriptionDAO().update(subscription);
        resolveSubscriptionContext( subscription ).refreshTimer( subscribe );
    }

    private SubscriptionContext registerSubscriptionContext( SipServletRequest subscribe, Subscription subscription ) throws EntityAlreadyExistsException {
        SubscriptionContext ctx = new SubscriptionContext( subscription, subscribe, this );
        add( ctx );
        return ctx;
    }

    private SubscriptionContext removeSubscriptionContext( Subscription subscribe ) throws OpxiException {
        logger.finest( "Remove Subscription Context: " + subscribe );
        SubscriptionContext ctx = (SubscriptionContext) subscriptions.remove( subscribe.getSessionId() );
        if ( ctx == null ) {
            throw new OpxiException( "Subscription Context Not Found '" + subscribe + "'" );
        }
        return ctx;
    }

    private SubscriptionContext resolveSubscriptionContext( Subscription subscribe ) throws OpxiException {
        logger.finest( "Resolve Subscription Context: " + subscribe );
        if ( subscriptions.containsKey( subscribe.getSessionId() ) ) {
            return (SubscriptionContext) subscriptions.get( subscribe.getSessionId() );
        } else {
            throw new OpxiException( "No SubscriptionContext found for " + subscribe );
        }
    }


    public Presence getPresence( UserAgent ua ) throws UserNotAvailableException, NoActivePublishContextException {
//        UserAgent ua = new UserAgent( AOR );
        if ( publisheHandlers.containsKey( ua ) ) {
            return publisheHandlers.get( ua ).getPresence();
        }
        throw new UserNotAvailableException( "No presence context available for '" + ua.getSipURIString() + "'" );
    }

    /**
     * Services PUBLISH SIP requests based on an existing registration for the publisher
     *
     * @param publish
     * @throws OpxiException
     * @throws IOException
     */
    public void servicePublish( SipServletRequest publish ) throws OpxiException, IOException {
        UserAgent ua = new UserAgent(
                SipUtil.getName( publish.getFrom().getURI() ),
                SipUtil.getDomain( publish.getFrom().getURI() )
        );
        synchronized ( LockManager.getPublishLock( ua ) ) {
            getPublishHandlerFor( ua ).handlePublish( publish );
        }
    }


    /**
     * Services presence status changes for internal resources, with no Publish request and resgistration
     *
     * @param presence The UserAgent with specified presence status
     * @throws OpxiException
     * @throws IOException
     */
    public void servicePresence( Presence presence ) throws OpxiException, IOException {
        // TODO bad design in PublishHandlers
        getOrAddPublishHandler( presence ).handlePublish( null );
    }

    private PoolPublishHandler getOrAddPublishHandler( Presence presence ) {
        PoolPublishHandler ph = null;
        if ( publisheHandlers.containsKey( presence.getUserAgent() ) ) {
            ph = (PoolPublishHandler) publisheHandlers.get( presence.getUserAgent() );
        } else {
            ph = new PoolPublishHandler( presence, this );
            publisheHandlers.put( presence.getUserAgent(), ph );
        }
        ph.setPresence( presence );
        return ph;
    }


    public boolean isActive( String AOR ) {
        try {
            boolean what = getPresence( new UserAgent( AOR ) ).isActive();
            return what;
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            return false;
        }
    }

    public PublishContext getActivePresenceContext( UserAgent ua ) throws NoActivePublishContextException {
        return getPublishHandlerFor( ua ).getActiveContext();
    }

    private AgentPublishHandler getPublishHandlerFor( UserAgent ua ) {
        if ( publisheHandlers.containsKey( ua ) ) {
            return (AgentPublishHandler) publisheHandlers.get( ua );
        } else {
            AgentPublishHandler aph = new AgentPublishHandler( ua, this );
            publisheHandlers.put( ua, aph );
            return aph;
        }
    }

    public void removePublishHandler( UserAgent ua ) {
        publisheHandlers.remove( ua );
    }

    public Collection<Presence> getAllPresences() {
        List<Presence> presences = new ArrayList<Presence>();
        for ( PublishHandler handler : publisheHandlers.values() ) {
            try {
                presences.add( handler.getPresence() );
            } catch ( NoActivePublishContextException e ) {
                logger.severe( e.getMessage() );
            }
        }
        return presences;
    }

    /**
     * This method does two main actions: It first stores the useragent's
     * presence info in Presence Service, then it NOTIFIes all
     * subscribers of them for new presence information.
     *
     * @param presence The UserAgent with presence information
     */
    public void notifyPresence( Presence presence ) throws OpxiException {
        notifyPresenceFromPublish( presence, null );
    }

    public void notifyPresenceFromPublish( Presence p, SipServletRequest publish ) {
        try {
            try {
                String logId = getServiceFactory().getAgentService().getAgentForUA( p.getUserAgent() ).getActivityLogId();
                getServiceFactory().getLogService().getAgentActivityLogger( logId ).addPresence( p );
            } catch ( OpxiException e ) {
                logger.warning( e.getMessage() );
            }

            List subs = findSubscriptionsFor( p.getUserAgent() );
            for ( int i = 0; i < subs.size(); i++ ) {
                SubscriptionContext subscriptionCtx = (SubscriptionContext) subs.get( i );
//                SubscriptionContext ctx = resolveSubscriptionContext( subscription );
//                if ( ctx != null ) {
                SipServletRequest subscriptionReq = subscriptionCtx.getSubscribeRequest();
                try {
                    Set<Registration> locations = getServiceFactory().getLocationService().findRegistrations(
                            subscriptionCtx.getSubscription().getSubscriber() );
                    for ( Registration reg : locations ) {
                        try {
                            createNotifyRequest( subscriptionReq, p, publish, reg.getLocation().getURI() ).send();
                        } catch ( IllegalStateException e ) {
                            logger.log( Level.SEVERE, "Invalid Subscription Request Session: " + subscriptionReq, e );
                        }
                    }
                } catch ( RegistrationNotFoundException e ) {
                    logger.warning( "No registration found for subscriber: " + e.getMessage() );
                }
//                }
            }
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        } catch ( IOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    public SipServletRequest createNotifyRequest( SipServletRequest subscribe, Presence notifier, SipServletRequest publish,
                                                  URI ruri ) throws ServletException, IOException {
        SipServletRequest newreq = subscribe.getSession().createRequest( NOTIFY );
        newreq.setHeader( EVENT, PRESENCE_EVENT );
        newreq.setHeader( SUBSCRIPTION_STATE, "active;expires=" + subscribe.getExpires() );
        newreq.setRequestURI( ruri );
        if ( publish != null && publish.getContentLength() > 0 ) {
            logger.finest( "Generate Notify request from publish content..." );
            newreq.setContent( publish.getContent(), publish.getContentType() );
        } else {
            logger.finest( "Generate Notify request from UA status..." );
            newreq.setContent( buildPublishContent( notifier ).getBytes( ApplicationConstants.UTF8 ), XML_CONTENT_TYPE );
        }
        return newreq;
    }

    public String buildPublishContent( Presence p ) {
        String sb = NOTIFY_XML_CONTENT;
        sb = sb.replaceAll( AOR_URI, p.getUserAgent().getSipURIString() );
        sb = sb.replaceAll( STATUS_BASIC, p.getBasic() );
        sb = sb.replaceAll( STATUS_NOTE, p.getNote() );
        sb = sb.replaceAll( ACTIVITY, p.getActivity() );

        /*if ( p.getNote().toLowerCase().contains( "busy" ) ) {
            sb = sb.replaceAll( ACTIVITY, ACTIVITES[1] );
        } else if ( p.getNote().toLowerCase().contains( "away" ) ) {
            sb = sb.replaceAll( ACTIVITY, ACTIVITES[2] );
        } else {
            sb = sb.replaceAll( ACTIVITY, ACTIVITES[0] );
        }*/
        return sb;
    }

    /**
     * Stores user's presence info in Presence Service
     *
     * @param ua
     * @throws OpxiException
     */
    /*private void storePresence( UserAgent ua ) {
        presenceTable.put( ua, ua );
    }*/


    /**
     * Returns a Subscription binding for the specified subscriber and notifier agent
     *
     * @return Subscription binding
     * @throws SubscriptionNotAvailableException
     *                       if no Subscription binding is exists
     * @throws OpxiException
     */
    public Subscription findSubscription( String sessionId ) throws SubscriptionNotAvailableException {
//        return getDAOFactory().getSubscriptionDAO().find(sessionId, subscriber, notifier);
        if ( subscriptions.containsKey( sessionId ) ) {
            return subscriptions.get( sessionId ).getSubscription();
        } else {
            throw new SubscriptionNotAvailableException( sessionId );
        }
    }

    /**
     * Lists all Subscription bindings whose notifier user agent is specified
     *
     * @param notifier Subscription notifier user agent
     * @return Subscription bindings
     * @throws OpxiException
     */
    public List<SubscriptionContext> findSubscriptionsFor( UserAgent notifier ) throws OpxiException {
        List<SubscriptionContext> subCtxs = new ArrayList<SubscriptionContext>();
        Object[] subscriptionCtxs = subscriptions.values().toArray();
        for ( int i = 0; i < subscriptionCtxs.length; i++ ) {
            SubscriptionContext subscriptionCtx = (SubscriptionContext) subscriptionCtxs[i];
            if ( subscriptionCtx.getSubscription().getNotifier().equals( notifier ) ) {
                subCtxs.add( subscriptionCtx );
            }
        }
        return subCtxs;
//        return getDAOFactory().getSubscriptionDAO().find(notifier);
    }

    /*public Presence getPresence(UserAgent ua) throws OpxiException {
        return getDAOFactory().getPresenceDAO().findbyAgent(ua);
    }*/

    /*public Presence getPresence(Long id) throws OpxiException {
        return (Presence) getDAOFactory().getPresenceDAO().load(Presence.class, id);
    }*/

    public List listObjects() {
        List all = new ArrayList();
        all.addAll( Arrays.asList( publisheHandlers.values().toArray() ) );
        all.addAll( Arrays.asList( subscriptions.values().toArray() ) );
//        all.addAll( Arrays.asList( presenceTable.values().toArray() ) );
        return all;
    }

    public void destroy() {
        for ( SubscriptionContext subscriptionContext : subscriptions.values() ) {
            subscriptionContext.destroy();
        }
        subscriptions.clear();

        for ( PublishHandler ph : publisheHandlers.values() ) {
            ph.destroy();
        }
        publisheHandlers.clear();

        CallManagerPresence.OPXI_CALL_MANAGER_P.setBasic( BASIC_STATUS_CLOSED );
        CallManagerPresence.OPXI_CALL_MANAGER_P.setNote( NOTE_STATUS_OFFLINE );

        logger.info( "PresenceService destroyed successfully." );
    }

}
