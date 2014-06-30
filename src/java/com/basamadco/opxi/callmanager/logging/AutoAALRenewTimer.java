package com.basamadco.opxi.callmanager.logging;

import com.basamadco.opxi.callmanager.call.AbstractTimerContext;
import com.basamadco.opxi.callmanager.call.TimerException;
import com.basamadco.opxi.callmanager.util.PropertyUtil;
import com.basamadco.opxi.callmanager.ServiceFactory;

import java.util.logging.Logger;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Jrad
 *         Date: Mar 9, 2009
 *         Time: 1:50:54 PM
 */
public class AutoAALRenewTimer extends AbstractTimerContext {

    private final static Logger logger = Logger.getLogger( AutoAALRenewTimer.class.getName() );

    private final static String AAL_LOG_REPORT_RATE_HOUR = PropertyUtil.getProperty( "opxi.logReport.aal.renewal.interval" );

    private final static String AAL_LOG_REPORT_TIME = PropertyUtil.getProperty( "opxi.logReport.aal.renewal.time" );


    public AutoAALRenewTimer( ServiceFactory serviceFactory, String id ) {
        super( serviceFactory, id );
        long service_report_period = Integer.parseInt( AAL_LOG_REPORT_RATE_HOUR ) * 60 * 60 * 1000;
        Calendar firstRun = Calendar.getInstance();
        firstRun.set( Calendar.AM_PM, Calendar.AM );
        firstRun.set( Calendar.HOUR, 0 );
        firstRun.set( Calendar.MINUTE, 0 );
        firstRun.set( Calendar.SECOND, 0 );
        firstRun.set( Calendar.DATE, firstRun.get( Calendar.DATE ) + 1 );
        long delay = firstRun.getTimeInMillis() - System.currentTimeMillis();
        setTimer( getServiceFactory().getSipService().getTimerService().createTimer(
                getServiceFactory().getSipService().getApplicationSession(),
                delay, service_report_period, true, false, this ) );
        logger.finer( "Created auto AAL renewal timer for: " + new Date( getTimer().scheduledExecutionTime() ) );
    }

    public void timeout() throws TimerException {
        getServiceFactory().getLogService().reNewAgentActivityLoggers( getServiceFactory().getAgentService().agents() );
    }
}
