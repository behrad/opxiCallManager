package com.basamadco.opxi.callmanager.sip.test;

import com.basamadco.opxi.callmanager.util.OpxiToolBox;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/**
 * Using no Junit for application tests, currently we're using
 * this sample <i>assert repository</i> class to run out tests.
 *  
 * @author Jrad
 */
public class OpxiAsserts {
	
	private static final Logger logger = Logger.getLogger( OpxiAsserts.class.getName() );
	
	private Test[] tests;
	
	public void runTests() {		
		for( int i = 0; i < tests.length; i++ ) {
			if( tests[i].enabled() ) {
				Thread t = new Thread( tests[i] );
                System.out.println( "Running '"  + OpxiToolBox.unqualifiedClassName( tests[i].getClass() ) + "'..." );
                t.start();
			}
		}
	}
	
	private OpxiAsserts( Test[] tests ) {
		this.tests = tests;
	}
	
	public static OpxiAsserts getTestSuite() {
        List testObjects = new ArrayList();
		try {
			Class[] testCases = OpxiToolBox.getClasses( "com.basamadco.opxi.callmanager.sip.test.tc" );
			for( int i = 0; i < testCases.length; i++ ) {
				if( testCases[i].getSuperclass().getName().equals( "com.basamadco.opxi.callmanager.sip.test.Test" ) ) {
//                    System.out.println( "adding " + testCases[ i ].getName() );
                    testObjects.add( testCases[ i ].newInstance() );
				}
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}
		Test[] t = new Test[ testObjects.size() ];
		for( int i=0; i<testObjects.size();i++ ) {
			t[ i ] = (Test)testObjects.get( i );
		}
		
		return new OpxiAsserts( t );
	}
	
	

}