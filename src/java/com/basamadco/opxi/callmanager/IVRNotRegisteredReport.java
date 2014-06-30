package com.basamadco.opxi.callmanager;

import com.basamadco.opxi.callmanager.call.AbstractTimerContext;
import com.basamadco.opxi.callmanager.call.TimerException;
import com.basamadco.opxi.callmanager.util.PropertyUtil;
import com.basamadco.opxi.callmanager.util.ResourceBundleUtil;
import com.basamadco.opxi.callmanager.sip.registrar.RegistrationNotFoundException;

import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Jan 22, 2009
 *         Time: 11:11:14 AM
 */
public class IVRNotRegisteredReport extends AbstractTimerContext {

    private static final Logger logger = Logger.getLogger(IVRNotRegisteredReport.class.getName());

    private static final long ivrReportInterval = Integer.parseInt( PropertyUtil.getProperty("opxi.callmanager.ivr.notRegisteredReport.interval") );



    protected IVRNotRegisteredReport( ServiceFactory serviceFactory, String id ) {
        super( serviceFactory, id );
        report();
    }

    public void timeout() throws TimerException {
        report();
    }

    private void report() {
        try {
            getServiceFactory().getLocationService().findRegistrations( MediaService.defaultMediaServerUA );
            cancel();
        } catch ( RegistrationNotFoundException e ) {
            getServiceFactory().getSipService().sendAdminIM(
                    ResourceBundleUtil.getMessage( "callmanager.admin.ivr.unavailable" )
            );
            getServiceFactory().getSipService().sendAdminIM(
                    ResourceBundleUtil.getMessage( "callmanager.admin.ivr.reRegister" )
            );
            setTimer( getServiceFactory().getSipService().getTimerService().createTimer(
                    getServiceFactory().getSipService().getApplicationSession(),
                    ivrReportInterval * 1000,
                    false,
                    this
            )
            );
        }

    }
}
