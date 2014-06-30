package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.profile.Parameter;
import com.basamadco.opxi.callmanager.entity.profile.Participation;
import com.basamadco.opxi.callmanager.profile.ProfileException;
import com.basamadco.opxi.callmanager.sip.util.SIPConstants;
import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;

import javax.servlet.sip.Address;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipURI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents voice applications as callable entities.
 *
 * @author Jrad
 *         Date: Jul 26, 2006
 *         Time: 2:12:53 PM
 */
public class Application extends ProxyTarget implements Comparable {

    private static final Logger logger = Logger.getLogger( Application.class.getName() );


    private String url;

    private Expression expression;

    private Integer priority;

    private Map<String, String> participationMap = new HashMap<String, String>( 3 );

    private Map<String, String> profileParameterMap = new HashMap<String, String>();

    private ApplicationIntegrationContext integrationCtx;

//    private String urlParams;

    public Application() {
    }


    public boolean evaluate( ApplicationIntegrationContext ctx ) throws ApplicationIntegrationException {
        try {
            return ((Boolean) expression.evaluate( ctx ));
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ApplicationIntegrationException( e );
        }
    }

//    public void service( CallService call ) throws OpxiException {
//        call.proxy( getTargetURIs() );
//    }

    /**
     * Simply returns false, as Voice Applications are implemented
     * only in proxy mode and they need no queue management.
     *
     * @return false
     */
    public boolean isQueueable() {
        return false;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl( String url ) {
        this.url = url;
    }


    public Integer getPriority() {
        return priority;
    }

    public void setPriority( Integer priority ) {
        this.priority = priority;
    }

    private ApplicationIntegrationContext getIntegrationCtx() {
        return integrationCtx;
    }

    public void setIntegrationCtx( ApplicationIntegrationContext integrationCtx ) {
        this.integrationCtx = integrationCtx;
    }

    public void setProfile( com.basamadco.opxi.callmanager.entity.profile.Application profile )
            throws ProfileException {
        expression = buildExpression( profile.getExpression() );
        setPriority( profile.getPriority() );
        buildParticipationMap( profile.getParticipation() );
        buildProfileParameterMap( profile.getParameter() );
    }

    public Map<String, String> getParticipationMap() {
        return participationMap;
    }

    public Map<String, String> getParameterMap() {
        return profileParameterMap;
    }

    public void buildProfileParameterMap( Parameter[] parameters ) {
        if ( parameters != null ) {
            profileParameterMap.clear();
            for ( Parameter parameter : parameters ) {
                profileParameterMap.put( parameter.getName(), parameter.getValue() );
            }
        }
    }

    public String getParticipationRole( String party ) {
        if ( participationMap.containsKey( party ) ) {
            return (String) participationMap.get( party );
        }
        throw new IllegalStateException( "No participation role specified for party '" + party +
                "' in application profile: " + getName() );
    }

    private void buildParticipationMap( Participation[] ps ) {
        // build call machine conf
        if ( ps != null ) {
            participationMap.clear();
            for ( Participation p : ps ) {
                participationMap.put( p.getParty(), p.getRole() );
            }
        }
    }

    private Expression buildExpression( String expString ) throws ProfileException {
        try {
            return ExpressionFactory.createExpression( expString );

        } catch ( Exception e ) {
            throw new ProfileException( e );
        }
    }

    /**
     * Constructs the actual access URI for this voice application
     *
     * @return The access URI on the Call Manager's media server
     * @throws com.basamadco.opxi.callmanager.OpxiException
     *
     */
    public List getTargetURIs() throws OpxiException {
        try {
            String url = getUrl().split( "\\?" )[0];
            String voiceAppUriStr = getServiceFactory().getMediaService().getVoiceAppURI( url );
            // ignore stored parameters...
            SipURI voiceAppURI = (SipURI) getServiceFactory().getSipService().getSipFactory().createURI( voiceAppUriStr );
            setParameters( voiceAppURI );
            return getServiceFactory().getSipService().toURIList( voiceAppURI );
        } catch ( ServletParseException e ) {
            throw new OpxiException( e );
        }

    }

    protected void setParameters( SipURI uri ) {
        HashMap<String, String> paramMap = buildParametersMap();

        for ( String name : paramMap.keySet() ) {
            String value = paramMap.get( name );
            uri.setParameter( name, value );
        }
    }


    private HashMap<String, String> buildParametersMap() {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        Iterator uriParams = ((SipURI) getRequest().getRequestURI()).getParameterNames();
        while ( uriParams.hasNext() ) {
            String pname = (String) uriParams.next();
            String pvalue = ((SipURI) getRequest().getRequestURI()).getParameter( pname );
            paramMap.put( pname, pvalue );
        }
        Iterator toParams = ((SipURI) getRequest().getTo().getURI()).getParameterNames();
        while ( toParams.hasNext() ) {
            String pname = (String) toParams.next();
            String pvalue = ((SipURI) getRequest().getTo().getURI()).getParameter( pname );
            paramMap.put( pname, pvalue );
        }
        Iterator toAddrParams = getRequest().getTo().getParameterNames();
        while ( toAddrParams.hasNext() ) {
            String pname = (String) toAddrParams.next();
            String pvalue = getRequest().getTo().getParameter( pname );
            paramMap.put( pname, pvalue );
        }
        // Added for Cisco Gateway compatibility when using REFER to transfer to an application
        try {
            Address referedBy = getRequest().getAddressHeader( SIPConstants.REFERRED_BY );
            if ( referedBy != null ) {
                Iterator referedByParams = referedBy.getParameterNames();
                while ( referedByParams.hasNext() ) {
                    String pname = (String) referedByParams.next();
                    String pvalue = referedBy.getParameter( pname );
                    paramMap.put( pname, pvalue );
                }
            }
        } catch ( ServletParseException e ) {
            logger.severe( e.getMessage() );
        }

        addIntegrationParameters( paramMap );

        addProfileParameters( paramMap );


        return paramMap;
    }

    protected void addProfileParameters( Map<String, String> paramMap ) {
        if ( getUrl().indexOf( "?" ) > 0 ) {
            String urlParams = getUrl().split( "\\?" )[1];
            for ( String param : urlParams.split( "&" ) ) {
                paramMap.put( param.split( "=" )[0], param.split( "=" )[1] );
            }
        }
    }


    private void addIntegrationParameters( Map<String, String> paramMap ) {
        for ( String parameter : profileParameterMap.keySet() ) {
            String value = profileParameterMap.get( parameter );
            try {
                Object result = buildExpression( value ).evaluate( getIntegrationCtx() );
                if ( result != null ) {
                    logger.finest( "Adding parameter as a context parameter <" + parameter + ", " + result + ">" );
                    paramMap.put( parameter, result.toString() );
                } else {
                    logger.finest( "Adding parameter as a simple parameter <" + parameter + ", " + value + ">" );
                    paramMap.put( parameter, value );
                }
            } catch ( Exception e ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
                logger.finest( "Adding parameter as a simple parameter <" + parameter + ", " + value + ">" );
                paramMap.put( parameter, value );
            }
        }
    }


    protected boolean hasUpdatableState() {
        return false;
    }

    public int compareTo( Object o ) {
        if ( o instanceof Application ) {
            int thisP = getPriority();
            int thatP = ((Application) o).getPriority();
            if ( thisP < thatP ) {
                return +1;
            } else if ( thisP == thatP ) {
                return 0;
            } else {
                return -1;
            }
        } else {
            return 0;
        }
    }

    public void destroy() {
        profileParameterMap.clear();
        participationMap.clear();
    }

}