package com.basamadco.opxi.callmanager.pool.rules;

import com.basamadco.opxi.callmanager.pool.PoolTarget;
//import com.basamad.opxicm.sip.rules.XMLValidationResult;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jrad
 *         Date: Feb 16, 2006
 *         Time: 3:36:24 PM
 */
public final class MatchingRuleParser {

    /*public static XMLValidationResult validate( String in ) {
        try {
            return validate( new ByteArrayInputStream( in.getBytes( "UTF-8" ) ) );
        } catch ( UnsupportedEncodingException e ) {
            return new XMLValidationResult( e.getMessage(), XMLValidationResult.ERROR );
        }
    }
    public static XMLValidationResult validate( InputStream in ) {
        SAXBuilder saxBuilder = new SAXBuilder( true );
        saxBuilder.setEntityResolver( new RuleXmlEntityResolver() );
        saxBuilder.setIgnoringElementContentWhitespace( true );
        try {
            saxBuilder.build(in);
            return new XMLValidationResult( "", XMLValidationResult.SUCCESS );
        } catch ( JDOMException e ) {
            return new XMLValidationResult( e.getMessage(), XMLValidationResult.ERROR );
        } catch ( IOException e ) {
            return new XMLValidationResult( e.getMessage(), XMLValidationResult.ERROR );
        }
    }*/

    public static Rule parse( String xml, PoolTarget ruleOwner ) throws NoSuchOperandException, JDOMException, IOException {
        ByteArrayInputStream bain = new ByteArrayInputStream( xml.getBytes( "UTF-8" ) );
        return parse( bain, ruleOwner );
    }

    public static Rule parse( InputStream in, PoolTarget ruleOwner ) throws NoSuchOperandException, JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder( false );
        saxBuilder.setEntityResolver( new RuleXmlEntityResolver() );
        saxBuilder.setIgnoringElementContentWhitespace( true );
        Document doc = saxBuilder.build(in);
        return interpret( doc, ruleOwner );
    }

    /*public static Rule parse( Reader in, PoolTarget ruleOwner ) throws NoSuchOperandException, JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder( false );
        saxBuilder.setEntityResolver( new RuleXmlEntityResolver() );
        saxBuilder.setIgnoringElementContentWhitespace( true );
        Document doc = saxBuilder.build( in );
        return interpret( doc, ruleOwner );
    }*/

    private static Condition createCondition( Element cElm ) throws NoSuchOperandException {
        if ( "matching-rule".equals( cElm.getName() ) )
            return createCondition( firstChild(cElm) );
        if ( "and".equals(cElm.getName() ) ) {
            List conds = new ArrayList();
            List elms = cElm.getChildren();
            for( Iterator iter = elms.iterator(); iter.hasNext(); conds.add( createCondition((Element)iter.next()) ) );
            return new And(conds);
        }
        if ( "or".equals(cElm.getName() ) ) {
            List conds = new ArrayList();
            List elms = cElm.getChildren();
            for( Iterator iter = elms.iterator(); iter.hasNext(); conds.add( createCondition((Element)iter.next()) ) );
            return new Or(conds);
        }
        if ( "not".equals(cElm.getName() ) )
            return new Not(createCondition(firstChild(cElm)));

        if ( "equal".equals(cElm.getName() ) )
            return new Equal( createOperand( cElm, 0 ), createOperand( cElm, 1 ) , "true".equals(cElm.getAttributeValue("ignore-case")));
//            return new Equal( cElm.getChild(REQUEST_PARAM_TAG_NAME).getTextNormalize(), cElm.getChild(GROUP_PARAM_TAG_NAME).getTextNormalize() , "true".equals(cElm.getAttributeValue("ignore-case")));

        if ( "contains".equals(cElm.getName() ) )
            return new Contains( createOperand( cElm, 0 ), createOperand( cElm, 1 ) , "true".equals(cElm.getAttributeValue("ignore-case")));
//            return new Contains(cElm.getChild(REQUEST_PARAM_TAG_NAME).getTextNormalize(), cElm.getChild(GROUP_PARAM_TAG_NAME).getTextNormalize(), "true".equals(cElm.getAttributeValue("ignore-case")));

        if ( "subdomain-of".equals(cElm.getName() ) )
            return new SubdomainOf( createOperand( cElm, 0 ), createOperand( cElm, 1 ) , false );
//            return new SubdomainOf(cElm.getChild(REQUEST_PARAM_TAG_NAME).getTextNormalize(), cElm.getChild(GROUP_PARAM_TAG_NAME).getTextNormalize(), false );

        return null;
    }

    private static Operand createOperand( Element elm, int index ) throws NoSuchOperandException {
        Element oprElm = getChild( elm, index );
        return OperandFactory.getOperand( oprElm.getName(), ((Attribute)oprElm.getAttributes().get( 0 )).getValue() );
    }

    private static Rule interpret( Document doc, PoolTarget ruleOwner ) throws NoSuchOperandException {
        Element root = doc.getRootElement(); // matching-rule element
        return new Rule( ruleOwner, createCondition( root ) );
    }

    private static Element firstChild( Element elm ) {
        return (Element)elm.getChildren().get(0);
    }

    private static Element getChild( Element elm, int i ) {
        return (Element)elm.getChildren().get( i );
    }

}