package com.basamadco.opxi.callmanager.queue;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Mar 9, 2006
 *         Time: 9:52:33 AM
 */
public class NoPendingQueueToSchedule extends OpxiException {

    public NoPendingQueueToSchedule( String msg ) {
        super( msg );
    }

}
