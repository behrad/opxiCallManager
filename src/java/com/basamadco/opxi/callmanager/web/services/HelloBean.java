package com.basamadco.opxi.callmanager.web.services;

import java.io.Serializable;

/**
 * @author Jrad
 *         Date: Oct 1, 2006
 *         Time: 10:32:03 AM
 *
 * @wsee.jaxrpc-mapping
 *                      root-type-qname="com.basamadco.opxi.callmanager.web.services.HelloBean"
 */
public class HelloBean implements Serializable {

    /**
     * @wsee.variable-mapping
     *                        name="message"
     */
    private String message;

    /**
     * @wsee.variable-mapping
     *                        name="loveMe"
     */
    private boolean loveMe = false;

    
    public HelloBean() {
    }

    public HelloBean( String name ) {
        this.message = "Hello " + message + "!";
        if( name.equalsIgnoreCase( "behrad" ) ) {
            loveMe = true;
        }
    }

    public void setMessage( String message ) {
        this.message = message;
    }

    public void setLoveMe( boolean loveMe ) {
        this.loveMe = loveMe;
    }

    public String getMessage() {
        return message;
    }

    public boolean getLoveMe() {
        return loveMe;
    }

}
