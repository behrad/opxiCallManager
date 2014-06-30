package com.basamadco.opxi.callmanager.queue;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Mar 8, 2006
 *         Time: 7:57:55 PM
 */
public class NoPendingCallInQueue extends OpxiException {

    public NoPendingCallInQueue( String msg ) {
        super( msg );
    }
}
