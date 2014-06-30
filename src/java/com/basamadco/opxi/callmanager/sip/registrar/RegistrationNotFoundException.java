package com.basamadco.opxi.callmanager.sip.registrar;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Jul 6, 2008
 *         Time: 11:57:10 AM
 */
public class RegistrationNotFoundException extends OpxiException {

    public RegistrationNotFoundException( String msg ) {
        super( msg );
    }
}
