package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Aug 14, 2006
 *         Time: 1:44:52 PM
 */
public class CallTransferException extends OpxiException {

    public CallTransferException( Throwable t ) {
        super( t );
    }

    public CallTransferException( String msg, Throwable t ) {
        super( msg, t );
    }

}
