package com.basamadco.opxi.callmanager.web;

import com.basamadco.opxi.callmanager.OpxiException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Jrad
 *         Date: Apr 4, 2009
 *         Time: 1:57:47 PM
 */
public class WaitingRoomReportServlet extends OpxiHttpServlet {

    private static final Logger logger = Logger.getLogger( WaitingRoomReportServlet.class.getName() );

    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        request.setCharacterEncoding( "UTF-8" );
        response.setContentType( "text/xml" );
        PrintWriter out = response.getWriter();

        String callId = request.getParameter( "callId" );

        String queueId = request.getParameter( "queueId" );

        try {
            int index = getServiceFactory().getQueueManagementService().queueForName( queueId ).getPendingCallIndex( callId );


            out.print( XML_TEMPLATE.replaceAll( QUEUE_ID, queueId ).replaceAll( CALLID, callId ).
                    replaceAll( INDEX, String.valueOf( index ) ) );

        } catch ( OpxiException e ) {
            logger.severe( e.getMessage() );
            out.print(
                    XML_TEMPLATE_ERROR.replaceAll( QUEUE_ID, queueId ).replaceAll( CALLID, callId ).
                            replaceAll( ERROR_MSG, e.getMessage() )
            );
        }
        out.flush();
    }


    private static final String QUEUE_ID = "queueId";

    private static final String CALLID = "callId";

    private static final String ERROR_MSG = "error-msg";

    private static final String INDEX = "index";


    private static final String XML_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "\n" +
            "<Queue id=\"" + QUEUE_ID + "\">" +
            "\t<PendingCallIndex id=\"" + CALLID + "\">" + INDEX + "</PendingCallIndex>" +
            "</Queue>";


    private static final String XML_TEMPLATE_ERROR = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "\n" +
            "<Error queue-Id=\"" + QUEUE_ID + "\" call-Id=\"" + CALLID + "\"><![CDATA[" +
            ERROR_MSG +
            "]]></Error>";
}
