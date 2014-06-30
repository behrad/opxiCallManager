package com.basamadco.opxi.callmanager.entity;

import com.basamadco.opxi.callmanager.sip.util.SIPConstants;
import com.basamadco.opxi.callmanager.sip.util.SipUtil;

import javax.servlet.sip.Address;
import javax.servlet.sip.URI;
import java.util.logging.Logger;

/**
 * Represents a SIP user agent in OpxiCallManager persistance schema.
 *
 * @author Jrad
 * @hibernate.class
 * @struts.dynaform name="UserAgentForm"
 * type="org.apache.struts.action.DynaActionForm"
 */
public class UserAgent extends ValueObject {

    private static final Logger logger = Logger.getLogger( UserAgent.class.getName() );

//	private Long id;

    private String name;

    private Domain domain;


    public UserAgent( String name, Domain domain ) {
        this();
        setDomain( domain );
        setName( name );
    }

    public UserAgent( String name, String domainName ) {
        this();
        setDomain( new Domain( domainName ) );
        setName( name );
    }

    public UserAgent( String SipURI ) {
        this();
        setName( SipUtil.getName( SipURI ) );
        setDomain( new Domain( SipUtil.getDomain( SipURI ) ) );
    }

    public UserAgent( URI SipURI ) {
        this();
        setName( SipUtil.getName( SipURI ) );
        setDomain( new Domain( SipUtil.getDomain( SipURI ) ) );
    }

    public UserAgent( Address contact ) {
        this( contact.getURI() );
    }

    public UserAgent() {
    }

    // Copy constructor
    public UserAgent( UserAgent ua ) {
        id = ua.getId();
        name = ua.getName();
        domain = ua.getDomain();
//        subscriptions = ua.getSubscriptions();
//        registrations = ua.getRegistrations();
    }

    /**
     * @hibernate.many-to-one class="com.basamadco.opxi.callmanager.entity.Domain"
     * cascade="save-update"
     */
    public Domain getDomain() {
        return domain;
    }

    public void setDomain( Domain domain ) {
        this.domain = domain;
    }

    /**
     * @hibernate.property
     * @struts.dynaform-field
     */
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getSipURIString() {
        return SipUtil.toSipURIString( getName(), getDomain().getName() );
    }

    public String getAORString() {
//        return getName() + "@" + getDomain().getName();
        return getSipURIString();
    }

    public boolean equals( Object o ) {
        if (this == o) return true;
        if (!( o instanceof UserAgent )) return false;
        final UserAgent userAgent = (UserAgent) o;
        if (domain != null ? !domain.equals( userAgent.domain ) : userAgent.domain != null) return false;
        return name != null ? name.equals( userAgent.name ) : userAgent.name == null;
    }

    public int hashCode() {
        int result;
        result = ( name != null ? name.hashCode() : 0 );
        result = 29 * result + ( domain != null ? domain.hashCode() : 0 );
        return result;
    }

    public String toString() {
        return "[AOR='" + getAORString() + "']";
    }

}
