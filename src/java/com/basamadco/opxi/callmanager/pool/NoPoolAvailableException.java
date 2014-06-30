package com.basamadco.opxi.callmanager.pool;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: May 9, 2006
 *         Time: 3:06:01 PM
 */
public class NoPoolAvailableException extends OpxiException {

    public NoPoolAvailableException( String msg ) {
        super( msg );
    }

}
