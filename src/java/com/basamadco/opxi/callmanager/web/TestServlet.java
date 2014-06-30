package com.basamadco.opxi.callmanager.web;

import com.basamadco.opxi.callmanager.OpxiException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Jun 28, 2006
 *         Time: 2:25:17 PM
 */
public class TestServlet extends OpxiHttpServlet {

    private static final Logger logger = Logger.getLogger( TestServlet.class.getName() );


    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        /*Enumeration enum = getServletContext().getAttributeNames();
        while (enum.hasMoreElements()) {
            String name = (String) enum.nextElement();
            Object value = getServletContext().getAttribute( name );
            logger.finer( "Context attribute: " + name  + "='" + value + "'" );
        }*/
        try {
            logger.finer(
                    getServiceFactory().getQueueManagementService().queueForName( "sales" ).toString()
            );
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

}
