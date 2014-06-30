package com.basamadco.opxi.callmanager.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Jul 3, 2006
 *         Time: 5:10:34 PM
 */
public class ActivityLogServlet extends OpxiHttpServlet {

    private static final Logger logger = Logger.getLogger( ActivityLogServlet.class.getName() );

    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        try {
            String logVOId = request.getParameter( "logVOId" );
            Class loggerClass = Class.forName( request.getParameter( "type" ).trim() );
            response.setContentType( "text/xml" );
            response.setCharacterEncoding( "UTF8" );
            PrintWriter xml = response.getWriter();
            if (loggerClass.getName().equals( "com.basamadco.opxi.callmanager.logging.AgentActivityLogger" )) {
                getServiceFactory().getLogService().getAgentActivityLogger( logVOId ).marshal( xml );
            } else {
                getServiceFactory().getLogService().getServiceActivityLogger().marshal( xml );
            }
            xml.flush();
        } catch ( Exception e ) {
            request.setAttribute( "exception", e );
            getServletContext().getRequestDispatcher( "/error.jsp" ).forward( request, response );
        }
    }

}
