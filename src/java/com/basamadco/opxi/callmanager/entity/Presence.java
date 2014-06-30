package com.basamadco.opxi.callmanager.entity;

import com.basamadco.opxi.callmanager.sip.util.SIPConstants;

import javax.servlet.sip.Address;

/**
 * Represents a user agent presence state in persistance schema.
 *
 * @author Jrad
 * @hibernate.class
 */
public class Presence extends Registration {

    private String basic;

    private String note;

    private static final String[] ACTIVITES = {"", "unknown", "busy", "away", "on-the-phone"};


    public static final int ACTIVITY_NULL = 0;
    public static final int ACTIVITY_UNKNOWN = 1;
    public static final int ACTIVITY_BUSY = 2;
    public static final int ACTIVITY_AWAY = 3;
    public static final int ACTIVITY_ON_THE_PHONE = 4;

    private int activity;

    private boolean active;

    private int msgIndex;


    public Presence( UserAgent user, Address location, String basic, String note, boolean active ) {
        super( user, location );
        this.basic = basic;
        this.note = note;
        this.activity = ACTIVITY_UNKNOWN;
        setActive( active );
        setComment( "Presence-Based Registration" );
        setMsgIndex( 0 );
    }

    /*public Presence( UserAgent user, Address location ) {
        this( user, location, SIPConstants.BASIC_STATUS_CLOSED, SIPConstants.NOTE_STATUS_OFFLINE , false );
    }*/

    public Presence( UserAgent user ) {
        this( user, null, SIPConstants.BASIC_STATUS_CLOSED, SIPConstants.NOTE_STATUS_OFFLINE, false );
    }

    public Presence( Presence copy ) {
        this( copy.getUserAgent(), copy.getLocation(), copy.getBasic(), copy.getNote(), copy.isActive() );
        setComment( copy.getComment() );
        setId( copy.getId() );
        setActivity( copy.activity );
        setMsgIndex( copy.getMsgIndex() );
        // what about submission and expiry?
    }

    public void copyStatus( Presence copy ) {
        setBasic( copy.getBasic() );
        setNote( copy.getNote() );
        setActive( copy.isActive() );
        setComment( copy.getComment() );
        setId( copy.getId() );
        setActivity( copy.activity );
        setMsgIndex( copy.getMsgIndex() );
        setExpiry( copy.getExpiry() );
        setSubmission( copy.getSubmission() );
    }

    /**
     * Call this method after calling setNote
     *
     * @param activity
     */
    public void setActivity( int activity ) {
        this.activity = activity;
    }

    public void setActivity( String activity ) {
        this.activity = getActivityCode( activity );
    }


    public int getMsgIndex() {
        return msgIndex;
    }

    public void setMsgIndex( int msgIndex ) {
        this.msgIndex = msgIndex;
    }

    /**
     * @hibernate.property
     */
    public String getBasic() {
        return basic;
    }

    public void setBasic( String basic ) {
        this.basic = basic;
    }

    /**
     * @hibernate.property
     */
    public String getNote() {
        return note;
    }

    public void setNote( String note ) {
        this.note = note;
//        if ( note.equalsIgnoreCase( ACTIVITES[1] ) ) {
//            setActivity( ACTIVITY_BUSY );
//        } else if ( note.equalsIgnoreCase( ACTIVITES[2] ) ) {
//            setActivity( ACTIVITY_AWAY );
//        } else {
//            setActivity( ACTIVITY_UNKNOWN );
//        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive( boolean active ) {
        this.active = active;
    }

    public boolean getActive() {
        return this.active;
    }

    public void incMsgIndex() {
        msgIndex++;
    }

    public String getActivity() {
        return ACTIVITES[activity];
    }

    public boolean isOnThePhone() {
        return activity == ACTIVITY_ON_THE_PHONE;
    }

    public boolean isBusy() {
        return activity == ACTIVITY_BUSY;
    }

    public boolean isAway() {
        return activity == ACTIVITY_AWAY;
    }

    public boolean isAvailable() {
        return activity == ACTIVITY_UNKNOWN;
    }

    public static int getActivityCode( String activity ) {
        if ( activity.equalsIgnoreCase( ACTIVITES[1] ) ) {
            return ACTIVITY_UNKNOWN;
        } else if ( activity.equalsIgnoreCase( ACTIVITES[2] ) ) {
            return ACTIVITY_BUSY;
        } else if ( activity.equalsIgnoreCase( ACTIVITES[3] ) ) {
            return ACTIVITY_AWAY;
        } else if ( activity.equalsIgnoreCase( ACTIVITES[4] ) ) {
            return ACTIVITY_ON_THE_PHONE;
        } else {
            return ACTIVITY_NULL;
        }
    }

}