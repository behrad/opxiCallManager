package com.basamadco.opxi.callmanager.sip.presence;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Feb 7, 2008
 *         Time: 11:53:09 AM
 */
public class NoActivePublishContextException extends OpxiException {

    public NoActivePublishContextException( String msg ) {
        super( msg );
    }
}
