package com.basamadco.opxi.callmanager.entity;

import java.util.Date;

/**
 * OpxiCallManager presence server entity model
 * for handling user agent subscribtions.
 * 
 * @hibernate.class
 * 
 * @author Jrad
 * 
 */
public class Subscription extends ValueObject {
		
//	private Long id;
	
	private UserAgent subscriber;
	
	private UserAgent notifier;
	
	private String event;
	
	private Date expiry;

    private String sessionId;

    /**
	 * @hibernate.property 
	 */
	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

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
	 * @hibernate.many-to-one
	 *  class="com.basamadco.opxi.callmanager.entity.UserAgent"
	 */
	public UserAgent getNotifier() {
		return notifier;
	}

	public void setNotifier(UserAgent notifier) {
		this.notifier = notifier;
	}
	
	/**
	 * @hibernate.many-to-one
	 *  class="com.basamadco.opxi.callmanager.entity.UserAgent"
	 */
	public UserAgent getSubscriber() {
		return subscriber;
	}
		
	public void setSubscriber(UserAgent subscriber) {
		this.subscriber = subscriber;
	}

    /**
	 * @hibernate.property
	 */
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId( String sessionId ) {
        this.sessionId = sessionId;
    }

    public Subscription() {
		
	}
	
	public Subscription( UserAgent subscriber, UserAgent notifier, String event, int expires, String sessionId ) {
		setSubscriber( subscriber );
		setNotifier( notifier );
		setEvent( event );
		setExpiry( getExpiryDate( expires ) );
        setSessionId( sessionId );
        setId( sessionId );
    }
	
	public Date getExpiryDate( int expires ) {
		return new Date( System.currentTimeMillis() + expires * 1000 );
	}

    public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subscription that = (Subscription) o;

        if (notifier != null ? !notifier.equals( that.notifier ) : that.notifier != null) return false;
        if (sessionId != null ? !sessionId.equals( that.sessionId ) : that.sessionId != null) return false;
        if (subscriber != null ? !subscriber.equals( that.subscriber ) : that.subscriber != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = ( subscriber != null ? subscriber.hashCode() : 0 );
        result = 31 * result + ( notifier != null ? notifier.hashCode() : 0 );
        result = 31 * result + ( sessionId != null ? sessionId.hashCode() : 0 );
        return result;
    }

    public String toString() {
        return "Subscription[id=" + id + ", from=" +
                subscriber.getName() + ", to=" +
                notifier.getName() + ", expire=" +
                expiry + ", sessionId=" +
                sessionId +  "]"; 
    }
}
