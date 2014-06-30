package com.basamadco.opxi.callmanager.web.vxml;

import com.basamadco.opxi.callmanager.sip.front.PostGreetingFront;
import com.basamadco.opxi.callmanager.util.PropertyUtil;
import com.basamadco.opxi.callmanager.web.OpxiHttpServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Jul 25, 2006
 *         Time: 6:33:45 PM
 */
public class GreetingVxmlServlet extends OpxiHttpServlet {

    private static final Logger logger = Logger.getLogger( WaitingRoomVxmlServlet.class.getName() );


    private static final String USERNAME = PropertyUtil.getProperty( "opxi.callmanager.exchange.username" );

    private static final String PASSWORD = PropertyUtil.getProperty( "opxi.callmanager.exchange.password" ).replaceAll( "#", "%35" );

    private static final String greetingTransferUser = PropertyUtil.getProperty( "opxi.callmanager.greeting.IVRtransferUser" );

    private static final String DEFAULT_VOX_URL = PropertyUtil.getProperty( "opxi.callmanager.greeting.defaultVOX" );

//    private static final String LOCAL_DOMAIN = DOMAIN;

    private static final String GREETING_CTX_ID_PARAM = PostGreetingFront.GREETING_CTX_CALLID_PARAM;


    public void init( ServletConfig config) throws ServletException {
        super.init(config);
    }

    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        response.setContentType( "text/xml" );
        PrintWriter VXML_STREAM = response.getWriter();

        String ctxIdStr = request.getParameter( "greeting_ctxId" );
        String mediaURIStr = request.getParameter( "mediaURI" );
        String ringtoneStr = request.getParameter( "ringtone" );
        if( isEmpty( ctxIdStr ) || isEmpty( ctxIdStr ) || isEmpty( ctxIdStr ) ){
            VXML_STREAM.print( USERNAME );
            VXML_STREAM.flush();
            return;
        }

        StringBuffer ctxId = new StringBuffer( ctxIdStr.replaceAll( "\"", "" ) );
        StringBuffer mediaURI = new StringBuffer( mediaURIStr );
        StringBuffer ringtone = new StringBuffer( ringtoneStr );

        String user = request.getParameter( "u" );
        String pass = request.getParameter( "p" );
        pass = pass.replaceAll( "#", "%35" );
        mediaURI.insert( 7, user + ":" + pass + "@" );
        ringtone.insert( 7, USERNAME + ":" + PASSWORD + "@" );

        String out = TRANSFER_VXML_TEMPLATE;
        out = out.replaceAll( AGENT_URI, ctxId.toString() ).replaceAll( GREETING_MEDIA, mediaURI.toString() );
        out = out.replaceAll( RINGTONE, ringtone.toString() );
        VXML_STREAM.print( out );
        VXML_STREAM.flush();
    }

    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doPost( request, response );
    }

    private boolean isEmpty( String str ) {
        return (str == null) || (str.length() == 0) || str.equalsIgnoreCase( "null" );
    }

//    private void sendMail( HttpServletRequest request ) {
//        String mailHost = "opxiappserver";
//        String to = request.getParameter( "mailTo" );
//        String from = "voiceMail@cc.basamad.acc";
//        String subject = "VoiceMessage";
//        String body = "This is an example voice message.";
//        try {
//            Properties props = System.getProperties();
//            props.put("mail.smtp.host", mailHost);
//            Session session = Session.getInstance(props,null);
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(from));
//            message.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress(to) });
//            message.setSubject(subject);
//            message.setContent(body, "text/plain");
//
//            BodyPart messageBodyPart = new MimeBodyPart();
//            messageBodyPart.setText( body );
//
////             Create a multi-part to combine the parts
//            BodyPart attachment = new MimeBodyPart();
//            String filename = "voicemail.vox";
//            File file = new File( filename );
//            FileOutputStream fos = new FileOutputStream( file );
//            fos.write( request.getParameter( "voice" ).getBytes() );
//            fos.close();
//            log( "Attachment saved: " + file.getAbsolutePath() );
//            DataSource source = new FileDataSource( file.getAbsolutePath() );
//            attachment.setDataHandler( new DataHandler( source ) );
//            attachment.setFileName( file.getName() );
//
//            Multipart multipart = new MimeMultipart();
//            multipart.addBodyPart( messageBodyPart );
//            multipart.addBodyPart( attachment );
//            message.setContent( multipart );
//            Transport.send(message);
//            log( "A mail message to " + to + " was successfully sent." );
//        } catch ( Exception e ) {
//            log( "Error sending mail: ", e );
//        }
//    }

    private static final String AGENT_URI = "@AGENT_URI";

    private static final String CALLER_CALLID = "@CALLER";

    private static final String GREETING_MEDIA = "@GREETING";

    private static final String RINGTONE = "@RINGTONE";


    private static final String TRANSFER_VXML_TEMPLATE = "<vxml version=\"2.0\" xmlns=\"http://www.w3.org/2001/vxml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "      xsi:schemaLocation=\"http://www.w3.org/2001/vxml http://www.w3.org/TR/voicexml20/vxml.xsd\">\n" +
            "\n" +
            "    <form id=\"form1\">\n" +
            "        <block>\n" +
            "            <prompt>\n" +
            "                <audio src=\"" + GREETING_MEDIA + "\"/>\n" +
            "            </prompt>\n" +
            "        </block>\n" +
//            "        <transfer dest=\"sip:" + greetingTransferUser + "@" + DOMAIN + ";"+GREETING_CTX_ID_PARAM+"="+AGENT_URI+";caller="
            "        <transfer dest=\"sip:" + greetingTransferUser + "@" + DOMAIN + ";"+GREETING_CTX_ID_PARAM+"="+AGENT_URI
//            +
//            CALLER_CALLID
            +"\"\n" +
            "                bridge=\"true\"\n" +
            "                maxtime=\"30s\"\n" +
            "                transferaudio=\"" + RINGTONE + "\">\n" +
            "        </transfer>\n" +
            "    </form>\n" +
            "\n" +
            "\n" +
            "</vxml>";


    private static final String DEFAULT_GREETING_VXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<vxml version=\"2.0\" application=\"votingAppRoot.vxml\"\n" +
            "\txmlns=\"http://www.w3.org/2001/vxml\"\n" +
            "\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "\txsi:schemaLocation=\"http://www.w3.org/2001/vxml http://www.w3.org/TR/voicexml20/vxml.xsd\">\n" +
            "\n" +
            "\t<meta name=\"defaultGreeting.vxml\" content=\"\" />\n" +
            "\t<meta name=\"Author\" content=\"\" />\n" +
            "\t<meta name=\"Date\" content=\"\" />\n" +
            "\t<meta name=\"Description\" content=\"\" />\n" +
            "\t<meta name=\"Support\" content=\"\" />\t\n" +
            "\n" +
            "\n" +
            "\t<form id = \"Form1\" scope = \"dialog\" > \n" +
            "\n" +
            "\t\t<block>\n" +
            "\t\t <audio  src=\"" + DEFAULT_VOX_URL + "\" />\n" +
            "\t\t</block>\n" +
            "\n" +
            "\t</form>\n" +
            "\t\n" +
            "</vxml>";




}
