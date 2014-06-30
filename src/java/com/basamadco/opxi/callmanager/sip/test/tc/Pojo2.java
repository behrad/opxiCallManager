package com.basamadco.opxi.callmanager.sip.test.tc;

import org.svenson.JSONProperty;

/**
 * @author Jrad
 *         Date: May 5, 2010
 *         Time: 9:59:50 AM
 */
public class Pojo2 {

    public Pojo2( String myName ) {
        this.myName = myName;
    }

    private String myName;

    @JSONProperty
    public String getMyName() {
        return myName;
    }

    public void setMyName( String myName ) {
        this.myName = myName;
    }
}
