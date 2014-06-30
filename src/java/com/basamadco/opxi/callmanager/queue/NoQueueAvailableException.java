package com.basamadco.opxi.callmanager.queue;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Feb 13, 2006
 *         Time: 12:16:39 PM
 */
public class NoQueueAvailableException extends OpxiException {

    public NoQueueAvailableException( String msg, Throwable t ) {
        super( msg, t );
    }

    public NoQueueAvailableException( String msg ) {
        super( msg );
    }

}
