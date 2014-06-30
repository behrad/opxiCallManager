package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.PresenceService;
import com.basamadco.opxi.callmanager.entity.Presence;
import com.basamadco.opxi.callmanager.entity.UserAgent;

import javax.servlet.sip.SipServletRequest;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Jan 1, 2008
 *         Time: 4:34:24 PM
 */
public class AgentPublishHandler extends PublishHandler {

    private static final Logger logger = Logger.getLogger( AgentPublishHandler.class.getName() );


    private Map<String, PublishContext> publishContexts = new ConcurrentHashMap<String, PublishContext>();


    public AgentPublishHandler( UserAgent userAgent, PresenceService service ) {
        super( userAgent, service );
    }

    public void handlePublish( SipServletRequest publish ) throws OpxiException {
        PublishContext ctx;
        if (isNewPublish( publish )) {
            ctx = new PublishContext( publish, this );
            registerPublishContext( ctx );
        } else {
            ctx = resolvePresenceContext( publish );
        }
        try {
            ctx.service( publish );
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            removePresenceContext( publish );
            throw e;
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            removePresenceContext( publish );
            throw new OpxiException( e );
        }
    }

    public PublishContext getActiveContext() throws NoActivePublishContextException {
        Object[] ctxs = publishContexts.values().toArray();
        for (int i = 0; i < ctxs.length; i++) {
            PublishContext ctx = (PublishContext) ctxs[i];
            if ( ctx.isActive() ) {
                return ctx;
            }
        }
        throw new NoActivePublishContextException( this.toString() );
    }

    public boolean existsOtherActiveCtxThan( PublishContext context ) {
        for (PublishContext ctx : publishContexts.values() ) {
            if (ctx != context && ctx.isActive()) {
                return true;
            }
        }
        return false;
    }

    private void registerPublishContext( PublishContext ctx ) {
        logger.finest( "Register new PublishContext: " + ctx );
        publishContexts.put( ctx.getPublishRequest().getCallId(), ctx );
    }

    private PublishContext resolvePresenceContext( SipServletRequest publish ) throws OpxiException {
        PublishContext ctx = publishContexts.get( publish.getCallId() );
        logger.finest( "Resolve PublishContext: " + ctx );
        return ctx;
    }

    public void removePresenceContext( SipServletRequest publish ) throws OpxiException {
        PublishContext ctx = publishContexts.remove( publish.getCallId() );
        logger.finest( "Remove PublishContext: " + ctx );
        try {
            service.getServiceFactory().getLocationService().handleContactUnregistration( ctx.getPresence() );
        } catch( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        } finally {
            ctx.destroy();
            checkIfDestroyable();
        }
    }

    public synchronized void activeContextNotification( PublishContext activeContext ) {
        Iterator ctxs = publishContexts.values().iterator();
        while (ctxs.hasNext()) {
            PublishContext publishContext = (PublishContext) ctxs.next();
            if (publishContext != activeContext) {
                if (publishContext.isActive()) {
                    logger.finest( "Set context inactive: " + publishContext );
                    publishContext.setActive( false );
                }
            }
        }
    }

    public Presence getPresence() throws NoActivePublishContextException {
        return getActiveContext().getPresence();
    }

    private boolean isNewPublish( SipServletRequest publish ) {
        // TODO if this the correct logic or publish contact should also be considered?
        return !publishContexts.containsKey( publish.getCallId() );
    }    

    private synchronized void checkIfDestroyable() {
        if (publishContexts.size() == 0) {
            destroy();
        }
    }

    public String toString() {
        StringBuffer buff = new StringBuffer( "PresenceHandler[UA='" + getUserAgent().getAORString() + "']" );
        Object[] ctxs = publishContexts.values().toArray();
        for (int i = 0; i < ctxs.length; i++) {
            buff.append( "<br>+ " ).append( ctxs[i] );
        }
        return buff.toString();
    }

    public void destroy() {
        for(PublishContext ctx : publishContexts.values() ) {
            ctx.destroy();
        }
        publishContexts.clear();
        super.destroy();
    }

    public static String createId( UserAgent ua ) {
        return ua.getAORString();
    }
}
