package com.basamadco.opxi.callmanager.entity.dao.database.hbm;

import com.basamadco.opxi.callmanager.entity.Subscription;
import com.basamadco.opxi.callmanager.entity.SubscriptionNotAvailableException;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.entity.Registration;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.database.SubscriptionDAO;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.expression.Expression;
import net.sf.hibernate.expression.Order;
import net.sf.hibernate.type.Type;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class HibernateSubscriptionDAO extends HibernateDAO implements SubscriptionDAO {
	
	public HibernateSubscriptionDAO( HibernateDAOFactory daof, SessionFactory sf ) {
		super( daof, sf );
	}

    public Class getValueObjectClass() {
        return Subscription.class;
    }

	public List find( UserAgent notifier ) throws DAOException {
		/*return genericFind( "from Subscription as sub where sub.expiry>? AND sub.notifier=?"
				, new Object[] { new Date(), notifier.getId() }, new Type[] { Hibernate.DATE, Hibernate.LONG } );
        */
        List criterions = new ArrayList();
        criterions.add( Expression.ge( "expiry", new Date() ) );
        criterions.add( Expression.eq( "notifier", notifier ) );
        return _genericFind( Registration.class, criterions, null );
    }

	public Subscription find( String sessionId, UserAgent subscriber, UserAgent notifier ) throws DAOException, SubscriptionNotAvailableException {
		/*List subs = genericFind( "from Subscription as s where s.expiry>? AND s.sessionId=? AND s.subscriber=? AND s.notifier=?"
				, new Object[] { new Date(), sessionId, subscriber.getId(), notifier.getId() }, new Type[] { Hibernate.DATE, Hibernate.STRING, Hibernate.LONG, Hibernate.LONG } );*/
        List criterions = new ArrayList();
        criterions.add( Expression.ge( "expiry", new Date() ) );
        criterions.add( Expression.eq( "notifier", notifier ) );
        criterions.add( Expression.eq( "subscriber", subscriber ) );
        criterions.add( Expression.eq( "sessionId", sessionId ) );
        List subs = _genericFind( Registration.class, criterions, null );
        if( subs.size() == 0 )
			throw new SubscriptionNotAvailableException( subscriber.getAORString() + " for " + notifier.getAORString() );
		return (Subscription)subs.get( 0 );		
	}
	
	
	
}
