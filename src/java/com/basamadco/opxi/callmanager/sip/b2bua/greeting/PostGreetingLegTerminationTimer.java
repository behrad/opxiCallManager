package com.basamadco.opxi.callmanager.sip.b2bua.greeting;

import com.basamadco.opxi.callmanager.ServiceFactory;
import com.basamadco.opxi.callmanager.call.CallService;
import com.basamadco.opxi.callmanager.call.Leg;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import com.basamadco.opxi.callmanager.util.PropertyUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Sep 5, 2006
 *         Time: 4:15:17 PM
 */
public class PostGreetingLegTerminationTimer extends com.basamadco.opxi.callmanager.call.AbstractTimerContext {

    private static final Logger logger = Logger.getLogger(PostGreetingLegTerminationTimer.class.getName());


    private static final int TIMEOUT = Integer.parseInt(PropertyUtil.getProperty("opxi.callmanager.greeting.IVRtransferUser.timeout"));

    private CallService callService;


    public PostGreetingLegTerminationTimer(ServiceFactory sf, CallService callService) {
        super(sf, callService.getInitialRequest().getCallId());
        this.callService = callService;
        setTimer(sf.getSipService().getTimerService().createTimer(
                callService.getInitialRequest().getApplicationSession(), TIMEOUT * 1000, false, this)
        );
    }

    public com.basamadco.opxi.callmanager.call.CallService getCallService() {
        return callService;
    }

    public void timeout() throws com.basamadco.opxi.callmanager.call.TimerException {
        try {
            com.basamadco.opxi.callmanager.call.Leg ivrLeg = getCallService().getLeg(com.basamadco.opxi.callmanager.call.Leg.IVR_TRANSFER_AGENT);
            ivrLeg.setLegTimer(null);
            ivrLeg.terminate();
        } catch (Throwable e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            throw new com.basamadco.opxi.callmanager.call.TimerException(e);
        }
    }

    public String toString() {
        return OpxiToolBox.unqualifiedClassName(getClass()) + ": " + callService.getId();
    }


    public void cancel() {
        com.basamadco.opxi.callmanager.call.Leg ivrLeg = getCallService().getLeg(Leg.IVR_TRANSFER_AGENT);
        ivrLeg.setLegTimer(null);
        super.cancel();
    }
}
