package com.basamadco.opxi.callmanager.entity;

import com.basamadco.opxi.callmanager.util.OpxiToolBox;

import javax.servlet.sip.Address;
import javax.servlet.sip.SipURI;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Represents an OpxiCallManager registration binding
 * in persistance schema.
 *
 * @author Jrad
 * @hibernate.class
 */
public class Registration extends ValueObject {

    private final static Logger logger = Logger.getLogger(Registration.class.getName());

    private UserAgent userAgent;

    private Address location;

    private Date submission;

    private Date expiry;

    private String comment;


    /**
     * @hibernate.property
     */
    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    /**
     * @hibernate.property
     */
    public Address getLocation() {
        return location;
    }

    public void setLocation(Address location) {
        this.location = location;
    }

    /**
     * @hibernate.property
     */
    public Date getSubmission() {
        return submission;
    }

    public void setSubmission(Date submission) {
        this.submission = submission;
    }

    /**
     * @hibernate.many-to-one class="com.basamadco.opxi.callmanager.entity.UserAgent"
     * cascade="none"
     */
    public UserAgent getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(UserAgent user) {
        this.userAgent = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean expired() {
        return new Date().after(expiry);
    }

    public Registration(UserAgent user, Address location, Date submission, Date expiry) {
        setUserAgent(user);
        setLocation(location);
        setSubmission(submission);
        setExpiry(expiry);
    }

    public Registration(UserAgent user, Address location) {
        setUserAgent(user);
        setLocation(location);
    }


    public long getInterval() {
        return getExpiry().getTime() - getSubmission().getTime();
    }

    public String getContactURI() {
        return getLocation().getURI().toString();
    }

    private SipURI getSipURI() {
        return (SipURI) getLocation().getURI();
    }

    public String getTransProtocol() {
        if (getSipURI().getTransportParam() != null) return getSipURI().getTransportParam().toUpperCase();
        if (getSipURI().isSecure()) return "TCP";
        return "UDP";
    }

    public int getPort() {
        return getSipURI().getPort();
    }

    public String getHost() {
        return getSipURI().getHost();
    }

    public boolean equals(Object o) {
        if (this == o) return true;

        if (this.getClass() != o.getClass()) return false;

        Registration that = (Registration) o;

        if (userAgent != null ? !userAgent.equals(that.userAgent) : that.userAgent != null) return false;

        if (!getHost().equalsIgnoreCase(that.getHost())) return false;

        if (getPort() != that.getPort()) return false;

//        if( !getTransProtocol().equalsIgnoreCase( that.getTransProtocol() ) ) return false;

        return true;
    }

    public boolean equalsIgnoreSubclasses(Object o) {
        if (this == o) return true;
        Registration that = (Registration) o;

        if (userAgent != null ? !userAgent.equals(that.userAgent) : that.userAgent != null) return false;

        if (!getHost().equalsIgnoreCase(that.getHost())) return false;
        if (getPort() != that.getPort()) return false;
//        if( !getTransProtocol().equalsIgnoreCase( that.getTransProtocol() ) ) return false;
        return true;
    }


    public int hashCode() {
        int result;
        result = (userAgent != null ? userAgent.hashCode() : 0);
        result = 31 * result + getClass().hashCode();
        if (getLocation() != null) {
            result = 31 * result + getHost().hashCode();
            result = 31 * result + getPort();
//            result = 31 * result + getTransProtocol().hashCode();
        }
        return result;
    }

    public String toString() {
        return OpxiToolBox.unqualifiedClassName(this.getClass()) + "[Id='"
                + getId() + "', UA='"
                + getUserAgent().getAORString()
                + "', ContactURI='"
                + getContactURI();
    }
}