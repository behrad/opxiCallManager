package com.basamadco.opxi.callmanager.profile;

import com.basamadco.opxi.callmanager.AbstractCallManagerService;
import com.basamadco.opxi.callmanager.pool.LIARWorkgroupPool;
import com.basamadco.opxi.callmanager.pool.LIARSkillBasedPool;
import com.basamadco.opxi.callmanager.pool.rules.AlwaysFalseRule;
import com.basamadco.opxi.callmanager.entity.profile.*;
import com.basamadco.opxi.callmanager.entity.profile.types.RepeatType;

import java.util.Date;

/**
 * @author Jrad
 *         Date: Oct 8, 2006
 *         Time: 7:41:21 PM
 */
public abstract class ProfileService extends AbstractCallManagerService {


    public abstract void createAgentProfile( OpxiCMEntityProfile profile ) throws ProfileException;


    public abstract OpxiCMEntityProfile readAgentProfile( String dn ) throws ProfileException;


    public void updateAgentProfile( OpxiCMEntityProfile profile, String contentType, byte[] greetingAudio ) throws ProfileException {
        getServiceFactory().getPoolService().agentProfileUpdateNotification( profile );
    }


    public abstract void deleteAgentProfile( String agentDN ) throws ProfileException;


    public abstract void createPoolTargetProfile( OpxiCMEntityProfile profile ) throws ProfileException;


    public abstract OpxiCMEntityProfile readPoolTargetProfile( String dn ) throws ProfileException;


    public void updatePoolTargetProfile( OpxiCMEntityProfile profile, String contentTYpe, byte[] waitAudio ) throws ProfileException {
        getServiceFactory().getPoolService().poolProfileUpdateNotification( profile );
    }


    public abstract void deletePoolTargetProfile( String poolDN ) throws ProfileException;

    public abstract void removeAttachment( String dN ) throws ProfileException;


    /**
     * Utility Methods
     */
    private static QueueProfile qp = null;

    private QueueProfile defaultQueueProfile() {
        if ( qp == null ) {
            qp = new QueueProfile();
            qp.setMaxDepth( 10 );
            qp.setMaxWaitingTime( 30 );
            qp.setWaitingAudio( "http://emptyWaitingAudio" );
        }
        return qp;
    }

    private static OpxiCMEntityProfile skillProfile = null;

    public OpxiCMEntityProfile defaultSkillProfile() {
        if ( skillProfile == null ) {
            skillProfile = new OpxiCMEntityProfile();
            OpxiCMEntityProfileChoice ch = new OpxiCMEntityProfileChoice();
            skillProfile.setOpxiCMEntityProfileChoice( ch );
            PoolTargetProfile ptp = new PoolTargetProfile();
            ptp.addQueueProfile( defaultQueueProfile() );
            //        ptp.setDN();
            ptp.setType( LIARSkillBasedPool.class.getName() );
            PoolTargetProfileChoice ptpc = new PoolTargetProfileChoice();
            SkillProfile sp = new SkillProfile();
            ptpc.setSkillProfile( sp );
            ptp.setPoolTargetProfileChoice( ptpc );
            MatchingRule rule = new MatchingRule();
            rule.setClassName( AlwaysFalseRule.class.getName() );
            sp.setMatchingRule( rule );
            sp.setPSSC( 1.0f );
            sp.setSSSC( 0.5f );
            skillProfile.getOpxiCMEntityProfileChoice().setPoolTargetProfile( ptp );
        }
        return skillProfile;
    }

    private static OpxiCMEntityProfile groupProfile = null;

    public OpxiCMEntityProfile defaultWorkgroupProfile() {
        if ( groupProfile == null ) {
            groupProfile = new OpxiCMEntityProfile();
            OpxiCMEntityProfileChoice ch = new OpxiCMEntityProfileChoice();
            groupProfile.setOpxiCMEntityProfileChoice( ch );
            PoolTargetProfile ptp = new PoolTargetProfile();
            ptp.addQueueProfile( defaultQueueProfile() );
            //        ptp.setDN();
            ptp.setType( LIARWorkgroupPool.class.getName() );
            PoolTargetProfileChoice ptpc = new PoolTargetProfileChoice();
            GroupProfile gp = new GroupProfile();
//            gp.setFrom( new Date() );
//            gp.setTo( new Date() );
//            gp.setRepeat( RepeatType.DAILY );
            ptpc.setGroupProfile( gp );
            ptp.setPoolTargetProfileChoice( ptpc );
            groupProfile.getOpxiCMEntityProfileChoice().setPoolTargetProfile( ptp );
        }
        return groupProfile;
    }

    private static OpxiCMEntityProfile agentProfile = null;

    public OpxiCMEntityProfile defaultAgentProfile() {
        if ( agentProfile == null ) {
            agentProfile = new OpxiCMEntityProfile();
            OpxiCMEntityProfileChoice ch = new OpxiCMEntityProfileChoice();
            agentProfile.setOpxiCMEntityProfileChoice( ch );
            AgentProfile ap = new AgentProfile();
//            ap.setAction( ProfileAction.DELETE );
            ap.setMaxOpenCalls( 1 );
//            ap.setTrunkDefaultAccess( false );
            //        ap.setDN( dn );
            GreetingAudio ga = new GreetingAudio();
            ga.setSrc( "http://emptySoundURI" );
            /*yousefi - payam*/
            ap.addGreetingAudio( ga );
            agentProfile.getOpxiCMEntityProfileChoice().setAgentProfile( ap );
        }
        return agentProfile;
    }

    protected boolean isSkillProfile( OpxiCMEntityProfile profile ) {
        return profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getPoolTargetProfileChoice().getSkillProfile() != null
                &&
                profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getPoolTargetProfileChoice().getGroupProfile() == null;
    }

    protected static final String DEFAULT_MATCHING_RULE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "\n" +
            "<matching-rule>\n" +
            "    <and>\n" +
            "        <equal>\n" +
            "            <group attribute=\"name\" />\n" +
            "            <request attribute=\"uri.user\"/>\n" +
            "        </equal>\n" +
            "    </and>\n" +
            "</matching-rule>";

}