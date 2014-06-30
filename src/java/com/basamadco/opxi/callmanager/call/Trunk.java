package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.OpxiException;

import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;
import java.util.List;
import java.util.logging.Logger;

public class Trunk extends ProxyTarget {

    private static final Logger logger = Logger.getLogger( Trunk.class.getName() );

    private String staticRoute;

    private String dialPattern;

    public Trunk() {
    }

    public void setTelephoneNumber( String pn ) {
        super.setTelephoneNumber( pn );

    }

    public void setStaticRoute( String sr ) {
        staticRoute = sr;
    }

    public String getStaticRoute() {
        return staticRoute;
    }

    public void setDialPattern( String dp ) {
        dialPattern = dp;
    }

    public String getDialPattern() {
        return dialPattern;
    }

    public void setKeywords( String str ) {
        if (str == null) return;
        if (str.startsWith( "dialPattern:" )) {
            setDialPattern( str.split( "dialPattern:" )[1] );
        } else if (str.startsWith( "staticRoute:" )) {
            setStaticRoute( str.split( "staticRoute:" )[1] );
        }
    }


    public void setKeywords( String[] cstr ) {
        if (cstr == null) return;
        for (int i = 0; i < cstr.length; i++) {
            setKeywords( cstr[i] );
        }
    }

    // *** *** *** Override Function

    public List getTargetURIs() throws OpxiException {
        SipURI suri;
        String staticRoute = getStaticRoute();
        String[] arr = staticRoute.split( ":" );
        String user = ( (SipURI) getRequest().getRequestURI() ).getUser();
        if (arr.length > 1) {
            suri = getServiceFactory().getSipService().getSipFactory().createSipURI( user, arr[0] );
            suri.setPort( Integer.parseInt( arr[1] ) );
        } else {
            suri = getServiceFactory().getSipService().getSipFactory().createSipURI( user, staticRoute );
        }
        return getServiceFactory().getSipService().toURIList( suri );
    }

    public void service( CallService call ) throws OpxiException {
        SipURI dest = (SipURI) getTargetURIs().get( 0 );
        SipURI src = (SipURI) getRequest().getFrom().getURI();
        if (dest.getHost().equalsIgnoreCase( src.getHost() ) && dest.getPort() == src.getPort()) {
            throw new CallLoopException( "Call Loop Detected" );
        } else {
            super.service( call );
        }
    }

    public boolean isQueueable() {
        return false;
    }

    protected boolean hasUpdatableState() {
        return false;
    }

}

