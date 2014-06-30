package com.basamadco.opxi.callmanager.sip.test;

import com.basamadco.opxi.callmanager.sip.OpxiSipServlet;
import com.basamadco.opxi.callmanager.util.PropertyUtil;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import java.io.IOException;
import java.util.logging.Logger;

public class TestServlet extends OpxiSipServlet {

    private static final Logger logger = Logger.getLogger( TestServlet.class.getName() );


    private static final boolean OPXI_DEBUG_ENABLED = Boolean.valueOf( PropertyUtil.getProperty( "opxi.callmanager.sip.tests.enabled" ) ).booleanValue();

    public void init() throws ServletException {
//        if( OPXI_DEBUG_ENABLED ) {
//            OpxiAsserts.getTestSuite().runTests();
//        }
    }

    protected void doInvite( SipServletRequest invite ) throws ServletException, IOException {
        logger.entering( this.getClass().getName(), "run" );
        
        logger.exiting( this.getClass().getName(), "run" );
    }

}
