package com.basamadco.opxi.callmanager.entity.dao.couch;

import com.basamadco.opxi.callmanager.entity.dao.webdav.ProfileDAO;
import com.basamadco.opxi.callmanager.entity.dao.couch.CouchDAO;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.profile.OpxiCMEntityProfile;
import com.basamadco.opxi.callmanager.profile.ProfileDocument;

/**
 * @author Jrad
 *         Date: May 10, 2010
 *         Time: 12:30:06 PM
 */
public class CouchProfileDAO extends CouchDAO implements ProfileDAO {


    private String owner;


    public CouchProfileDAO( BaseDAOFactory daof, String owner ) throws DAOException {
        super( daof );
        this.owner = owner;
    }

    public void writeProfile( OpxiCMEntityProfile profile ) throws DAOException {
        ProfileDocument doc = new ProfileDocument( profile );
        getDb().createDocument( doc );
    }

    public void updateProfile( OpxiCMEntityProfile profile ) throws DAOException {
        ProfileDocument doc = getDb().getDocument(
                ProfileDocument.class, ProfileDocument.genDocIdByOwner( owner ) );
        doc.updateProfile( profile, getDb() );

    }

    public OpxiCMEntityProfile readProfile() throws DAOException {
        return getDb().getDocument( ProfileDocument.class, ProfileDocument.genDocIdByOwner( owner ) ).getProfile(
                getDb()
        );
    }

    public String writeResource( String name, String contentType, byte[] content ) throws DAOException {
        ProfileDocument doc = getDb().getDocument(
                ProfileDocument.class, ProfileDocument.genDocIdByOwner( owner )
        );
        if ( doc.hasAttachmentWithId( name ) ) {
            doc.updateAttach( name, contentType, content, getDb() );
        } else {
            getDb().createAttachment( doc.getId(), doc.getRevision(), name, contentType, content );
        }
        return getURL( doc.getId(), name );
    }

    public void deleteProfile() throws DAOException {
        getDb().delete(
                getDb().getDocument( ProfileDocument.class, ProfileDocument.genDocIdByOwner( owner ) )
        );
    }

    public void deleteResource( String resourceName ) throws DAOException {
        ProfileDocument doc = getDb().getDocument(
                ProfileDocument.class, ProfileDocument.genDocIdByOwner( owner )
        );
        getDb().deleteAttachment( doc.getId(), doc.getRevision(), resourceName );
    }
}
