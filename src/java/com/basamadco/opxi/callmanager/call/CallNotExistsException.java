package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 * Date: Jan 29, 2006
 * Time: 9:56:09 AM
 */
public class CallNotExistsException extends OpxiException {

    public CallNotExistsException(String callId) {
        super( "No CallService exists for Id: " + callId );    //To change body of overridden methods use File | Settings | File Templates.
    }

}
