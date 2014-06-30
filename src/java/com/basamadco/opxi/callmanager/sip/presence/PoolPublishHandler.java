package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.PresenceService;
import com.basamadco.opxi.callmanager.entity.Presence;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.sip.util.SIPConstants;

import javax.servlet.sip.SipServletRequest;
import java.io.IOException;

/**
 * @author Jrad
 *         Date: Feb 2, 2008
 *         Time: 3:54:33 PM
 */
public class PoolPublishHandler extends PublishHandler {

    private Presence presence;


    public PoolPublishHandler( Presence presence, PresenceService service ) {
        super( presence.getUserAgent(), service );
        setPresence( presence );
    }

    public void handlePublish( SipServletRequest publish ) throws OpxiException {
        getPresenceService().notifyPresence( getPresence() );
        if( getPresence().getBasic().equalsIgnoreCase( SIPConstants.BASIC_STATUS_CLOSED ) ) {
            destroy();
        }
    }

    public Presence getPresence() {
        return presence;
    }

    public void setPresence( Presence presence ) {
        this.presence = presence;
    }

    public String toString() {
        return "PresenceHandler[UA='" + getUserAgent() + "']";
    }
}
