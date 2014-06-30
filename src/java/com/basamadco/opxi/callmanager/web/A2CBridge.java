package com.basamadco.opxi.callmanager.web;

import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import com.basamadco.opxi.callmanager.util.ResourceBundleUtil;
import com.basamadco.opxi.crm.CRMAccessModule;
import com.basamadco.opxi.crm.OpxiCRMFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * User: LSG
 * Date: Oct 21, 2006
 * Time: 1:37:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class A2CBridge extends OpxiHttpServlet {

    private static final Logger logger = Logger.getLogger( A2CBridge.class.getName() );


    public static final String CALLER_DISPLAYNAME_PARAMETER_NAME = "caller_displayname";
    public static final String CALLER_SIPUSERNAME_PARAMETER_NAME = "caller_sipusername";
    public static final String CALLER_SIPDOMAIN_PARAMETER_NAME = "caller_sipdomain";
    public static final String ACTION = "A2CBridge";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = null;
        CRMAccessModule crm = OpxiCRMFactory.getSystemCRMAccessModule();
        
        String display_name = request.getParameter(CALLER_DISPLAYNAME_PARAMETER_NAME);
        String user_name = request.getParameter(CALLER_SIPUSERNAME_PARAMETER_NAME);
        String domain = request.getParameter(CALLER_SIPDOMAIN_PARAMETER_NAME);
        
        logger.finest( "Domain: '" + domain + "'" );   
        logger.finest( "Username: '" + user_name + "'" );
        logger.finest( "Display Name: '" + display_name + "'" );

        if ( domain.equalsIgnoreCase( OpxiToolBox.getLocalDomain() ) ) {
            //local domain
            logger.finest("Is local domain: TRUE" );
            url = crm.getAccountUrlInDomain( user_name );
        } else {
            logger.finest("Is local domain: FALSE, search based on user's phone number." );
            url = crm.getAccountUrl( display_name, user_name );
        }
        if( url != null ) {
            response.sendRedirect( url );
        } else {
            request.setAttribute( "msg", ResourceBundleUtil.getMessage( "callmanager.crm.profileNotFound" ) );
            request.getRequestDispatcher( "/customerNotFound.jsp" ).forward( request, response );
            logger.log( Level.SEVERE, "Couldn't resolve callee url" );
        }
    }


    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        doGet( httpServletRequest, httpServletResponse );
    }
}
