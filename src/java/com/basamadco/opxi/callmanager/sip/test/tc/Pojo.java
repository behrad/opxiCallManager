package com.basamadco.opxi.callmanager.sip.test.tc;

import org.jcouchdb.document.BaseDocument;
import org.svenson.JSONProperty;

/**
 * @author Jrad
 *         Date: May 4, 2010
 *         Time: 4:09:13 PM
 */
public class Pojo extends BaseDocument {

    public Pojo( String name ) {
        this.name = name;
    }


    private String name;

    private Pojo2 object;

    @JSONProperty
    public Pojo2 getObject() {
        return object;
    }

    public void setObject( Pojo2 object ) {
        this.object = object;
    }

    @JSONProperty
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

}
