package com.basamadco.opxi.callmanager.entity.dao.webdav;

import com.basamadco.opxi.callmanager.entity.profile.OpxiCMEntityProfile;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;

/**
 * @author Jrad
 *         Date: Oct 4, 2006
 *         Time: 11:42:30 AM
 */
public interface ProfileDAO {


    public void writeProfile( final OpxiCMEntityProfile profile ) throws DAOException;


    public void updateProfile( OpxiCMEntityProfile profile ) throws DAOException;


    public OpxiCMEntityProfile readProfile() throws DAOException;


    public String writeResource( String name, String contentType, byte[] content ) throws DAOException;


    public void deleteResource( String resourceName ) throws DAOException;


    public void deleteProfile() throws DAOException;


//    public void createHierarchy() throws DAOException;
//
//
//    public void deleteHierarchy() throws DAOException;

}