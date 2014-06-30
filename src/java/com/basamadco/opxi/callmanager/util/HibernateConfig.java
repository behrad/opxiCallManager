package com.basamadco.opxi.callmanager.util;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.cfg.Configuration;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Configures a Hibernate Configuration
 * @author Jrad
 *
 */
public class HibernateConfig {

    private static final Logger logger = Logger.getLogger( HibernateConfig.class.getName() );

    private List packages;

    private Configuration config;

    public HibernateConfig( Configuration conf ) {
        config = conf;
        packages = new ArrayList();
    }

    public HibernateConfig( List config ) {
        this.packages = config;
    }

    public void addPackage( String packName ) {
        packages.add( packName );
    }

    public Configuration getConfiguration() throws ClassNotFoundException, MappingException {
        for( int i = 0; i < packages.size(); i++ ) {
            addResources( (String)packages.get(i) );
        }
        return config;
    }

    private void addResources( String packName ) throws MappingException {
        logger.finer( "1: " + packName );
        String dir = packName.replace( '.', File.separatorChar );
        logger.finer( dir );
        logger.finer( "2: " + this.getClass().getClassLoader() );
        logger.finer( "3: " + this.getClass().getClassLoader().getResource( dir ) );
        logger.finer( "4: " + this.getClass().getClassLoader().getResource( dir ).getPath() );
        File f = new File( this.getClass().getClassLoader().getResource( dir ).getPath() );
        File[] classFiles = f.listFiles( new HbmXmlFilter() );
        for( int i = 0; i < classFiles.length;i++ ) {
            logger.finer( dir + File.separator + classFiles[i].getName() );
            config.addResource( dir + File.separator + classFiles[i].getName() );
//			classes[ i ] = Class.forName( packName + "." + classFiles[ i ].getMessage().split( ".class" )[0] );
        }
    }

}

class HbmXmlFilter implements FilenameFilter {
	public boolean accept(File dir,String name) {
		if( name.indexOf( ".hbm.xml" ) == name.length() - 8 )
			return true;
		return false;

	}
}