package com.basamadco.opxi.callmanager.sip.test.tc;

import com.basamadco.opxi.callmanager.sip.test.Test;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.profile.*;
import com.basamadco.opxi.callmanager.entity.profile.types.RepeatType;

import java.util.Date;
import java.util.Calendar;

/**
 * @author Jrad
 *         Date: Nov 8, 2006
 *         Time: 9:49:00 AM
 */
public class WSTest extends Test {


    public WSTest() {
//        setEnabled();
    }

    public void run() {
        try {
            String xml = "";

            /*OpxiCMEntityProfile p = new OpxiCMEntityProfile();
            OpxiCMEntityProfileChoice c = new OpxiCMEntityProfileChoice();
            AgentProfile ap = new AgentProfile();
            ap.setDN( BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO().getDN( "agent05" ) );
            ap.setMaxOpenCalls( 1 );

            SkillScore ss = new SkillScore();
            ss.setSkillName( "Farsi" );
            ss.setSkillScore( 1.0f );
            SkillScore ss2 = new SkillScore();
            ss2.setSkillName( "sales" );
            ss2.setSkillScore( 1.0f );
            ap.addSkillScore( ss );
            ap.addSkillScore( ss2 );
            GreetingAudio ga = new GreetingAudio();
            ga.setSrc( "http://opxiappserver.cc.basamad.acc/exchange/agent05/opxi/callmanager/greetingAudio" );
            ap.addGreetingAudio( ga );
            c.setAgentProfile( ap );
            p.setOpxiCMEntityProfileChoice( c );
            ApplicationIntegration ai = new ApplicationIntegration();
            Application app = new Application();
            app.setName( "CN=voiceApp,CN=Services,OU=OPXi,DC=cc,DC=basamad,DC=acc" );
            app.setExpression( "timer.create(10000)" );
            app.setPriority( 5 );
            Participation pp = new Participation();
            pp.setParty( "Caller" );
            pp.setRole( "required" );
            Participation pp2 = new Participation();
            pp2.setParty( "Agent" );
            pp2.setRole( "teardown" );
            app.addParticipation( pp );
            app.addParticipation( pp2 );
            ai.addApplication( app );
            p.setApplicationIntegration( ai );*/
//            BaseDAOFactory.getWebdavDAOFactory().getAgentProfileDAO( "agent05" ).writeProfile( p );

            OpxiCMEntityProfile p = BaseDAOFactory.getWebdavDAOFactory().getPoolTargetProfileDAO( "hardWorkers" ).readProfile();
            GroupProfile wg = new GroupProfile();
            Calendar from = Calendar.getInstance();
            from.set( 2009, Calendar.MARCH, 20, 8, 0 );
            wg.setFrom( from.getTime() );

            Calendar to = Calendar.getInstance();
            to.set( 2009, Calendar.NOVEMBER, 12, 16, 15 );
            wg.setTo( to.getTime() );

            wg.setRepeat( RepeatType.DAILY );

            p.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getPoolTargetProfileChoice().setGroupProfile( wg );

            Application app = new Application();
            app.setName( "CN=voiceApp,CN=Services,OU=OPXi,DC=cc,DC=basamad,DC=acc" );
            app.setExpression( "this.agentsOnline==0" );
            app.setPriority( 5 );
            Participation pp = new Participation();
            pp.setParty( "Caller" );
            pp.setRole( "required" );
            app.addParticipation( pp );
            ApplicationIntegration ai = new ApplicationIntegration();
            ai.addApplication( app );
            p.setApplicationIntegration( ai );
            BaseDAOFactory.getWebdavDAOFactory().getPoolTargetProfileDAO( "hardWorkers" ).writeProfile( p );
            /*AdminService_ServiceLocator sl = new AdminService_ServiceLocator();
            sl.getAdminServicePort().updatePoolProfile( xml, null );
            System.out.println( "bean: " + bean.getMessage() );
            System.out.println( "bean: " + bean.isLoveMe() );*/
            /*for( int i = 0; i < res.length; i++ ) {
                for (int j = 0; j < res[i].length; j++) {
                    String re = res[i][j];
                    System.out.println( "[" + i + ", " + j + "]='" + re + "'" );
                }
            }*/
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
