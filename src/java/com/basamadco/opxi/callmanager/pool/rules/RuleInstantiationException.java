package com.basamadco.opxi.callmanager.pool.rules;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Oct 28, 2006
 *         Time: 4:01:16 PM
 */
public class RuleInstantiationException extends OpxiException {


    public RuleInstantiationException( String msg, Throwable t ) {
        super( msg, t );
    }

    public RuleInstantiationException( Throwable t ) {
        super( t );
    }
}
