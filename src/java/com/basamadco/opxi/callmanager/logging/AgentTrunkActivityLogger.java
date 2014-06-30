package com.basamadco.opxi.callmanager.logging;

import com.basamadco.opxi.activitylog.schema.*;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: AM
 * Date: Oct 18, 2007
 * Time: 2:10:43 AM
 */
public class AgentTrunkActivityLogger extends AbstractTrunkActivityLogger {

    private static final Logger logger = Logger.getLogger( ServiceActivityLogger.class.getName() );

    public AgentTrunkActivityLogger( OpxiActivityLogger parent ) {
        super( parent );
        TrunkSvc ts = parent.getLogVO().getAgentActivity().getTrunkSvc();
        if (ts != null) {
            if (ts.getTrunkCalls() == null) {
                ts.setTrunkCalls( (TrunkCalls) parent.initStatistics( new TrunkCalls() ) );
                addStatistics( TrunkCalls.class, SummaryStatistics.newInstance() );
            }
        }
    }

    protected void addTrunk( TrunkUsage tu, com.basamadco.opxi.callmanager.call.CallService cs ) {
        addTrunk( tu, cs.getTarget().getTelephoneNumber(), new Date( cs.getConnectTime() ), cs.duration() );
    }

    protected TrunkSvc getTrunkSvc() {
//        return _trunkSvc;
        return parent.getLogVO().getAgentActivity().getTrunkSvc();
        //TODO :  this method must throw an exception when return value is null.
    }

    protected void setTrunkSvc( TrunkSvc ts ) {
//        _trunkSvc = ts;
        parent.getLogVO().getAgentActivity().setTrunkSvc( ts );
    }

    private void addTrunk( TrunkUsage tu, String target, Date dt, long duration ) {

        Trunk t = new Trunk();
        float time = duration / 1000f;

        t.setTarget( target );
        t.setStartTime( dt );
        t.setDuration( time );

        tu.addTrunk( t );
        tu.setAttempt( tu.getAttempt() + 1 );
        getTrunkSvc().setAttempt( getTrunkSvc().getAttempt() + 1 );

        if (time != 0) {

            getStatistics( TrunkCalls.class ).addValue( time );
            getTrunkSvc().setTrunkCalls( (TrunkCalls) StatisticsTransformer.getInstance().transform(
                    getStatistics( TrunkCalls.class ), getTrunkSvc().getTrunkCalls() ) );

            getStatistics( getTrunkId( tu.getName() ) ).addValue( time );
            tu.setCalls( (Calls) StatisticsTransformer.getInstance().transform(
                    getStatistics( getTrunkId( tu.getName() ) ), tu.getCalls() ) );

        }
    }

    public void logTrunkUsage( com.basamadco.opxi.callmanager.call.CallService cs ) {

        com.basamadco.opxi.callmanager.call.Trunk t = (com.basamadco.opxi.callmanager.call.Trunk) cs.getTarget();
        TrunkUsage tu = getOrAddTrunkUsage( t );
        addTrunk( tu, cs );
    }
}
