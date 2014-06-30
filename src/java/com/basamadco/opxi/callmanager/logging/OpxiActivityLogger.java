package com.basamadco.opxi.callmanager.logging;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;
import com.basamadco.opxi.activitylog.schema.OpxiActivityLog;
import com.basamadco.opxi.activitylog.schema.Statistics;
import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.ServiceFactory;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOFactoryException;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.webdav.StorageDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.webdav.Storage;
import com.basamadco.opxi.callmanager.call.AbstractTimerContext;
import com.basamadco.opxi.callmanager.call.TimerException;
import com.basamadco.opxi.callmanager.util.PropertyUtil;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import java.io.Serializable;
import java.io.Writer;
import java.util.Map;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Jul 5, 2006
 *         Time: 12:51:58 PM
 */
public abstract class OpxiActivityLogger extends AbstractTimerContext implements StatisticalLogger {

    private static final Logger logger = Logger.getLogger( OpxiActivityLogger.class.getName() );

    private OpxiActivityLog logVO;

    private Map serviceStatistics;


    // TODO Convert timers to SipServlet API (JSR116) based ServletTimer
//    private Timer draftLogRefreshTimer;

    private static final String DRAFT_LOG_REFRESH_RATE_MIN = PropertyUtil.getProperty( "opxi.logReport.draft_refresh_rate" );

    private static final boolean DRAFT_LOG_ENABLED = Boolean.valueOf( PropertyUtil.getProperty( "opxi.logReport.draft.enabled" ) ).booleanValue();


    public OpxiActivityLogger( ServiceFactory serviceFactory, Serializable object ) {
        super( serviceFactory, "" );
        this.serviceStatistics = new ConcurrentHashMap();
        this.logVO = initLogVO( object );
        setId( this.logVO.getId() );
        long draftLogRefreshRate = Integer.parseInt( DRAFT_LOG_REFRESH_RATE_MIN ) * 60 * 1000;
//        logger.finest( "Current logger rate: " + draftLogRefreshRate );
        if ( DRAFT_LOG_ENABLED ) {
            setTimer( serviceFactory.getSipService().getTimerService().createTimer(
                    serviceFactory.getSipService().getApplicationSession(),
                    draftLogRefreshRate, draftLogRefreshRate, true, false, this ) );
            logger.finest( "Created draft logger timer for: " + new Date( getTimer().scheduledExecutionTime() ) );
        }
//        draftLogRefreshTimer = new Timer( "OpxiActivityLoggerTimer-" + getLogVO().getId() );
//        draftLogRefreshTimer.schedule( this, draftLogRefreshRate, draftLogRefreshRate );

    }

    public void setId( String id ) {
        getLogVO().setId( id );
    }

    public String getId() {
        return getLogVO().getId();
    }

    public void marshal( Writer writer ) throws ValidationException, MarshalException {
        getLogVO().marshal( writer );
    }


    protected abstract OpxiActivityLog initLogVO( final Serializable object );


    protected abstract void doForceSpecific() throws ForceActionException;


    protected abstract void beforeForceLog( Object cause );


    protected final void forceLog( String[] owners ) throws DAOFactoryException {
        for ( int i = 0; i < owners.length; i++ ) {
            String owner = owners[i];
            logger.finest( "Forcing log for: '" + owner + "'" );
            try {
                BaseDAOFactory.getConfigStorageDAOFactory().getLogReportDAO( owner, getLogVO() ).writeLog();
            } catch ( DAOException e ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
            }
        }
    }


    public void timeout() throws TimerException {
        try {
            BaseDAOFactory.getWebdavDAOFactory().getTempLogReportDAO( getLogVO() ).writeLog();
            logger.finest( "LogVO with Id '" + getLogVO().getId() + "' is saved..." );
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new TimerException( e );
        }
    }

    /*public void run() {
        try {
            BaseDAOFactory.getWebdavDAOFactory().getTempLogReportDAO( getLogVO() ).writeLog();
            logger.fine( "LogVO with Id '" + getLogVO().getId() + "' is saved..." );
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }*/

    protected OpxiActivityLog getLogVO() {
        return logVO;
    }


    void forceLog( Object cause ) throws ForceActionException {
        beforeForceLog( cause );
        doForceSpecific();
    }

    public SummaryStatistics getStatistics( Class statisticsType ) {
        return (SummaryStatistics) serviceStatistics.get( statisticsType );
    }

    public void addStatistics( Class type, SummaryStatistics stat ) {
        serviceStatistics.put( type, stat );
    }

    protected Statistics initStatistics( Statistics stat ) {
        stat.setCount( 0 );
        stat.setSum( 0 );
        stat.setMax( 0 );
        stat.setMin( 0 );
        stat.setMean( 0 );
        stat.setVariance( 0 );
        return stat;
    }

//    protected void updateStatistics( Statistics stat, int value ) {
//        if( value != 0 ) {
//            updateStatistics( stat, value );
//        }
//    }

    protected void updateStatistics( Statistics stat, float value ) {
        if ( value != 0 ) {
            stat.setCount( stat.getCount() + 1 );
            stat.setSum( stat.getSum() + value );
            if ( stat.getMax() != 0 ) {
                stat.setMax( (stat.getMax() < value) ? value : stat.getMax() );
            } else {
                stat.setMax( value );
            }
            if ( stat.getMin() != 0 ) {
                stat.setMin( (stat.getMin() > value) ? value : stat.getMin() );
            } else {
                stat.setMin( value );
            }
            if ( stat.getMean() != 0 ) {
                stat.setMean( (stat.getMean() + value) / 2f );
            } else {
                stat.setMean( value );
            }
        }
    }

    protected void updateCountingStatistics( Statistics stat, int value ) {
        if ( value != 0 ) {
            stat.setCount( stat.getCount() + value );
            stat.setSum( stat.getSum() + value );

            value = stat.getCount();
            if ( stat.getMax() != 0 ) {
                stat.setMax( (stat.getMax() < value) ? value : stat.getMax() );
            } else {
                stat.setMax( value );
            }
            if ( stat.getMin() != 0 ) {
                stat.setMin( (stat.getMin() > value) ? value : stat.getMin() );
            } else {
                stat.setMin( value );
            }
            if ( stat.getMean() != 0 ) {
                stat.setMean( (stat.getMean() + value) / 2f );
            } else {
                stat.setMean( value );
            }
        }
    }

    public void dispose() {
        serviceStatistics.clear();
        cancel();
//        draftLogRefreshTimer.purge();
//        draftLogRefreshTimer.cancel();
    }

    public String toString() {
        return this.getClass().getName() + ": " + getLogVO().getId();
    }
}