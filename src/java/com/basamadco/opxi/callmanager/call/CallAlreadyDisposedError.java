package com.basamadco.opxi.callmanager.call;

/**
 * @author Jrad
 *         Date: May 29, 2006
 *         Time: 1:32:57 PM
 */
public class CallAlreadyDisposedError extends IllegalStateException {

    public CallAlreadyDisposedError( String callId ) {
        super( callId );
    }

    public String getCallId() {
        return super.getMessage();
    }
}
