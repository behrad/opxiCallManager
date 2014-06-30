package com.basamadco.opxi.callmanager.sip.listener;

import com.basamadco.opxi.callmanager.call.TimerContext;
import com.basamadco.opxi.callmanager.call.TimerException;

import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.TimerListener;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Apr 9, 2006
 *         Time: 11:47:08 AM
 */
public class CallManagerTimerListener implements TimerListener {

    private static final Logger logger = Logger.getLogger(CallManagerTimerListener.class.getName());


    public void timeout(ServletTimer timer) {
        logger.finer("Timout - timer context type: " + timer.getInfo().getClass().getName());
        if (timer.getInfo() instanceof TimerContext) {
            logger.finer("Timout - timer context Id: " + ((TimerContext) timer.getInfo()).getId());
            try {
                ((TimerContext) timer.getInfo()).timeout();
            } catch (TimerException e) {
                logger.severe("Failed to execute timer: " + e.getMessage());
            }
        } else {
            logger.severe("Don't know what to do! Timer object not supported: " + timer.getInfo().getClass().getName());
        }
    }

}
