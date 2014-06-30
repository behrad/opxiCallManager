package com.basamadco.opxi.callmanager.pool;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Jul 10, 2008
 *         Time: 3:01:37 PM
 */
public class AgentAlreadyLoadedException extends OpxiException {

    public AgentAlreadyLoadedException( String msg ) {
        super( msg );
    }
}
