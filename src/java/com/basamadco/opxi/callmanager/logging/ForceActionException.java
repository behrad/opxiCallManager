package com.basamadco.opxi.callmanager.logging;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Jul 9, 2006
 *         Time: 12:32:17 PM
 */
public class ForceActionException extends OpxiException {

    public ForceActionException( String msg, Throwable t ) {
        super( msg, t );
    }

    public ForceActionException( String msg ) {
        super( msg );
    }

}
