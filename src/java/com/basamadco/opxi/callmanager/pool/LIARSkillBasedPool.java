package com.basamadco.opxi.callmanager.pool;

import com.basamadco.opxi.callmanager.profile.ProfileException;

import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Sep 3, 2006
 *         Time: 3:28:26 PM
 */
public class LIARSkillBasedPool extends SkillBasedPool {

    private static final Logger logger = Logger.getLogger( LIARSkillBasedPool.class.getName() );


    public LIARSkillBasedPool( PoolTarget poolTarget ) throws PoolInitializationException, ProfileException {
        super( poolTarget, new LIARPoolContainer() );
    }

}