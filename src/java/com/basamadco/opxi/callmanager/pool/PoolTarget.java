package com.basamadco.opxi.callmanager.pool;

import com.basamadco.opxi.callmanager.call.CallService;
import com.basamadco.opxi.callmanager.call.CallServiceException;
import com.basamadco.opxi.callmanager.call.QueueTarget;
import com.basamadco.opxi.callmanager.entity.profile.OpxiCMEntityProfile;
import com.basamadco.opxi.callmanager.entity.profile.PoolTargetProfile;
import com.basamadco.opxi.callmanager.profile.ProfileException;

import java.util.logging.Logger;

/**
 * Represents any entity that may be used as a container for Agents.
 * PoolTarget should read it's profile so that it can provide the
 * necessary information to AgentPool to behave consistant.
 * @author Jrad
 *         Date: Apr 24, 2006
 *         Time: 12:29:23 PM
 * @see com.basamadco.opxi.callmanager.pool.AgentPool
 */
public class PoolTarget extends QueueTarget {

    private static final Logger logger = Logger.getLogger( PoolTarget.class.getName() );


    protected String id;

    protected String name;

    private OpxiCMEntityProfile profile;


    public PoolTarget( String name ) {
        this.name = name;
        this.id = name;
    }

    protected PoolTarget( PoolTarget target ) throws ProfileException {
        super( target );
        setId( target.getId() );
        setName( target.getName() );
        setProfile( target.getProfile() );
        assignProfile( target.getProfile() );
    }

    public void assign( CallService call ) throws CallServiceException {
        throw new IllegalStateException( "Not implemented yet..." );
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public OpxiCMEntityProfile getProfile() {
        return profile;
    }

    private void setProfile( OpxiCMEntityProfile profile ) throws ProfileException {
        this.profile = profile;
    }

    /**
     * Calls for PoolTargets are managed through queue management system
     * @return true always
     */
    public boolean isQueueable() {
        return true;
    }

    protected void applyProfile( OpxiCMEntityProfile profile ) throws ProfileException {
        setProfile( profile );
        super.applyProfile( profile );
    }

    /**
     * Pool profiles can be applied in any state at any time!
     * @return always true
     */
    protected boolean hasUpdatableState() {
        return true;
    }


    public boolean equals( Object o ) {
        if ( o instanceof PoolTarget ) {
            return name.equals( ( (PoolTarget) o ).getName() );
        }
        if ( o instanceof String  ) { // don't actually like this kind of equality but it's realy usefull!
            return name.equals( o );
        }
        return super.equals( o );
    }

    public String toString() {
        return "PoolTarget[id=" + id +
                ", name=" + name +
                ", type=" + profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getType() +
                ", " + super.toString() +
                "]";
    }

}
