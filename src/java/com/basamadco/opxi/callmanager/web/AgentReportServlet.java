package com.basamadco.opxi.callmanager.web;

import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
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
 *         Date: Oct 10, 2009
 *         Time: 10:35:19 AM
 */
public class AgentReportServlet extends OpxiHttpServlet {

    private static final Logger logger = Logger.getLogger( AgentReportServlet.class.getName() );

    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        request.setCharacterEncoding( "UTF-8" );
        String itemId = request.getParameter( "item" ).trim().replaceAll( "\"", "" );
        String agent = request.getParameter( "agent" ).trim().replaceAll( "\"", "" );
        request.setAttribute( "msg", "" );

        String[] ids = itemId.split( ";" );
        try {
            String logId = getServiceFactory().getAgentService().getAgentByAOR( SipUtil.toSipURIString( agent ) ).getActivityLogId();
            getServiceFactory().getLogService().getAgentActivityLogger( logId ).addAgentReportItem( ids );
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            request.setAttribute( "msg", e.getMessage() );
        }

        for ( String id : ids ) {
            try {

                String msg = ((String) request.getAttribute( "msg" ));
                if ( msg != null ) {
                    msg += "<br>added item id=" + id;
                }
                request.setAttribute( "msg", msg );
            } catch ( Throwable e ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
                request.setAttribute( "msg", e.getMessage() );
            }
        }
        request.getRequestDispatcher( "/agentReport.jsp" ).forward( request, response );
    }


    protected void doPost( HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse ) throws ServletException, IOException {
        doGet( httpServletRequest, httpServletResponse );
    }

}
