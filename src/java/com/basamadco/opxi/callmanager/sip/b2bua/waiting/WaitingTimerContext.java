package com.basamadco.opxi.callmanager.sip.b2bua.waiting;

import com.basamadco.opxi.callmanager.ServiceFactory;
import com.basamadco.opxi.callmanager.call.CallService;
import com.basamadco.opxi.callmanager.queue.Queue;
import com.basamadco.opxi.callmanager.call.AbstractTimerContext;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Nov 18, 2006
 *         Time: 11:05:51 AM
 */
public class WaitingTimerContext extends AbstractTimerContext {

    private static final Logger logger = Logger.getLogger(WaitingTimerContext.class.getName());


    private com.basamadco.opxi.callmanager.call.CallService callInWait;


    public WaitingTimerContext(ServiceFactory serviceFactory, CallService call) {
        super(serviceFactory, call.getId());
        callInWait = call;
    }

    public void timeout() throws com.basamadco.opxi.callmanager.call.TimerException {
        try {

            Queue queue = getServiceFactory().getQueueManagementService().queueForName(callInWait.getHandlerQueueName());
            queue.maxCallWaitTimeReached(callInWait);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new com.basamadco.opxi.callmanager.call.TimerException(e);
        }
    }

}
