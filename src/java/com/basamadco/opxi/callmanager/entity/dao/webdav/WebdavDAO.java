package com.basamadco.opxi.callmanager.entity.dao.webdav;

import com.basamadco.opxi.callmanager.entity.dao.BaseDAO;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;

/**
 * @author Jrad
 *         Date: Oct 4, 2006
 *         Time: 10:49:42 AM
 */
public interface WebdavDAO extends BaseDAO {

    /**
     * @param buffer StringBuffer to be written to the webdav resource
     */

    public void putResource( ByteArrayOutputStream buffer ) throws DAOException;


    public void putResource( ByteArrayOutputStream buffer, Hashtable properties ) throws DAOException;


    public void putResource( String name, ByteArrayOutputStream buffer ) throws DAOException;


    public void putResource( String name, ByteArrayOutputStream buffer, Hashtable properties ) throws DAOException;


    public void putResource( String name, byte[] content ) throws DAOException;


    public void putResource( String name, byte[] content, Hashtable properties ) throws DAOException;


    public void deleteResource( String name ) throws DAOException;

    /**
     * @return A Reader object from which WebDAV resource can be read
     */
    public StringBuffer getResource() throws DAOException;

    public StringBuffer getResource( String name ) throws DAOException;


    public void mkdir( String dir ) throws DAOException;


    public void rmdir( String dir ) throws DAOException;


    public String getHierarchy();

}