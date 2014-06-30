package com.basamadco.opxi.callmanager.call.route;

import com.basamadco.opxi.callmanager.call.CallTarget;
import com.basamadco.opxi.callmanager.entity.EntityNotExistsException;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipURI;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Nov 19, 2007
 *         Time: 12:26:52 PM
 */
public class ByNameRouter extends CallRouter {

    private static final Logger logger = Logger.getLogger( ByNameRouter.class.getName() );

    public CallTarget findRouteTarget( SipServletRequest request ) throws CallRouteException {
        String user = ( (SipURI) request.getRequestURI() ).getUser();
        try {
            return BaseDAOFactory.getDirectoryDAOFactory().getCallTargetDAO().getCallTargetByName( user );
        } catch ( EntityNotExistsException e ) {
            logger.fine( "No target found for name '" + user + "': " + e.getMessage() );
        } catch ( Exception e ) {
            logger.severe( e.getMessage() );
        }
        return null;
    }

    public String toString() {
        return "By-Name Directory Based Call Target Router";
    }
}
