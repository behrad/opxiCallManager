package com.basamadco.opxi.callmanager.sip.registrar;

import com.basamadco.opxi.callmanager.OpxiException;

import javax.servlet.sip.SipServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: AM
 * Date: Dec 10, 2007
 * Time: 11:29:36 PM
 */
public class UnregisterEvent extends RegistrarEvent {

    private static final Logger logger = Logger.getLogger( UnregisterEvent.class.getName() );

    public UnregisterEvent( RegistrationContext registrationContext ) {
        super( registrationContext );
    }

    public void service() throws OpxiException, IOException {
        logger.info( "UnRregister event '" + getRegistration() + "'" );
        try {
            getRegistration().setComment( "un-Registration recieved." );
            getServiceFactory().getLocationService().handleContactUnregistration( getRegistration() );
            getServiceFactory().getRegistrarService().removeContextFor( getRegistration() );
        } catch ( OpxiException e ) {
            logger.warning( "Could not handle agent service un-register for '" + getRegistration()
                    + "': " +  e.getMessage() );
        } finally {            
            SipServletResponse response = getRegister().createResponse( SipServletResponse.SC_OK );
            response.setExpires( 0 );
            response.send();
        }
    }

}
