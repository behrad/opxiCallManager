package com.basamadco.opxi.callmanager.logging;

import com.basamadco.opxi.activitylog.schema.Calls;
import com.basamadco.opxi.activitylog.schema.TrunkCalls;
import com.basamadco.opxi.activitylog.schema.TrunkSvc;
import com.basamadco.opxi.activitylog.schema.TrunkUsage;
import com.basamadco.opxi.callmanager.call.Trunk;
import com.basamadco.opxi.callmanager.call.CallService;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import java.util.logging.Logger;


/**
 * Created by IntelliJ IDEA.
 * User: AM
 * Date: Oct 15, 2007
 * Time: 12:14:28 AM
 */
public class TrunkActivityLogger extends AbstractTrunkActivityLogger {

    private static final Logger logger = Logger.getLogger( ServiceActivityLogger.class.getName() );

    public TrunkActivityLogger( OpxiActivityLogger parent ) {
        super( parent );
        TrunkSvc ts = parent.getLogVO().getServiceActivity().getTrunkSvc();
        if ( ts != null ) {
            if ( ts.getTrunkCalls() == null ) {
                ts.setTrunkCalls( (TrunkCalls) parent.initStatistics( new TrunkCalls() ) );
                addStatistics( TrunkCalls.class, SummaryStatistics.newInstance() );
            }
        }
    }

    public TrunkSvc getTrunkSvc() {
        return parent.getLogVO().getServiceActivity().getTrunkSvc();
        //TODO :  this method must throw an exception when return value is null.
    }

    public void setTrunkSvc( TrunkSvc ts ) {
        parent.getLogVO().getServiceActivity().setTrunkSvc( ts );
    }

    public void logTrunkUsage( CallService cs ) {

        Trunk t = (Trunk) cs.getTarget();
        TrunkUsage tu = getOrAddTrunkUsage( t );

        tu.setAttempt( tu.getAttempt() + 1 );
        getTrunkSvc().setAttempt( getTrunkSvc().getAttempt() + 1 );

        long duration = cs.duration();
        float time = duration / 1000f;

        if ( time > 0 ) {

            getStatistics( TrunkCalls.class ).addValue( time );

            setTrunkCalls( (TrunkCalls) StatisticsTransformer.getInstance().transform(
                    getStatistics( TrunkCalls.class ), getTrunkCalls() ) );

            getStatistics( getTrunkId( tu.getName() ) ).addValue( time );

            tu.setCalls( (Calls) StatisticsTransformer.getInstance().transform(
                    getStatistics( getTrunkId( tu.getName() ) ), tu.getCalls() ) );

        }
    }

}
