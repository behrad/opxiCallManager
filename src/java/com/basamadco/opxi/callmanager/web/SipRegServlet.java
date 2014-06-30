package com.basamadco.opxi.callmanager.web;

import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author Jrad
 *         Date: Jun 29, 2008
 *         Time: 11:15:56 AM
 */
public class SipRegServlet extends SipServlet {

    protected void doRegister( SipServletRequest sipServletRequest ) throws ServletException, IOException {
        System.out.println( "BILBILAK..." );
        sipServletRequest.createResponse( 200 ).send();
    }
    
}
