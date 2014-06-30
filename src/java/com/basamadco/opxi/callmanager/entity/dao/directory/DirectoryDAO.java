package com.basamadco.opxi.callmanager.entity.dao.directory;

import com.basamadco.opxi.callmanager.entity.DirectoryEntity;
import com.basamadco.opxi.callmanager.entity.EntityNotExistsException;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAO;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;

/**
 * This DAO access the ldap.
 * 
 * @author Jrad
 *
 */
public interface DirectoryDAO extends BaseDAO {

//    public DirectoryEntity searchByPhoneNumber( String phoneNumber ) throws DAOException;

    public DirectoryEntity read( String cn ) throws EntityNotExistsException, DAOException;

//    public PoolTarget readPoolTarget( String cn ) throws DAOException;

    public String getCNForName( String name ) throws DAOException;

    public String getCNForDN( String dN );

    public String[] getCNForDN( String[] dNs );

//    public QueueTarget readQueueTarget( String cn ) throws EntityNotExistsException, DAOException;

//    public void read( String cn, DirectoryEntity entity ) throws EntityNotExistsException, DAOException;

    public void updateAttribute( String cn, String attrName, String attrValue ) throws DAOException;

    public void updateAttribute( String cn, String attrName, String[] attrValues ) throws DAOException;

    public String getAttributeValue( String CN, String attr_name ) throws DAOException;

    public String[] getAttributeValues( String CN, String attr_name ) throws DAOException;

//    public String getMatchingRule( String cn ) throws DAOException;

    public String getSearchBase();

    public String getDN( DirectoryEntity entity ) throws DAOException;

    public String getDN( String CN ) throws DAOException;

    public String[] listAllByCNs() throws DAOException;

}