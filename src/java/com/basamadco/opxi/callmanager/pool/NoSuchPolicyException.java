package com.basamadco.opxi.callmanager.pool;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Jan 29, 2006
 *         Time: 10:34:35 AM
 */
public class NoSuchPolicyException extends OpxiException {
    public NoSuchPolicyException(String msg) {
        super( "No HuntingPolicy exists for name: " + msg );    //To change body of overridden methods use File | Settings | File Templates.
    }
}
