package com.basamadco.opxi.callmanager.logging;

import org.apache.commons.math.stat.descriptive.StatisticalSummary;
import com.basamadco.opxi.activitylog.schema.Statistics;

/**
 * @author Jrad
 *         Date: Aug 26, 2006
 *         Time: 6:01:06 PM
 */
public abstract class StatisticsTransformer {

    private final static StatisticsTransformer singleInstance = new IntegerStatisticsTransformer();

    public static StatisticsTransformer getInstance() {
        return singleInstance;
    }

    public abstract Statistics transform( final StatisticalSummary statSummary, Statistics statistics );

}
