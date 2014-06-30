package com.basamadco.opxi.callmanager.web;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.ServiceFactory;
import com.basamadco.opxi.callmanager.sip.util.ApplicationConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.sip.SipFactory;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Jun 28, 2006
 *         Time: 2:56:15 PM
 */
public class OpxiHttpServlet extends HttpServlet implements ApplicationConstants {

    private static final Logger logger = Logger.getLogger( OpxiHttpServlet.class.getName() );


    private static final String SIP_FACTORY = "javax.servlet.sip.SipFactory";

    private Object getAttribute( String attr_name ) throws ServletException {
        if( getServletContext().getAttribute( attr_name ) != null )
            return getServletContext().getAttribute( attr_name );
        throw new ServletException( "No " + attr_name + " context attribute bound to OpxiHttpServlet" );
    }

    /**
     * Returns Sip Servlet spec. SipFactory object for
     * opxiCallManager context.
     *
     * @return Current initialized SipFactory object in the
     * opxiCallManager context
     * @throws ServletException
     */
    protected SipFactory getSipFactory() throws ServletException {
        return (SipFactory)getAttribute( SIP_FACTORY );
    }

    protected ServiceFactory getServiceFactory() throws OpxiException {
        try {
            return (ServiceFactory)getAttribute( SERVICE_FACTORY );
        } catch ( ServletException e ) {
            throw new OpxiException( e );
        }
    }

}
