package com.basamadco.opxi.callmanager.profile;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Oct 8, 2006
 *         Time: 7:46:33 PM
 */
public class ProfileException extends OpxiException {


    public ProfileException( Throwable t ) {
        super( t );
    }

    public ProfileException( String msg ) {
        super( msg );
    }
}
