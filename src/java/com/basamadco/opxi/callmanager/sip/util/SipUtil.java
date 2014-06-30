package com.basamadco.opxi.callmanager.sip.util;

import com.basamadco.opxi.callmanager.util.OpxiToolBox;

import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class just contains some handy static methods GLOBALY
 * used in the OpxiCallManager server.
 *
 * @author Jrad
 */
public abstract class SipUtil {

    private static final Logger logger = Logger.getLogger( SipUtil.class.getName() );


    private static final String URI_REGEX = "^(sip|tel|SIP|TEL):([^@]+)@(.+)";

    private static final String SIP = "sip";

    private static final char COLON = ':';

    private static final char AT = '@';

    private static final Pattern URI_PATTERN = Pattern.compile( URI_REGEX );

    public static boolean isValidSipURI( String sipURI ) {
        if (OpxiToolBox.isEmpty( sipURI )) {
            return false;
        }
        return URI_PATTERN.matcher( sipURI ).matches();
    }

    public static String getQualifiedName( String sipURI ) {
        if (isValidSipURI( sipURI )) {
            return sipURI.split( SIP + COLON )[1];
        }
        return sipURI;
    }

    /**
     * Returns the host part of a SIP uri formated as in
     * RFC3261
     *
     * @param aor address-of-record
     * @return host part of the input address-of-record
     */
    public static String getDomain( String aor ) {
//        Matcher domain = URI_PATTERN.matcher( aor );
//        if( domain.find() ) {
//            return domain.group( 3 );
//        } else {
//            return "";
//        }
        if (aor.indexOf( "@" ) > 1) {
            String strs = aor.split( "@" )[1];
            if (strs.indexOf( ";" ) > -1)
                strs = strs.split( ";" )[0];
            else
                strs = strs.split( ">" )[0];
            if (strs.indexOf( ":" ) > -1)
                strs = strs.split( ":" )[0];
            return strs;
        } else {
            return aor.split( ":" )[1].split( ":" )[0];
        }
    }

    public static String getDomain( URI aor ) {
        return getDomain( aor.toString() );
    }

    /**
     * Returns the uername part of a SIP uri formated as in
     * RFC3261
     *
     * @param aor address-of-reord
     * @return username part of the address-of-record
     */
    public static String getName( String aor ) {
        Matcher userName = URI_PATTERN.matcher( aor );
        if (userName.find()) {
            return userName.group( 2 );
        } else {
            return "";
        }
        /*if( aor.indexOf( "@" ) > 1 ) {
            return aor.split( "@" )[0].split(":")[1];
        }
        return "null";*/
    }

    public static String getName( URI aor ) {
        return getName( aor.toString() );
    }

    public static String toSipURIString( String name, String domain ) {
        return SIP + COLON + name + AT + domain;
    }

    public static String toSipURIString( String name ) {
        return toSipURIString( name, ApplicationConstants.DOMAIN );
    }

    public static String toURIString( URI uri ) {
        //sip:mrma3d@192.168.128.52;rinstance=3798jkhd9879
        return uri.toString().split( ";" )[0].split( ":" )[1];
    }

}