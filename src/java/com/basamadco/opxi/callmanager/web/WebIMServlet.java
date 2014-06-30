package com.basamadco.opxi.callmanager.web;

import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.entity.Registration;
import com.basamadco.opxi.callmanager.sip.util.SIPConstants;
import com.basamadco.opxi.callmanager.call.CallServiceFactory;
import com.basamadco.opxi.callmanager.call.CallNotExistsException;
import com.basamadco.opxi.callmanager.OpxiException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.List;
import java.util.Set;
import java.nio.charset.Charset;

/**
 * @author Jrad
 *         Date: Mar 15, 2007
 *         Time: 11:02:22 AM
 */
public class WebIMServlet extends OpxiHttpServlet {

    private static final Logger logger = Logger.getLogger( WebIMServlet.class.getName() );

    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        request.setCharacterEncoding( "UTF-8" );
        String target = null;
        String text = null;
        if ( request.getParameter( "text" ) != null ) {
            text = request.getParameter( "text" ).trim().replaceAll( "\"", "" );
        }

        if ( request.getParameter( "target" ) != null ) {
            target = request.getParameter( "target" ).trim().replaceAll( "\"", "" );
            sendMessage( request, target, SIPConstants.MIME_TEXT_PLAIN, text );
        } else {
            if ( request.getParameter( "caller" ) != null ) {
                target = request.getParameter( "caller" ).trim().replaceAll( "\"", "" );
                try {
                    CallServiceFactory.getCallByCallerId( target ).setIM( text );
                } catch ( CallNotExistsException e ) {
                    logger.severe( e.getMessage() );
                    request.setAttribute( "msg", e.getMessage() );
                }
            }
        }

        logger.finest( "************************** target Param: '" + target + "'" );
        logger.finest( "************************** text Param: '" + text + "'" );

        request.getRequestDispatcher( "/sendIM.jsp" ).forward( request, response );
    }


    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        request.setCharacterEncoding( "UTF-8" );
        String target = null;


        String contentType = request.getContentType();
//        String line = null;
//        StringBuffer text = new StringBuffer();

//        while ((line = request.getReader().readLine()) != null) {
//            text.append( line );
//        }

//        String text2 = slurp( request.getInputStream() );


        BufferedReader br = new BufferedReader( new InputStreamReader( request.getInputStream(), "UTF8" ) );
        StringBuffer sb = new StringBuffer();
        String line2 = null;
        while ( (line2 = br.readLine()) != null ) {
            sb.append( line2 + "\n" );
        }


        logger.finest( "************************** Request URI: '" + request.getRequestURI() + "'" );
        logger.finest( "************************** Request Content-Type: '" + contentType + "'" );
        logger.finest( "************************** Request Content-Length: '" + request.getContentLength() + "'" );
//        logger.finest( "************************** Request Body(reader): '" + text + "'" );
        logger.finest( "************************** Request Body: '" + sb + "'" );
//        logger.finest( "************************** Request Body(IS->Reader): '" + sb + "'" );


        if ( request.getParameter( "target" ) != null ) {
            target = request.getParameter( "target" ).trim().replaceAll( "\"", "" );
            sendMessage( request, target, contentType, sb.toString() );
        } else {
            if ( request.getParameter( "caller" ) != null ) {
                target = request.getParameter( "caller" ).trim().replaceAll( "\"", "" );
                try {
                    CallServiceFactory.getCallByCallerId( target ).setIM( sb.toString() );
                } catch ( OpxiException e ) {
                    logger.severe( e.getMessage() );
                    request.setAttribute( "msg", e.getMessage() );
                }
            }
        }
        request.getRequestDispatcher( "/sendIM.jsp" ).forward( request, response );
    }

    private void sendMessage( HttpServletRequest request, String target, String contentType, String text ) {
        try {
            String msg = "";
            UserAgent ua = new UserAgent( target );
            Set<Registration> regs = getServiceFactory().getLocationService().findRegistrations( ua );
            for ( Registration reg : regs ) {
                logger.finest( "Registration: " + reg );
                getServiceFactory().getSipService().sendIM( reg, text, contentType );
                msg += "Message is sent to '" + reg.getContactURI() + "'<br>";
            }
            request.setAttribute( "msg", msg );
        } catch ( Throwable e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            request.setAttribute( "msg", e.getMessage() );
        }
    }


    public static String slurp( InputStream in ) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for ( int n; (n = in.read( b )) != -1; ) {
            out.append( new String( b, 0, n ) );
        }
        return out.toString();
    }

}
