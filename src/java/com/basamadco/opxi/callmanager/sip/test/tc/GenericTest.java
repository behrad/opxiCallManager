package com.basamadco.opxi.callmanager.sip.test.tc;

import com.basamadco.opxi.callmanager.entity.profile.*;
import com.basamadco.opxi.callmanager.entity.dao.webdav.Storage;
import com.basamadco.opxi.callmanager.pool.LIARWorkgroupPool;
import com.basamadco.opxi.callmanager.pool.rules.AlwaysFalseRule;
import com.basamadco.opxi.callmanager.sip.presence.BusyEvent;
import com.basamadco.opxi.callmanager.sip.presence.OfflineEvent;
import com.basamadco.opxi.callmanager.sip.test.Test;
import com.basamadco.opxi.callmanager.rule.WorkgroupPresencePlan;
import com.basamadco.opxi.callmanager.logging.doc.AgentActivityDocument;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;

import com.novell.ldap.connectionpool.PoolManager;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.jcouchdb.db.ServerImpl;
import org.jcouchdb.db.Database;

import java.util.Date;
import java.util.Enumeration;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.net.Inet4Address;

/**
 * @author Jrad
 *         Date: Mar 12, 2006
 *         Time: 4:39:31 PM
 */
public class GenericTest extends Test {


    public GenericTest() {
        setEnabled();
    }


    public void run() {
        try {

            /*final String SERVER_ADDRESS = "192.168.128.40";
            final String DB_NAME = "opxiportal";
            String userName = "payam";
            String password = "123aaa)";

            ServerImpl serverImpl = new ServerImpl( SERVER_ADDRESS,
                    ServerImpl.DEFAULT_PORT );

            serverImpl.setCredentials( new AuthScope( SERVER_ADDRESS, ServerImpl.DEFAULT_PORT ),
                    new UsernamePasswordCredentials( userName, password )
            );

            Database db = new Database( serverImpl, DB_NAME );*/
            /*
            Pojo me = new Pojo( "behrad3" );
            me.setObject( new Pojo2( "myBehrad3" ) );
            Attachment attch = new Attachment( "text/xml", "<html><h1>salam</h1></html>".getBytes() );
            //http://COUCHDB_SERVER:PORT/DB_NAME/DOC_ID/ATTACHMENT_NAME
            me.addAttachment( "attch", attch );
*/
//            AgentActivityDocument doc = new AgentActivityDocument();
//            doc.setAgentName( "goftego@cc.basamad.acc" );

//            http://192.168.128.40:5984/opxiportal/51a15b4d47f486f2edeca9ac9600010a

            /*AgentActivityDocument aal = db.getDocument( AgentActivityDocument.class, "MoAleykom" );
//            AgentActivityDocument aal = new AgentActivityDocument();            
            System.out.println( "++++++++++++++++++++++++++++ " + aal.getType() );
            System.out.println( "++++++++++++++++++++++++++++ " + new Date( Long.valueOf( aal.getBeginTime() ) ) );
            System.out.println( "++++++++++++++++++++++++++++ " + new Date( Long.valueOf( aal.getEndTime() ) ) );
//            System.out.println( "++++++++++++++++++++++++++++ " + new String( aal.getLog( db ) ) );
            aal.setLog( aal.getLog( db ) );*/


//            AgentActivityDocument doc = new AgentActivityDocument();
//            doc.setBeginTime( Long.toString( System.currentTimeMillis() ) );
//            doc.setEndTime( Long.toString( System.currentTimeMillis() + 2000 ) );
//            doc.setAgentName( "babaei" );
//            doc.setLog( "salam".getBytes() );
//            doc.setId( "MoAleykom" );
//            JSONParser.defaultJSONParser().parse(  )
//            aal.setType( "babaghoori4" );
//            db.updateDocument( aal );
//            System.out.println( "++++++++++++++++++++++++++++ID: " + aal.getId() );
//            db.getDocument(  )


//            serverImpl.shutDown();


//            System.out.println( "_______________________________ " + "cc.basamad.acc".replaceAll( "\\.", "_" ) );


//            System.out.println( "__________________________" + Storage.valueOf( "exchange" ) );
            /*String st2 = "<dm:note>Busy</dm:note>";
            Matcher m = Pattern.compile( ".*<\\w+:note>(\\w*)</\\w+:note>.*" ).matcher( st );
            if ( m.matches() ) {
                System.out.println( m.groupCount() );
                for ( int i = 0; i < m.groupCount(); i++ ) {
                    System.out.println( "Matched: " + m.group( i ) );
                }
            }*/


            String st3 = "<note>Busy</note>";
//            System.out.println( "Matched: " + Pattern.compile( "<\\w+:note>(\\w*)</\\w+:note>" ).matcher( st3 ).matches() );
//            for( String token : text.split( "," ) ) {
//                System.out.println( "aaaa: '" + token + "'" );
//            }


//            long a = 100000;
//            long d = 60 * 10;
//            System.out.println( "==================== " + Math.sqrt( (double)(d*d) ) );
//            System.out.println( "==================== " + Math.sqrt( (double)(d*d + a) ) );
//            double x = ( d / Math.sqrt( d*d + a ) );
////            double x = (double)(0.5 * ( 1 - ( d / Math.sqrt( d*d + a ) ) ));
//            System.out.println( "++++++++++++++++++++++ " + x );


//            Calendar firstRun = Calendar.getInstance();
//            firstRun.set( Calendar.HOUR, 0 );
//            firstRun.set( Calendar.AM_PM, Calendar.AM );
//            firstRun.set( Calendar.MINUTE, 0 );
//            firstRun.set( Calendar.SECOND, 0 );
//            firstRun.set( Calendar.DATE, firstRun.get( Calendar.DATE ) + 1 );

//            System.out.println( "----- " + Calendar.MONDAY );

            /* AdminServiceImpl service = new AdminServiceImpl();
            String[][] rules = service.getRuleNames();
            for( String[] rule : rules ) {
                for( String ruleItem : rule ) {
                    System.out.print( ruleItem );
                }
                System.out.println( "---- " );
            }*/

            /*List values = BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO().getPoolMemberships("goftego");
            for (Object value : values ) {
                System.out.println( "++++++++++++++++++++++++ " + value );
            }*/

            OpxiCMEntityProfile profile = new OpxiCMEntityProfile();
            OpxiCMEntityProfileChoice ch = new OpxiCMEntityProfileChoice();
            profile.setOpxiCMEntityProfileChoice( ch );
            PoolTargetProfile ptp = new PoolTargetProfile();
            ptp.setDN( "CN=LG_Primary_Group,CN=Workgroups,OU=OPXi,DC=cc,DC=basamad,DC=acc" );

            QueueProfile qp = new QueueProfile();
            qp.setMaxDepth( 10 );
            qp.setMaxWaitingTime( 30 );
            qp.setWaitingAudio( "http://opxiappserver.cc.basamad.acc/public/opxiPublic/goftego/callmanager/waitingAudio" );
            qp.setIdleTimeToSchedule( 5 );
            ptp.addQueueProfile( qp );
            ptp.setType( LIARWorkgroupPool.class.getName() );
            PoolTargetProfileChoice ptpc = new PoolTargetProfileChoice();
            GroupProfile sp = new GroupProfile();
            ptpc.setGroupProfile( sp );
            ptp.setPoolTargetProfileChoice( ptpc );
            MatchingRule rule = new MatchingRule();
            rule.setClassName( AlwaysFalseRule.class.getName() );


            /*ApplicationIntegration ap = new ApplicationIntegration();
            Application app = new Application();

            app.setName( "AA09990" );
            app.setExpression( "call.state==18" );

            Participation caller = new Participation();
            caller.setParty( "Caller" );
            caller.setRole( "Required" );
            app.addParticipation( caller );
            Participation tg = new Participation();
            tg.setParty( "Transferee to Greeting" );
            tg.setRole( "Required" );
            app.addParticipation( tg );*/

            /*Participation agent = new Participation();
          agent.setParty( "Agent" );
          agent.setRole( "Teardown" );
          app.addParticipation( agent );*/

            /*Parameter p = new Parameter();
            p.setName( "postVisit" );
            p.setValue( "true" );
            app.addParameter( p );

            Parameter p1 = new Parameter();
            p1.setName( "queueName" );
            p1.setValue( "this.name" );
            app.addParameter( p1 );

            Parameter p2 = new Parameter();
            p2.setName( "agentName" );
            p2.setValue( "call.handlerAgent" );
            app.addParameter( p2 );
            ap.addApplication( app );
*/

            /*Application app2 = new Application();
            app2.setName( "AA09990" );
            app2.setExpression( "call.state==5 and timer.create(20000).timeout" );
            Participation caller2 = new Participation();
            caller2.setParty( "Caller" );
            caller2.setRole( "Required" );
            app2.addParticipation( caller2 );
            Participation agent2 = new Participation();
            agent2.setParty( "Agent" );
            agent2.setRole( "Teardown" );
            app2.addParticipation( agent2 );

            ap.addApplication( app2 );


            Application app3 = new Application();
            app3.setName( "AA09990" );
            app3.setExpression( "call.state==13" );
            Participation caller3 = new Participation();
            caller3.setParty( "Caller" );
            caller3.setRole( "Required" );
            app3.addParticipation( caller3 );
            Parameter pp = new Parameter();
            pp.setName( "queueLength" );
            pp.setValue( "this.pendingCallsSize" );
            app3.addParameter( pp );

            ap.addApplication( app3 );

            profile.setApplicationIntegration( ap );
*/
            RuleSet rs = new RuleSet();
            rs.setTargetService( "PresenceService" );


            Rule rr = new Rule();
            rr.setPriority( 1 );
            Parameter from = new Parameter();
            Parameter to = new Parameter();
            Parameter duration = new Parameter();
            from.setName( "from" );
            from.setValue( "0:00" );
            to.setName( "to" );
            to.setValue( "7:00" );
            duration.setName( "duration" );
            duration.setValue( "3" );
            Parameter busyAgentsLimit = new Parameter();
            busyAgentsLimit.setName( "usageAgentRatio" );
            busyAgentsLimit.setValue( "0.5" );
            Parameter utl = new Parameter();
            utl.setName( "usageTimeLimit" );
            utl.setValue( "20" );
            Parameter ucl = new Parameter();
            ucl.setName( "usageCountLimit" );
            ucl.setValue( "3" );


            rr.addParameter( busyAgentsLimit );
            rr.addParameter( from );
            rr.addParameter( to );
            rr.addParameter( duration );
            rr.addParameter( ucl );
            rr.addParameter( utl );
            rr.setName( WorkgroupPresencePlan.class.getName() );
            rr.setOnEvent( BusyEvent.class.getName() );
            rr.setMode( "Allowed" );
            rs.addRule( rr );
//          profile.addRuleSet( rs );


            Rule rr2 = new Rule();
            rr2.setPriority( 1 );
            /*Parameter from2 = new Parameter();
          Parameter to2 = new Parameter();
          Parameter duration2 = new Parameter();
          from2.setName( "from" );
          from2.setValue( "0:00" );
          to2.setName( "to" );
          to2.setValue( "7:00" );
          duration2.setName( "duration" );
          duration2.setValue( "3" );
          Parameter busyAgentsLimit2 = new Parameter();
          busyAgentsLimit2.setName( "usageAgentRatio" );
          busyAgentsLimit2.setValue( "1" );



          rr2.addParameter( busyAgentsLimit2 );
          rr2.addParameter( from2 );
          rr2.addParameter( to2 );
          rr2.addParameter( duration2 );*/
            rr2.setName( WorkgroupPresencePlan.class.getName() );
            rr2.setOnEvent( OfflineEvent.class.getName() );
            rr2.setMode( "NotAllowed" );
//          rs.addRule( rr2 );

//            profile.addRuleSet( rs );


            profile.getOpxiCMEntityProfileChoice().setPoolTargetProfile( ptp );


//            BaseDAOFactory.getWebdavDAOFactory().getPoolTargetProfileDAO("LG_Primary_Group").writeProfile(profile);

            /*String dir = "com/basamadco/opxi/callmanager/pool";
        System.out.println( this.getClass().getClassLoader().getResource( dir ).getPath() );*/
//            OpxiToolBox.getClasses( AgentPool.class );

            /*File f = new File( "C:\\Program%20Files\\IBM\\WebSphere\\AppServer14\\profiles\\AppSrv01\\installedApps\\opxiMasterServerNode01Cell\\opxiCallManager.ear\\opxiCallManager.sar\\WEB-INF\\classes\\com\\basamadco\\opxi\\callmanager\\pool\\rules".replaceAll( "%20", " " ) );


            System.out.println( "-----f='" + f.getAbsolutePath() + "'" );
            System.out.println( f.isDirectory() );*/
//            Registration reg1 = new Registration( );

            /*Set<Registration> s = new HashSet<Registration>();
            s.add( 2 );
            s.add( 3 );
            s.add( 2 );
            for( int i : s ) {
                System.out.println( i );
            }*/

        } catch ( Exception e ) {
            System.out.println( "In catch block..." + e );
        }
    }

}