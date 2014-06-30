package com.basamadco.opxi.callmanager.sip.b2bua;

import com.basamadco.opxi.callmanager.call.CallService;
import com.basamadco.opxi.callmanager.ServiceFactory;

import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Nov 18, 2006
 *         Time: 11:27:21 AM
 */
public class ReferTransferTimerContext extends com.basamadco.opxi.callmanager.call.AbstractTimerContext {

    private static final Logger logger = Logger.getLogger(ReferTransferTimerContext.class.getName());


    private CallService transferee;

    public ReferTransferTimerContext(ServiceFactory serviceFactory, com.basamadco.opxi.callmanager.call.CallService call) {
        super(serviceFactory, call.getId());
        transferee = call;
    }


    public void timeout() throws com.basamadco.opxi.callmanager.call.TimerException {
        transferee.teardown("Refer transfer invite timeout!");
    }

}
