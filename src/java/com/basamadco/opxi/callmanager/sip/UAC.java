package com.basamadco.opxi.callmanager.sip;

import java.io.Serializable;
import java.util.*;
import javax.servlet.sip.*;
import org.apache.log4j.*;

/**
 * UAC represents a simple user agent client which 
 * tries to contact to opxiCallManager.
 * 
 * @author Jrad
 */
public class UAC implements Serializable {

    private static final Logger logger = Logger.getLogger( UAC.class.getName() );

    public UAC( Address address_of_record, List locations ) {
        setAOR( address_of_record );
        setLocations( locations );
    }

    private Address AOR;

    private List<Address> locations;

    /**
     * returns "Address Of Record" of a user representing a SIP/SIPS
     * url as specified by SIP REGISTER method To header.
     * @return a SIP/SIPS address
     */
    public Address getAOR() {
        return AOR;
    }

    protected void setAOR(Address AOR) {
        this.AOR = AOR;
    }

    public List<Address> getLocations() {
        return locations;
    }

    protected void setLocations(List locations ) {
        this.locations = locations;
    }

    public String toString() {
        StringBuffer message =  new StringBuffer("[AOR="+AOR+", contact urls: " );
        ListIterator locationsiter = locations.listIterator();
        while( locationsiter.hasNext() ) {
            Address add = (Address)locationsiter.next();
            message.append( add + ", " );
        }
        return message.append("]").toString();
    }
}