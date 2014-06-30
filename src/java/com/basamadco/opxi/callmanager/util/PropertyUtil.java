package com.basamadco.opxi.callmanager.util;

import java.util.*;
import java.io.*;

import java.util.logging.Logger;import java.util.logging.Level;

/**
 * This utility class is Responsible for loading OpxiCallManager
 * configuration properties file named "opxiCallManager.properties" in
 * root directory of local classpath.
 * 
 * @author Jrad
 *
 */
public class PropertyUtil {
	
	private static final Logger logger = Logger.getLogger( PropertyUtil.class.getName() );
	
	private final static String propertiesFile = "opxiCallManager.properties";
	
	private static Properties setting = null;
	
	static {
		try {
			setting = new Properties();
			setting.load( PropertyUtil.class.getClassLoader().getResourceAsStream( propertiesFile ) );			
		} catch( IOException e ) {
			logger.severe( "Unable to read settings from file: " + propertiesFile );
            logger.log( Level.SEVERE, e.getMessage(), e );
            e.printStackTrace();
		}
	}
	/**
	 * Returns a configuration parameter loaded from default 
	 * properties file.
	 * 
	 * @param key key for which the corresponding value in properties
	 * file with be returned
	 * @return String value of the specified key 
	 */
	public static String getProperty( String key ) {
		if ( setting != null )
			return setting.getProperty( key );
		return null;
	}
	
	public static Properties getProperties() {
		return new Properties(setting);
	}
}
