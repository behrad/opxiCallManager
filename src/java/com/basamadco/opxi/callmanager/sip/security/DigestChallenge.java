package com.basamadco.opxi.callmanager.sip.security;

import com.basamadco.opxi.callmanager.sip.util.SIPConstants;

/**
 * Represents RFC2617[3.2.1] specified WWW-Authenticate Response Header
 * OpxiCallManager initializes an object from this class to challenge
 * the user agent client.
 * @author Jrad
 */
public class DigestChallenge implements SIPConstants {

    private String    m_realm;
    private String    m_nonce;
    private String    m_qop;
    private boolean   m_staleFlag;
    private int       m_maxBuf;
    private String    m_characterSet;
    private String    m_algorithm;

    DigestChallenge() {
    }

    /**
     * Return the list of the All the Realms
     *
     * @return  List of all the realms 
     */
    public String getRealm()
    {
        return m_realm;
    }

    /**
     * @return Returns the Nonce
     */
    public String getNonce()
    {
        return m_nonce;
    }

    /**
     * Return the quality-of-protection
     * 
     * @return The quality-of-protection
     */
    public String getQop()
    {
        return m_qop;
    }

    /**
     * @return The state of the Staleflag
     */
    public boolean getStaleFlag()
    {
        return m_staleFlag;
    }

    /**
     * @return The Maximum Buffer value
     */
    public int getMaxBuf()
    {
        return m_maxBuf;
    }

    /**
     * @return character set values as string
     */
    public String getCharacterSet()
    {
        return m_characterSet;
    }

    /**
     * @return The String value of the algorithm
     */
    public String getAlgorithm()
    {
        return m_algorithm;
    }

    void setAlgorithm(String m_algorithm) {
        this.m_algorithm = m_algorithm;
    }

    void setCharacterSet(String set) {
        m_characterSet = set;
    }

    void setNonce(String m_nonce) {
        this.m_nonce = m_nonce;
    }

    void setQop(String m_qop) {
        this.m_qop = m_qop;
    }

    void setRealm(String m_realm) {
        this.m_realm = m_realm;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer( DIGEST_HEADER_START ).append( WS );
        buffer.append( DIGEST_HEADER_QOP).append( IS ).append( QUOTE ).append( getQop() ).append( QUOTE ).append( COMMA ).append( WS );
        buffer.append( DIGEST_HEADER_REALM).append( IS ).append( QUOTE ).append( getRealm() ).append( QUOTE ).append( COMMA ).append( WS );
        buffer.append( DIGEST_HEADER_NONCE).append( IS ).append( QUOTE ).append( getNonce() ).append( QUOTE ).append( COMMA ).append( WS );
        buffer.append( DIGEST_HEADER_ALGORITHM).append( IS ).append( QUOTE ).append( getAlgorithm() ).append( QUOTE ).append( COMMA ).append( WS );
        buffer.append( DIGEST_HEADER_CHARSET).append( IS ).append( QUOTE ).append( getCharacterSet() ).append( QUOTE );
        return buffer.toString();
    }


}