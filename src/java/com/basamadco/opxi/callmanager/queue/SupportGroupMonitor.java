package com.basamadco.opxi.callmanager.queue;

import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Apr 17, 2010
 *         Time: 12:33:05 PM
 */
public class SupportGroupMonitor extends TimerTask {

    private static final Logger logger = Logger.getLogger( SupportGroupMonitor.class.getName() );


    private Queue queue;

    private Integer[] delays;


    public SupportGroupMonitor( Queue queue ) {
        this.queue = queue;
        delays = new Integer[queue.getSupportGroups().size()];
        queue.getSupportGroups().keySet().toArray( delays );
    }

    public void run() {
        long currentDelay = queue.longestWaitTime();
//        logger.finest( "$$$$$$$$$$$$$$$$$$$ Delay in queue " + queue.getName() + " is: " + currentDelay );
        for ( int delay : delays ) {
            if ( delay > 0 ) {
                if ( currentDelay < delay * 1000 ) {
                    queue.getServiceFactory().getPoolService().removeSupportGroup( queue.getName(),
                            queue.getSupportGroups().get( delay ) );
                } else {
                    queue.handleSupportGroup( queue.getSupportGroups().get( delay ) );
                }
            }
        }
    }

}
