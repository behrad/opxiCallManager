package com.basamadco.opxi.callmanager.sip.front;

import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import java.io.IOException;

/**
 * @author Jrad
 *         Date: Apr 4, 2006
 *         Time: 3:14:52 PM
 */
public class TelURIFront extends Front {

    private final static Logger logger = Logger.getLogger( TelURIFront.class.getName() );

    protected void doInitialInvite( SipServletRequest request ) throws ServletException, IOException {
//        try {
//            CallService call = getCallService( request.getSession() );
//            String telNumber = ( (TelURL)request.getRequestURI() ).getPhoneNumber();
//            DirectoryDAOFactory daof = ( DirectoryDAOFactory) BaseDAOFactory.getDAOFactory( BaseDAOFactory.DIRECTORY );
//            DirectoryEntity target = daof.getQueueTargetDAO().searchByPhoneNumber( telNumber );
//            if( !target.isGroup() ) {
//                logger.finer( "Number is for Agent: " + target.getCN() );
//                request.setRequestURI( getSipFactory().createSipURI( target.getCN(), DOMAIN ) );
//                call.setState( CallService.PROXY );
//                doProxy( request );
//            } else {
//                logger.finer( "Number is for Group: " + target.getCN() );
//                call.setState( CallService.QUEUE );
//                // A very simple front type matching between group and queue
//                call.setHandlerQueueName( daof.getQueueTargetDAO().getAttributeValue( target.getCN(), "displayName" ) );
//                doQueue( request );
//            }
//        } catch( OpxiException e ) {
//            logger.severe( "Couldn't handleCallFor incomming call request ", e );
//            sendErrorResponse( request, e );
//        }
    }

}
