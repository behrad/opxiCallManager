package com.basamadco.opxi.callmanager.sip.registrar;

import com.basamadco.opxi.callmanager.sip.util.SIPConstants;
import com.basamadco.opxi.callmanager.util.PropertyUtil;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.Address;
import javax.servlet.ServletException;
import java.util.ListIterator;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: AM
 * Date: Dec 11, 2007
 * Time: 1:15:27 AM
 */
public class RegisterUtility implements SIPConstants {

    private static final int min_expiry = Integer.parseInt( PropertyUtil.getProperty( "opxi.callmanager.sip.registrar.min-expiry" ) );

    /**
     * Checks a request if it's a remove REGISTER request
     * for all contacts for request's address-of-record.<br>
     * <p/>
     * Behaivour is as RFC3261 [10.2.2 Removing Bindings]:
     * 1)The REGISTER-specific Contact header field value
     * of "*" applies to all registrations, but it MUST NOT
     * be used unless the Expires header field is present
     * with a value of "0".<br>
     * <p/>
     * Use of the "*" Contact header field value allows a
     * registering UA to remove all bindings associated with
     * an address-of-record without knowing their precise
     * values.<br>
     *
     * @param request sip request
     * @return true if a sip REGISTER is requesting to
     *         cancel contact registrations.
     */
    public static boolean isAllRemoval( SipServletRequest request ) {
        return ( request.getHeader( CONTACT ).equals( "*" ) && request.getExpires() == 0 );
    }

    /**
     * Checks a request if it's a remove REGISTER request for a specific contact location.
     * <p/>
     * Behaivour is as RFC3261 [10.2.2 Removing Bindings]:<br>
     * 1)A UA requests the immediate removal of a binding
     * by specifying an expiration interval of "0" for
     * that contact address in a REGISTER request.
     *
     * @param request sip request
     * @return true if a sip REGISTER is requesting to
     *         cancel contact registrations.
     * @throws ServletException
     */
    public static boolean isCertainRemoval( SipServletRequest request ) throws ServletException {
        if (request.getExpires() == 0)
            return true;
        for (ListIterator contacts = request.getAddressHeaders( CONTACT ); contacts.hasNext();) {
            Address contact = (Address) contacts.next();
            return contact.getExpires() == 0;
        }
        return false;
    }

    /**
     * Checks if an expires value obays to registrar server rules.
     *
     * @param expires an integer expiry value
     * @return registrar minimum acceptable value for the expires header if input is less than it, else the expires input itself.
     */
    public static int validateExpiry( int expires ) {
        if (expires <= min_expiry)
            return min_expiry;
        return expires;
    }

    /**
     * Checks if expires parameter of input contact addresses obay to registrar server rules.
     * Each expires parameter would be operated so: <br>
     * 1)if expires parameter is a negative value, it gets the validated input value of expires<br>
     * 2)if expires parameter is 0, it will get no changes<br>
     * 3)if expires parameter is a positive integer, it will updated to it's validated value<br>
     *
     * @param addresses list of SIP Address objects
     * @return list of validated Address objects
     */
    public static List validateExpiries( ListIterator addresses, int expires ) {
        List list = new ArrayList();
        expires = validateExpiry( expires );
        while (addresses.hasNext()) {
            Address s = (Address) addresses.next();
            if (s.getExpires() < 0)
                s.setExpires( expires );
            else if (s.getExpires() > 0)
                s.setExpires( validateExpiry( s.getExpires() ) );
            list.add( s );
        }
        return list;
    }

}
