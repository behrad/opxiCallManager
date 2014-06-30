package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.PresenceService;
import com.basamadco.opxi.callmanager.sip.listener.ApplicationSessionManager;
import com.basamadco.opxi.callmanager.sip.AbstractContext;
import com.basamadco.opxi.callmanager.entity.Subscription;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipApplicationSession;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Dec 17, 2007
 *         Time: 2:41:17 PM
 */
public class SubscriptionContext extends PresenceContext {

    private static final Logger logger = Logger.getLogger( SubscriptionContext.class.getName() );
    

    private Subscription subscription;

    private SipServletRequest subscribeRequest;

    private SubscriptionTimer timer;

    private PresenceService service;

    private SipApplicationSession initiatorSession;


    public SubscriptionContext( Subscription subscription, SipServletRequest subscribe, PresenceService service ) {
        this.subscription = subscription;
        this.subscribeRequest = subscribe;
        this.service = service;
        this.initiatorSession = subscribe.getApplicationSession();
        getInitiatorSession().setAttribute( AbstractContext.class.getName(), this );
    }

    public void createTimer() {
        timer = new SubscriptionTimer( service.getServiceFactory(), this );
    }

    public void cancelTimer() {
        timer.cancel();
    }

    public void refreshTimer( SipServletRequest subscribe ) {
        this.subscribeRequest = subscribe;
        timer.cancel();
        timer = new SubscriptionTimer( service.getServiceFactory(), this );
    }

    public SipServletRequest getSubscribeRequest() {
        return subscribeRequest;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public SipApplicationSession getInitiatorSession() {
        return initiatorSession;
    }

    public void destroy() {
        cancelTimer();
        getInitiatorSession().removeAttribute( AbstractContext.class.getName() );
//        getInitiatorSession().invalidate();
    }

    public String toString() {
        return "SubscriptionContext[subscriber='" + subscription.getSubscriber().getName() + "', notifier='"
                + subscription.getNotifier().getName() + "', callId='" + subscribeRequest.getCallId() + "']";
    }


}