package com.basamadco.opxi.callmanager.entity.dao.database.hbm;

import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.database.*;
import com.basamadco.opxi.callmanager.util.HibernateConfig;
import com.basamadco.opxi.callmanager.util.PropertyUtil;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;

public class HibernateDAOFactory extends DatabaseDAOFactory {
	
	/**
	 * Hibernate Connection username to be read from opxi.db.username property.
	 */
	private static final String USERNAME = PropertyUtil.getProperty( "opxi.db.username" );

	/**
	 * Hibernate Connection password to be read from opxi.db.password property.
	 */
	private static final String PASSWORD = PropertyUtil.getProperty( "opxi.db.password" );

	/**
	 * Hibernate Connection url to be read from opxi.db.conn_url property.
	 */
	private static final String CONN_URL = PropertyUtil.getProperty( "opxi.db.conn_url" );

	/**
	 * Hibernate Connection driver class to be read from opxi.db.driver property.
	 */
	private static final String DRIVER   = PropertyUtil.getProperty( "opxi.db.driver" );

	/**
	 * Hibernate DBMS dialect to be read from opxi.db.hibernateDialect property.
	 */
	private static final String DIALECT = PropertyUtil.getProperty( "opxi.db.hibernateDialect" );
	
	
	private static SessionFactory sessionFactory = null;
	
	
	public PresenceDAO getPresenceDAO() throws DAOException {
		try {
			return new HibernatePresenceDAO( this, configureHibernate() );
			
		} catch( MappingException e ) {
			throw new DAOException( e.getMessage() );
		} catch( HibernateException e ) {
			throw new DAOException( e.getMessage() );
		} catch( ClassNotFoundException e ) {
			throw new DAOException( e.getMessage() );
		}
	}

	public RegistrationDAO getRegistrationDAO() throws DAOException {
		try {
			return new HibernateRegistrationDAO( this, configureHibernate() );
			
		} catch( MappingException e ) {
			throw new DAOException( e.getMessage() );
		} catch( HibernateException e ) {
			throw new DAOException( e.getMessage() );
		} catch( ClassNotFoundException e ) {
			throw new DAOException( e.getMessage() );
		}
	}

	public SubscriptionDAO getSubscriptionDAO() throws DAOException {
		try {
			return new HibernateSubscriptionDAO( this, configureHibernate() );
			
		} catch( MappingException e ) {
			throw new DAOException( e.getMessage() );
		} catch( HibernateException e ) {
			throw new DAOException( e.getMessage() );
		} catch( ClassNotFoundException e ) {
			throw new DAOException( e.getMessage() );
		}
	}

	public UserAgentDAO getUserAgentDAO() throws DAOException {
		try {
			return new HibernateUserAgentDAO( this, configureHibernate() );
			
		} catch( MappingException e ) {
			throw new DAOException( e.getMessage() );
		} catch( HibernateException e ) {
			throw new DAOException( e.getMessage() );
		} catch( ClassNotFoundException e ) {
			throw new DAOException( e.getMessage() );
		}		
	}

	public DomainDAO getDomainDAO() throws DAOException {
		try {
			return new HibernateDomainDAO( this, configureHibernate() );
			
		} catch( MappingException e ) {
			throw new DAOException( e.getMessage() );
		} catch( HibernateException e ) {
			throw new DAOException( e.getMessage() );
		} catch( ClassNotFoundException e ) {
			throw new DAOException( e.getMessage() );
		}
	}

	public SessionFactory configureHibernate() throws MappingException, HibernateException, ClassNotFoundException {
		if( sessionFactory == null ) {
            Configuration config = new Configuration();
            config.configure();
            HibernateConfig hc = new HibernateConfig( config );
//			hc.addPackage( "com.basamadco.opxi.callmanager.entity" );

//			.setProperty( "hibernate.dialect", DIALECT )
//            .setProperty( "hibernate.connection.datasource", "jdbc/mysql" )
//            .setProperty( "hibernate.session_factory_name", "HibernateSessionFactory" )
////            .setProperty( "net.sf.hibernate.transaction.JTATransactionFactory",
////                    "net.sf.hibernate.transaction.WebSphereTransactionManagerLookup" )
//
////            .setProperty( "hibernate.connection.driver_class", DRIVER )
////			.setProperty( "hibernate.connection.url", CONN_URL )
//			.setProperty( "hibernate.connection.username",USERNAME )
//			.setProperty( "hibernate.connection.password", PASSWORD )
//
//            .setProperty( "hibernate.c3p0.min_size", "10" )
//			.setProperty( "hibernate.c3p0.max_size", "50" )
//			.setProperty( "hibernate.c3p0.timeout", "1500" )
//			.setProperty( "hibernate.c3p0.max_statements", "50" )
//            ;

            sessionFactory = hc.getConfiguration().buildSessionFactory();
		}
		return sessionFactory;
	}

}
