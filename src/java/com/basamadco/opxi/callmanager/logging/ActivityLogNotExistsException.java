package com.basamadco.opxi.callmanager.logging;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Jul 5, 2006
 *         Time: 1:21:03 PM
 */
public class ActivityLogNotExistsException extends OpxiException {

    public ActivityLogNotExistsException( String msg ) {
        super( msg );
    }

}
