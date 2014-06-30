package com.basamadco.opxi.callmanager.queue;

import com.basamadco.opxi.callmanager.OpxiException;

public class NoCallInQueueException extends OpxiException {

    public NoCallInQueueException( String msg ) {
        super( msg );
    }
}