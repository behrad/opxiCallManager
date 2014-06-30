package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.OpxiException;

import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.List;

/**
 * This CallTarget is a fake call target to use when routing outbound calls with static outbound router
 * and no mapped entity exits for this kind of targets in directory server till now
 *
 * @author Jrad
 *         Date: Nov 19, 2007
 *         Time: 12:02:15 PM
 * @deprecated Replaced by Trunk
 */
public class OutboundTarget extends ProxyTarget {

    private static final Logger logger = Logger.getLogger( OutboundTarget.class.getName());

    private Pattern phonePattern;

    private String AOR;


    public OutboundTarget( String phonePattern, String AOR ) {
        setAOR( AOR );
        setPhonePattern( phonePattern ); 
    }

    public void setPhonePattern( String phonePattern ) {
        this.phonePattern = Pattern.compile( phonePattern );
    }


    public void setAOR( String AOR ) {
        this.AOR = AOR;
    }

    public boolean matches( String phoneNumber ) {
        return phonePattern.matcher( phoneNumber ).matches();
    }

    public List getTargetURIs() throws OpxiException {
        try {
            URI uri = getServiceFactory().getSipService().getSipFactory().createURI( AOR );
            ((SipURI)uri).setUser( getTelephoneNumber() );
            return getServiceFactory().getSipService().toURIList( uri );
        } catch ( ServletParseException e ) {
            throw new OpxiException( e );
        }
    }


    protected boolean hasUpdatableState() {
        return false;
    }
}
