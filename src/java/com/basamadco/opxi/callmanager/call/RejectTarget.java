package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.OpxiException;

import javax.servlet.sip.URI;

/**
 * @author Jrad
 *         Date: Nov 22, 2007
 *         Time: 12:45:42 PM
 */
public class RejectTarget extends CallTarget {

    private int rejectCode;


    public int getRejectCode() {
        return rejectCode;
    }

    public void setRejectCode( int rejectCode ) {
        this.rejectCode = rejectCode;
    }

    public RejectTarget( int code, String reason ) {
        setRejectCode( code );
        setName( reason );
    }
    

    public URI getTargetURI() throws OpxiException {
        throw new IllegalStateException( "Method not applicable for this type." );
    }


    public void service( CallService call ) throws OpxiException {
        call.reject( getRejectCode(), getName() );
    }

    public boolean isQueueable() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected boolean hasUpdatableState() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
