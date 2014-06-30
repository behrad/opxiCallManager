package com.basamadco.opxi.callmanager.entity.dao.database.hbm;

import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.entity.UserNotAvailableException;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.database.DatabaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.database.UserAgentDAO;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.type.Type;

import java.util.List;

public class HibernateUserAgentDAO extends HibernateDAO implements UserAgentDAO {
	
	public HibernateUserAgentDAO( HibernateDAOFactory daof, SessionFactory sf ) {
		super( daof, sf );
	}

    public Class getValueObjectClass() {
        return UserAgent.class;
    }

    /**
     * @param name
     * @param domain
     * @return
     * @throws DAOException
     * @throws UserNotAvailableException
     * @deprecated
     */
    public UserAgent find( String name, String domain ) throws DAOException, UserNotAvailableException {
		List agents = genericFind( "from UserAgent as ua where ua.name=? AND ua.domain.name=?", 
				new String[] { name, domain }, new Type[] { Hibernate.STRING, Hibernate.STRING } );
		if( agents.size() == 0 )
			throw new UserNotAvailableException( name + "@" + domain );
		
		return (UserAgent)agents.get( 0 );
	}
	
	public UserAgent findOrSave( String name, String domain ) throws DAOException {
		UserAgent ua = null;		
		try {			
			ua = find( name, domain );
		} catch( UserNotAvailableException e ) {
			ua = new UserAgent( name, domain );
			ua.setDomain( ((DatabaseDAOFactory)getDAOFactory()).getDomainDAO().findOrSave( domain ) );
			ua.setId( save( ua ) );
		}
		return ua;
	}
	
	
}
