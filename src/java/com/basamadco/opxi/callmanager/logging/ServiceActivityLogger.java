package com.basamadco.opxi.callmanager.logging;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;
import com.basamadco.opxi.activitylog.schema.*;
import com.basamadco.opxi.callmanager.util.LockManager;
import com.basamadco.opxi.callmanager.entity.dao.webdav.StorageDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.DAOFactoryException;
import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryDAOFactory;
import com.basamadco.opxi.callmanager.ServiceFactory;
import com.basamadco.opxi.callmanager.call.Trunk;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Jrad
 *         Date: Aug 20, 2006
 *         Time: 7:12:35 PM
 */
public class ServiceActivityLogger extends OpxiActivityLogger {

    private static final Logger logger = Logger.getLogger( ServiceActivityLogger.class.getName() );

    private Map serviceLoggers = new ConcurrentHashMap();


    TimedVariableStatistics onlineAgents;


    public ServiceActivityLogger( ServiceFactory serviceFactory ) {
        super( serviceFactory, null );
    }

    protected OpxiActivityLog initLogVO( final Serializable object ) {
        // a null input!
        OpxiActivityLog logVO = new OpxiActivityLog();
        ServiceActivity sa = new ServiceActivity();
        sa.setBegin( new Date() );
        sa.setCallAttempts( 0 );

        sa.setIncomingCalls( (IncomingCalls) initStatistics( new IncomingCalls() ) );
        sa.setLocalCalls( (LocalCalls) initStatistics( new LocalCalls() ) );
        sa.setOutgoingCalls( (OutgoingCalls) initStatistics( new OutgoingCalls() ) );
        sa.setInstantMessaging( (InstantMessaging) initStatistics( new InstantMessaging() ) );
        sa.setOnlineAgents( (OnlineAgents) initStatistics( new OnlineAgents() ) );

        addStatistics( IncomingCalls.class, SummaryStatistics.newInstance() );
        addStatistics( LocalCalls.class, SummaryStatistics.newInstance() );
        addStatistics( OutgoingCalls.class, SummaryStatistics.newInstance() );
        addStatistics( InstantMessaging.class, SummaryStatistics.newInstance() );
        addStatistics( OnlineAgents.class, SummaryStatistics.newInstance() );
        onlineAgents = new TimedVariableStatistics();


        sa.setTrunkSvc( new TrunkSvc() );
        logVO.setServiceActivity( sa );

        return logVO;
    }

    public void incCallAttempts() {
        getLogVO().getServiceActivity().setCallAttempts( getLogVO().getServiceActivity().getCallAttempts() + 1 );
    }

    public void addIncomingCall( long time ) {

        if ( time > 0 ) {
            getStatistics( IncomingCalls.class ).addValue( time / 1000f );
            getLogVO().getServiceActivity().setIncomingCalls( (IncomingCalls) StatisticsTransformer.getInstance().transform(
                    getStatistics( IncomingCalls.class ), getLogVO().getServiceActivity().getIncomingCalls() ) );
        }
    }

    public void addLocalCall( long time ) {
        if ( time > 0 ) {
            getStatistics( LocalCalls.class ).addValue( time / 1000f );
            getLogVO().getServiceActivity().setLocalCalls( (LocalCalls) StatisticsTransformer.getInstance().transform(
                    getStatistics( LocalCalls.class ), getLogVO().getServiceActivity().getLocalCalls() ) );
        }
    }

    public void addOutgoingCall( long time ) {
        if ( time > 0 ) {
            getStatistics( OutgoingCalls.class ).addValue( time / 1000f );
            getLogVO().getServiceActivity().setOutgoingCalls( (OutgoingCalls) StatisticsTransformer.getInstance().transform(
                    getStatistics( OutgoingCalls.class ), getLogVO().getServiceActivity().getOutgoingCalls() ) );
        }
    }

    private static final String TRUNK_LOCK = "TrunkLock-";

    private TrunkActivityLogger addTrunkActivityLogger() {
        synchronized ( LockManager.getLockById( TRUNK_LOCK ) ) {
            TrunkActivityLogger tal = new TrunkActivityLogger( this );
            serviceLoggers.put( TRUNK_LOCK, tal );
            return tal;
        }
//        synchronized (LockManager.getLockById(TRUNK_LOCK + name.toLowerCase())) {
//            TrunkActivityLogger tal = new TrunkActivityLogger(this);
//            getLogVO().getServiceActivity().setTrunkSvc(tal.getTrunkSvc());
//            serviceLoggers.put(TRUNK_LOCK + name.toLowerCase(), tal);
//            return tal;
//        }
    }

    protected TrunkActivityLogger getTrunkActivityLogger() throws ActivityLogNotExistsException {
        synchronized ( LockManager.getLockById( TRUNK_LOCK ) ) {
            if ( serviceLoggers.containsKey( TRUNK_LOCK ) ) {
                return (TrunkActivityLogger) serviceLoggers.get( TRUNK_LOCK );
            }
            throw new ActivityLogNotExistsException( "No trunk activity logger exist." );
        }
//        synchronized (LockManager.getLockById(TRUNK_LOCK + name.toLowerCase())) {
//            if (serviceLoggers.containsKey(TRUNK_LOCK + name.toLowerCase())) {
//                return (TrunkActivityLogger) serviceLoggers.get(TRUNK_LOCK + name.toLowerCase());
//            }
//            throw new ActivityLogNotExistsException("trunk: " + name);
//        }
    }

    protected TrunkActivityLogger getOrAddTrunkActivityLogger( Trunk trunk ) throws ActivityLogNotExistsException {
        try {
            return getTrunkActivityLogger();
        } catch ( ActivityLogNotExistsException e ) {
            return addTrunkActivityLogger();
        }
    }

    public TrunkActivityLogger getOrAddTrunkActivityLogger( com.basamadco.opxi.callmanager.call.CallService cs ) throws ActivityLogNotExistsException {
        Trunk trunk = (Trunk) cs.getTarget();
        return getOrAddTrunkActivityLogger( trunk );
    }

    public void addInstantMessaging( long msgSize ) {
        if ( msgSize > 0 ) {
            getStatistics( InstantMessaging.class ).addValue( msgSize );
            getLogVO().getServiceActivity().setInstantMessaging( (InstantMessaging) StatisticsTransformer.getInstance().transform(
                    getStatistics( InstantMessaging.class ), getLogVO().getServiceActivity().getInstantMessaging() ) );
        }
    }

    public void setOnlineAgents( int n ) {
        onlineAgents.addSample( n );
        getLogVO().getServiceActivity().getOnlineAgents().setCount( n );
        getLogVO().getServiceActivity().getOnlineAgents().setMax( onlineAgents.getMax() );
        getLogVO().getServiceActivity().getOnlineAgents().setMin( onlineAgents.getMin() );
        getLogVO().getServiceActivity().getOnlineAgents().setMean( onlineAgents.getAverage() );
        /*getStatistics(OnlineAgents.class).addValue(n);
        getLogVO().getServiceActivity().setOnlineAgents((OnlineAgents) StatisticsTransformer.getInstance().transform(
                getStatistics(OnlineAgents.class), getLogVO().getServiceActivity().getOnlineAgents()));*/

    }

    private static final String QS_LOCK = "QueueServiceLock-";

    public QueueActivityLogger addQueueService( String name ) {
        synchronized ( LockManager.getLockById( QS_LOCK + name.toLowerCase() ) ) {
            QueueActivityLogger qal = new QueueActivityLogger( this, name );
            getLogVO().getServiceActivity().addQueueService( qal.getQueueService() );
            serviceLoggers.put( QS_LOCK + name.toLowerCase(), qal );
            return qal;
        }
    }

    public QueueActivityLogger _getQueueActivityLogger( String name ) throws ActivityLogNotExistsException {
        synchronized ( LockManager.getLockById( QS_LOCK + name.toLowerCase() ) ) {
            if ( serviceLoggers.containsKey( QS_LOCK + name.toLowerCase() ) ) {
                return (QueueActivityLogger) serviceLoggers.get( QS_LOCK + name.toLowerCase() );
            }
            throw new ActivityLogNotExistsException( "queue: " + name );
        }
    }

    public QueueActivityLogger getOrAddQueueActivityLogger( String name ) throws ActivityLogNotExistsException {
        try {
            return _getQueueActivityLogger( name );
        } catch ( ActivityLogNotExistsException e ) {
            return addQueueService( name );
        }
    }


    private static final String AS_LOCK = "ApplicationServiceLock-";

    private ApplicationActivityLogger addApplicationService( String name ) {
        synchronized ( LockManager.getLockById( AS_LOCK + name.toLowerCase() ) ) {
            ApplicationActivityLogger aal = new ApplicationActivityLogger( this, name );
            getLogVO().getServiceActivity().addApplicationService( aal.getApplicationService() );
            serviceLoggers.put( AS_LOCK + name.toLowerCase(), aal );
            return aal;
        }
    }

    private ApplicationActivityLogger getApplicationServiceLogger( String name ) throws ActivityLogNotExistsException {
        synchronized ( LockManager.getLockById( AS_LOCK + name.toLowerCase() ) ) {
            if ( serviceLoggers.containsKey( AS_LOCK + name.toLowerCase() ) ) {
                return (ApplicationActivityLogger) serviceLoggers.get( AS_LOCK + name.toLowerCase() );
            }
            throw new ActivityLogNotExistsException( "application: " + name );
        }
    }

    public ApplicationActivityLogger getOrAddApplicationServiceLogger( String name ) {
        try {
            return getApplicationServiceLogger( name );
        } catch ( ActivityLogNotExistsException e ) {
            return addApplicationService( name );
        }
    }

    private static final String UH_LOCK = "UnsuccessfullHandingLock-";

    public void addUnsuccessfulHandling( String name, String description ) {
        synchronized ( LockManager.getLockById( UH_LOCK + name.toLowerCase() ) ) {
            UnsuccessfulHandling unsuccess = getOrCreateUnsuccessfulHandling( name, description );
            unsuccess.setCount( unsuccess.getCount() + 1 );
        }
    }

    private UnsuccessfulHandling getOrCreateUnsuccessfulHandling( String name, String description ) {
        for ( int i = 0; i < getLogVO().getServiceActivity().getUnsuccessfulHandlingCount(); i++ ) {
            if ( getLogVO().getServiceActivity().getUnsuccessfulHandling()[i].getName().equalsIgnoreCase( name ) ) {
                return getLogVO().getServiceActivity().getUnsuccessfulHandling()[i];
            }
        }
        UnsuccessfulHandling unsuccess = new UnsuccessfulHandling();
        unsuccess.setCount( 0 );
        unsuccess.setName( name );
        unsuccess.setDescription( description );
        getLogVO().getServiceActivity().addUnsuccessfulHandling( unsuccess );
        return unsuccess;
    }


    protected void beforeForceLog( Object cause ) {
        ServiceActivity sa = getLogVO().getServiceActivity();
        sa.setEnd( new Date() );
        sa.setIncomingCalls( (IncomingCalls) StatisticsTransformer.getInstance().transform(
                getStatistics( IncomingCalls.class ), new IncomingCalls() )
        );
        sa.setOutgoingCalls( (OutgoingCalls) StatisticsTransformer.getInstance().transform(
                getStatistics( OutgoingCalls.class ), new OutgoingCalls() )
        );
        sa.setLocalCalls( (LocalCalls) StatisticsTransformer.getInstance().transform(
                getStatistics( LocalCalls.class ), new LocalCalls() )
        );
        sa.setInstantMessaging( (InstantMessaging) StatisticsTransformer.getInstance().transform(
                getStatistics( InstantMessaging.class ), new InstantMessaging() )
        );

        /*sa.setOnlineAgents((OnlineAgents) StatisticsTransformer.getInstance().transform(
                getStatistics(OnlineAgents.class), new OnlineAgents())
        );*/
//        sa.getTrunkSvc().setTrunkCalls((TrunkCalls) StatisticsTransformer.getInstance().transform(
//                getStatistics(TrunkCalls.class), new TrunkCalls())
//        );
    }


    protected void doForceSpecific() throws ForceActionException {
        try {

            DirectoryDAOFactory adao = BaseDAOFactory.getDirectoryDAOFactory();
            String[] managers = adao.getPoolTargetDAO().getMembers( "Employee-Service-Managers" );
//            DirectoryDAOFactory adao = (DirectoryDAOFactory) BaseDAOFactory.getDAOFactory( BaseDAOFactory.DIRECTORY );
//            String agentManager = adao.getAgentDAO().getManagerNameFor( agentName );
            forceLog( managers );
        } catch ( DAOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ForceActionException( e.getMessage(), e );
        } catch ( DAOFactoryException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ForceActionException( e.getMessage(), e );
        }
    }


    public void dispose() {
        for ( Iterator loggers = serviceLoggers.values().iterator(); loggers.hasNext(); ) {
            ((ChildActivityLogger) loggers.next()).dispose();
        }
        serviceLoggers.clear();
        super.dispose();
    }

}