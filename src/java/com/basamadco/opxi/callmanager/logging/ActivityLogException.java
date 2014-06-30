package com.basamadco.opxi.callmanager.logging;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Aug 13, 2006
 *         Time: 6:24:47 PM
 */
public class ActivityLogException extends OpxiException {

    public ActivityLogException( String msg, Throwable t ) {
        super( msg, t );
    }

    public ActivityLogException( Throwable t ) {
        super( t );
    }

    public ActivityLogException( String msg ) {
        super( msg );
    }

}
