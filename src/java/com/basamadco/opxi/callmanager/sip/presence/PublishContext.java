package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.PresenceService;
import com.basamadco.opxi.callmanager.entity.Presence;
import com.basamadco.opxi.callmanager.pool.Agent;
import com.basamadco.opxi.callmanager.rule.AgentPresenceUsage;
import com.basamadco.opxi.callmanager.sip.AbstractContext;
import com.basamadco.opxi.callmanager.sip.util.SIPConstants;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import com.basamadco.opxi.callmanager.util.ResourceBundleUtil;

import javax.servlet.sip.*;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Nov 1, 2007
 *         Time: 9:39:53 AM
 */
public class PublishContext extends PresenceContext {

    private static final Logger logger = Logger.getLogger( PublishContext.class.getName() );


    private PresenceEvent event;

    private AgentPublishHandler handler;

    //TODO Timers should be encapsulated by a consistent SipApplicationSession 
    private PresenceTimer timer;

    private boolean active = true;

    private boolean isNew = true;

    private SipServletRequest initPublishRequest;

    private Presence presence;

    private Presence oldPresence;

    private AgentPresenceUsage usageCtx;


    public PublishContext( SipServletRequest publish, AgentPublishHandler handler ) throws OpxiException {
        this.handler = handler;
        this.initPublishRequest = publish;
        initPublishRequest.getApplicationSession().setAttribute( AbstractContext.class.getName(), this );
        String eventType = publish.getHeader( PresenceServlet.EVENT );
        if ( !eventType.equalsIgnoreCase( PresenceServlet.PRESENCE_EVENT ) ) {
            throw new OpxiException( "Event Type Not supported by presenceContext: " + eventType );
        }
        presence = new Presence( handler.getUserAgent() );
        presence.setId( publish.getCallId() );
        extractPresenceFromMessage( publish, presence );
        oldPresence = new Presence( presence );
    }


    public void service( SipServletRequest publish ) throws OpxiException, IOException {
        presence.incMsgIndex();

        if ( timer != null ) {
            timer.cancel();
        }

        event = createFromMessage( publish );

        if ( isCurrentActiveCtxInHandler() ) {

            if ( getUsageCtx() != null ) {
                logger.finest( "Already set a Usage Context: " + getUsageCtx() );
                if ( getUsageCtx().subsequentEventReceived( event ) ) {
                    _service( publish, false );
                } else {
                    _service( publish, true );
                }
            } else {
                logger.finest( "No Usage Context: " + getUsageCtx() );
                _service( publish, true );
            }
            if ( !(event instanceof UnPublishEvent) ) {
                timer = new PresenceTimer( getHandler().getPresenceService().getServiceFactory(), this );
            }
        } else {
            logger.finer( "Service passive publish event: " + event );
            event.handlePassiveEvent();
            getHandler().removePresenceContext( getPublishRequest() );
        }
        isNew = false; // This is not more a new context
    }


    private void _service( SipServletRequest publish, boolean checkRules ) throws OpxiException, IOException {
        Agent agent = null;
        boolean approved = true;
        if ( checkRules ) {
            agent = getPresenceService().getServiceFactory().getAgentService()
                    .getOrLoadAgent( getHandler().getUserAgent() );
            approved = agent.approves( event );
        }
        if ( approved ) {
            logger.finer( "Service active publish event: " + event );

            extractPresenceFromMessage( publish, presence );
            event.setActive();
            event.setStatus();
            event.handleActiveEvent();
            oldPresence.copyStatus( presence );
            if ( event instanceof UnPublishEvent || event instanceof TimeoutPresenceEvent ) {
                getHandler().removePresenceContext( getPublishRequest() );
            }
        } else {
            logger.finer( "Publish event is not acceptable against entity rule profile." );
            sendOK( "Not Acceptable Against Rule Profile" );
            if ( event instanceof UnPublishEvent ) {
                getHandler().removePresenceContext( getPublishRequest() );
            }
        }
    }

    public void sendOK( String msg ) {
        SipServletResponse res = getPublishRequest().createResponse( SipServletResponse.SC_OK, msg );
        res.setHeader( SIPConstants.SIP_ETAG, SIPConstants.ETAG_VALUE );
        if ( getPublishRequest().getExpires() >= 0 ) {
            res.setExpires( getPublishRequest().getExpires() );
        }
        try {
            res.send();
        } catch ( IOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    public void sendOK() {
        sendOK( "OK" );
    }

    public void sendInactiveResponse() {
        SipServletResponse res = getPublishRequest().createResponse( 403, "Publish Context Inactive" );
        res.setExpires( getPublishRequest().getExpires() );
//        res.setHeader( SIPConstants.SIP_ETAG, SIPConstants.ETAG_VALUE );
        try {
            res.send();
        } catch ( IOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    public void sendConditionalErrorResponse() {
        SipServletResponse res = getPublishRequest().createResponse( 412, "Re-Publish Not Acceptable" );
        res.setExpires( getPublishRequest().getExpires() );
//        res.setHeader( SIPConstants.SIP_ETAG, SIPConstants.ETAG_VALUE );
        try {
            res.send();
        } catch ( IOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    public void sendErrorResponse( String msg ) {
        SipServletResponse res = getPublishRequest().createResponse( 406, msg );
        res.setExpires( getPublishRequest().getExpires() );
//        res.setHeader( SIPConstants.SIP_ETAG, SIPConstants.ETAG_VALUE );
        try {
            res.send();
        } catch ( IOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    private PresenceEvent createFromMessage( SipServletRequest publish ) throws OpxiException {
        PresenceEvent event = null;
        if ( publish.getExpires() == 0 ) { // UnPublish Message
            event = new UnPublishEvent( this, publish );
        } else { // Publish
            if ( publish.getContentLength() == 0 && // No-Change RePublish!  (publish with no body)
                    publish.getHeader( PresenceServlet.SIP_IF_MATCH ).equals( PresenceServlet.ETAG_VALUE ) ) {
                event = NoBodyRePublishEvent.createSpecificEvent( this, publish );
            } else { // PUBLISH/RE-PUBLISH with changes
                String basicStatus = getStateBasic( publish );
                if ( basicStatus.equalsIgnoreCase( BASIC_STATUS_OPEN ) ) {
                    String noteStatus = getStateNote( publish, basicStatus ).toLowerCase();
                    if ( noteStatus.contains( "away" ) || noteStatus.contains( "busy" )
                            || noteStatus.contains( "phone" )
                            ) {
                        event = OpenPublishEvent.createSpecificEvent( this, publish );
                    } else {
                        event = AvailablePublishEvent.createSpecificEvent( this, publish );
                    }
                } else {
                    event = new OfflineEvent( this, publish );
                }
            }
        }
        return event;
    }

    public void extractPresenceFromMessage( SipServletRequest publish, Presence presence ) throws OpxiException {
        if ( publish.getContentLength() > 0 ) {
            presence.setBasic( getStateBasic( publish ) );
            presence.setActivity( getActivity( publish ) );
            presence.setNote( getStateNote( publish, presence.getBasic() ) );
        }
        Date now = new Date();
        presence.setSubmission( now );
        presence.setExpiry( new Date( now.getTime() + publish.getExpires() * 1000 ) );
        try {
            presence.setLocation( publish.getAddressHeader( CONTACT ) );
        } catch ( ServletParseException e ) {
            logger.severe( e.getMessage() );
            throw new OpxiException( e );
        }
    }

    /**
     * Checks if this is a fresh publish context (i.e. no refresh publishes received yet)
     *
     * @return true if no old state available, otherwise false
     */
    public boolean isNew() {
        return isNew;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive( boolean active ) {
//        logger.finest( "++++++++++ SetActive: " + active );
        this.active = active;
        if ( active ) {
            handler.activeContextNotification( this );
        } else {
            getPresenceService().getServiceFactory().getSipService().sendIM(
                    getContactAddress().getURI(),
                    ResourceBundleUtil.getMessage( "callmanager.presence.context.inactive" )
            );
        }
    }

    /**
     * Checks if this context should handle PUBLISH actions for this handler's UserAgent
     *
     * @return true if input is an active context, otherwise false
     */
    private synchronized boolean isCurrentActiveCtxInHandler() {
        if ( !isActive() ) {
            return false;
        } else {
            if ( !isNew() ) { // we don't have problems for old-active contexts
                return true;
            } else { // here we should see if new-active context is acceptable or not!
                /* When this is a new-active context, check if any other old active context exists!
                 * and should this context overwrite them or not!
                 */
                if ( !getHandler().existsOtherActiveCtxThan( this ) ) {
                    return true; // I am the only active context
                } else { // I am a new active context but another active context exists
                    setActive( getPresence().isActive() );
                    return isActive();
                }
            }
        }
    }

    public AgentPublishHandler getHandler() {
        return handler;
    }

    public Address getContactAddress() {
        try {
            return getPublishRequest().getAddressHeader( CONTACT );
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            return null;
        }
    }

    public String getStateBasic( SipServletRequest request ) throws OpxiException {
        try {
            if ( request.getContentLength() > 0 ) {
                String st = new String( (byte[]) request.getContent(), UTF8 );
                if ( st.indexOf( "<basic>" ) > 0 ) {
                    st = st.split( "<basic>" )[1];
                    st = st.split( "</basic>" )[0];
                    return st;
                }
            }
            throw new OpxiException( "Request Content Length is zero" );
        } catch ( IOException e ) {
            throw new OpxiException( e );
        }
    }


    public String getStateNote( SipServletRequest request, String statusBasic ) throws OpxiException {
        try {
            if ( request.getContentLength() > 0 ) {
                String st = new String( (byte[]) request.getContent(), UTF8 );
                if ( st.indexOf( "note>" ) > 0 ) {
                    st = st.split( "note>" )[1];
                    st = st.split( "</" )[0];
                    return st;
                }
                if ( statusBasic.equalsIgnoreCase( BASIC_STATUS_OPEN ) )
                    return NOTE_STATUS_ONLINE;
                else if ( statusBasic.equalsIgnoreCase( BASIC_STATUS_CLOSED ) )
                    return NOTE_STATUS_OFFLINE;
            }
            throw new OpxiException( "Request Content Length is zero" );
        } catch ( IOException e ) {
            throw new OpxiException( e );
        }
    }

    public String getActivity( SipServletRequest request ) {
        try {
            if ( request.getContentLength() > 0 ) {
                String st = new String( (byte[]) request.getContent(), UTF8 );
                if ( st.indexOf( "activities>" ) > 0 ) {
                    st = st.split( "activities>" )[1];
                    st = st.split( "</" )[0];
                    if ( st.indexOf( ":" ) > 0 ) {
                        st = st.split( ":" )[1].split( "/>" )[0];
                    }
                    return st;
                }
                return "";
            }
            throw new IllegalStateException( "Request Content Length is zero" );
        } catch ( IOException e ) {
            throw new IllegalStateException( e );
        }
    }

    public Presence getPresence() {
        return presence;
    }

    public Presence getOldPresence() {
        return oldPresence;
    }

    public AgentPresenceUsage getUsageCtx() {
        return usageCtx;
//        return null;
    }

    public void registerUsageCtx( AgentPresenceUsage usageCtx ) {
        if ( usageCtx == null ) {
            logger.finest( "Unregister usageCtx: " + this.usageCtx );
        } else {
            logger.finest( "Register usageCtx: " + usageCtx );
        }
        this.usageCtx = usageCtx;
    }

    public SipServletRequest getPublishRequest() {
        if ( event != null ) {
//            logger.fine( "+++++++++++++++++++++++++++++++++ initialPublish: " + event.getPublish().getApplicationSession());
            return event.getPublish();
        }
//        logger.fine( "+++++++++++++++++++++++++++++++++ initialPublish: " + initPublishRequest.getApplicationSession());
        return initPublishRequest;
    }

    public PresenceService getPresenceService() {
        return handler.getPresenceService();
    }

    public SipApplicationSession getApplicationSession() {
        return initPublishRequest.getApplicationSession();
    }

    public String toString() {
        return "PublishContext[UserAgent='" +
                getPresence().getUserAgent() +
                "', Contact='" +
                getContactAddress().getURI().toString() +
                "', callId='" +
                getPublishRequest().getCallId() +
                "', isActiveCtx='" + isActive() +
                "', basic='" + getPresence().getBasic() +
                "', activity='" + getPresence().getActivity() +
                "', note='" + getPresence().getNote() +
                "', active='" + getPresence().isActive() +
                "', toTimeout='" + (timer != null ?
                OpxiToolBox.duration( timer.getTimer().scheduledExecutionTime() - System.currentTimeMillis() )
                :
                "null") +
                "', usgCtx='" + getUsageCtx() +
                "']";


    }

    public void destroy() {
        if ( timer != null ) {
            timer.cancel();
        }
        initPublishRequest.getApplicationSession().removeAttribute( AbstractContext.class.getName() );
//        getInitiatorSession().invalidate();
    }
}
