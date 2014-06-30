package com.basamadco.opxi.callmanager.sip.front;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.call.*;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * @author Jrad
 *         Date: Jul 17, 2006
 *         Time: 10:18:29 AM
 */
public class PostGreetingFront extends Front {

    private static final Logger logger = Logger.getLogger( PostGreetingFront.class.getName() );

    public static final String GREETING_CTX_CALLID_PARAM = "opxi-greeting-callId";

    protected void doInitialInvite( SipServletRequest invite ) throws ServletException, IOException {
        CallService call = null;
        try {
            String callId = invite.getAddressHeader( TO ).getParameter( GREETING_CTX_CALLID_PARAM );
            /*GreetingContext ctx = getServiceFactory().getMediaService().releaseGreetingContext( ctxId );
            invite.getSession().setAttribute( PostGreetingServlet.GREETING_CTX_ID_PARAM, ctx );*/
            call = CallServiceFactory.getCallService( callId );
            call.addPostGreetingLeg( invite );
            if ( call.getTransferTimer() != null ) {
                call.getTransferTimer().cancel();
            }
//            CallService call = getLeg( SipSessionManager.getByCallId( ctx.getCallerId() ) ).getCallService();
//            call.addPostGreetingLeg( invite );
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, "Transfer to agent failed: " + e, e );
            sendErrorResponse( invite, e );
        } catch ( CallAlreadyDisposedError e ) {
            logger.log( Level.SEVERE, "Transfer to agent failed: " + e, e );
            sendErrorResponse( invite, e );
        } catch ( IllegalStateException e ) {
            logger.log( Level.SEVERE, "Transfer to agent failed: " + e, e );
            sendErrorResponse( invite, e );
        }
    }

}
