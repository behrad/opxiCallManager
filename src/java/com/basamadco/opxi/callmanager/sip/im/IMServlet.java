package com.basamadco.opxi.callmanager.sip.im;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.Registration;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.logging.AgentActivityLogger;
import com.basamadco.opxi.callmanager.pool.Agent;
import com.basamadco.opxi.callmanager.sip.OpxiSipServlet;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Apr 4, 2006
 *         Time: 2:53:05 PM
 */
public class IMServlet extends OpxiSipServlet {

    private static final Logger logger = Logger.getLogger( IMServlet.class.getName() );

    protected void doMessage( SipServletRequest request ) throws ServletException, IOException {
        try {
            /*Set<Registration> contacts = getServiceFactory().getLocationService().findRegistrations(
                    new UserAgent(request.getTo().getURI())
            );*/
            Registration reg =
                    getServiceFactory().getPresenceService().getPresence( new UserAgent( request.getTo().getURI() ) );
//            initialProxy( request, toContactURI(contacts) );
            initialProxy( request, reg.getLocation().getURI() );


            /* SipServletRequest im = getServiceFactory().getSipService().getSipFactory().createRequest( getSipFactory().createApplicationSession(),
                                "MESSAGE", request.getFrom().getURI(), request.getTo().getURI() );
                        if (request.getContentType() != null) {
                            im.setContent( request.getRawContent(), request.getContentType() );
                        }
                        im.setRequestURI( reg.getLocation().getURI() );
                        im.send();


                        SipServletRequest im2 = getServiceFactory().getSipService().getSipFactory().createRequest( getSipFactory().createApplicationSession(),
                                "MESSAGE", request.getFrom().getURI(), request.getTo().getURI() );
                        im2.setContent( "This is a machine generated IM", MIME_TEXT_PLAIN );
                        im2.setRequestURI( reg.getLocation().getURI() );
                        im2.send();
            */

//            getServiceFactory().getSipService().sendIM( reg, request.getRawContent().toString(), request.getContentType() );
//            getServiceFactory().getSipService().sendIM( reg, "This is a machine generated IM" );

        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            sendErrorResponse( request, e );
        }

        if ( request.getContentType().equalsIgnoreCase( MIME_TEXT_PLAIN ) ||
                request.getContentType().equalsIgnoreCase( MIME_TEXT_HTML ) ) {
            if ( request.getContentLength() > 0 ) {
                try {
                    Agent senderAgent = getServiceFactory().getAgentService().getAgentByAOR( request.getFrom().getURI().toString() );
                    AgentActivityLogger aal = (AgentActivityLogger)
                            getServiceFactory().getLogService().getAgentActivityLogger( senderAgent.getActivityLogId() );
                    aal.addIMService( request.getTo().getURI().toString(), request.getContent().toString() );

                    getServiceFactory().getLogService().getServiceActivityLogger().addInstantMessaging( request.getContentLength() );
                } catch ( Exception e ) {
                    logger.log( Level.SEVERE, e.getMessage(), e );
                }
            }
        }
    }

}
