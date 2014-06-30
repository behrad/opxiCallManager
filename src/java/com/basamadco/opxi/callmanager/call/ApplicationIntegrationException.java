package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Nov 22, 2006
 *         Time: 12:26:50 PM
 */
public class ApplicationIntegrationException extends OpxiException {


    public ApplicationIntegrationException( Throwable t ) {
        super( t.getMessage(), t );
    }
}
