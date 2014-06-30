package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.ServiceFactory;

import javax.servlet.sip.ServletTimer;

/**
 * @author Jrad
 *         Date: Nov 18, 2006
 *         Time: 11:10:33 AM
 */
public abstract class AbstractTimerContext implements TimerContext {

    private ServletTimer timer;

    private ServiceFactory serviceFactory;

    private String id;

    protected AbstractTimerContext(ServiceFactory serviceFactory, String id) {
        this.serviceFactory = serviceFactory;
        this.id = id;
    }

    protected ServiceFactory getServiceFactory() {
        return serviceFactory;
    }


    public ServletTimer getTimer() {
        return timer;
    }

    public void setTimer(ServletTimer timer) {
        this.timer = timer;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void cancel() {
        if (getTimer() != null) {
            getTimer().cancel();
        }
    }

}
