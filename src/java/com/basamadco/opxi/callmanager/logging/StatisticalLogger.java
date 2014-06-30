package com.basamadco.opxi.callmanager.logging;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;

/**
 * @author Jrad
 *         Date: Aug 27, 2006
 *         Time: 11:02:35 AM
 */
public interface StatisticalLogger {

    public SummaryStatistics getStatistics( Class statisticsType );

    public void addStatistics( Class type, SummaryStatistics stat );

}
