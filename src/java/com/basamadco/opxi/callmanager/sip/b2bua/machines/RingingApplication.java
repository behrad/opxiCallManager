package com.basamadco.opxi.callmanager.sip.b2bua.machines;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.sip.b2bua.GenericB2BUA;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.URI;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * This B2BUA machine establishes a connection between a caller party in Ringing state
 * and a application at media server party.
 *  
 * @author Jrad
 *         Date: Nov 23, 2006
 *         Time: 1:54:56 PM
 */
public class RingingApplication extends GenericB2BUA {

    private static final Logger logger = Logger.getLogger( RingingApplication.class.getName() );


    protected URI getCalleeURI( SipServletRequest request ) throws OpxiException, ServletException {
        throw new IllegalStateException( "Invalid method called." );
    }

    protected String getUACRoleName() {
        return com.basamadco.opxi.callmanager.call.Leg.VOICE_APP;
    }


    protected void doCallee1xx( SipServletResponse callee1xx ) throws ServletException, IOException {
        // Just eat the 1xx from new application party!
        logger.finest( "Eating 1xx from application" );
    }

}