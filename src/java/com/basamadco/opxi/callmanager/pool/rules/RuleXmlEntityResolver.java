package com.basamadco.opxi.callmanager.pool.rules;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * @author Jrad
 *         Date: Feb 16, 2006
 *         Time: 3:56:21 PM
 */
public class RuleXmlEntityResolver implements EntityResolver {

    private final String m_sipXmlPublicID = "-//AC and C Basamad Co//DTD SIP Matching Rule 1.0//EN";

    private String m_sipXmlRes;

    RuleXmlEntityResolver() {
        m_sipXmlRes = "/com/basamadco/opxi/callmanager/sip/rules/matching-rule-1.0.dtd";
    }

    public InputSource resolveEntity( String publicId, String systemId ) {
        if (publicId.equals( m_sipXmlPublicID ) ) {
            java.io.InputStream in = (RuleXmlEntityResolver.class).getResourceAsStream( m_sipXmlRes );
            if (in != null) {
                return new InputSource(in);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
