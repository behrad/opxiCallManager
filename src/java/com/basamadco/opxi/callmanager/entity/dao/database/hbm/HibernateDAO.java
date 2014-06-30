package com.basamadco.opxi.callmanager.entity.dao.database.hbm;

import com.basamadco.opxi.callmanager.entity.EntityNotExistsException;
import com.basamadco.opxi.callmanager.entity.ValueObject;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.database.DatabaseDAO;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import net.sf.hibernate.*;
import net.sf.hibernate.expression.Criterion;
import net.sf.hibernate.expression.Order;
import net.sf.hibernate.type.Type;
import java.util.logging.Logger;
import java.util.logging.Level;

import java.io.Serializable;
import java.util.List;

public abstract class HibernateDAO implements DatabaseDAO {

    private static final Logger logger = Logger.getLogger( HibernateDAO.class.getName() );

    protected SessionFactory db;

    private HibernateDAOFactory factory;

    public HibernateDAO( HibernateDAOFactory DAOFactory, SessionFactory sf ) {
        db = sf;
        factory = DAOFactory;
    }

    public void delete( ValueObject data ) throws DAOException {
        Session session = null;
        Transaction t = null;
        try {
            session = db.openSession();
            t = session.beginTransaction();
            session.delete( data );
            t.commit();
        } catch( HibernateException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
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

    public ValueObject load( Class clazz, Long id ) throws DAOException {
        ValueObject data;
        Session session = null;
        Transaction t = null;
        try {
            session = db.openSession();
            t = session.beginTransaction();
            data = (ValueObject)session.get( clazz, id );
            t.commit();
        } catch( HibernateException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
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
        return data;
    }

    public String save( ValueObject data ) throws DAOException {
        Serializable id;
        Session session = null;
        Transaction t = null;
        try {
            session = db.openSession();
            t = session.beginTransaction();
            id = session.save( data );
            t.commit();
        } catch( HibernateException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
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
        return (String)id;
    }

    public void update( ValueObject data ) throws DAOException {
        Session session = null;
        Transaction t = null;
        try {
            session = db.openSession();
            t = session.beginTransaction();
            session.update( data );
            t.commit();
        } catch( HibernateException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
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

    protected List genericFind( String hql_query, Object[] param, Type[] type ) throws DAOException {
        List dataset = null;
        Session session = null;
        Transaction t = null;
        try {
            session = db.openSession();
            t = session.beginTransaction();
            dataset = session.find( hql_query, param, type );
            t.commit();
        } catch( HibernateException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
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
        return dataset;
    }

    protected List _genericFind( Class type, List filters, Order order ) throws DAOException {
        List dataset = null;
        Session session = null;
        Transaction t = null;
        try {
            session = db.openSession();
            t = session.beginTransaction();
            Criteria criteria = session.createCriteria( type );
            if( order != null ) {
                criteria.addOrder( order );
            }
            for (int i = 0; i < filters.size(); i++) {
                Criterion criterion = (Criterion)filters.get( i );
                criteria.add( criterion );
            }
            dataset = criteria.list();
            t.commit();
        } catch( HibernateException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
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
        return dataset;
    }

    /*protected List genericFind( String hql_query, Object param, Type type ) throws DAOException {
        List dataset = null;
        Session session = null;
        Transaction t = null;
        try {
            session = db.openSession();
            t = session.beginTransaction();
            dataset = session.find( hql_query, param, type );
            t.commit();
        } catch( HibernateException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
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
        return dataset;
    }*/

    protected List genericFind( String hql_query ) throws DAOException {
        List dataset = null;
        Session session = null;
        Transaction t = null;
        try {
            session = db.openSession();
            t = session.beginTransaction();
            dataset = session.find( hql_query );
            t.commit();
        } catch( HibernateException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            logger.finer( "Try to reconnect to DB here!" );
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
        return dataset;
    }

    /*public ValueObject readByName( String name ) throws DAOException, EntityNotExistsException {
        return dynaRead( getNameFieldName(), name );
    }*/

    public List listAll() throws DAOException {
        return genericFind( "from " + getValueObjectClass() + " as e" );
    }

    public String getNameFieldName() {
        return "name";
    }

    public BaseDAOFactory getDAOFactory() {
        return factory;
    }

    public void destroy() {
        try {
            db.close();
        } catch( HibernateException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    /*protected ValueObject dynaRead( String id, String value ) throws DAOException, EntityNotExistsException {
        List data = genericFind( "from " + OpxiToolBox.unqualifiedClassName( getValueObjectClass() ) + " as e where e." +
                id + "=?", value, Hibernate.STRING );
        if( data.size() == 0 )
            throw new EntityNotExistsException( getValueObjectClass() + " with " + id + " = " + value  );

        return (ValueObject)data.get( 0 );
    }*/
}
