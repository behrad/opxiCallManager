package com.basamadco.opxi.callmanager.sip.im;

import com.basamadco.opxi.callmanager.sip.OpxiSipServlet;
import com.basamadco.opxi.callmanager.entity.Registration;
import com.basamadco.opxi.callmanager.MediaService;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.ServletException;
import java.util.logging.Logger;
import java.io.IOException;

/**
 * @author Jrad
 *         Date: Apr 13, 2009
 *         Time: 4:42:44 PM
 */
public class MediaServerRobot extends OpxiSipServlet {

    private final static Logger logger = Logger.getLogger( MediaServerRobot.class.getName() );


    protected void doMessage( SipServletRequest request ) throws ServletException, IOException {
        if ( request.getContentLength() > 0 ) {
            if ( request.getContentType().startsWith( MIME_TEXT_PLAIN ) ) {
                request.createResponse( SipServletResponse.SC_OK ).send();
                String command = request.getContent().toString();
                int aliveCalls = Integer.parseInt( command );
                getServiceFactory().getMediaService().serverUpdate(
                        new Registration( MediaService.defaultMediaServerUA, request.getAddressHeader( CONTACT ) )
                        , aliveCalls );
            }
        }
    }

}
