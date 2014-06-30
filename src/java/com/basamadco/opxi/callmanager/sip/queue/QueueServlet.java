package com.basamadco.opxi.callmanager.sip.queue;

import com.basamadco.opxi.callmanager.sip.CallServlet;
import com.basamadco.opxi.callmanager.util.PropertyUtil;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * A simple Queue manager able of different hunting policies.
 *
 * @author Jrad
 */
public class QueueServlet extends CallServlet {

    private static final Logger logger = Logger.getLogger( QueueServlet.class.getName() );


    public static final String NO_AGENT_APPLICATION =
            PropertyUtil.getProperty( "opxi.callmanager.queue.agentNotAvailableApp.phoneNumber" );


    protected void doInitialInvite( SipServletRequest request ) throws ServletException, IOException {
        /*UASLeg caller = (UASLeg)getLeg( request.getSession() );
        CallService call = caller.getCallService();
        Queue queue = null;
        try {
            if( call.getState() != CallService.QUEUE ) {
                call.setState( CallService.REJECTED );
                caller.reject( 500, "Invalid Call State '" + call.getStateString() + "'" );
            } else {
                queue = getServiceFactory().getQueueManagementService().queueForName( call.getHandlerQueueName() );

                //TODO calls pend when a budy agent goes offline and we have a positive queue sive
                if( !queue.doesApplicationInvolve( call ) ) {
//                    if( !queue.hasPendingCalls() ) {
//                        logger.finer( queue.getName() + " has no pending calls." );
                        queue.assignAgent( call );
                        doGreeting( request );
//                    } else {
//                        if( queue.isCallOverflowed() ) {
//                            logger.finer( queue.getName() + " is overflowed. (queue has pending calls)" );
//                            call.setState( CallService.REJECTED );
//                            caller.reject( SipServletResponse.SC_BUSY_HERE, queue.getName() + " Overflowed" );
//                        } else {
//                            logger.finer( queue.getName() + " is pending new call.(queue has pending calls)" );
//                            call.createWaitTimer( getTimerService(), queue.getMaxCallWaitTime() );
//                            queue.pend( call );
//                            doWait( request );
//                        }
//                    }
                }
            }
        } catch ( HuntingException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            call.setState( CallService.REJECTED );
            caller.reject( SipServletResponse.SC_SERVER_INTERNAL_ERROR, e.getMessage() );
        } catch ( NoIdleAgentException e ) {
            if( queue.isCallOverflowed() ) { // queueDepth==0
                logger.finer( queue.getName() + " is rejecting overflowed call for NoIdleAgent." );
                call.setState( com.basamadco.opxi.callmanager.call.CallService.REJECTED );
                caller.reject( SipServletResponse.SC_BUSY_HERE, queue.getName() + " is rejecting overflowed call for NoIdleAgent." );
            } else {
                logger.finer( queue.getName() + " is pending call for NoIdeAgent." );
                call.createWaitTimer( getTimerService(), queue.getMaxCallWaitTime() );
                queue.pend( call );
                doWait( request );
            }
        } catch( NoPoolAvailableException e ) {
            logger.warning( e.getMessage() );
            call.setState( CallService.REJECTED );
            //TODO send to a custom voice menu
            *//*SipCallController scc = new SipCallController();
            scc.involveApplications( call );*//*

            try {
                CallTarget target = BaseDAOFactory.getDirectoryDAOFactory().
                        getCallTargetDAO().getCallTargetByPhoneNumber( NO_AGENT_APPLICATION );
                target.setServiceFactory( getServiceFactory() );
                target.setRequest( call.getInitialRequest() );
                call.setTarget( target );
                target.service( call );
//                call.addProxyTarget( target.getTargetURIs() );
//                call.setState( CallService.PROXY );
//                doProxy( request );
            } catch (OpxiException e1) {
                caller.reject( SipServletResponse.SC_BUSY_HERE, "No Agent Available" );
                logger.log( Level.SEVERE, e.getMessage(), e1 );
            }

//            caller.reject( SipServletResponse.SC_BUSY_HERE, "No Agent Available" );
            try {
                getServiceFactory().getLogService().getServiceActivityLogger().getOrAddQueueActivityLogger( queue.getName() )
                        .addUnsuccessfulService( "No Agent Available", "No agent available to handle calls." );
            } catch ( ActivityLogNotExistsException e1 ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
            }
        } catch ( QueueNotExistsException e ) {
            try {
                getServiceFactory().getLogService().getServiceActivityLogger()
                        .addUnsuccessfulHandling( "Queue Not Found", "Please check opxi.log for callee information." );
            } catch ( ActivityLogNotExistsException e1 ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
            }
            logger.warning( e.getMessage() );
            call.setState( com.basamadco.opxi.callmanager.call.CallService.REJECTED );
            caller.reject( SipServletResponse.SC_NOT_FOUND, call.getHandlerQueueName() + " Not Found" );
        } catch( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            //TODO Should we dispose calls here? or after rejecting!?
            call.setState( com.basamadco.opxi.callmanager.call.CallService.REJECTED );
            caller.reject( 500, e.getMessage() );
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            call.setState( CallService.REJECTED );
            caller.reject( 500, e.getMessage() );
        }*/
    }
}