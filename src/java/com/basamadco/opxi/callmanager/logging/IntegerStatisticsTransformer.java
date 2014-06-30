package com.basamadco.opxi.callmanager.logging;

import com.basamadco.opxi.activitylog.schema.Statistics;
import org.apache.commons.math.stat.descriptive.StatisticalSummary;

/**
 * @author Jrad
 *         Date: Aug 26, 2006
 *         Time: 6:07:31 PM
 */
public class IntegerStatisticsTransformer extends StatisticsTransformer {

    public Statistics transform( final StatisticalSummary statSummary, Statistics statistics ) {
        statistics.setCount   ( (int)statSummary.getN()          );
        if( statSummary.getN() == 0 ) {
            statistics.setSum     ( 0 );
            statistics.setMin     ( 0 );
            statistics.setMax     ( 0 );
            statistics.setMean    ( 0 );
            statistics.setVariance( 0 );
            statistics.setStandardDeviation( 0 );
        } else {
            statistics.setSum     ( (float)statSummary.getSum()      );
            statistics.setMin     ( (float)statSummary.getMin()      );
            statistics.setMax     ( (float)statSummary.getMax()      );
            statistics.setMean    ( (float)statSummary.getMean()     );
            statistics.setVariance( (float)statSummary.getVariance() );
            statistics.setStandardDeviation( (float)statSummary.getStandardDeviation() );
        }
        return statistics;
    }

}
