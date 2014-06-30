package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Nov 18, 2006
 *         Time: 10:51:39 AM
 */
public class TimerException extends OpxiException {


    public TimerException( Throwable t ) {
        super( t.getMessage(), t );
    }

    public TimerException( String msg ) {
        super( msg );
    }
}
