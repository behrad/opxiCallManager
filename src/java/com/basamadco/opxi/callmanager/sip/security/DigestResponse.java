package com.basamadco.opxi.callmanager.sip.security;

import java.util.Iterator;

import com.basamadco.opxi.callmanager.sip.util.SIPConstants;
import com.novell.security.sasl.SaslException;

/**
 * Represents RFC2617[3.2.2] specified Authorization Request Header.
 * 
 * @author Jrad
 */
public class DigestResponse implements SIPConstants {
	
	private String realm;
	
	private String username;
	
	private String qop;
	
	private String cnonce;
	
	private String nonce;
	
	private String charset;
	
	private String nc;
	
	private String response;
	
	private String algorithm;
	
	private String uri;
	
	/**
	 * Will create a DigestResponse wrapper object on the input
	 * byte stream containing the client's Authorization header.
	 * 
	 * @param digest_response
	 * @throws SaslException
	 */
	DigestResponse( byte[] digest_response ) throws SaslException {	 	   
	    DirectiveList dirList = new DirectiveList( digest_response );
        dirList.parseDirectives();
        checkSemantics(dirList);	     
	}
	
	private void checkSemantics( DirectiveList dirList ) {
	    Iterator directives = dirList.getIterator();
	    ParsedDirective directive;
	    String name;

	    while (directives.hasNext())
	    {
	        directive = (ParsedDirective)directives.next();
	        name = directive.getName();
	        if (name.equals(DIGEST_HEADER_REALM))
	            setRealm(directive.getValue());
	        else if (name.equals(DIGEST_HEADER_NONCE))
	            setNonce(directive.getValue());
	        else if (name.equals(DIGEST_HEADER_QOP))
	            setQop(directive.getValue());
	        else if (name.equals(DIGEST_HEADER_CLIENT_NONCE))
	            setCnonce(directive.getValue());
	        else if (name.equals(DIGEST_HEADER_CHARSET))
	            setCharset(directive.getValue());
	        else if (name.equals(DIGEST_HEADER_ALGORITHM))
	            setAlgorithm(directive.getValue());
	        else if (name.equals(DIGEST_HEADER_NONCE_COUNTER))
	            setNc(directive.getValue());
	        else if (name.equals(DIGEST_HEADER_RESPONSE))
	            setResponse(directive.getValue());
	        else if (name.equals(DIGEST_HEADER_URI))
	            setUri(directive.getValue());
	        else if (name.equals(DIGEST_HEADER_USERNAME))
	            setUsername(directive.getValue());
	    }
	}

	String getAlgorithm() {
		return algorithm;
	}

	private void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	String getCharset() {
		return charset;
	}

	private void setCharset(String charset) {
		this.charset = charset;
	}

	String getCnonce() {
		return cnonce;
	}

	private void setCnonce(String cnonce) {
		this.cnonce = cnonce;
	}

	String getNc() {
		return nc;
	}

	private void setNc(String nc) {
		this.nc = nc;
	}

	String getNonce() {
		return nonce;
	}

	private void setNonce(String nonce) {
		this.nonce = nonce;
	}

	String getQop() {
		return qop;
	}

	private void setQop(String qop) {
		this.qop = qop;
	}

	String getRealm() {
		return realm;
	}

	private void setRealm(String realm) {
		this.realm = realm;
	}

	String getResponse() {
		return response;
	}

	private void setResponse(String response) {
		this.response = response;
	}

	String getUri() {
		return uri;
	}

	private void setUri(String uri) {
		this.uri = uri;
	}

	String getUsername() {
		return username;
	}

	private void setUsername(String username) {
		this.username = username;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer( DIGEST_HEADER_START ).append( WS );
		buffer.append( DIGEST_HEADER_USERNAME).append( IS ).append( QUOTE ).append( getUsername() ).append( QUOTE ).append( COMMA ).append( WS );
		buffer.append( DIGEST_HEADER_QOP).append( IS ).append( QUOTE ).append( getQop() ).append( QUOTE ).append( COMMA ).append( WS );
		buffer.append( DIGEST_HEADER_REALM).append( IS ).append( QUOTE ).append( getRealm() ).append( QUOTE ).append( COMMA ).append( WS );
		buffer.append( DIGEST_HEADER_NONCE).append( IS ).append( QUOTE ).append( getNonce() ).append( QUOTE ).append( COMMA ).append( WS );
		buffer.append( DIGEST_HEADER_NONCE_COUNTER).append( IS ).append( QUOTE ).append( getNc() ).append( QUOTE ).append( COMMA ).append( WS );
		buffer.append( DIGEST_HEADER_CLIENT_NONCE).append( IS ).append( QUOTE ).append( getCnonce() ).append( QUOTE ).append( COMMA ).append( WS );
		buffer.append( DIGEST_HEADER_ALGORITHM).append( IS ).append( QUOTE ).append( getAlgorithm() ).append( QUOTE ).append( COMMA ).append( WS );
		buffer.append( DIGEST_HEADER_CHARSET).append( IS ).append( QUOTE ).append( getCharset() ).append( QUOTE ).append( COMMA ).append( WS );
		buffer.append( DIGEST_HEADER_URI).append( IS ).append( QUOTE ).append( getUri() ).append( QUOTE ).append( COMMA ).append( WS );
		buffer.append( DIGEST_HEADER_RESPONSE).append( IS ).append( QUOTE ).append( getResponse() ).append( QUOTE );
		return buffer.toString();
	}
	
}
