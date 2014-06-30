package com.basamadco.opxi.callmanager.logging.doc;

import org.jcouchdb.document.BaseDocument;
import org.jcouchdb.document.Attachment;
import org.jcouchdb.db.Database;
import org.svenson.JSONProperty;
import com.basamadco.opxi.callmanager.doc.OpxiDocument;

/**
 * @author Jrad
 *         Date: May 7, 2010
 *         Time: 11:49:30 AM
 */
public abstract class LogDocument extends OpxiDocument {


    private String beginTime;


    private String endTime;


    private static final String ATTACH_NAME = "log.xml";


    private static final String ATTACH_CONTENT_TYPE = "text/xml";


    @JSONProperty
    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime( String beginTime ) {
        this.beginTime = beginTime;
    }        


    @JSONProperty
    public String getEndTime() {
        return endTime;
    }


    public void setEndTime( String endTime ) {
        this.endTime = endTime;
    }    

    public byte[] getLog( Database db ) throws Exception {
        return getAttachment( ATTACH_NAME, db );
    }
    
    public void setLog( byte[] logXml ) {
        Attachment attach = new Attachment( ATTACH_CONTENT_TYPE, logXml );
        addAttachment( ATTACH_NAME, attach );
    }
}