package com.basamadco.opxi.callmanager.entity;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * This kind of Exception is thrown when no registration binding for a particular
 * user agent is exists.
 * 
 * @author Jrad
 *
 */
public class RegistrationNotAvailableException extends OpxiException {

    public RegistrationNotAvailableException( String msg ) {
        super( msg );
    }

}
