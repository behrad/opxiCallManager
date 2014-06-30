package com.basamadco.opxi.callmanager.call.route;

import com.basamadco.opxi.callmanager.call.CallTarget;
import com.basamadco.opxi.callmanager.entity.EntityNotExistsException;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.sip.util.SIPConstants;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipURI;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Nov 19, 2007
 *         Time: 11:30:48 AM
 */
public class TrunkRouter extends CallRouter implements SIPConstants {

    private static final Logger logger = Logger.getLogger( TrunkRouter.class.getName() );

    public CallTarget findRouteTarget( SipServletRequest request ) throws CallRouteException {
        String user = ((SipURI) request.getRequestURI()).getUser();
        try {
            if ( getServiceFactory().getLocationService().isRegisteredContact( request.getAddressHeader( CONTACT ) ) ) {
                if ( OpxiToolBox.isPhoneNumber( user ) ) {
                    CallTarget target = BaseDAOFactory.getDirectoryDAOFactory().getCallTargetDAO().getCallTargetByPattern( user );
                    if ( getServiceFactory().getAgentService().getAgentByAOR(
                            request.getFrom().getURI().toString()
                    ).hasTrunkAccess( target.getName() ) ) {
                        target.setTelephoneNumber( user );
                        logger.finer( "Trunk exists for dialed number: " + user );
                        return target;
                    } else {
                        throw new CallRouteException( "Outgoing Calls Not Allowed" );
                    }
                }
            } else {
                throw new CallRouteException( "No Registrations Found: Trunk Usage Disabled" );
            }
        } catch ( EntityNotExistsException e ) {
            logger.finer( "No trunk matched for number '" + user + "': " + e.getMessage() );
        } catch ( CallRouteException e ) {
            throw e;
        } catch ( Exception e ) {
            logger.severe( e.getMessage() );
        }
        return null;
    }

    public String toString() {
        return "Trunk Pattern Directory Based Call Target Router";
    }
}
