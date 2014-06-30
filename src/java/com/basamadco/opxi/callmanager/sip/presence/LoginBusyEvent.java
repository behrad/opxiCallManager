package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import com.basamadco.opxi.callmanager.util.ResourceBundleUtil;

import javax.servlet.sip.SipServletRequest;
import java.io.IOException;

/**
 * @author Jrad
 *         Date: Jul 8, 2008
 *         Time: 6:01:16 PM
 */
public class LoginBusyEvent extends BusyPublishEvent {

    public LoginBusyEvent( PublishContext ctx, SipServletRequest publish ) {
        super( ctx, publish );
    }

    public void handleActiveEvent() throws OpxiException, IOException {
        getPresence().setComment( "Available Busy Publish Received" );
        getPresenceService().getServiceFactory().getLocationService().handleContactRegistration( getPresence() );

        getPresenceService().getServiceFactory().getAgentService().assignToPool( getPresence().getUserAgent() );
        getContext().sendOK();
        getPresenceService().notifyPresenceFromPublish( getPresence(), getContext().getPublishRequest() );

        UserAgent manager = new UserAgent(
                BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO().getManagerNameFor( getPresence().getUserAgent().getName() ),
                OpxiToolBox.getLocalDomain()
        );
        getPresenceService().getServiceFactory().getSipService().sendPresenceIM( manager,
                ResourceBundleUtil.getMessage( "callmanager.presence.loginBusy.manager", getPresence().getUserAgent().getName() )
        );

        if ( getContext().isNew() ) {
            String logId = getPresenceService().getServiceFactory().getAgentService().getAgentForUA(
                    getPresence().getUserAgent() ).getActivityLogId();
            getPresenceService().getServiceFactory().getLogService().getAgentActivityLogger( logId ).addPresence( getPresence() );
        }
    }
}
