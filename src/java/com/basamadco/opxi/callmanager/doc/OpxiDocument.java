package com.basamadco.opxi.callmanager.doc;

import org.jcouchdb.document.BaseDocument;
import org.jcouchdb.db.Database;
import org.svenson.JSONProperty;

/**
 * @author Jrad
 *         Date: May 10, 2010
 *         Time: 1:42:28 PM
 */
public class OpxiDocument extends BaseDocument {


    private String type;


    private String owner;


    public OpxiDocument() {
    }

    public OpxiDocument( String id ) {
        setId( id );
    }

    @JSONProperty
    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    @JSONProperty( ignoreIfNull = true )
    public String getOwner() {
        return owner;
    }

    public void setOwner( String owner ) {
        this.owner = owner;
    }

    public boolean hasAttachmentWithId( String id ) {
        return getAttachments().containsKey( id );
    }

    public byte[] getAttachment( String attachName, Database db ) throws Exception {
        if ( !getAttachments().get( attachName ).isStub() ) {
            return getAttachments().get( attachName ).getData().getBytes( "UTF8" );
        } else {
            return db.getAttachment( getId(), attachName );
        }
    }

}
