package com.basamadco.opxi.callmanager.entity.dao.database.hbm;

import com.basamadco.opxi.callmanager.entity.Domain;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.database.DomainDAO;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.type.Type;

import java.util.List;

public class HibernateDomainDAO extends HibernateDAO implements DomainDAO {
	
	public HibernateDomainDAO( HibernateDAOFactory daof, SessionFactory sf ) {
		super( daof, sf );
	}

    public Class getValueObjectClass() {
        return Domain.class;
    }

	public Domain findOrSave(String name) throws DAOException {
		List domains = genericFind( "from Domain as d where d.name=?",
				new String[] { name }, new Type[] { Hibernate.STRING } );
		if( domains.size() == 0 ) {
			Domain d = new Domain( name );
			d.setId( save( d ) );
			return d;
		}
		return (Domain)domains.get( 0 );
	}
	
	
	
}