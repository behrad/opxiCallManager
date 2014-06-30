package com.basamadco.opxi.callmanager.logging;

/**
 * @author Jrad
 *         Date: Jul 3, 2006
 *         Time: 9:36:30 AM
 */
public interface LogManager {

    public String registerLogger( OpxiActivityLogger logger );

    public OpxiActivityLogger getLogger( String id ) throws ActivityLogNotExistsException ;

    public void closeLogger( String id, Object cause ) throws ForceActionException;

    public void dispose();

}
