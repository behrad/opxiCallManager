package com.basamadco.opxi.callmanager.web;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.logging.AgentActivityLogger;
import com.basamadco.opxi.callmanager.sip.listener.SipSessionManager;
import com.basamadco.opxi.callmanager.call.Leg;
import com.basamadco.opxi.callmanager.call.CallService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Jrad
 *         Date: Apr 18, 2006
 *         Time: 4:13:33 PM
 */
public class WebTransfer extends OpxiHttpServlet {

    // Sample query:
    // http://localhost/opxiCallManager/transfer?callId=1234&toURI=http%3a//192.168.128.30/opxiCallManager/vxml?name=greet.xml

    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        String callId = request.getParameter( "callId" ).trim().replaceAll( "\"", "" );
        String target = request.getParameter( "target" ).trim().replaceAll( "\"", "" );
        try {
            Leg leg = (Leg) SipSessionManager.getByCallId( callId ).getAttribute( Leg.class.getName() );
            CallService call = leg.getCallService();
//            CallService call = CallServiceFactory.getCallService( callId );
            call.transfer( Leg.CALLER, getSipFactory().createURI( target ) );
            // Agent has triggered a transfer
            try {
                String aalId = getServiceFactory().getAgentService().getAgentByAOR( call.getHandlerAgent() ).getActivityLogId();
                AgentActivityLogger aal = getServiceFactory().getLogService().getAgentActivityLogger( aalId );
                aal.addTransferService( call.getId(), target );
            } catch ( OpxiException e ) {
                e.printStackTrace();
            }
            request.setAttribute( "msg", "A call transfer action is triggered in Opxi Call Manager." );
        } catch ( Throwable e ) {
            e.printStackTrace();
            request.setAttribute( "msg", e.getMessage() );
        }
        request.getRequestDispatcher( "/transfer.jsp" ).forward( request, response );
    }

}
