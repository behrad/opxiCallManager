package com.basamadco.opxi.callmanager.sip.registrar;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * This Exception is thrown when no SIP "Authorization" header
 * is exists to the server by the user agent client.
 *  
 * @author Jrad
 *
 */
public class NoAuthorizationHeaderAvailable extends OpxiException {

    public NoAuthorizationHeaderAvailable( String msg ) {
        super( msg );
    }
}
