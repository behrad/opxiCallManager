package com.basamadco.opxi.callmanager.sip.test.tc;

import com.basamadco.opxi.callmanager.entity.Registration;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.entity.dao.database.DatabaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.database.hbm.HibernateDAOFactory;
import com.basamadco.opxi.callmanager.sip.test.Test;
import net.sf.hibernate.Criteria;
import net.sf.hibernate.expression.Expression;

import java.util.Date;
import java.util.List;

public class HbmTest extends Test {
	
	public HbmTest() {
//		setEnabled();
	}         
	
	public void run() {
		
		try {
            /*System.out.println( "==================================================================================" );
            HibernateDAOFactory daf = (HibernateDAOFactory)DatabaseDAOFactory.getDAOFactory( DatabaseDAOFactory.DATABASE );
            *//*SessionFactory sf = daf.configureHibernate();
            Session session = sf.openSession();
            Transaction tx = session.beginTransaction();*//*

            UserAgent ua = daf.getUserAgentDAO().find( "mram3d", "cc.basamad.acc" );
            *//*Criteria c = new Criteria();
            c.add( Expression.ge( "", new Date() ) );*//*
//            daf.getRegistrationDAO().find( ua, "192.168" );
            List list = daf.getRegistrationDAO().find( ua );
            System.out.println( "Size: " + list.size() );
            for (int i = 0; i < list.size(); i++) {
                Registration registration = (Registration) list.get( i );
                System.out.println( registration );
            }
            System.out.println( "==================================================================================" );
//            HuntingProfile h = new HuntingProfile( "SequentialHuntingPolicy" );
//            session.save( h );
//            Skill s = new Skill( "skill01" );
//            s.setHuntingProfile( h );
//            session.save( s );

//            QueueTarget qt = (QueueTarget)session.load( QueueTarget.class.getName(), new Long( 2 ) );
//            System.out.println( "++++++++++++++++++++ OLALA: " + qt.getHuntingPolicy() );
//            MemoryDAOFactory mdf = (MemoryDAOFactory)BaseDAOFactory.getDAOFactory( BaseDAOFactory.MEMORY );
//            QueueTarget qt = (QueueTarget)mdf.getQueueTargetDAO().getInstanceForName( "some skill" );
//            System.out.println( "========================= Object " + qt  );
//            System.out.println( "========================= QT " + qt.getGreetingMsgURI() );
//            System.out.println( "========================= QT " + qt.agentList().size() );
//            System.out.println( "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% " );
//            QueueTarget qt2 = (QueueTarget)mdf.getQueueTargetDAO().getInstanceForName( "some skill" );
//            System.out.println( "========================= Object " + qt2  );
//            System.out.println( "========================= QT " + qt2.getGreetingMsgURI() );
//            System.out.println( "========================= QT " + qt2.agentList().size() );

//            Long key = new Long( 1 );
//            QueueTarget qt2 = (QueueTarget) session.load( QueueTarget.class.getName(), key );
//            System.out.println( "========================= Object " + qt2  );
//            System.out.println( "========================= QT " + qt2.getGreetingMsgURI() );
//            System.out.println( "========================= QT " + qt2.getMembers().size() );
//            Skill skill = (Skill) session.load( Skill.class.getName(), key );
//            System.out.println( "========================= Object " + skill  );
//            System.out.println( "========================= Skill " + skill.getGreetingMsgURI() );
//            System.out.println( "========================= Skill " + skill.getMessage() );


//            Registration r = new Registration( new UserAgent( "bebe", "domain.com" ), "bebeslocation.domain.com" );
//            Presence p = new Presence();
//            p.setBasic( "basic" );
//            p.setNote( "note" );
//            p.setRegistration( r );
//            session.save( p );
//            Presence p1 = (Presence) session.load( Presence.class.getName(), new Long( 1 ) );
//            Registration r1 = (Registration) session.load( Registration.class.getName(), new Long( 1 ) );
//            System.out.println( "====================note: " + r1.getPresence().getNote() );
//            System.out.println( "====================agent: " + r1.getUserAgent().getMessage() );
//            UserAgent ua = new UserAgent( "nazi", "naz.com" );
//            Registration r = new Registration( ua );
//            r.setLocation( "nazi location" );
//            ua.addRegistration( r );
//            session.save( ua );



//            UserAgent ua2 = (UserAgent)session.load( UserAgent.class.getName(), new Long( 3 ) );
//            Hibernate.initialize( ua2.getRegistrations() );            
//            Group g = new Group( "some group" );
//            g.setGreetingMsgURI( "http://group.greetingMsgURI.com" );
//            g.setQueueDepth( new Integer( 2 ) );
//            Skill skill = new Skill( "some skill", "some skill desc" );
//            skill.setQueueDepth( new Integer( 5 ) );
//            skill.setGreetingMsgURI( "http://skill.gretting.com" );
//            g.addSkill( skill );
//            session.save( skill );

////            Group gr = (Group)session.load( Group.class.getName(), new Long( 1 ) );
////            tx.commit();
////			session.close();
////            System.out.println( "####################### Group Name    : " + gr.getMessage() );
////            System.out.println( "####################### Group Greeting: " + gr.getGreetingMsgURI() );
////            System.out.println( "####################### Group Skills  : " + gr.getPoolMemberships().size() );
//
////            Skill sk = (Skill)session.load( Skill.class.getName(), new Long( 2 ) );
////            System.out.println( "####################### Skill Name    : " + sk.getMessage() );
////            System.out.println( "####################### Skill Greeting: " + sk.getGreetingMsgURI() );
////            System.out.println( "####################### Skill Groups  : " + sk.getGroups().size() );
            *//*tx.commit();
			session.close();*//**/
        } catch( Exception e ) {
			e.printStackTrace();
		}
	}

}
