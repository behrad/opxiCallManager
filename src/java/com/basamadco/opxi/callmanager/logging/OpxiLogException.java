package com.basamadco.opxi.callmanager.logging;

import com.basamadco.opxi.callmanager.OpxiException;

public class OpxiLogException extends OpxiException {

    public OpxiLogException( String msg, Throwable t ) {
        super( msg, t );
    }

    public OpxiLogException( Throwable t ) {
        super( t );
    }

    public OpxiLogException( String msg ) {
        super( msg );
    }


}
