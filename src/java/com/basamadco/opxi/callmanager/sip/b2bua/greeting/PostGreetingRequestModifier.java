package com.basamadco.opxi.callmanager.sip.b2bua.greeting;

import com.basamadco.opxi.callmanager.web.A2CBridge;
import com.basamadco.opxi.callmanager.SipService;
import com.basamadco.opxi.callmanager.sip.util.SIPConstants;
import com.basamadco.opxi.callmanager.sip.RequestModifier;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.Address;

/**
 * @author Jrad
 *         Date: Nov 29, 2007
 *         Time: 1:14:21 PM
 */
public class PostGreetingRequestModifier implements RequestModifier {

    private Address callInfoHeader;

    private Address to;

    private Address from;


    public PostGreetingRequestModifier( SipService ss, Address from, Address to ) {
        setCallInfoHeader( createCallInfoHeader( ss, from, to ) );
        setTo( to );
        setFrom( from );
    }

    private Address getCallInfoHeader() {
        return callInfoHeader;
    }

    private void setCallInfoHeader( Address callInfoHeader ) {
        this.callInfoHeader = callInfoHeader;
    }

    public Address getFrom() {
        return from;
    }

    public void setFrom( Address from ) {
        this.from = from;
    }

    public Address getTo() {
        return to;
    }

    public void setTo( Address to ) {
        this.to = to;
    }

    private final static char AMPERSAND = '&';
    private final static char EQUAL = '=';
    private static final String ORIGINAL_TO = "original_to";

    private Address createCallInfoHeader( SipService ss, Address from, Address to ) {
        String sipuser = ((SipURI)from.getURI()).getUser();
        String displayname = from.getDisplayName();
        String domain = ((SipURI)from.getURI()).getHost();
        StringBuffer callInfo_buffer = new StringBuffer(  "/" + A2CBridge.ACTION  + "?" );
        if ( sipuser != null ) {
            callInfo_buffer.append( AMPERSAND ).append( A2CBridge.CALLER_SIPUSERNAME_PARAMETER_NAME )
                    .append( EQUAL ).append( sipuser.replaceAll( "\\+", "%2B" ) );
        }
        if ( displayname != null) {
            callInfo_buffer.append( AMPERSAND ).append( A2CBridge.CALLER_DISPLAYNAME_PARAMETER_NAME )
                    .append( EQUAL ).append( displayname );
        }
        if ( domain != null) {
            callInfo_buffer.append( AMPERSAND ).append( A2CBridge.CALLER_SIPDOMAIN_PARAMETER_NAME )
            .append( EQUAL ).append( domain );
        }

        callInfo_buffer.append( AMPERSAND ).append( ORIGINAL_TO ).append( EQUAL ).append( to.toString() );

//        logger.finest( "call info: '" + callInfo_buffer + "'" );
        Address callInfo = ss.getSipFactory().createAddress( ss.getLocalURL( callInfo_buffer.toString() ) );
        callInfo.setParameter( "purpose", "info" );
        return callInfo;
    }

    public void modify( SipServletRequest request ) {
        request.setAddressHeader( SIPConstants.CALL_INFO, getCallInfoHeader() );
//        request.setAddressHeader( SIPConstants.FROM, getFrom() );
    }
}
