package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.Presence;
import com.basamadco.opxi.callmanager.sip.util.SIPConstants;

import javax.servlet.sip.SipServletRequest;

/**
 * @author Jrad
 *         Date: Nov 1, 2007
 *         Time: 10:01:19 AM
 */
public abstract class OpenPublishEvent extends PresenceEvent implements SIPConstants {


    public OpenPublishEvent( PublishContext ctx, SipServletRequest publish ) {
        super( ctx, publish );
    }

    public static PresenceEvent createSpecificEvent( PublishContext ctx, SipServletRequest newPublish ) {
        if ( !ctx.isNew() ) {
            if ( ctx.getPresence().getBasic().equalsIgnoreCase( SIPConstants.BASIC_STATUS_CLOSED ) ) {
                // agent was closed already
                return new LoginBusyEvent( ctx, newPublish );
            } else {
                if ( Presence.getActivityCode( ctx.getActivity( newPublish ) ) == Presence.ACTIVITY_ON_THE_PHONE ) {
                    return new OnThePhoneEvent( ctx, newPublish );
                }
                if ( ctx.getPresence().isActive() ) { // was available
                    return new BusyEvent( ctx, newPublish );
                } else {
                    return new BusyBusyEvent( ctx, newPublish );
                }
            }
        } else {
            return new LoginBusyEvent( ctx, newPublish );
        }
    }

    public void setStatus() throws OpxiException {
//        getPresence().setBasic( getContext().getStateBasic( publish ) );
//        getPresence().setNote( getContext().getStateNote( publish, getPresence().getBasic() ) );
    }
}
