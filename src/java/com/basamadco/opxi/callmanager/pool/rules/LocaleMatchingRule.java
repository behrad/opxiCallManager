package com.basamadco.opxi.callmanager.pool.rules;

import com.basamadco.opxi.callmanager.util.OpxiToolBox;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipURI;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * A locale matching rule to support native language rule for skills
 * This has a table of country codes and their spoken languages, so
 * can match if incoming requests country code section of caller id
 * is speaking the same language as the selected language.
 *
 * @author Jrad
 *         Date: Nov 4, 2006
 *         Time: 1:44:14 PM
 */
public class LocaleMatchingRule extends AbstractMatchingRule {

    private static final Logger logger = Logger.getLogger( LocaleMatchingRule.class.getName() );


    private static final char PLUS = '+';

    private String areaCodePattern;

    private Pattern areaRegxPattern;

//    private String[] countryCodes = new String[ 0 ];

    /**
     * Returns the language this rule will match countries for
     *
     * @return language property value set for this matching rule
     */
    public String getAreaCodePattern() {
        return areaCodePattern;
    }

    public void setAreaCodePattern( String areaCodePattern ) {
        this.areaCodePattern = areaCodePattern;
        areaRegxPattern = Pattern.compile( areaCodePattern );
//        resolveAcceptCountryCodes();
    }

    public boolean evaluate( SipServletRequest request ) {
        String callerId = ((SipURI) request.getFrom().getURI()).getUser();
        logger.finest( "Evaluation result [callerId=" + callerId +
                "]: " + areaRegxPattern.matcher( callerId ).matches() );
        return areaRegxPattern.matcher( callerId ).matches();
    }

    public String getRuleInfo() {
        StringBuffer buffer = new StringBuffer( "Request.from.uri.user matches '" );
        buffer.append( getAreaCodePattern() );
//        for (int i = 0; i < countryCodes.length; i++) {
//            buffer.append( countryCodes[i] );
//            if( i != countryCodes.length - 1 ) {
//                buffer.append( "|" );
//            }
//        }
        buffer.append( "'" );
        return buffer.toString();
    }

//    private void resolveAcceptCountryCodes() {
//        if( areaCodePattern != null ) {
//            areaCodePattern = areaCodePattern.toLowerCase();
//            if( LANGUAGE_LOOKUP_TABLE.containsKey( areaCodePattern ) ) {
//                countryCodes = (String[])LANGUAGE_LOOKUP_TABLE.get( areaCodePattern );
//            }
//        }
//    }

    // Seems no need for a config file yet...
//    private static final Map LANGUAGE_LOOKUP_TABLE = new HashMap();
//    static {
//        LANGUAGE_LOOKUP_TABLE.put( "fa", new String[] { "98" } );
//        LANGUAGE_LOOKUP_TABLE.put( "en", new String[] { "1" } );
//    }

}
