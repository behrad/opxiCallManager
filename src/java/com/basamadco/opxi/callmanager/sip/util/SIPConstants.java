package com.basamadco.opxi.callmanager.sip.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A common repository for all digest parameter names which are
 * defined in RFC3262[section: 22(referencing RFC2617)] and any
 * Security related constants for OpxiCallManager.
 *
 * @author Jrad
 */
public interface SIPConstants {

/*
	 * --------------------------------------------------------------
	 * Some of Standard SIP Header names
	 * --------------------------------------------------------------
	 */

    /**
     * Authorization SIP Header defined by RFC3261
     */
    public static final String AUTHORIZATION = "Authorization";

    /**
     * WWW-Authenticate SIP Header defined by RFC3261
     */
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

    /**
     * Contact SIP Header defined by RFC3261
     */
    public static final String CONTACT = "Contact";


    public static final String TO = "To";


    public static final String FROM = "From";

    /**
     * Contact SIP Header defined by RFC3261
     */
    public static final String ALLOW = "Allow";

    /**
     * REFER SIP Header defined by RFC3515
     */
    public static final String REFER_TO = "Refer-To";

    /**
     * REFER SIP Header defined by RFC3981
     */
    public static final String REFERRED_BY = "Referred-By";


    public static final String CALL_ID = "Call-ID";


    public static final String CSEQ = "CSeq";


    public static final String RECORD_ROUTE = "Record-Route";


    public static final String ROUTE = "Route";


    public static final String MAX_FORWARDS = "Max-Forwards";


    public static final String CALL_INFO = "Call-Info";

    /**
     * Via SIP Header defined by RFC3261
     */
    public static final String VIA = "Via";

    /**
     * Event SIP Header defined by RFC3265
     */
    public static final String EVENT = "Event";


    public static final String SUBSCRIPTION_STATE = "Subscription-State";


    public static final String SIP_ETAG = "SIP-ETag";


    public static final String ETAG_VALUE = "opxi-callManager-default-Etag";


    public static final String SIP_IF_MATCH = "SIP-If-Match";

    /**
     * INVITE SIP Method defined by RFC3265
     */
    public static final String INVITE = "INVITE";

    /**
     * BYE SIP Method defined by RFC3265
     */
    public static final String BYE = "BYE";

    /**
     * CANCEL SIP Method defined by RFC3265
     */
    public static final String CANCEL = "CANCEL";

    /**
     * REFER SIP Method defined by RFC3515
     */
    public static final String REFER = "REFER";

    /**
     * Notify SIP Method defined by RFC3265
     */
    public static final String NOTIFY = "NOTIFY";

    /**
     * Publish SIP Method defined by RFC3265
     */
    public static final String PUBLISH = "PUBLISH";


    public static final String MESSAGE = "MESSAGE";

    /**
     * SIP PUBLISH offline status note
     */
    public static final String NOTE_STATUS_OFFLINE = "offline";
    public static final String NOTE_STATUS_ONLINE = "available";

    /**
     * SIP PUBLISH open basic status
     */
    public static final String BASIC_STATUS_OPEN = "open";

    /**
     * SIP PUBLISH closed basic status
     */
    public static final String BASIC_STATUS_CLOSED = "closed";

    /**
     * RFC2617 Digest string constant
     */
    public static final String DIGEST_HEADER_START = "Digest";

    /**
     * RFC2617 Algorithm parameter
     */
    public static final String DIGEST_HEADER_ALGORITHM = "algorithm";

    /**
     * RFC2617 qop parameter
     */
    public static final String DIGEST_HEADER_QOP = "qop";

    /**
     * RFC2617 opaque parameter
     */
    public static final String DIGEST_HEADER_OPAQUE = "opaque";

    /**
     * RFC2617 realm parameter
     */
    public static final String DIGEST_HEADER_REALM = "realm";

    /**
     * RFC2617 charset parameter
     */
    public static final String DIGEST_HEADER_CHARSET = "charset";

    /**
     * RFC2617 nonce parameter
     */
    public static final String DIGEST_HEADER_NONCE = "nonce";

    /**
     * RFC2617 nonce count parameter
     */
    public static final String DIGEST_HEADER_NONCE_COUNTER = "nc";

    /**
     * RFC2617 client nonce parameter
     */
    public static final String DIGEST_HEADER_CLIENT_NONCE = "cnonce";

    /**
     * RFC2617 response parameter
     */
    public static final String DIGEST_HEADER_RESPONSE = "response";

    /**
     * RFC2617 username parameter
     */
    public static final String DIGEST_HEADER_USERNAME = "username";

    /**
     * RFC2617 uri parameter
     */
    public static final String DIGEST_HEADER_URI = "uri";

    /**
     * RFC2617 "," character constant
     */
    public static final char COMMA = ',';

    /**
     * RFC2617 space character constant
     */
    public static final char WS = ' ';

    /**
     * RFC2617 <"> character constant
     */
    public static final char QUOTE = '"';

    /**
     * RFC2617 "=" character constant
     */
    public static final char IS = '=';

    /**
     * OpxiCallManager supported digest algorithm
     */
    public static final String ALGORITHM_MD5 = "md5";

    /**
     * OpxiCallManager supported encoding for convertions
     */
    public static final String UTF8 = "utf-8";

    /**
     * OpxiCallManager supported quality of protection option
     */
    public static final String QOP_AUTH = "auth";

    /**
     * OpxiCallManager nonce counter value
     */
    public static final String NC_VALUE = "00000001";


    public static final String MIME_TEXT_PLAIN = "text/plain";


    public static final String MIME_TEXT_HTML = "text/html";


    public static final String MIME_TEXT_XML = "text/xml";


    public static final String REMOTE_PARTY_ID = "Remote-Party-ID";

}
