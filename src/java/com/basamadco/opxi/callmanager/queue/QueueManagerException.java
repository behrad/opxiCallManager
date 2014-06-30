package com.basamadco.opxi.callmanager.queue;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Feb 6, 2006
 *         Time: 4:09:18 PM
 */
public class QueueManagerException extends OpxiException {

    public QueueManagerException( String msg ) {
        super( msg );
    }

    public QueueManagerException( String msg, Throwable t ) {
        super( msg, t );
    }

}
