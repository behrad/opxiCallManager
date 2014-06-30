package com.basamadco.opxi.callmanager.profile;

import com.basamadco.opxi.callmanager.entity.profile.OpxiCMEntityProfile;
import com.basamadco.opxi.callmanager.entity.profile.GreetingAudio;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOFactoryException;
import com.basamadco.opxi.callmanager.entity.dao.directory.AgentDAO;
import com.basamadco.opxi.callmanager.entity.dao.directory.PoolTargetDAO;

/**
 * @author Jrad
 *         Date: May 10, 2010
 *         Time: 1:30:26 PM
 */
public class CouchProfileService extends ProfileService {

    private com.basamadco.opxi.callmanager.entity.dao.couch.CouchDAOFactory daof;

    public CouchProfileService() throws DAOFactoryException {
        this.daof = BaseDAOFactory.getCouchDAOFactory();
    }

    public void createAgentProfile( OpxiCMEntityProfile profile ) throws ProfileException {
        try {
            AgentDAO adao = BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO();
            String agentName = adao.getCNForDN( profile.getOpxiCMEntityProfileChoice().getAgentProfile().getDN() );
            daof.getAgentProfileDAO( agentName ).writeProfile( profile );
        } catch ( Exception e ) {
            throw new ProfileException( e );
        }
    }

    public OpxiCMEntityProfile readAgentProfile( String dn ) throws ProfileException {
        try {
            AgentDAO adao = BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO();
            String agentName = adao.getCNForDN( dn );
            return daof.getAgentProfileDAO( agentName ).readProfile();
        } catch ( Exception e ) {
            throw new ProfileException( e );
        }
    }

    public void updateAgentProfile( OpxiCMEntityProfile profile, String contentType, byte[] greetingAudio ) throws ProfileException {
        try {
            AgentDAO adao = BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO();
            String agentName = adao.getCNForDN( profile.getOpxiCMEntityProfileChoice().getAgentProfile().getDN() );
            if ( greetingAudio != null ) {
                GreetingAudio ga = new GreetingAudio();
                ga.setSrc(
                        daof.getAgentProfileDAO( agentName ).writeResource( "greeting", contentType, greetingAudio )
                );
                profile.getOpxiCMEntityProfileChoice().getAgentProfile().addGreetingAudio( ga );
            }
            daof.getAgentProfileDAO( agentName ).updateProfile( profile );
            super.updateAgentProfile( profile, contentType, greetingAudio );
        } catch ( Exception e ) {
            throw new ProfileException( e );
        }
    }

    public void deleteAgentProfile( String agentDN ) throws ProfileException {
        try {
            AgentDAO adao = BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO();
            String agentName = adao.getCNForDN( agentDN );
            daof.getAgentProfileDAO( agentName ).deleteProfile();
        } catch ( Exception e ) {
            throw new ProfileException( e );
        }
    }

    public void createPoolTargetProfile( OpxiCMEntityProfile profile ) throws ProfileException {
        try {
            PoolTargetDAO adao = BaseDAOFactory.getDirectoryDAOFactory().getPoolTargetDAO();
            String poolName = adao.getCNForDN( profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getDN() );
            daof.getPoolTargetProfileDAO( poolName ).writeProfile( profile );
        } catch ( Exception e ) {
            throw new ProfileException( e );
        }
    }

    public OpxiCMEntityProfile readPoolTargetProfile( String dn ) throws ProfileException {
        try {
            PoolTargetDAO adao = BaseDAOFactory.getDirectoryDAOFactory().getPoolTargetDAO();
            String poolName = adao.getCNForDN( dn );
            return daof.getPoolTargetProfileDAO( poolName ).readProfile();
        } catch ( Exception e ) {
            throw new ProfileException( e );
        }
    }

    public void updatePoolTargetProfile( OpxiCMEntityProfile profile, String contentType, byte[] waitAudio ) throws ProfileException {
        try {
            PoolTargetDAO adao = BaseDAOFactory.getDirectoryDAOFactory().getPoolTargetDAO();
            String poolName = adao.getCNForDN( profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getDN() );
            if ( waitAudio != null ) {
                if ( profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getQueueProfileCount() > 0 ) {
                    profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getQueueProfile( 0 ).setWaitingAudio(
                            daof.getPoolTargetProfileDAO( poolName ).writeResource( "waiting", contentType, waitAudio )
                    );
                } else {
                    throw new ProfileException( "No QueueProfile is set." );
                }
            }
            daof.getPoolTargetProfileDAO( poolName ).updateProfile( profile );
            super.updatePoolTargetProfile( profile, contentType, waitAudio );
        } catch ( Exception e ) {
            throw new ProfileException( e );
        }

    }

    public void deletePoolTargetProfile( String poolDN ) throws ProfileException {
        try {
            PoolTargetDAO adao = BaseDAOFactory.getDirectoryDAOFactory().getPoolTargetDAO();
            String poolName = adao.getCNForDN( poolDN );
            daof.getPoolTargetProfileDAO( poolName ).deleteProfile();
        } catch ( Exception e ) {
            throw new ProfileException( e );
        }
    }

    public void removeAttachment( String dN ) throws ProfileException {
        try {
            PoolTargetDAO adao = BaseDAOFactory.getDirectoryDAOFactory().getPoolTargetDAO();
            String name = adao.getCNForDN( dN );
            OpxiCMEntityProfile profile = daof.getPoolTargetProfileDAO( name ).readProfile();
            if ( profile.getOpxiCMEntityProfileChoice().getAgentProfile()
                    != null ) {
                profile.getOpxiCMEntityProfileChoice().getAgentProfile().clearGreetingAudio();
                daof.getPoolTargetProfileDAO( name ).deleteResource( "greeting" );
                updateAgentProfile( profile, null, null );
            } else if ( profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getQueueProfileCount() > 0 ) {
                profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getQueueProfile( 0 ).setWaitingAudio( "http://emptyWaitingAudio" );
                daof.getPoolTargetProfileDAO( name ).deleteResource( "waiting" );
                updatePoolTargetProfile( profile, null, null );
            }
        } catch ( Exception e ) {
            throw new ProfileException( e );
        }
    }
}
