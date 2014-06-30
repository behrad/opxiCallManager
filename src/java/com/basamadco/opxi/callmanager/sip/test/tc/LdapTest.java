package com.basamadco.opxi.callmanager.sip.test.tc;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryDAOFactory;
import com.basamadco.opxi.callmanager.sip.test.Test;

import java.util.List;

public class LdapTest extends Test {

//    private static Logger logger = Logger.getLogger( LdapTest.class.getName() );

    public LdapTest() {
//        setEnabled();
    }

    public void run() {
//        logger.finer( "Running LdapTest... " );
        try {

            DirectoryDAOFactory adao = (DirectoryDAOFactory) BaseDAOFactory.getDAOFactory( BaseDAOFactory.DIRECTORY );

            try {

                List<String> l = adao.getAgentDAO().getPoolMemberships( "agent05" );
                for ( String s : l ) {

                    System.out.println( "&&&&&&&&&&&& " + s );
                }
//                System.out.println( "&&&&&&&&&&&& " + adao.getCallTargetDAO().getDN( "sales" ) );
//                System.out.println( "&&&&&&&&&&&& " + adao.getPoolTargetDAO().getDN( "sales" ) );
//                System.out.println( "&&&&&&&&&&&& " + adao.getQueueTargetDAO().getDN( "sales" ) );
//                adao.getCallTargetDAO().getCallTargetByPhoneNumber( "" );
//                System.out.println( ((String)adao.getAgentDAO().read( "agent01" ).getHomeURI() ) );
//                System.out.println( ((String)adao.getPoolTargetDAO().getHomeURI( "sales" ) ) );
//                System.out.println( ((String)adao.getPoolTargetDAO().getHomeURI( "group01" ) ) );

//                adao.getAgentDAO().updateAttribute( "agent04", "opxiCMProfileURI", "http://opxiappserver.cc.basamad.acc/exchange/agent04/opxi" );

//                DirectoryEntity entity = adao.getCallTargetDAO().getCallTargetByName( "agent03" )
//                List list = adao.getAgentDAO().getPoolMemberships( "agent03" );
//                for (int i = 0; i < list.size(); i++) {
//                    String s = (String) list.get( i );
//                    System.out.println( i + ": " + s );
//                }
//                DirectoryEntity entity2 = adao.getCallTargetDAO().read( "sales" );
//                System.out.println( "Entity: " + entity2 );
//                DirectoryEntity entity3 = adao.getCallTargetDAO().read( "group01" );
//                System.out.println( "Entity: " + entity3 );
//                DirectoryEntity entity4 = adao.getCallTargetDAO().read( "voiceApp" );
//                System.out.println( "Entity: " + entity4 );

//                String[] s = adao.getPoolTargetDAO().getMembers( "Employee-Service-Managers" );
//                for (int i = 0; i < s.length; i++) {
//                    String s1 = s[i];
//                    System.out.println( "+==: " + s1 );
//                }
//                System.out.println( "entity: " + calltarget );
//                CallTarget ct = adao.getCallTargetDAO().getCallTargetByName( "sales" );
//                if( ct.isVoiceApplication() ) {
//                    Application app = (Application)ct;
//                    System.out.println( "Voice app: " + app.getName() );
//                    System.out.println( "Voice app url: " + app.getUrl() );
//                } else {
//                    System.out.println( "Queue target: " + ct.getName() );
//                    System.out.println( ((QueueTarget)ct).getWaitingMsgURI() );
//                }
//                System.out.println( adao.getCallTargetDAO().getAttributeValue( "voiceApp", "isApplication" ) );
//                System.out.println( adao.getCallTargetDAO().getAttributeValue( "voiceApp", "keywords" ) );
            } catch ( DAOException e ) {
                System.out.println( e );
//            } catch ( EntityNotExistsException e ) {
//                System.out.println( e );
            } catch ( Throwable e ) {
                System.out.println( e );
            }

//            String admin = adao.getAgentDAO().getManagerNameFor( "agent01" );
//            System.out.println( "3: " + admin );
//            System.out.println( "4: " + adao.getAgentDAO().getUserPassword( admin ) );
//            adao.getQueueTargetDAO().updateAttribute( "sales", "soundUrl", "http://opximasterserver/opxiCallManager/melody.vox" );
//            adao.getAgentDAO().updateAttribute( "agent01", "soundUrl", "http://opximasterserver/opxiCallManager/Greeting.vox" );
//            System.out.println( adao.getQueueTargetDAO().getAttributeValue( "sales", "soundUrl" ) );
//            System.out.println( adao.getQueueTargetDAO().getAttributeValue( "skill-1144493326805", "cn" ) );
//            adao.getAgentDAO().updateAttribute( "agent03", "maxOpenCalls", "2" );
//            System.out.println( adao.getAgentDAO().getAttributeValue( "agent03", "maxOpenCalls" ) );
//            adao.getQueueTargetDAO().updateAttribute( "sales", "maxLength-queue", "15" );
//            System.out.println( adao.getAgentDAO().getAttributeValue( "agent01", "skillEfficiency" ) );
//            System.out.println( adao.getPoolTargetDAO().getAttributeValue( "skill-1144493326805", "matchingRuleString" ) );
//            System.out.println( ((QueueTarget)adao.getQueueTargetDAO().read( "agent01" ) ).getTelephoneNumber() );
//            String cn = adao.getPoolTargetDAO().getCNForName( "sales" );
//            System.out.println( ((PoolTarget)adao.getPoolTargetDAO().read( cn ) ).getMatchingRule() );
//            System.out.println( adao.getPoolTargetDAO().read( cn ) );
//            System.out.println( adao.getAgentDAO().read( "agent01" ) );
//            System.out.println( adao.getQueueTargetDAO().read( cn ) );

            //            adao.getAgentDAO().updateAttribute( "agent01", "greetingMsgURI", "http%3a//192.168.128.30/opxiCallManager/vxml?name=greet.xml" );
//            System.out.println( adao.getAgentDAO().getAttributeValue( "agent01", "greetingMsgURI" ) );
//            System.out.println( adao.getAgentDAO().getAttributeValue( "agent01", "displayName" ) );
//            adao.getGroupDAO().updateAttribute( adao.getGenericDAO().getCNForName( "sales" ), "queueDepth", "2" );
//            System.out.println( adao.getGroupDAO().getAttributeValue( adao.getGenericDAO().getCNForName( "sales" ), "queueDepth" ) );
//            System.out.println( "===" );
//            adao.getGroupDAO().updateAttribute( "sales", "opxi-pssc", "1.0" );
//            adao.getGroupDAO().updateAttribute( "sales", "opxi-sssc", "0.1" );
//            adao.getGroupDAO().updateAttribute( "info", "opxi-pssc", "1.0" );
//            adao.getGroupDAO().updateAttribute( "info", "opxi-sssc", "0.1" );
//            adao.getGroupDAO().updateAttribute( "english", "opxi-pssc", "1.0" );
//            adao.getGroupDAO().updateAttribute( "english", "opxi-sssc", "0.1" );
//
//            adao.getAgentDAO().updateAttribute( "agent01", "skillEfficiency", "sales:1.0" );
//            adao.getAgentDAO().updateAttribute( "agent02", "skillEfficiency", "info:1.0" );
//            adao.getAgentDAO().updateAttribute( "agent03", "skillEfficiency", "english:1.0" );
//
//            adao.getAgentDAO().updateAttribute( "agent04", "skillEfficiency", new String[] { "english:1.0", "sales:0.5" } );

//            DirectoryResult res = adao.getGenericDAO().searchByPhoneNumber( "1111" );
//            System.out.println( "===================== " + res.getCN() + " a" + res.isAgent() + " g" + res.isGroup() );

//            adao.getGroupDAO().updateAttribute( "sales", "waitingMsgURI", "http%3a//192.168.128.30/opxiCallManager/vxml" );
//            adao.getGroupDAO().updateAttribute( "info", "waitingMsgURI", "http%3a//192.168.128.30/opxiCallManager/vxml" );
//            adao.getGroupDAO().updateAttribute( "english", "waitingMsgURI", "http%3a//192.168.128.30/opxiCallManager/vxml" );
//            QueueTarget e = adao.getGroupDAO().readQueueTarget( "info" );
//            System.out.println( e.getName() );
//            System.out.println( e.getGreetingMsgURI() );
//            System.out.println( e.getHuntingPolicy() );

//            adao.getAgentDAO().updateAttribute( "agent04", "maxConcurrentCalls", "1" );

//            UserAgent ua = new UserAgent( "behrad", "cc.basamad.acc" );
//            Agent agent = new Agent( ua );
//            agent.setMaxOpenCalls( new Integer( 2 ) );
//            agent.setTelephoneNumber( "88941253" );
//            agent.setGreetingMsgURI( "http://some.uri.vox" );

//            String[] skills = adao.getAgentDAO().getPoolMemberships( "agentMan" );
//            for (int i = 0; i < skills.length; i++) {
//                String skill = skills[i];
//                System.out.println( "=============*******=============SKILL: " + skill );
//            }

//            update( "agent01" );
//            update( "agent02" );
//            update( "agent03" );
//            update( "agent04" );
//            update( "agent05" );
//            update( "agent06" );

//            Group g = (Group)adao.getGroupDAO().read( "Group11" );
//            adao.getAgentDAO().addMembership( "adighodi", "Group11" );
//            adao.getAgentDAO().addMembership( "behrad2", "Group11" );
//            List skills = adao.getAgentDAO().getPoolMemberships( "hamedhatami" );
//            for (int i = 0; i < skills.size(); i++) {
//                Object o =  skills.get( i );
//                System.out.println( "===============SKILL: " + o );
//            }

//            adao.getGroupDAO().updateAttribute( "sales", "matchingRuleString", xml );
//            adao.getGroupDAO().updateAttribute( "info", "matchingRuleString", xml );

//            adao.getGroupDAO().updateAttribute( "english", "matchingRuleString", xml );
//
//
//            System.out.println( "$$$$$$$$$$$$$$ " + adao.getGroupDAO().getAttributeValue( "group1", "cn" ) );

//            System.out.println( adao.getGroupDAO().getAttributeValue( "info", "matchingRuleString" ) );
//            System.out.println( adao.getGroupDAO().getAttributeValue( "english", "matchingRuleString" ) );
//            System.out.println( adao.getAgentDAO().getAttributeValue( "adighodi", "matchingRuleString" ) );

//            adao.getAgentDAO().updateAttribute( "behrad2", "matchingRuleString", xml );
//            adao.getAgentDAO().updateAttribute( "adighodi", "matchingRuleString", xml );

//            String matchingRuleXml = adao.getAgentDAO().getMatchingRule( "agentMan" );
//            System.out.println( "=============*******=============M R XML: " + matchingRuleXml );
//            adao.getAgentDAO().updateAttribute( "hamedhatami", "maxConcurrentCalls", "2" );
//            adao.getAgentDAO().updateAttribute( "hamedhatami", "greetingMsgURI", "http://greet.world" );
//            adao.getAgentDAO().updateAttribute( "hamedhatami", "waitingMsgURI", "http://wait.world" );
//            adao.getAgentDAO().updateAttribute( "hamedhatami", "queueDepth", "2" );
//            adao.getAgentDAO().updateAttribute( "hamedhatami", "huntingPolicy", "SingleAgentHuntingPolicy" );
//            adao.getAgentDAO().updateAttribute( "hamedhatami", "telephoneNumber", "128" );
//
//            adao.getAgentDAO().updateAttribute( "agentMan", "maxConcurrentCalls", "3" );
//            adao.getAgentDAO().updateAttribute( "agentMan", "waitingMsgURI", "http://wait.world" );
//            adao.getAgentDAO().updateAttribute( "agentMan", "queueDepth", "3" );
//            adao.getAgentDAO().updateAttribute( "agentMan", "huntingPolicy", "SingleAgentHuntingPolicy" );
//            adao.getAgentDAO().updateAttribute( "agentMan", "telephoneNumber", "129" );
//            System.out.println( "================= attrib value: " + adao.getAgentDAO().getAttributeValue( "hamedhatami", "maxConcurrentCalls" ) );

//            String cn = "agentMan"; //
//            String groupCn = "Employees";
//            Agent callAgent = adao.getAgentDAO().readAgentFor( new UserAgent( "agentMan", "domain" ) );
//            System.out.println( "===================== Agent greeting uru: " + callAgent.getGreetingMsgURI() );

//            AttributeResolver gar = new GroupAttributeResolver( "greetingMsgURI" );
//            System.out.println( "================= Value of greetingMsgURI: " + gar.getValue( "Group1" ) );

//            DirectoryEntry agent = adao.getDirectoryDAO().readAgentByName( cn );
//            System.out.println( "DN: " + agent.getCN() );
//            for( int i = 0; i < agent.getMemberOf().length; i++ ) {
//                System.out.println( "Membership group: " + agent.getMemberOf()[ i ] );
//            }
//            System.out.println( " This should be True: " + adao.getDirectoryDAO().isGroup( groupCn ) );
//            System.out.println( " This should be False: " + adao.getDirectoryDAO().isGroup( "xyz" ) );
//            Map agent = adao.getDirectoryAgentDAO().search( filter, new String[]{ "memberOf", "cn" } );
//
//            for( int i = 0; i < agent.length; i++ ) {
//                for( int j = 0; j < agent[i].length; j++ ) {
//                    System.out.print( "Attribute= " +  agent[i][j] + ", " );
//                }
//                System.out.println();
//            }
//			AgentDAO adao = DatabaseDAOFactory.getDAOFactory( DatabaseDAOFactory.DIRECTORY ).getCallAgentDAO();
////			List list = adao.getAllAgents();
////			for( int i = 0; i < list.size(); i++ ) {
////				logger.finer( "Agent name: " + ((Agent)list.get( i )).getUserName() );
////			}
////			? i++ ) {
////				logger.finer( "Group name: " + ((Group)groups.get( i )).getMessage() );
////				List listAgents = adao.getAgentsInGroup( (Group)groups.get( i ) );
////				for( int j = 0; j < listAgents.size(); j++ ) {
////					logger.finer( "Agent name: " + listAgents.get( j ) );
////				}
////			}
//		logger.finer( "LdapTest completed successfully... " );
            System.out.println( "=========================================================" );
        } catch ( OpxiException e ) {
//            logger.severe( "LdapTest Failed..." + e );
        } catch ( Exception e ) {
//            logger.severe( "LdapTest Failed..." + e.getClass() + " " + e );
            System.out.println( "ERROR: " + e.getMessage() );
            e.printStackTrace();
        }
    }

    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<!DOCTYPE sip-app" + " PUBLIC \"-//AC and C Basamad Co//DTD SIP Matching Rule 1.0//EN\"" +
            " \"http://www.basamadco.com/dtd/matching-rule-1.0.dtd\">" +

            "<matching-rule>" +
            "<and>" +
            "<equal>" +
            "<group attribute=\"name\"/>" +
            "<literal value=\"english\"/>" +
            "</equal>" +
            "</and>" +
            "</matching-rule>";

    private void update( String name ) throws Exception {
        DirectoryDAOFactory adao = (DirectoryDAOFactory) BaseDAOFactory.getDAOFactory( BaseDAOFactory.DIRECTORY );
        adao.getAgentDAO().updateAttribute( name, "maxConcurrentCalls", "2" );
        adao.getAgentDAO().updateAttribute( name, "greetingMsgURI", "http://greet.world" );
        adao.getAgentDAO().updateAttribute( name, "waitingMsgURI", "http://wait.world" );
        adao.getAgentDAO().updateAttribute( name, "queueDepth", "2" );
        adao.getAgentDAO().updateAttribute( name, "huntingPolicy", "SingleAgentHuntingPolicy" );
        adao.getAgentDAO().updateAttribute( name, "telephoneNumber", "128" );
        adao.getAgentDAO().updateAttribute( name, "matchingRuleString", xml );
    }

}
