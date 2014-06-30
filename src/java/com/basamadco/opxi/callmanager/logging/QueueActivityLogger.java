package com.basamadco.opxi.callmanager.logging;

import com.basamadco.opxi.activitylog.schema.*;
import com.basamadco.opxi.callmanager.util.LockManager;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Aug 24, 2006
 *         Time: 10:55:34 AM
 */
public class QueueActivityLogger extends ChildActivityLogger {

    private static final Logger logger = Logger.getLogger( QueueActivityLogger.class.getName() );

    private QueueService queueService;

    private TimedVariableStatistics onlineAgents = new TimedVariableStatistics();

    private TimedVariableStatistics callsWaiting = new TimedVariableStatistics();


    public QueueActivityLogger( OpxiActivityLogger parent, String name ) {
        super( parent );
        queueService = new QueueService();
        queueService.setCallAttempts( 0 );
        queueService.setName( name );

        addStatistics( CallServiceTime.class, SummaryStatistics.newInstance() );
        addStatistics( CallsWaiting.class, SummaryStatistics.newInstance() );
        addStatistics( CallWaitTime.class, SummaryStatistics.newInstance() );
        addStatistics( OnlineAgents.class, SummaryStatistics.newInstance() );

        queueService.setCallServiceTime( (CallServiceTime) parent.initStatistics( new CallServiceTime() ) );
        queueService.setCallsWaiting( (CallsWaiting) parent.initStatistics( new CallsWaiting() ) );
        queueService.setCallWaitTime( (CallWaitTime) parent.initStatistics( new CallWaitTime() ) );
        queueService.setOnlineAgents( (OnlineAgents) parent.initStatistics( new OnlineAgents() ) );
    }

    public void incCallAttempts() {
        queueService.setCallAttempts( queueService.getCallAttempts() + 1 );
    }

    public void addCallServiceTime( long time ) {
        if ( time > 0 ) {
            getStatistics( CallServiceTime.class ).addValue( time / 1000f );
            queueService.setCallServiceTime( (CallServiceTime) StatisticsTransformer.getInstance().transform(
                    getStatistics( CallServiceTime.class ), queueService.getCallServiceTime() ) );
        }
//        parent.updateStatistics( queueService.getCallServiceTime(), time/1000f );
    }

    public void updateCallsWaiting( int curCallsWaiting ) {
        /*getStatistics(CallsWaiting.class).addValue(curCallsWaiting);
        queueService.setCallsWaiting((CallsWaiting) StatisticsTransformer.getInstance().transform(
                getStatistics(CallsWaiting.class), queueService.getCallsWaiting()));*/
        callsWaiting.addSample( curCallsWaiting );
        queueService.getCallsWaiting().setCount( curCallsWaiting );
        queueService.getCallsWaiting().setMax( callsWaiting.getMax() );
        queueService.getCallsWaiting().setMin( callsWaiting.getMin() );
        queueService.getCallsWaiting().setMean( callsWaiting.getAverage() );
    }

    /*public void incCallsWaiting() {
        int callsWaiting = queueService.getCallsWaiting().getCount() + 1;
//        queueService.getCallsWaiting().setCount( callsWaiting );
        getStatistics( CallsWaiting.class ).addValue( callsWaiting );
        queueService.setCallsWaiting( (CallsWaiting)StatisticsTransformer.getInstance().transform(
                                getStatistics( CallsWaiting.class ), queueService.getCallsWaiting() ) );
//        parent.updateCountingStatistics( queueService.getCallsWaiting(), 1 );
    }

    public void decCallsWaiting() {
        int callsWaiting = queueService.getCallsWaiting().getCount() - 1;
//        queueService.getCallsWaiting().setCount( callsWaiting );
        getStatistics( CallsWaiting.class ).addValue( callsWaiting );
        queueService.setCallsWaiting( (CallsWaiting)StatisticsTransformer.getInstance().transform(
                                getStatistics( CallsWaiting.class ), queueService.getCallsWaiting() ) );
//        parent.updateCountingStatistics( queueService.getCallsWaiting(), -1 );
    }*/

    public void addCallWaitTime( int time ) {
        if ( time > 0 ) {
            getStatistics( CallWaitTime.class ).addValue( time / 1000f );
            queueService.setCallWaitTime( (CallWaitTime) StatisticsTransformer.getInstance().transform(
                    getStatistics( CallWaitTime.class ), queueService.getCallWaitTime() ) );
        }
//        parent.updateStatistics( queueService.getCallWaitTime(), time/1000f );
    }

    public void setOnlineAgents( int agentsSize ) {
        /*getStatistics(OnlineAgents.class).addValue(agentsSize);
        queueService.setOnlineAgents((OnlineAgents) StatisticsTransformer.getInstance().transform(
                getStatistics(OnlineAgents.class), queueService.getOnlineAgents()));*/
        onlineAgents.addSample( agentsSize );
        queueService.getOnlineAgents().setCount( agentsSize );
        queueService.getOnlineAgents().setMax( onlineAgents.getMax() );
        queueService.getOnlineAgents().setMin( onlineAgents.getMin() );
        queueService.getOnlineAgents().setMean( onlineAgents.getAverage() );
    }

    /*public void incOnlineAgents() {
        int listAgents = queueService.getOnlineAgents().getCount() + 1;
        queueService.getOnlineAgents().setCount( listAgents );
        getStatistics( OnlineAgents.class ).addValue( listAgents );
        // TODO add transform
//        parent.updateCountingStatistics( queueService.getOnlineAgents(), +1 );
    }

    public void decOnlineAgents() {
        int listAgents = queueService.getOnlineAgents().getCount() - 1;
        queueService.getOnlineAgents().setCount( listAgents );
        getStatistics( OnlineAgents.class ).addValue( listAgents );
        // TODO add transform
//        parent.updateCountingStatistics( queueService.getOnlineAgents(), -1 );
    }*/

    public QueueService getQueueService() {
        return queueService;
    }

    public void setQueueService( QueueService queueService ) {
        this.queueService = queueService;
    }

    private static final String UH_LOCK = "QUnsuccessfullHandingLock";

    public void addUnsuccessfulService( String name, String description ) {
        synchronized ( LockManager.getLockById( UH_LOCK + name ) ) {
            UnsuccessfulService unsuccess = getOrCreateUnsuccessfulHandling( name, description );
            unsuccess.setCount( unsuccess.getCount() + 1 );
        }
    }

    private UnsuccessfulService getOrCreateUnsuccessfulHandling( String name, String description ) {
        for ( int i = 0; i < queueService.getUnsuccessfulServiceCount(); i++ ) {
            if ( queueService.getUnsuccessfulService()[i].getName().equalsIgnoreCase( name ) ) {
                return queueService.getUnsuccessfulService()[i];
            }
        }
        UnsuccessfulService unsuccess = new UnsuccessfulService();
        unsuccess.setCount( 0 );
        unsuccess.setName( name );
        unsuccess.setDescription( description );
        queueService.addUnsuccessfulService( unsuccess );
        return unsuccess;
    }

    public void dispose() {
        super.dispose();
    }

}
