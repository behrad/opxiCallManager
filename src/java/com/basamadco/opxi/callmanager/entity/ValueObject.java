package com.basamadco.opxi.callmanager.entity;

import java.io.Serializable;

/**
 * @author Jrad
 *         Date: Sep 23, 2006
 *         Time: 9:21:10 AM
 */
public class ValueObject implements Serializable {

    protected String id;

    // I know this is not the right place, but ...
    private String action;


    public ValueObject() {
    }

    /**
	 * @hibernate.id generator-class="identity"
     *
     * @struts.dynaform-field
	 */
    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    /**
     *
     * @struts.dynaform-field
	 */
    public String getAction() {
        return action;
    }

    public void setAction( String action ) {
        this.action = action;
    }

}
