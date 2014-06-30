package com.basamadco.opxi.callmanager.pool;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Aug 1, 2006
 *         Time: 11:12:50 AM
 */
public class AgentTransferException extends OpxiException {

    public AgentTransferException( String message, Throwable t ) {
        super( message, t );
    }
}
