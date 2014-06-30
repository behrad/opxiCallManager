package com.basamadco.opxi.callmanager.profile;

import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.DAOFactoryException;
import com.basamadco.opxi.callmanager.entity.dao.directory.AgentDAO;
import com.basamadco.opxi.callmanager.entity.dao.directory.PoolTargetDAO;
import com.basamadco.opxi.callmanager.entity.dao.directory.CallTargetDAO;
import com.basamadco.opxi.callmanager.entity.dao.webdav.AgentProfileDAO;
import com.basamadco.opxi.callmanager.entity.dao.webdav.PoolTargetProfileDAO;
import com.basamadco.opxi.callmanager.entity.dao.webdav.StorageDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.webdav.exchange.ExchangeAgentProfileDAO;
import com.basamadco.opxi.callmanager.entity.dao.webdav.exchange.ExchangeDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.webdav.exchange.ExchangePoolTargetProfileDAO;
import com.basamadco.opxi.callmanager.entity.profile.GreetingAudio;
import com.basamadco.opxi.callmanager.entity.profile.OpxiCMEntityProfile;
import com.basamadco.opxi.callmanager.call.CallTarget;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * @author Jrad
 *         Date: Oct 8, 2006
 *         Time: 7:48:21 PM
 */
public class WebdavProfileService extends ProfileService {

    private static final Logger logger = Logger.getLogger( WebdavProfileService.class.getName() );


    private ExchangeDAOFactory daof;

    public WebdavProfileService() throws DAOFactoryException {
        daof = BaseDAOFactory.getWebdavDAOFactory();
    }

    public void createAgentProfile( OpxiCMEntityProfile profile ) throws ProfileException {
        try {
            AgentDAO adao = BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO();
            String agentName = adao.getCNForDN( profile.getOpxiCMEntityProfileChoice().getAgentProfile().getDN() );
            ExchangeAgentProfileDAO profileDAO = (ExchangeAgentProfileDAO) daof.getAgentProfileDAO( agentName );
            profileDAO.createHierarchy();
            profileDAO.writeProfile( profile );
        } catch ( DAOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ProfileException( e );
        } catch ( DAOFactoryException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ProfileException( e );
        }
    }

    public void updateAgentProfile( OpxiCMEntityProfile newProfile, String contentType, byte[] greetingAudio ) throws ProfileException {
        try {
            String dn = newProfile.getOpxiCMEntityProfileChoice().getAgentProfile().getDN();
            AgentProfileDAO apd = daof.getAgentProfileDAO(
                    BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO().getCNForDN( dn )
            );
            if ( greetingAudio != null ) {
                GreetingAudio ga = new GreetingAudio();
                ga.setSrc( apd.writeResource( "greetingAudio", contentType, greetingAudio ) );
                newProfile.getOpxiCMEntityProfileChoice().getAgentProfile().addGreetingAudio( ga );
            }
//            OpxiCMEntityProfile currentProfile = apd.readProfile();
//            OpxiCMEntityProfile profile = mergeProfilesForUpdate( currentProfile, newProfile );
            apd.writeProfile( newProfile );
            logger.finer( "**************** AgentProfile written to webdav... " );
            super.updateAgentProfile( newProfile, contentType, greetingAudio );
        } catch ( DAOFactoryException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ProfileException( e );
        } catch ( DAOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ProfileException( e );
        }
    }

    public void deleteAgentProfile( String agentDN ) throws ProfileException {
        try {
            AgentDAO adao = BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO();
            String name = adao.getCNForDN( agentDN );
            ((ExchangeAgentProfileDAO) daof.getAgentProfileDAO( name )).deleteHierarchy();
        } catch ( DAOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ProfileException( e );
        } catch ( DAOFactoryException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ProfileException( e );
        }
    }


    public void createPoolTargetProfile( OpxiCMEntityProfile profile ) throws ProfileException {
        try {
            PoolTargetDAO adao = BaseDAOFactory.getDirectoryDAOFactory().getPoolTargetDAO();
            String poolName = adao.getCNForDN( profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getDN() );
            PoolTargetProfileDAO profileDAO = daof.getPoolTargetProfileDAO( poolName );
            ((ExchangePoolTargetProfileDAO) profileDAO).createHierarchy();
//            if( isSkillProfile( profile ) ) {
//                daof.getMatchingRuleDAO( poolName ).updateMatchingRule( DEFAULT_MATCHING_RULE );
//            }
            profileDAO.writeProfile( profile );
        } catch ( DAOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ProfileException( e );
        } catch ( DAOFactoryException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ProfileException( e );
        }
    }

    public void updatePoolTargetProfile( OpxiCMEntityProfile profile, String contentType, byte[] resource ) throws ProfileException {
        try {
            PoolTargetDAO adao = BaseDAOFactory.getDirectoryDAOFactory().getPoolTargetDAO();
            String poolName = adao.getCNForDN( profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getDN() );
            updatePoolTargetProfile( poolName, profile, "waitingAudio", contentType, resource );

        } catch ( DAOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ProfileException( e );
        } catch ( DAOFactoryException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ProfileException( e );
        }
    }


    /*public void updateSkillProfile( OpxiCMEntityProfile profile, byte[] resource ) throws ProfileException {
        try {
            PoolTargetDAO adao = BaseDAOFactory.getDirectoryDAOFactory().getPoolTargetDAO();
            String poolName = adao.getCNForDN( profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getDN() );
//            updateMatchingRule( poolName, matchingRule );
            updatePoolTargetProfile( poolName, profile, "waitingAudio", resource );
        } catch ( DAOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ProfileException( e );
        } catch ( DAOFactoryException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ProfileException( e );
        }
    }*/

    private void updatePoolTargetProfile( String poolName, OpxiCMEntityProfile newProfile,
                                          String resourceName, String contentType, byte[] resource ) throws ProfileException {
        try {
            PoolTargetProfileDAO ptpd = daof.getPoolTargetProfileDAO( poolName );
            if ( resourceName != null && resource != null ) {
                if ( newProfile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getQueueProfileCount() > 0 ) {
                    newProfile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getQueueProfile( 0 ).setWaitingAudio(
                            ptpd.writeResource( resourceName, contentType, resource )
                    );
                } else {
                    throw new ProfileException( "No QueueProfile is set." );
                }
            }
//            OpxiCMEntityProfile currentProfile = ptpd.readProfile();
//            OpxiCMEntityProfile profile = mergeProfilesForUpdate( currentProfile, newProfile );
            ptpd.writeProfile( newProfile );
            super.updatePoolTargetProfile( newProfile, contentType, resource );
        } catch ( DAOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ProfileException( e );
        }
    }

    /*private void updateMatchingRule( String skillName, String matchingRule ) throws ProfileException {
        try {
            daof.getMatchingRuleDAO( skillName ).updateMatchingRule( matchingRule );
        } catch ( DAOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ProfileException( e );
        }
    }*/

    public void deletePoolTargetProfile( String poolDN ) throws ProfileException {
        try {
            PoolTargetDAO adao = BaseDAOFactory.getDirectoryDAOFactory().getPoolTargetDAO();
            String name = adao.getCNForDN( poolDN );
            ((ExchangePoolTargetProfileDAO) daof.getPoolTargetProfileDAO( name )).deleteHierarchy();
        } catch ( DAOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ProfileException( e );
        } catch ( DAOFactoryException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ProfileException( e );
        }
    }


    public OpxiCMEntityProfile readAgentProfile( String dn ) throws ProfileException {
        try {
            AgentDAO adao = BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO();
            String name = adao.getCNForDN( dn );
            return daof.getAgentProfileDAO( name ).readProfile();
        } catch ( DAOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ProfileException( e );
        } catch ( DAOFactoryException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ProfileException( e );
        }
    }

    public OpxiCMEntityProfile readPoolTargetProfile( String dn ) throws ProfileException {
        try {
            PoolTargetDAO adao = BaseDAOFactory.getDirectoryDAOFactory().getPoolTargetDAO();
            String poolName = adao.getCNForDN( dn );
            return daof.getPoolTargetProfileDAO( poolName ).readProfile();
        } catch ( DAOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ProfileException( e );
        } catch ( DAOFactoryException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ProfileException( e );
        }
    }

    public void removeAttachment( String dN ) throws ProfileException {
        try {

            CallTarget target = BaseDAOFactory.getDirectoryDAOFactory().getCallTargetDAO().getCallTargetById( dN );


            OpxiCMEntityProfile profile = daof.getPoolTargetProfileDAO( target.getName() ).readProfile();
            if ( target.isAgent() ) {
                profile.getOpxiCMEntityProfileChoice().getAgentProfile().clearGreetingAudio();
                daof.getPoolTargetProfileDAO( target.getName() ).deleteResource( "greetingAudio" );
                updateAgentProfile( profile, null, null );
            } else if ( profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getQueueProfileCount() > 0 ) {
                profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getQueueProfile( 0 ).setWaitingAudio( "empty" );
                daof.getPoolTargetProfileDAO( target.getName() ).deleteResource( "waitingAudio" );
                updatePoolTargetProfile( profile, null, null );
            }
        } catch ( Exception e ) {
            throw new ProfileException( e );
        }
    }

    /*private OpxiCMEntityProfile mergeProfilesForUpdate( OpxiCMEntityProfile currentProfile, OpxiCMEntityProfile newProfile ) {
        if( newProfile.getOpxiCMEntityProfileChoice().getAgentProfile().getAction().getType() == ProfileAction.UPDATE_TYPE ) {
            currentProfile.setAgentProfile( newProfile.getOpxiCMEntityProfileChoice().getAgentProfile() );
        } else { // no operation!
            for (int i = 0; i < newProfile.getOpxiCMEntityProfileChoice().getAgentProfile().getGreetingAudio().length; i++) {
                GreetingAudio greetingAudio = newProfile.getOpxiCMEntityProfileChoice().getAgentProfile().getGreetingAudio()[i];
                if( greetingAudio.getAction().getType() == ProfileAction.UPDATE_TYPE ) {
                    // TODO to be clearly defined and implemented
                }
            }
        }
        if( newProfile.getOpxiCMEntityProfileChoice().getPoolTargetProfile() != null ) {
            if( newProfile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getAction().getType() == ProfileAction.UPDATE_TYPE ) {
                currentProfile.setPoolTargetProfile( newProfile.getOpxiCMEntityProfileChoice().getPoolTargetProfile() );
            } else { // everything other UPDATE means no operation!
                if( newProfile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getQueueProfileCount() > 0 ) {
                    if( newProfile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getQueueProfile()[ 0 ].getAction().getType() ==
                            ProfileAction.UPDATE_TYPE ) {
                        currentProfile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().setQueueProfile( newProfile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getQueueProfile() );
                    }
                }
                SkillProfile sp = newProfile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getPoolTargetProfileChoice().getSkillProfile();
                if( sp != null ) {
                    if( sp.getAction().getType() == ProfileAction.UPDATE_TYPE ) {
                        currentProfile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getPoolTargetProfileChoice().setSkillProfile( sp );
                    } else {
                        if( sp.getMatchingRule() != null ) {
                            // TODO ...!
                        }
                    }

                }
            }
        }
        return currentProfile;
    }*/


}