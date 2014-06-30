package com.basamadco.opxi.callmanager.call.route;

import com.basamadco.opxi.callmanager.call.CallTarget;
import com.basamadco.opxi.callmanager.call.RejectTarget;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import com.basamadco.opxi.callmanager.sip.util.ApplicationConstants;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.SipServletResponse;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Nov 22, 2007
 *         Time: 12:43:19 PM
 */
public class DefaultNoUserRouter extends CallRouter {

    private static final Logger logger = Logger.getLogger( DefaultNoUserRouter.class.getName() );

    public CallTarget findRouteTarget( SipServletRequest request ) throws CallRouteException {
        String user = ((SipURI) request.getRequestURI()).getUser();
        String domain = ((SipURI) request.getRequestURI()).getHost();
        logger.finer("Received a call request for user=" + user + ", host=" + domain);
        if (domain.equals( OpxiToolBox.getLocalIP())) {
            domain = ApplicationConstants.DOMAIN;
        }
        if (OpxiToolBox.isEmpty(user) || OpxiToolBox.isEmpty(domain)) {
            return new RejectTarget( SipServletResponse.SC_NOT_ACCEPTABLE, "Empty Request URI" );
        }
        return null;
    }

    public String toString() {
        return "Empty Request URI Reject Router";
    }
}
