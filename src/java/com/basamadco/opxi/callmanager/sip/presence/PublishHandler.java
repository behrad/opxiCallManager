package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.PresenceService;
import com.basamadco.opxi.callmanager.entity.Presence;
import com.basamadco.opxi.callmanager.entity.UserAgent;

import javax.servlet.sip.SipServletRequest;
import java.io.IOException;

/**
 * @author Jrad
 *         Date: Feb 2, 2008
 *         Time: 3:49:58 PM
 */
public abstract class PublishHandler {

    protected UserAgent userAgent;

    protected PresenceService service;

    public PublishHandler( UserAgent userAgent, PresenceService service ) {
        this.userAgent = userAgent;
        this.service = service;
    }

    public abstract void handlePublish( SipServletRequest publish ) throws OpxiException;


    public abstract Presence getPresence() throws NoActivePublishContextException;


    public PresenceService getPresenceService() {
        return service;
    }

    protected UserAgent getUserAgent() {
        return userAgent;
    }

    public void destroy() {
        service.removePublishHandler( this.getUserAgent() );
    }

}
