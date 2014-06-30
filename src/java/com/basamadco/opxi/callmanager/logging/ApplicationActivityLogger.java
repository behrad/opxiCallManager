package com.basamadco.opxi.callmanager.logging;

import com.basamadco.opxi.activitylog.schema.ApplicationService;
import com.basamadco.opxi.activitylog.schema.InService;
import com.basamadco.opxi.activitylog.schema.ServiceTime;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

/**
 * @author Jrad
 *         Date: Aug 29, 2006
 *         Time: 10:29:25 AM
 */
public class ApplicationActivityLogger extends ChildActivityLogger {

    private ApplicationService appService;

    public ApplicationActivityLogger( OpxiActivityLogger parent, String name ) {
        super( parent );
        appService = new ApplicationService();
        appService.setName( name );
        appService.setServiceTime( (ServiceTime)parent.initStatistics( new ServiceTime() ) );
        appService.setInService( (InService)parent.initStatistics( new InService() ) );
        addStatistics( ServiceTime.class, SummaryStatistics.newInstance() );
        addStatistics( InService.class, SummaryStatistics.newInstance() );
    }

    public void addServiceTime( long time ) {
        if( time > 0 ) {
            getStatistics( ServiceTime.class ).addValue( time/1000f );
            appService.setServiceTime( (ServiceTime)StatisticsTransformer.getInstance().transform(
                                    getStatistics( ServiceTime.class ), appService.getServiceTime() ) );
        }
    }

    public ApplicationService getApplicationService() {
        return appService;
    }

}
