package com.basamadco.opxi.callmanager.pool.rules;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Mar 5, 2006
 *         Time: 12:07:51 PM
 */
public class NoSuchOperandException extends OpxiException {

    public NoSuchOperandException( String msg ) {
        super( msg );
    }
}
