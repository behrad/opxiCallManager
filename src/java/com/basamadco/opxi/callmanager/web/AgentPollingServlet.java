package com.basamadco.opxi.callmanager.web;

import com.basamadco.opxi.callmanager.sip.util.SipUtil;
import com.basamadco.opxi.callmanager.OpxiException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.IOException;

/**
 * @author Jrad
 *         Date: Apr 19, 2010
 *         Time: 1:11:24 PM
 */
public class AgentPollingServlet extends OpxiHttpServlet {

    private static final Logger logger = Logger.getLogger( AgentPollingServlet.class.getName() );

    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        request.setCharacterEncoding( "UTF-8" );
        String callerId = request.getParameter( "callerId" ).trim().replaceAll( "\"", "" );
        String agent = request.getParameter( "agent" ).trim().replaceAll( "\"", "" );
        String poll = request.getParameter( "poll" ).trim().replaceAll( "\"", "" );
        request.setAttribute( "msg", "" );


        try {
            String logId = getServiceFactory().getAgentService().getAgentByAOR( agent ).getActivityLogId();
            getServiceFactory().getLogService().getAgentActivityLogger( logId ).addPoll( callerId, poll );
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            request.setAttribute( "msg", e.getMessage() );
        }
        request.getRequestDispatcher( "/agentReport.jsp" ).forward( request, response );
    }


    protected void doPost( HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse ) throws ServletException, IOException {
        doGet( httpServletRequest, httpServletResponse );
    }

}
