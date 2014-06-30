package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.call.AbstractTimerContext;
import com.basamadco.opxi.callmanager.call.TimerException;
import com.basamadco.opxi.callmanager.ServiceFactory;
import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.util.PropertyUtil;

import javax.servlet.sip.SipServletRequest;
import java.util.logging.Logger;
import java.io.IOException;

/**
 * @author Jrad
 *         Date: Jan 21, 2007
 *         Time: 5:13:57 PM
 */
public class PresenceTimer extends AbstractTimerContext {

    private static final Logger logger = Logger.getLogger(PresenceTimer.class.getName());

    private static final int PUBLISH_TIMER_THRESHHOLD = Integer.parseInt(
            PropertyUtil.getProperty("opxi.callmanager.sip.presence.publishTimerThreshHold"));


    private PublishContext context;


    public PresenceTimer(ServiceFactory serviceFactory, PublishContext context) {
        super(serviceFactory, context.getPublishRequest().getCallId());
        this.context = context;
        setTimer(serviceFactory.getSipService().getTimerService().createTimer(
                context.getApplicationSession(),
                (context.getPublishRequest().getExpires() * 1000) + PUBLISH_TIMER_THRESHHOLD * 1000, false, this
        )
        );
    }

    public SipServletRequest getPublishRequest() {
        return context.getPublishRequest();
    }


    public void timeout() throws TimerException {
        PresenceEvent event = new TimeoutPresenceEvent( context, getPublishRequest() );
        try {
            if( context.isActive() ) {
                event.handleActiveEvent();
            } else {
                event.handlePassiveEvent();
            }
        } catch ( OpxiException e ) {
            throw new TimerException( e );
        } catch ( IOException e ) {
            throw new TimerException( e );
        }
    }

}
