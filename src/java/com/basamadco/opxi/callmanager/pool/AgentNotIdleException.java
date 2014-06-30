package com.basamadco.opxi.callmanager.pool;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * @author Jrad
 *         Date: Aug 15, 2006
 *         Time: 1:44:41 PM
 */
public class AgentNotIdleException extends OpxiException {

    public AgentNotIdleException( String agentName ) {
        super( agentName );
    }

}
