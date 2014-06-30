package com.basamadco.opxi.callmanager.entity.dao.database.hbm;


import com.basamadco.opxi.callmanager.entity.Presence;
import com.basamadco.opxi.callmanager.entity.PresenceNotAvailableException;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.database.PresenceDAO;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.type.Type;

import java.util.Date;
import java.util.List;

public class HibernatePresenceDAO extends HibernateDAO implements PresenceDAO {

    public HibernatePresenceDAO( HibernateDAOFactory daof, SessionFactory sf ) {
        super( daof, sf );
    }

    public Class getValueObjectClass() {
        return Presence.class;
    }

    public Presence find( String registration ) throws PresenceNotAvailableException, DAOException {
        List ps = genericFind( "from Presence as p where p.registration=?"
                , new Object[] { registration }, new Type[] { Hibernate.STRING } );
        if( ps.size() == 0 )
            throw new PresenceNotAvailableException( registration );
        return (Presence)ps.get( 0 );
    }

    public Presence findbyAgent( UserAgent ua ) throws PresenceNotAvailableException, DAOException {
        List ps = genericFind( "from Presence as p where p.registration.userAgent=? AND p.registration.expiry>?"
                , new Object[] { ua.getId(), new Date() }, new Type[] { Hibernate.LONG, Hibernate.DATE } );
        if( ps.size() == 0 )
            throw new PresenceNotAvailableException( ua.getAORString() );
        return (Presence)ps.get( 0 );
    }

}
