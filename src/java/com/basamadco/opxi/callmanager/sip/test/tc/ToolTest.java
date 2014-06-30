package com.basamadco.opxi.callmanager.sip.test.tc;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.DAOFactoryException;
import com.basamadco.opxi.callmanager.entity.dao.webdav.ProfileDAO;
import com.basamadco.opxi.callmanager.entity.profile.*;
import com.basamadco.opxi.callmanager.pool.PoolTarget;
import com.basamadco.opxi.callmanager.sip.test.Test;
import com.basamadco.opxi.callmanager.web.services.AdminServiceImpl;

import java.io.StringReader;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Jrad
 *         Date: Sep 20, 2006
 *         Time: 11:55:54 AM
 */
public class ToolTest extends Test {

    public ToolTest() {
//        setEnabled();
    }

    public void run() {

        class A {

        }
        class B extends A {

        }
        Set<A> list = new HashSet<A>();
        list.add( new A() );
        list.add( new B() );
        list.add( new A() );
        for ( A a : list ) {
            System.out.println( a );
        }

//        delete();
//        read();
//        update();
//        create();
//        putTest();
//        getTest();
    }

    public void create() {
        try {
            AdminServiceImpl service = new AdminServiceImpl();
//            service.createAgentProfile( "CN=agent01,CN=Employees,OU=OPXi,DC=cc,DC=basamad,DC=acc" );
//            service.createSkillProfile( BaseDAOFactory.getDirectoryDAOFactory().getPoolTargetDAO().read( "sales" ).getDN() );
            service.createWorkgroupProfile( BaseDAOFactory.getDirectoryDAOFactory().getPoolTargetDAO().read( "group01" ).getDN() );
        } catch ( Exception e ) {
            System.out.println( e );
            e.printStackTrace();
        }
    }

    private static final String AP = "<?xml version='1.0' ?>\n" +
            "<opxiCMEntityProfile xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://opxi.basamadco.com/opxiCMEntityProfile.xsd\" xsi:schemaLocation=\"http://opxi.basamadco.com/opxiCMEntityProfile.xsd file://c:\\java\\workspace\\opxiCallManager\\src\\resources\\opxiCMEntityProfile.xsd\">\n" +
            "\t<AgentProfile MaxOpenCalls=\"3\" DN=\"CN=agent01,CN=Employees,OU=OPXi,DC=cc,DC=basamad,DC=acc\">\n" +
            "\t\t<GreetingAudio Src=\"http://opxiappserver/public/melody.vox\"/>\t\t\n" +
            "\t</AgentProfile>\t\n" +
            "</opxiCMEntityProfile>";
    private static final String GP = "<?xml version='1.0' ?>\n" +
            "<opxiCMEntityProfile xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://opxi.basamadco.com/opxiCMEntityProfile.xsd\" xsi:schemaLocation=\"http://opxi.basamadco.com/opxiCMEntityProfile.xsd file://c:\\java\\workspace\\opxiCallManager\\src\\resources\\opxiCMEntityProfile.xsd\">\n" +
            "\t<PoolTargetProfile DN=\"CN=group01,CN=Workgroups,OU=OPXi,DC=cc,DC=basamad,DC=acc\" Type=\"com.basamadcp.Pool\">\n" +
            "\t\t<QueueProfile WaitingAudio=\"\" MaxDepth=\"50\" MaxWaitingTime=\"100\"/>\n" +
            "\t\t<GroupProfile/>\n" +
            "\t</PoolTargetProfile>\n" +
            "</opxiCMEntityProfile>";
    private static final String SP = "<?xml version='1.0' ?>\n" +
            "<opxiCMEntityProfile xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://opxi.basamadco.com/opxiCMEntityProfile.xsd\" xsi:schemaLocation=\"http://opxi.basamadco.com/opxiCMEntityProfile.xsd file://c:\\java\\workspace\\opxiCallManager\\src\\resources\\opxiCMEntityProfile.xsd\">\t\n" +
            "\t<PoolTargetProfile DN=\"CN=sales,CN=Skills,OU=OPXi,DC=cc,DC=basamad,DC=acc\" Type=\"somePool\">\n" +
            "\t\t<QueueProfile WaitingAudio=\"asasas\" MaxDepth=\"12\" Exp=\"\" MaxWaitingTime=\"13\"/>\n" +
            "\t\t<SkillProfile SSSC=\"2\" PSSC=\"1\">\n" +
            "\t\t</SkillProfile>\n" +
            "\t</PoolTargetProfile>\n" +
            "</opxiCMEntityProfile>";
    private static final String MR = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "\n" +
            "<matching-rule>\n" +
            "    <and>\n" +
            "        <equal>\n" +
            "            <group attribute=\"name\" />\n" +
            "            <request attribute=\"uri.user\"/>\n" +
            "        </equal>\n" +
            "    </and>\n" +
            "</matching-rule>";

    public void update() {
        try {
            String s = "<opxiCMEntityProfile xmlns=\"http://opxi.basamadco.com/opxiCMEntityProfile.xsd\">" +
                    "<PoolTargetProfile DN=\"CN=sales,CN=Skills,OU=OPXi,DC=cc,DC=basamad,DC=acc\">" +
                    "\t\t<QueueProfile WaitingAudio=\"asasas\" MaxDepth=\"12\" Exp=\"\" MaxWaitingTime=\"13\"/>\n" +
                    "<SkillProfile PSSC=\"1.0\" SSSC=\"1.0\"><MatchingRule><URI>&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;\n" +
                    "\n" +
                    "&lt;matching-rule&gt;\n" +
                    "    &lt;and&gt;\n" +
                    "        &lt;equal&gt;\n" +
                    "            &lt;group attribute=\"name\" /&gt;\n" +
                    "            &lt;request attribute=\"uri.user\"/&gt;\n" +
                    "        &lt;/equal&gt;\n" +
                    "    &lt;/and&gt;\n" +
                    "&lt;/matching-rule&gt;</URI></MatchingRule></SkillProfile></PoolTargetProfile></opxiCMEntityProfile>";
            AdminServiceImpl service = new AdminServiceImpl();
//            service.updateAgentProfile( AP, new byte[] { 'a', 'b' } );
//            service.updatePoolProfile( s, new byte[]{'a', 'b'} );
//            service.updateWorkgroupProfile( GP, new byte[] { 'a', 'b' } );
        } catch ( Exception e ) {
            e.printStackTrace();
            System.out.println( e );
        }
    }

    public void read() {
        try {
            AdminServiceImpl service = new AdminServiceImpl();
            String s = service.readAgentProfile( "CN=agent01,CN=Employees,OU=OPXi,DC=cc,DC=basamad,DC=acc" );
            OpxiCMEntityProfile p = OpxiCMEntityProfile.unmarshal( new StringReader( s ) );
            System.out.println( s );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void delete() {
        try {
            AdminServiceImpl service = new AdminServiceImpl();
            service.deleteAgentProfile( "CN=agent01,CN=Employees,OU=OPXi,DC=cc,DC=basamad,DC=acc" );
            System.out.println( "success!" );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void getTest() {
        try {
            OpxiCMEntityProfile profile;
            PoolTarget target = (PoolTarget) BaseDAOFactory.getDirectoryDAOFactory().getPoolTargetDAO().read( "group01" );
            ProfileDAO ptpd = BaseDAOFactory.getWebdavDAOFactory().getPoolTargetProfileDAO( target.getName() );
            profile = ptpd.readProfile();
            System.out.println( profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile() );
            System.out.println( "Success..." );
        } catch ( DAOException e ) {
            System.out.println( e );
        } catch ( DAOFactoryException e ) {
            System.out.println( e );
        } catch ( OpxiException e ) {
            System.out.println( e );
        }
    }

    public void putTest() {
        try {
            OpxiCMEntityProfile profile = new OpxiCMEntityProfile();
            PoolTargetProfile p = new PoolTargetProfile();
            p.setDN( "myDN" );
            p.setType( "myType" );
            QueueProfile qp = new QueueProfile();
            qp.setExp( "exp" );
            qp.setMaxDepth( 2 );
            qp.setMaxWaitingTime( 60 );
            qp.setWaitingAudio( "http://" );
            p.addQueueProfile( qp );
//            SkillProfile s = new SkillProfile();
//            s.setPSSC( 0.1f );
//            s.setSSSC( 2.0f );
//            MatchingRule rule = new MatchingRule();
//            rule.setURI( "matching-rule.xml" );
//            s.setMatchingRule( rule );
            GroupProfile g = new GroupProfile();
            PoolTargetProfileChoice ch = new PoolTargetProfileChoice();
//            ch.setSkillProfile( s );
            ch.setGroupProfile( g );
            p.setPoolTargetProfileChoice( ch );
            profile.getOpxiCMEntityProfileChoice().setPoolTargetProfile( p );
            PoolTarget target = (PoolTarget) BaseDAOFactory.getDirectoryDAOFactory().getPoolTargetDAO().read( "group01" );
            ProfileDAO ptpd = BaseDAOFactory.getWebdavDAOFactory().getPoolTargetProfileDAO( target.getName() );
            ptpd.writeProfile( profile );
            System.out.println( "Success..." );
        } catch ( DAOException e ) {
            System.out.println( e );
        } catch ( DAOFactoryException e ) {
            System.out.println( e );
        } catch ( OpxiException e ) {
            System.out.println( e );
        }
    }

}
