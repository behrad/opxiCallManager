package com.basamadco.opxi.callmanager.web.vxml;

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
 * Voice XML Servlet
 *
 */
public class WaitingRoomVxmlServlet extends OpxiHttpServlet {

    private static final Logger logger = Logger.getLogger( WaitingRoomVxmlServlet.class.getName() );

    private static final String USERNAME = PropertyUtil.getProperty( "opxi.callmanager.exchange.username" );

    private static final String PASSWORD = PropertyUtil.getProperty( "opxi.callmanager.exchange.password" ).replaceAll( "#", "%35" );


    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

        protected void doPost( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        response.setContentType( "text/xml" );
        PrintWriter opxiIVR = response.getWriter();
        StringBuffer mediaURI = new StringBuffer( request.getParameter( "mediaURI" ) );
        String user = request.getParameter( "u" );
        if( user != null ) {
            String pass = request.getParameter( "p" );
            pass = pass.replaceAll( "#", "%35" );
            mediaURI.insert( 7, user + ":" + pass + "@" );
        } else {
            mediaURI.insert( 7, USERNAME + ":" + PASSWORD + "@" );
        }
        String out = VXML_TEMPLATE.replaceAll( MEDIA_URI, mediaURI.toString() );
        opxiIVR.print( out );
        opxiIVR.flush();
    }

    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doPost( request, response );
    }

    private static final String MEDIA_URI = "@MEDIA_URI";
    private static final String VXML_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "\n" +
            "<vxml version=\"2.0\" xmlns=\"http://www.w3.org/2001/vxml\"\n" +
            "\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "\txsi:schemaLocation=\"http://www.w3.org/2001/vxml http://www.w3.org/TR/voicexml20/vxml.xsd\">\n" +
            "\t<form id=\"someForm\">\n" +
            "\t\t<block>\n" +
            "\t\t\t<prompt>\n" +
            "\t\t\t\t<audio src=\"" + MEDIA_URI + "\"/>\n" +
            "\t\t\t</prompt>\n" +
            "\t\t</block>\n" +
            "\t</form>\n" +
            "</vxml>";

}