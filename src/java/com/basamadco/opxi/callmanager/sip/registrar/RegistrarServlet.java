package com.basamadco.opxi.callmanager.sip.registrar;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.Registration;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.sip.OpxiSipServlet;
import com.basamadco.opxi.callmanager.sip.UAC;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;

import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Registrar Sip Servlet
 *
 * @author Jrad
 * @version 1.0
 */
public class RegistrarServlet extends OpxiSipServlet {

    private static final Logger logger = Logger.getLogger( RegistrarServlet.class.getName() );

    /**
     * Called by sip requests with method=REGISTER to handleCallFor
     * a basic SIP REGISTER functionality.
     *
     * @param register SipServletRequest
     * @throws ServletException
     */
    protected void doRegister( SipServletRequest register ) throws ServletException, IOException {
        logger.finest( "A registration request recieved. Call-ID = " + register.getCallId() );
        try {
            getServiceFactory().getRegistrarService().service( register );
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            sendErrorResponse( register, e );
        }
    }

    /**
     * Just new/refresh Register requests are validated
     */
    public boolean toValidate( SipServletRequest request ) throws ServletException {
        return !( RegisterUtility.isCertainRemoval( request ) || RegisterUtility.isAllRemoval( request ) );
    }

}
