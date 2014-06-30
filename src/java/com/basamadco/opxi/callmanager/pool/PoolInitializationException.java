package com.basamadco.opxi.callmanager.pool;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Oct 7, 2006
 *         Time: 11:20:54 AM
 */
public class PoolInitializationException extends OpxiException {


    public PoolInitializationException( String msg, Throwable t ) {
        super( msg, t );
    }

    public PoolInitializationException( Throwable t ) {
        super( t );
    }
}
