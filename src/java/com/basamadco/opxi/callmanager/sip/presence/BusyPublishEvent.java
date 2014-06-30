package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.sip.util.SIPConstants;
import com.basamadco.opxi.callmanager.entity.Presence;

import javax.servlet.sip.SipServletRequest;

/**
 * @author Jrad
 *         Date: Nov 1, 2007
 *         Time: 10:46:46 AM
 */
public abstract class BusyPublishEvent extends OpenPublishEvent {


    public BusyPublishEvent( PublishContext ctx, SipServletRequest publish ) {
        super( ctx, publish );
    }

    public void setActive() {
        getPresence().setActive( false );
    }


}
