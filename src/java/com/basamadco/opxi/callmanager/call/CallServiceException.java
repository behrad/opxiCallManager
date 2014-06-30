package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Nov 22, 2006
 *         Time: 9:43:33 AM
 */
public class CallServiceException extends OpxiException {


    public CallServiceException( String msg, Throwable t ) {
        super( msg, t );
    }

    public CallServiceException( Throwable t ) {
        super( t.getMessage(), t );
    }
}
