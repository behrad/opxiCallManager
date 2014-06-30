package com.basamadco.opxi.callmanager.sip.registrar;


import com.basamadco.opxi.callmanager.OpxiException;

import javax.servlet.sip.SipServletResponse;
import java.util.logging.Logger;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: AM
 * Date: Dec 10, 2007
 * Time: 10:53:06 PM
 */
public class RegisterEvent extends RegistrarEvent {

    private static final Logger logger = Logger.getLogger( RegisterEvent.class.getName() );

    public RegisterEvent( RegistrationContext registrationContext ) {
        super( registrationContext );
    }

    public void service() throws OpxiException, IOException {
        logger.info( "Register event: '" + getContext().getRegistration() + "'" );
        getRegistration().setComment( "Registration recieved." );
        getServiceFactory().getLocationService().handleContactRegistration( getRegistration() );
        SipServletResponse response = getRegister().createResponse( SipServletResponse.SC_OK );
        response.addHeader( CONTACT, getRegister().getHeader( CONTACT ) );
        response.send();
        String logId = getServiceFactory().getAgentService().getAgentForUA( getRegistration().getUserAgent() ).getActivityLogId();
        getServiceFactory().getLogService().getAgentActivityLogger( logId ).addRegistration( getRegistration() );
        getContext().sendWelcomeMessage();
    }

}
