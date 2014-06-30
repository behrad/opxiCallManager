package com.basamadco.opxi.callmanager.pool.rules;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.URI;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TelURL;

/**
 * @author Jrad
 *         Date: Mar 5, 2006
 *         Time: 11:42:23 AM
 */
public class RequestOperand implements Operand {

    public RequestOperand( String name ) {
        if (( m_var = varNameToInt( name ) ) < 0)
            throw new IllegalArgumentException( "Bad variable name: " + name );
        if ( m_var > 23 )
            m_param = getParamName( name );
    }

    private static final String SIP_REQUEST_VAR_NAMES[] = {
            "method", "uri", "uri.scheme", "uri.user", "uri.host", "uri.port", "uri.tel", "from",
            "from.display-name", "from.uri", "from.uri.scheme", "from.uri.user", "from.uri.host",
            "from.uri.port", "from.uri.tel", "to", "to.display-name", "to.uri", "to.uri.scheme",
            "to.uri.user", "to.uri.host", "to.uri.port", "to.uri.tel"
    };

    private int m_var;

    private String m_param;

    public String getName() {
        return "SipServletRequest";
    }

    public String bind( Object o ) {
        if( !(o instanceof SipServletRequest ) ) {
            throw new IllegalArgumentException( "RequestAttributeResolver just accepts SipServletRequest instances." );
        }
        return getValue( (SipServletRequest)o );
    }

    public String getAttributeName() {
        if (m_var < 23)
            return varName( m_var );
        if (m_var == 24)
            return "uri.param." + m_param;
        if (m_var == 25)
            return "uri.from.param." + m_param;
        if (m_var == 26)
            return "uri.to.param." + m_param;
        else
            return null;
    }

    private int varNameToInt( String varName ) {
        for (int i = 0; i < 23; i++)
            if (SIP_REQUEST_VAR_NAMES[i].equals( varName ))
                return i;

        if (varName.startsWith( "uri.param." ))
            return 24;
        if (varName.startsWith( "from.uri.param." ))
            return 25;
        return !varName.startsWith( "to.uri.param." ) ? -1 : 26;
    }

    private String varName( int var ) {
        if (var >= 0 || var < 23)
            return SIP_REQUEST_VAR_NAMES[var];
        else
            return null;
    }

    private String getParamName( String varName ) {
        if (varName.startsWith( "uri.param." ))
            return varName.substring( 18 );
        if (varName.startsWith( "from.uri.param." ))
            return varName.substring( 23 );
        if (varName.startsWith( "to.uri.param." ))
            return varName.substring( 21 );
        else
            return null;
    }

    private String getValue( SipServletRequest req ) {
        return getValue( req, m_var, m_param );
    }

    private String getValue( SipServletRequest req, int var, String param ) {
        switch (var) {
            case 0:
                return req.getMethod();

            case 1:
                return req.getRequestURI().toString();

            case 2:
                return req.getRequestURI().getScheme();

            case 3:
                return getUser( req.getRequestURI() );

            case 4:
                return getHost( req.getRequestURI() );

            case 5:
                return getPort( req.getRequestURI() );

            case 6:
                return getTel( req.getRequestURI() );

            case 7:
                return req.getFrom().toString();

            case 8:
                return req.getFrom().getDisplayName();

            case 9:
                return req.getFrom().getURI().toString();

            case 10:
                return req.getFrom().getURI().getScheme();

            case 11:
                return getUser( req.getFrom().getURI() );

            case 12:
                return getHost( req.getFrom().getURI() );

            case 13:
                return getPort( req.getFrom().getURI() );

            case 14:
                return getTel( req.getFrom().getURI() );

            case 15:
                return req.getTo().toString();

            case 16:
                return req.getTo().getDisplayName();

            case 17:
                return req.getTo().getURI().toString();

            case 18:
                return req.getTo().getURI().getScheme();

            case 19:
                return getUser( req.getTo().getURI() );

            case 20:
                return getHost( req.getTo().getURI() );

            case 21:
                return getPort( req.getTo().getURI() );

            case 22:
                return getTel( req.getTo().getURI() );

            case 24:
                return getParam( req.getRequestURI(), param );

            case 25:
                return getParam( req.getFrom().getURI(), param );

            case 26:
                return getParam( req.getTo().getURI(), param );

            case 23:
            default:
                return null;
        }
    }

    private String getUser( URI uri ) {
        if (uri.isSipURI())
            return ( (SipURI) uri ).getUser();
        else
            return null;
    }

    private String getHost( URI uri ) {
        if (uri.isSipURI())
            return ( (SipURI) uri ).getHost();
        else
            return null;
    }

    private String getPort( URI uri ) {
        if (uri.isSipURI()) {
            SipURI sipURI = (SipURI) uri;
            int port = sipURI.getPort();
            if (port < 0)
                return "sips".equals( sipURI.getScheme() ) ? "5061" : "5060";
            else
                return Integer.toString( port );
        } else {
            return null;
        }
    }

    private String getTel( URI uri ) {
        if (uri.isSipURI()) {
            SipURI sipURI = (SipURI) uri;
            if ("phone".equals( sipURI.getUserParam() ))
                return stripVisuals( sipURI.getUser() );
        } else if ("tel".equals( uri.getScheme() ))
            return stripVisuals( ( (TelURL) uri ).getPhoneNumber() );
        return null;
    }

    private String stripVisuals( String s ) {
        StringBuffer buf = new StringBuffer( s.length() );
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt( i );
            if ("-.()".indexOf( c ) < 0)
                buf.append( c );
        }

        return buf.toString();
    }

    private String getParam( URI uri, String param ) {
        if (uri.isSipURI())
            return ( (SipURI) uri ).getParameter( param );
        if ("tel".equals( uri.getScheme() ))
            return ( (TelURL) uri ).getParameter( param );
        else
            return null;
    }

}
