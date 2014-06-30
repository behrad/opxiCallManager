package com.basamadco.opxi.callmanager.logging;

import com.basamadco.opxi.callmanager.call.AbstractTimerContext;
import com.basamadco.opxi.callmanager.call.TimerException;
import com.basamadco.opxi.callmanager.ServiceFactory;
import com.basamadco.opxi.callmanager.util.PropertyUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Jan 15, 2008
 *         Time: 5:43:20 PM
 */
public class LogServiceTimer extends AbstractTimerContext {

    private final static Logger logger = Logger.getLogger(LogServiceTimer.class.getName());

    private final static String SERVICE_LOG_REPORT_RATE_HOUR = PropertyUtil.getProperty("opxi.logReport.serviceLog.report_rate");


    public LogServiceTimer( ServiceFactory serviceFactory, String id ) {
        super( serviceFactory, id );
        long service_report_period = Integer.parseInt(SERVICE_LOG_REPORT_RATE_HOUR) * 60 * 60 * 1000;
        Calendar firstRun = Calendar.getInstance();
        firstRun.set(Calendar.HOUR, firstRun.get(Calendar.HOUR) + 1);
        firstRun.set(Calendar.MINUTE, 0);
        firstRun.set(Calendar.SECOND, 0);
        long delay = firstRun.getTimeInMillis() - System.currentTimeMillis();
        setTimer( getServiceFactory().getSipService().getTimerService().createTimer(
                getServiceFactory().getSipService().getApplicationSession(),
                delay, service_report_period, true, false, this) );
        logger.finer("Created service logger timer for: " + new Date(getTimer().scheduledExecutionTime()));
    }

    public void timeout() throws TimerException {
        getServiceFactory().getLogService().restart();
    }
}
