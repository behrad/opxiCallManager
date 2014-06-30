package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.ServiceFactory;
import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.call.AbstractTimerContext;
import com.basamadco.opxi.callmanager.call.TimerException;
import com.basamadco.opxi.callmanager.entity.Subscription;
import com.basamadco.opxi.callmanager.util.PropertyUtil;

import javax.servlet.sip.SipServletRequest;

/**
 * @author Jrad
 *         Date: Dec 17, 2007
 *         Time: 2:43:27 PM
 */
public class SubscriptionTimer extends AbstractTimerContext {


    private static final int SUBSCRIBE_TIMER_THRESHHOLD = Integer.parseInt(
            PropertyUtil.getProperty("opxi.callmanager.sip.presence.publishTimerThreshHold"));


    private SubscriptionContext ctx;

    public SubscriptionTimer( ServiceFactory serviceFactory, SubscriptionContext ctx ) {
        super(serviceFactory, ctx.getSubscribeRequest().getCallId());
        this.ctx = ctx;
        setTimer(serviceFactory.getSipService().getTimerService().createTimer(
                ctx.getInitiatorSession(),
                (ctx.getSubscribeRequest().getExpires() * 1000) + SUBSCRIBE_TIMER_THRESHHOLD * 1000, false, this
        )
        );
    }

    public void timeout() throws TimerException {
        try {
            getServiceFactory().getPresenceService().unsubscribe( ctx.getSubscription() );
        } catch (OpxiException e) {
            throw new TimerException(e);
        }
    }
}
