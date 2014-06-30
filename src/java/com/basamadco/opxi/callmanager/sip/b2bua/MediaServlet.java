package com.basamadco.opxi.callmanager.sip.b2bua;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.call.Leg;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.URI;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Jul 25, 2006
 *         Time: 12:13:38 PM
 */
public class MediaServlet extends GenericB2BUA {

    private static final Logger logger = Logger.getLogger(MediaServlet.class.getName());

    protected URI getCalleeURI(SipServletRequest request) throws OpxiException, ServletException {
        /*String greetVxml = ((SipURI)request.getRequestURI()).getParameter( "mediaURI" ); //should be for now transferVXML
        logger.finer( greetVxml );*/
        throw new IllegalStateException("This Service is disabled for now...");
//        return getServiceFactory().getMediaService().getVoiceAppURI( greetVxml );
    }

    protected String getUACRoleName() {
        return Leg.VOICE_APP;
    }

}
