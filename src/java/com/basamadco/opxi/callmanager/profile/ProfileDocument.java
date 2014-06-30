package com.basamadco.opxi.callmanager.profile;

import org.jcouchdb.document.Attachment;
import org.jcouchdb.db.Database;
import com.basamadco.opxi.callmanager.doc.OpxiDocument;
import com.basamadco.opxi.callmanager.entity.profile.OpxiCMEntityProfile;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import com.basamadco.opxi.callmanager.sip.util.SIPConstants;
import com.basamadco.opxi.callmanager.sip.util.ApplicationConstants;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.StringReader;

/**
 * @author Jrad
 *         Date: May 10, 2010
 *         Time: 1:34:35 PM
 */
public class ProfileDocument extends OpxiDocument {

    private static final Logger logger = Logger.getLogger( ProfileDocument.class.getName() );


    private static final String PREFIX = "opxiCM";

    private static final String SUFFIX = "Profile";


    private static final String AGENT_TYPE = "opxiCMAgentProfile";

    private static final String GROUP_TYPE = "opxiCMGroupProfile";

    private static final String SKILL_TYPE = "opxiCMSkillProfile";


    private static final String ATTACH_NAME = "profile.xml";


    public ProfileDocument() {
    }

    public ProfileDocument( OpxiCMEntityProfile profile ) {
        try {
            if ( profile.getOpxiCMEntityProfileChoice().getAgentProfile() != null ) {
                setType( AGENT_TYPE );
                setOwner( BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO().getCNForDN(
                        profile.getOpxiCMEntityProfileChoice().getAgentProfile().getDN()
                ) );
            } else if ( profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getPoolTargetProfileChoice().getGroupProfile() != null ) {
                setType( GROUP_TYPE );
                setOwner( BaseDAOFactory.getDirectoryDAOFactory().getPoolTargetDAO().getCNForDN(
                        profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getDN()
                ) );
            } else {
                setType( SKILL_TYPE );
                setOwner( BaseDAOFactory.getDirectoryDAOFactory().getPoolTargetDAO().getCNForDN(
                        profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getDN()
                ) );
            }
            Attachment attach = new Attachment(
                    SIPConstants.MIME_TEXT_XML,
                    OpxiToolBox.exchangeObjectToXMLFile( profile ).toByteArray()
            );
            addAttachment( ATTACH_NAME, attach );
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }


    @Override
    public void setOwner( String owner ) {
        super.setOwner( owner );
        setId( genDocIdByOwner( owner ) );
    }

    public OpxiCMEntityProfile getProfile( Database db ) throws DAOException {
        try {
            return OpxiCMEntityProfile.unmarshal( new StringReader(
                    new String( getAttachment( ATTACH_NAME, db ), ApplicationConstants.UTF8 )
            )
            );
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new DAOException( e.getMessage() );
        }
    }

    public void setProfile( OpxiCMEntityProfile profile ) throws DAOException {
        Attachment attch = new Attachment( SIPConstants.MIME_TEXT_XML,
                OpxiToolBox.exchangeObjectToXMLFile( profile ).toByteArray()
        );
        addAttachment( ATTACH_NAME, attch );
    }

    public void updateProfile( OpxiCMEntityProfile profile, Database db ) throws DAOException {
        updateAttach( ATTACH_NAME, SIPConstants.MIME_TEXT_XML,
                OpxiToolBox.exchangeObjectToXMLFile( profile ).toByteArray(), db
        );
    }

    public void updateAttach( String attachId, String contentType, byte[] content, Database db ) {
        db.updateAttachment( getId(), getRevision(), attachId, contentType, content );
    }


    public static String genDocIdByOwner( String owner ) {
        return PREFIX + owner + SUFFIX;
    }


}
