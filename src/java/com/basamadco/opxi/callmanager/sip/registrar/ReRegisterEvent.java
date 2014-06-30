package com.basamadco.opxi.callmanager.sip.registrar;

import com.basamadco.opxi.callmanager.OpxiException;

import javax.servlet.sip.SipServletResponse;
import java.util.logging.Logger;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: AM
 * Date: Dec 10, 2007
 * Time: 11:00:10 PM
 */
public class ReRegisterEvent extends RegistrarEvent {

    private static final Logger logger = Logger.getLogger( ReRegisterEvent.class.getName() );

    public ReRegisterEvent( RegistrationContext registrationContext ) {
        super( registrationContext );
    }

    public void service() throws OpxiException, IOException {
        logger.info( "ReRegister event '" + getContext().getRegistration() + "'" );        
        getRegistration().setComment( "re-Registration recieved." );
        getServiceFactory().getLocationService().handleRefreshContact( getRegistration() );
        SipServletResponse response = getContext().getRecentRegisterMessage().createResponse( SipServletResponse.SC_OK );
        response.addHeader( CONTACT, getContext().getRecentRegisterMessage().getHeader( CONTACT ) );
        response.send();
    }

}
