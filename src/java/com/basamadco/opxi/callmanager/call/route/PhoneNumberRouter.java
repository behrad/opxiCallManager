package com.basamadco.opxi.callmanager.call.route;

import com.basamadco.opxi.callmanager.call.CallTarget;
import com.basamadco.opxi.callmanager.entity.EntityNotExistsException;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;

import javax.servlet.sip.SipURI;
import javax.servlet.sip.SipServletRequest;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Nov 19, 2007
 *         Time: 11:43:43 AM
 */
public class PhoneNumberRouter extends CallRouter {

    private static final Logger logger = Logger.getLogger( PhoneNumberRouter.class.getName() );

    public CallTarget findRouteTarget( SipServletRequest request ) throws CallRouteException {
        String user = ((SipURI) request.getRequestURI()).getUser();
        if ( OpxiToolBox.isPhoneNumber( user ) ) {
            try {
                CallTarget target = BaseDAOFactory.getDirectoryDAOFactory().getCallTargetDAO().
                        getCallTargetByPhoneNumber(user);
                return target;
            } catch ( EntityNotExistsException e ) {
                logger.finer("No entity matched for phone number: '" + user + "'" );
            } catch ( Exception e ) {
                logger.severe( e.getMessage() );
            }
        }
        return null;
    }

    public String toString() {
        return "Phone Number Directory Based Call Target Router";
    }
}
