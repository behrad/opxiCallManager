package com.basamadco.opxi.callmanager.logging;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Aug 27, 2006
 *         Time: 10:55:00 AM
 */
public class ChildActivityLogger implements StatisticalLogger {

    private static final Logger logger = Logger.getLogger(ServiceActivityLogger.class.getName());

    protected OpxiActivityLogger parent;

    protected Map statisticsMap;


    public ChildActivityLogger( OpxiActivityLogger parent ) {
        this.parent = parent;
        statisticsMap = new ConcurrentHashMap();
    }

    public SummaryStatistics getStatistics( Class statisticsType ) {
        return (SummaryStatistics)statisticsMap.get( statisticsType );
    }

    public SummaryStatistics getStatistics( String name ) {
        return (SummaryStatistics)statisticsMap.get( name );
    }

    public void addStatistics( Class type, SummaryStatistics stat ) {
        statisticsMap.put( type, stat );
    }
    public void addStatistics( String name , SummaryStatistics stat ) {
        statisticsMap.put( name , stat );
    }
    public void dispose() {
        statisticsMap.clear();
    }

}
