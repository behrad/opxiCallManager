package com.basamadco.opxi.callmanager.sip.security;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.sip.util.SIPConstants;
import com.novell.security.sasl.SaslException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is a simple implementation of a SIP Authentication
 * Server based on Digest MD5 algorithm.
 *
 * @author Jrad
 */
public class SIPDigestMD5Server implements SIPConstants {

    /**
     * Log4J logger object used by opxi sipservlets to logg.
     */
    protected static final Logger logger = Logger.getLogger(SIPDigestMD5Server.class.getName());

    /**
     * Creates a RFC2617 digest challenge based for the specified
     * realm and SIP dialog ID of the user agent client.
     *
     * @param key   UA SIP Dialog ID
     * @param realm UA Realm
     * @return Digest challenge header
     */
    public static String createDigestChallenge(String key, String realm) {
        DigestChallenge digest_challenge = new DigestChallenge();
        digest_challenge.setAlgorithm(ALGORITHM_MD5);
        digest_challenge.setCharacterSet(UTF8);
        digest_challenge.setQop(QOP_AUTH);
        digest_challenge.setNonce(generateNonce(key, realm));
        digest_challenge.setRealm(realm);
        return digest_challenge.toString();
    }

    /**
     * Tries to bind a previously challenged client to DIRECTORY
     * using client's dialog ID and digest response.
     *
     * @param digest_response_header SIP "Authorization" header
     * @return true if client has valid credentials
     * @throws DigestResponseException if the input digest response
     *                                 is not a valid Authorization header.
     */
    public static boolean bind(String method, String digest_response_header) throws OpxiException {
        try {
            byte[] dr = digest_response_header.split(DIGEST_HEADER_START + " ")[1].getBytes(UTF8);
            DigestResponse digest_response = new DigestResponse(dr);
//			if( !digest_response.getNc().equals( NC_VALUE ) )
//				return false;

            String password =
                    BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO().getUserPassword(digest_response.getUsername());
            if (password == null) {
                password = ""; //Let the user's not in directory server register with a '' password!
            }
//            logger.finest( "Fetched user[="+digest_response.getUsername()+"] password: " + password );
            return evaluate(method, digest_response, password);
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DigestResponseException(UTF8 + " encoding not supported!");
        } catch (SaslException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DigestResponseException(e);
        } catch (OpxiException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Evaluates client's digest response to make sure if is valid
     * response to corresponding challenged made by server as specified
     * by Digest Authentication Mechanism in RFC2617
     *
     * @param response Client returned digest response
     * @param password Client password read by server from DIRECTORY.
     * @return true if credentials are valid
     * @throws SaslException
     */
    private static boolean evaluate(String method, DigestResponse response, String password) throws SaslException {
        char[] m_HA1 = digestCalcHA1(response.getAlgorithm(), response.getUsername(), response.getRealm(),
                password, response.getNonce(), response.getCnonce());
        char[] authres = digestCalcResponse(m_HA1, response.getNonce(), response.getNc(), response.getCnonce(),
                response.getQop(), method, response.getUri(), true);
        if (!response.getResponse().equals(new String(authres)))
            return false;
        return true;
    }

    /**
     * Generates server nonce value specified by RFC2617
     *
     * @return 32 character Hexadecimal representation of nonce value.
     */
    private static String generateNonce(String key, String realm) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(key.getBytes(UTF8));
            md.update(":".getBytes(UTF8));
            md.update(String.valueOf(System.currentTimeMillis()).getBytes(UTF8));
            md.update(":".getBytes(UTF8));
            md.update(realm.getBytes(UTF8));
            result = new String(convertToHex(md.digest()));
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

    /**
     * Calculates the HA1 portion of the response
     *
     * @param algorithm   Algorith to use.
     * @param userName    User being authenticated
     * @param realm       realm information
     * @param password    password of teh user
     * @param nonce       nonce value
     * @param clientNonce Clients Nonce value
     * @return HA1 portion of the response in a character array
     * @throws SaslException If an error occurs
     */
    static char[] digestCalcHA1(
            String algorithm,
            String userName,
            String realm,
            String password,
            String nonce,
            String clientNonce) throws SaslException {
        byte[] hash;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(userName.getBytes("UTF-8"));
            md.update(":".getBytes("UTF-8"));
            md.update(realm.getBytes("UTF-8"));
            md.update(":".getBytes("UTF-8"));
            md.update(password.getBytes("UTF-8"));
            hash = md.digest();

            if ("md5-sess".equals(algorithm)) {
                md.update(hash);
                md.update(":".getBytes("UTF-8"));
                md.update(nonce.getBytes("UTF-8"));
                md.update(":".getBytes("UTF-8"));
                md.update(clientNonce.getBytes("UTF-8"));
                hash = md.digest();
            }
        }
        catch (NoSuchAlgorithmException e) {
            throw new SaslException("No provider found for MD5 hash", e);
        }
        catch (UnsupportedEncodingException e) {
            throw new SaslException(
                    "UTF-8 encoding not supported by platform.", e);
        }

        return convertToHex(hash);
    }


    /**
     * This function calculates the response-value of the response directive of
     * the digest-response as documented in RFC 2831
     *
     * @param HA1                H(A1)
     * @param serverNonce        nonce from server
     * @param nonceCount         8 hex digits
     * @param clientNonce        client nonce
     * @param qop                qop-value: "", "auth", "auth-int"
     * @param method             method from the request
     * @param digestUri          requested URL
     * @param clientResponseFlag request-digest or response-digest
     * @return Response-value of the response directive of the digest-response
     * @throws SaslException If an error occurs
     */
    static char[] digestCalcResponse(
            char[] HA1,            /* H(A1) */
            String serverNonce,    /* nonce from server */
            String nonceCount,     /* 8 hex digits */
            String clientNonce,    /* client nonce */
            String qop,            /* qop-value: "", "auth", "auth-int" */
            String method,         /* method from the request */
            String digestUri,      /* requested URL */
            boolean clientResponseFlag) /* request-digest or response-digest */
            throws SaslException {
        byte[] HA2;
        byte[] respHash;
        char[] HA2Hex;

        // calculate H(A2)
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (clientResponseFlag) {
                md.update(method.getBytes("UTF-8"));
            }
            md.update(":".getBytes("UTF-8"));
            md.update(digestUri.getBytes("UTF-8"));
            if ("auth-int".equals(qop)) {
                md.update(":".getBytes("UTF-8"));
                md.update("00000000000000000000000000000000".getBytes("UTF-8"));
            }
            HA2 = md.digest();
            HA2Hex = convertToHex(HA2);

            // calculate response
            md.update(new String(HA1).getBytes("UTF-8"));
            md.update(":".getBytes("UTF-8"));
            md.update(serverNonce.getBytes("UTF-8"));
            md.update(":".getBytes("UTF-8"));
            if (qop.length() > 0) {
                md.update(nonceCount.getBytes("UTF-8"));
                md.update(":".getBytes("UTF-8"));
                md.update(clientNonce.getBytes("UTF-8"));
                md.update(":".getBytes("UTF-8"));
                md.update(qop.getBytes("UTF-8"));
                md.update(":".getBytes("UTF-8"));
            }
            md.update(new String(HA2Hex).getBytes("UTF-8"));
            respHash = md.digest();
        }
        catch (NoSuchAlgorithmException e) {
            throw new SaslException("No provider found for MD5 hash", e);
        }
        catch (UnsupportedEncodingException e) {
            throw new SaslException(
                    "UTF-8 encoding not supported by platform.", e);
        }

        return convertToHex(respHash);
    }

    /**
     * This function takes a 16 byte binary md5-hash value and creates a 32
     * character (plus    a terminating null character) hex-digit
     * representation of binary data.
     *
     * @param hash 16 byte binary md5-hash value in bytes
     * @return 32 character (plus    a terminating null character) hex-digit
     *         representation of binary data.
     */
    private static char[] convertToHex(byte[] hash) {
        int i;
        char[] hex = new char[32];

        for (i = 0; i < 16; i++) {
            //convert value of top 4 bits to hex char
            hex[i * 2] = getHexChar((byte) ((hash[i] & 0xf0) >> 4));
            //convert value of bottom 4 bits to hex char
            hex[(i * 2) + 1] = getHexChar((byte) (hash[i] & 0x0f));
        }

        return hex;
    }

    /**
     * This function returns hex character representing the value of the input
     *
     * @param value Input value in byte
     * @return Hex value of the Input byte value
     */
    private static char getHexChar(byte value) {
        switch (value) {
            case 0:
                return '0';
            case 1:
                return '1';
            case 2:
                return '2';
            case 3:
                return '3';
            case 4:
                return '4';
            case 5:
                return '5';
            case 6:
                return '6';
            case 7:
                return '7';
            case 8:
                return '8';
            case 9:
                return '9';
            case 10:
                return 'a';
            case 11:
                return 'b';
            case 12:
                return 'c';
            case 13:
                return 'd';
            case 14:
                return 'e';
            case 15:
                return 'f';
            default:
                return 'Z';
        }
    }

}
