package com.basamadco.opxi.callmanager.sip.b2bua.waiting;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.call.CallService;
import com.basamadco.opxi.callmanager.call.Leg;
import com.basamadco.opxi.callmanager.call.LegState;
import com.basamadco.opxi.callmanager.queue.Queue;
import com.basamadco.opxi.callmanager.sip.b2bua.GenericB2BUA;
import com.basamadco.opxi.callmanager.util.PropertyUtil;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.URI;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueuedServlet extends GenericB2BUA {

    private static final Logger logger = Logger.getLogger(QueuedServlet.class.getName());

    private static final int maxOverflows = Integer.parseInt(PropertyUtil.getProperty("opxi.callmanager.queue.maxOverflowCalls"));

    /**
     * A local scoped VOICE_APP load balancer which is only used for queue wait message play back.
     * TODO There should be a more granulated and centeralized load balacing policy available.
     */
//    private static final Map overflowedCalls = new ConcurrentHashMap();

    private static volatile int maxOverflowCallCount;

    protected URI getCalleeURI(SipServletRequest request) throws ServletException, OpxiException {
        /*String wr_uri = getCallService( request.getSession() ).getWaitingRoom();
        try {
            return getServiceFactory().getMediaService().getWaitingRoomMediaURI( wr_uri, null, null ).toString();
        } catch( ServletParseException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new OpxiException( e.getMessage() );
        }*/
        throw new IllegalStateException("Depricated Method!!!");
    }

    protected String getUACRoleName() {
        return com.basamadco.opxi.callmanager.call.Leg.WAITING_MEDIA;
    }

    protected void doInitialInvite(SipServletRequest request) throws ServletException, IOException {
        CallService call = getCallService(request.getSession());
        if (call.getState() == CallService.REJECTED) {
            if (maxOverflowCallCount < maxOverflows) {
                logger.finer("Adding overflowed call: " + maxOverflowCallCount);
                maxOverflowCallCount++;
                setIsOverflowCall(request.getSession());
            } else {
                logger.finer("Rejecting overflowed call: " + call);
//                terminate( front, "Media Resource Busy" );
                getLeg(request.getSession()).setState(LegState.IDLE);
                request.createResponse(SipServletResponse.SC_BUSY_HERE).send();
                return;
            }
        }
        try {
            Queue queue = getServiceFactory().getQueueManagementService().queueForName(call.getHandlerQueueName());
            super.doInitialInvite(request);
        } catch (OpxiException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            sendErrorResponse(request, e);
            getLeg(request.getSession()).setState(LegState.IDLE);
//            terminate( front, e.getMessage() );
        }
    }

    protected void doCallerAck(SipServletRequest callerAck) throws ServletException, IOException {
        super.doCallerAck(callerAck);
        Leg caller = getLeg(callerAck.getSession());
        CallService call = caller.getCallService();
        if (call.getState() == CallService.QUEUED) {
            call.setState(CallService.WAITING);
            if (call.getHandlerAgent() != null) { // Call is marked for some agent in schedule phase
                call.setState(CallService.ASSIGNED);
                // TODO so bad :~( !
                try {
                    call.transfer(caller.getRoleName(), getGreetingURI());
                } catch (OpxiException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    logger.severe("What to do know?");
                }
            }
        }
    }

    protected void doCallerBye(SipServletRequest callerBye) throws ServletException, IOException {
        if (isOverflowCall(callerBye.getSession())) {
            maxOverflowCallCount--;
            logger.finer("Removing overflowed call: " + maxOverflowCallCount);
        }
        super.doCalleeBye(callerBye);
    }

    protected void doCalleeBye(SipServletRequest calleeBye) throws ServletException, IOException {
        if (isOverflowCall(calleeBye.getSession())) {
            maxOverflowCallCount--;
            logger.finer("Removing overflowed call: " + maxOverflowCallCount);
        }
        super.doCalleeBye(calleeBye);
    }

    private void setIsOverflowCall(SipSession leg) {
        leg.setAttribute("overflowedCall", Boolean.TRUE);
    }

    private boolean isOverflowCall(SipSession leg) {
        Boolean yesOrNo = (Boolean) leg.getAttribute("overflowedCall");
        if (yesOrNo != null) {
            return yesOrNo.booleanValue();
        }
        return false;
    }

    /*protected void doErrorToRefer(SipServletResponse response) throws IOException, ServletException {
        logger.finer("caller couldn't refer to callee, handle QMS state here!");
//        logCallState( response.getSession() );
        CallService call = getCallService(response.getSession());
        throw new IllegalStateException("You should clear call in error/REFER: " + response.getCallId());
//        getLeg( response.getSession() ).setState( LegState.IDLE );
//        terminate( front, "Couldn't Transfer: " + response.getStatus() + " to refer." );
    }*/

    protected void doNotify(SipServletRequest InWaitLegNotify) throws ServletException, IOException {
        // To reply to refer incomming notify messages
        String eventHeader = InWaitLegNotify.getHeader(EVENT).trim();
        eventHeader = eventHeader.split(";")[0];
        if (eventHeader.equalsIgnoreCase(REFER)) {
            String msg = new String((byte[]) InWaitLegNotify.getContent(), UTF8);
//            logger.finer( "Notify Message: " + msg );
            if (msg.indexOf(String.valueOf(SipServletResponse.SC_REQUEST_TERMINATED)) > 0
                    || msg.indexOf(String.valueOf(SipServletResponse.SC_BUSY_HERE)) > 0
                    || msg.indexOf(String.valueOf(SipServletResponse.SC_TEMPORARLY_UNAVAILABLE)) > 0
                    ) {
                doErrorTransferNotification(InWaitLegNotify);
            } else if (msg.indexOf(String.valueOf(SipServletResponse.SC_OK)) > 0) {
                doSuccessTransferNotification(InWaitLegNotify);
            }
            InWaitLegNotify.createResponse(SipServletResponse.SC_OK).send();
        } else {
            InWaitLegNotify.createResponse(489, "Event Package Not Supported").send(); // Bad Event! RFC 3265
        }
    }

    protected void doSuccessTransferNotification(SipServletRequest InWaitLegNotify) throws ServletException, IOException {
        // force original call tear down.        
        logger.finer("Receive 200/NOTIFY with 200/OK body when transfering from waiting room: let's don't force BYE message to both peers - " + InWaitLegNotify.getCallId());
        // The relay of 200/NOTIFY should force other leg to force BYE! but IVR doesn't understand this (REFER was generated internally by call manager)
        // TRY to postpone basic leg termination after when transferee to agent INVITE came in
        // Commented in 6133 (when using astrix)
//        try {
//            Leg inWait = getLeg( InWaitLegNotify.getSession() );
//            inWait.getPeer().terminate();
//            inWait.terminate();
//        } catch( IllegalStateException e ) {
//            logger.warning( "Try to terminate waiting call legs failed: " + e );
//        }
    }

    protected void doErrorTransferNotification(SipServletRequest InWaitLegNotify) throws ServletException, IOException {
//        getLeg( InWaitLegNotify.getSession() ).getCallService().teardown( "Transfer Canceled" );

        // Cisco gateway is not surely sending a BYE to "in waiting" leg
        Leg inWait = getLeg(InWaitLegNotify.getSession());
        Leg waiting = inWait.getPeer();
        inWait.terminate();
        waiting.terminate();
    }

}
