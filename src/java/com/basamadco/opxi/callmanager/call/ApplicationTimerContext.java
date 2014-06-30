package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.ServiceFactory;
import com.basamadco.opxi.callmanager.sip.SipCallController;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Dec 2, 2006
 *         Time: 5:11:23 PM
 */
public class ApplicationTimerContext extends AbstractTimerContext {

    private static final Logger logger = Logger.getLogger(ApplicationTimerContext.class.getName());

    private ApplicationIntegrationContext ctx;

    private boolean timeout = false;


    public ApplicationTimerContext(ApplicationIntegrationContext ctx, long timeout) {
        super(ctx.getApplication().getServiceFactory(), ctx.getCall().getId());
        this.ctx = ctx;
        setTimer(ctx.getApplication().getServiceFactory().getSipService().getTimerService().createTimer(
                ctx.getApplication().getServiceFactory().getSipService().getApplicationSession(), timeout, false, this
        )
        );
        logger.finest("Created application integration timer for: " + new Date(getTimer().scheduledExecutionTime()));
        logger.finest("Created application integration timer INFO: " + getTimer().getInfo());
        logger.finest("Created application integration timer AppSession: " + getTimer().getApplicationSession());

    }

    public void timeout() throws TimerException {
        setTimeout(true);
        logger.finest("ApplicationTimerContext timeout. Re-evaluate...");
        try {
            if (ctx.evaluate()) { //Check if application integration rule still evaluates TRUE
                CallController cc = new SipCallController(ctx.getCall());
                try {
                    ctx.getCall().setApplicationContext(ctx);
                    cc.involveApplications();
                } catch (CallServiceException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    throw new TimerException(e);
                }
            }
        } catch (ApplicationIntegrationException e) {
            throw new TimerException(e);
        }
    }

    public void destroy() {
        if (getTimer() != null) {
            getTimer().cancel();
        }
    }


    public boolean getTimeout() {
        return timeout;
    }

    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }
}
