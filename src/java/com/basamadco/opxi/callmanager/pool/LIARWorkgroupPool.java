package com.basamadco.opxi.callmanager.pool;

import com.basamadco.opxi.callmanager.profile.ProfileException;

/**
 * @author Jrad
 *         Date: Sep 3, 2006
 *         Time: 3:59:45 PM
 */
public class LIARWorkgroupPool extends WorkgroupAgentPool {

    public LIARWorkgroupPool( PoolTarget poolTarget ) throws PoolInitializationException, ProfileException {
        super( poolTarget, new LIARPoolContainer() );
    }

}