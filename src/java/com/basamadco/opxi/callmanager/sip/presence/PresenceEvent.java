package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.PresenceService;
import com.basamadco.opxi.callmanager.entity.Presence;
import com.basamadco.opxi.callmanager.sip.util.SIPConstants;

import javax.servlet.sip.SipServletRequest;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * @author Jrad
 *         Date: Nov 1, 2007
 *         Time: 9:40:58 AM
 */
public abstract class PresenceEvent implements SIPConstants {


    private static final Logger logger = Logger.getLogger( PresenceEvent.class.getName() );


    protected SipServletRequest publish;

    protected PublishContext presenceContext;

    protected Presence newPresence;


    public abstract void handleActiveEvent() throws OpxiException, IOException;


    public void handlePassiveEvent() throws OpxiException, IOException {
        passiveEventAction();
    }


    public abstract void setActive();


    public abstract void setStatus() throws OpxiException;


    public PresenceEvent( PublishContext presenceContext, SipServletRequest publish ) {
        this.presenceContext = presenceContext;
        this.publish = publish;
        this.newPresence = new Presence( presenceContext.getHandler().getUserAgent() );
        this.newPresence.setId( publish.getCallId() );
        try {
            presenceContext.extractPresenceFromMessage( publish, newPresence );
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    protected void passiveEventAction() throws OpxiException {
        getPresence().setComment( "Inactive Publish Mode" );
        getContext().sendInactiveResponse();
    }


    public SipServletRequest getPublish() {
        return publish;
    }

    public void setPublish( SipServletRequest publish ) {
        this.publish = publish;
    }

    public Presence getPresence() {
//        return newP
        return getContext().getPresence();
    }

    public PublishContext getContext() {
        return presenceContext;
    }

    public PresenceService getPresenceService() {
        return presenceContext.getPresenceService();
    }

}