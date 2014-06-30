package com.basamadco.opxi.callmanager.sip.registrar;

import com.basamadco.opxi.callmanager.OpxiException;

import java.util.logging.Logger;
import java.io.IOException;

/**
 * @author Jrad
 *         Date: Jan 26, 2008
 *         Time: 9:43:51 PM
 */
public class RecoverRegisterEvent extends RegistrarEvent {

    private static final Logger logger = Logger.getLogger( RecoverRegisterEvent.class.getName() );

    private String callId;

    public RecoverRegisterEvent( RegistrationContext registrationContext, String callId ) {
        super( registrationContext );
        this.callId = callId;
    }

    public void service() throws OpxiException, IOException {
        logger.info( "Recover previous orphan register event in context: '" + getRegistration() + "'" );
        getRegistration().setComment( "lost registration renewal" );
        getServiceFactory().getLocationService().handleRecoverRegister( getRegistration(), callId );
    }
    
}
