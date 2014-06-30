package com.basamadco.opxi.callmanager.logging;

import java.util.Date;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Feb 5, 2008
 *         Time: 5:00:44 PM
 */
public class TimedVariableStatistics {

    private static final Logger logger = Logger.getLogger(TimedVariableStatistics.class.getName());

    private long startTime;

    private long lastSampleTime;

    private long lastSample;

    private long historySum;

    private long max;

    private long min;

    private float average;


    public TimedVariableStatistics() {
        startTime = System.currentTimeMillis();
        lastSampleTime = startTime;
    }

    public void addSample( long n ) {
        logger.finest( "adding Sample '" + n + "' to " + this );
        long now = System.currentTimeMillis();
        if( now > startTime ) {
            logger.finest( lastSample + " * (" + new Date( now - lastSampleTime ) + ")" );
            historySum += lastSample * ( now - lastSampleTime );
            logger.finest( "historySum: " + historySum );
            average = (float)historySum / ( now - startTime );
        }
        max = ( n > max ? n : max );
        min = ( n < min ? n : min );
        lastSample = n;
        lastSampleTime = now;
        logger.finest( this.toString() );
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    public float getAverage() {
        return average;
    }

    public String toString() {
        return "[lastSample="+lastSample+", at="+new Date( lastSampleTime )+", max="+max+", min="+min+", average="+average+"]";
    }
}
