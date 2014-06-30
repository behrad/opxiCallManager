package com.basamadco.opxi.callmanager.logging;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import java.util.Map;
import java.util.UUID;

/**
 * @author Jrad
 *         Date: Jul 3, 2006
 *         Time: 9:40:59 AM
 */
public class InMemoryLogManager implements LogManager {

    private static final Logger logger = Logger.getLogger( InMemoryLogManager.class.getName() );


    private Map loggersMap = new ConcurrentHashMap();


    public String registerLogger( OpxiActivityLogger OALogger ) {
        OALogger.getLogVO().setId( UUID.randomUUID().toString() );
        Map map = getTypeMap( OALogger.getClass() );
//        logger.finer( "Adding LogVO["+OALogger.getLogVO().getId()+"] in loggersMap" );
        map.put( OALogger.getLogVO().getId(), OALogger );
        return OALogger.getLogVO().getId();
    }

    public OpxiActivityLogger getLogger( String id ) throws ActivityLogNotExistsException  {
//        Integer hash = computeHash( id );
        if( id != null) {
            Map logVOMap = getTypeMap( null );
            logger.finer( "Fetching LogVO["+id+"] in loggersMap" );
            if( logVOMap.containsKey( id ) ) {
                return (OpxiActivityLogger)logVOMap.get( id );
            }
        }
        throw new ActivityLogNotExistsException( id );
    }

    public void closeLogger( String id, Object cause ) throws ForceActionException {
        Map map = getTypeMap( null );
        OpxiActivityLogger logVO = (OpxiActivityLogger)map.remove( id );
        if( logVO == null ) {
            throw new IllegalStateException( "No LogVO with id " + id + " can be removed." );
        }
        logVO.forceLog( cause );
        logVO.dispose();
//        return logVO;
    }

    private Map getTypeMap( Class type ) {
//        Integer typeHash = computeTypeHash( type );
//        logger.finer( "TypeHash for type["+type.getName()+"] is " + typeHash );
//        if( loggersMap.containsKey( typeHash ) ) {
//            return (Map)loggersMap.get( typeHash );
//        }
//        return createTypeMap( type );
        return loggersMap;
    }

    private Map createTypeMap( Class type ) {
//        Map newTypeMap = new ConcurrentHashMap( 13 );
//        loggersMap.put( computeTypeHash( type ), newTypeMap );
//        return newTypeMap;
        return null;
    }

//    private Integer computeTypeHash( Class type ) {
//        return new Integer( type.getName().hashCode() );
//    }
//    private Integer computeHash( String id ) {
//        return new Integer( id.hashCode() );
//    }

    public void dispose() {
//        Iterator iterator = loggersMap.keySet().iterator();
//        synchronized( iterator ) {
//            while (iterator.hasNext()) {
//                Map map = (Map)iterator.next();
//                map.clear();
//            }
//        }
        loggersMap.clear();
        logger.info( "InMemoryLogManager destroyed successfully." );
    }

}