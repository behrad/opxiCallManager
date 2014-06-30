package com.basamadco.opxi.callmanager.entity.dao.database.hbm;

import com.basamadco.opxi.callmanager.entity.Registration;
import com.basamadco.opxi.callmanager.entity.RegistrationNotAvailableException;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.entity.UserNotAvailableException;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.DAOFactoryException;
import com.basamadco.opxi.callmanager.entity.dao.database.RegistrationDAO;
import com.basamadco.opxi.callmanager.entity.dao.database.DatabaseDAOFactory;
import net.sf.hibernate.*;
import net.sf.hibernate.expression.Criterion;
import net.sf.hibernate.expression.Expression;
import net.sf.hibernate.expression.Order;
import net.sf.hibernate.type.Type;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HibernateRegistrationDAO extends HibernateDAO implements RegistrationDAO {
	
	private static final Logger logger = Logger.getLogger( HibernateRegistrationDAO.class.getName() );
	
	public HibernateRegistrationDAO( HibernateDAOFactory daof, SessionFactory sf ) {
		super( daof, sf );
	}

    public Class getValueObjectClass() {
        return Registration.class;
    }

	public void deleteRegistrations( UserAgent ua ) throws DAOException {
		Session session = null;
		Transaction t = null;
		try {
			session = db.openSession();
			t = session.beginTransaction();			
			session.delete( "from Registration as reg where reg.user.name=? AND reg.user.domain.name=?"
					, new Object[] { ua.getName(), ua.getDomain().getName() }, new Type[] { Hibernate.STRING, Hibernate.STRING } );			
			t.commit();			
		} catch( HibernateException e ) {
			try {
				if( t!= null )
					t.rollback();
			}catch( HibernateException he ) { logger.log( Level.SEVERE, he.getMessage(), he ); }
			throw new DAOException( e.getMessage() );
		} finally {
			try {
				if( session != null )
					session.close();
			}catch( HibernateException he ) { logger.log( Level.SEVERE, he.getMessage(), he ); }
		}		
		
	}

	public Registration find(UserAgent ua, String location) throws DAOException, RegistrationNotAvailableException {
		/*List regs = genericFind( "from Registration as reg where reg.expiry>? AND reg.location like '%"+location+"%' AND reg.userAgent.name=? AND reg.userAgent.domain.name=? ORDER BY submission DESC"
				, new Object[] { new Date(), ua.getName(), ua.getDomain().getName() }, new Type[] { Hibernate.DATE, Hibernate.STRING, Hibernate.STRING } );
*/
        checkUA( ua );
        List criterions = new ArrayList();
        criterions.add( Expression.ge( "expiry", new Date() ) );
        criterions.add( Expression.eq( "userAgent", ua ) );
        criterions.add( Expression.like( "location", location ) );
        List regs = _genericFind( Registration.class, criterions, Order.desc( "submission" ) );

        if( regs.size() == 0 ) {
//			System.out.println( "counldn't find: " + ua + " in " + location );
			throw new RegistrationNotAvailableException( ua + " with contact host: " + location );			
		}
		return (Registration)regs.get( 0 );
	}
	
	public List find( UserAgent ua ) throws DAOException {
        checkUA( ua );
        List criterions = new ArrayList();
        criterions.add( Expression.ge( "expiry", new Date() ) );
        criterions.add( Expression.eq( "userAgent", ua ) );
        List regs = _genericFind( Registration.class, criterions, Order.desc( "submission" ) );
        logger.finer( "Registrations found for " + ua + ": " + regs.size() );
        for (int i = 0; i < regs.size(); i++) {
            Registration registration = (Registration) regs.get( i );
            logger.finer( "in " + registration.getLocation() + " with id: " + registration.getId() );
        }

        return regs;
	}

    private void checkUA( UserAgent ua ) throws DAOException {
        /*if( ua.getId() == null ) {
            // READ USERAGENT
            try {
                ua = DatabaseDAOFactory.getDatabaseDAOFactory().getUserAgentDAO().find( ua.getName(), ua.getDomain().getName() );
            } catch ( Exception e ) {
                throw new DAOException( e.getMessage(), e );
            }
        }*/
    }

}
