package com.basamadco.opxi.callmanager.entity.dao.directory.ldap;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;
import com.basamadco.opxi.callmanager.call.CallTarget;
import com.basamadco.opxi.callmanager.util.PropertyUtil;

import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Mar 8, 2007
 *         Time: 5:58:04 PM
 */
final class CallTargetCacheManager {

    private static final Logger logger = Logger.getLogger( CallTargetCacheManager.class.getName() );


    private final ConcurrentHashMap CALL_TARGET_CACHE = new ConcurrentHashMap( 50 );


    private static final long CACHE_UPDATE_RATE = Integer.parseInt(
                    PropertyUtil.getProperty( "opxi.callmanager.ldap.callTarget.cacheRefreshRate" ) ) * 60 * 1000;


    private static final CallTargetCacheManager CTCM = new CallTargetCacheManager();

    public static CallTargetCacheManager getInstance() {
        return CTCM;
    }

    private CallTargetCacheManager() {
    }

    public CallTarget lookUpCacheFor( String phoneNumber ) {
        logger.finest( "Looking up memory cache for a CallTarget by phone#: " + phoneNumber );
        if( CALL_TARGET_CACHE.containsKey( phoneNumber ) ) {
            CacheEntry entry = (CacheEntry)CALL_TARGET_CACHE.get( phoneNumber );
            if( isFresh( entry ) ) {
                CallTarget ct = entry.dataItem;
                logger.finest( "Found fresh CallTarget in cache: " + ct.getName() );
                return ct;
            }
            logger.finest( "Found dirty CallTarget in cache, returning null to force reading a new version." );
            return null;
        }
        logger.finest( "Not Found a matching CallTarget for phone#: " + phoneNumber );
        return null;
    }
    
    public void updateCacheByKey( String phoneNumber, CallTarget ct ) {
        logger.finest( "Updated memory cache with [Phone#, TargetName]=['" + phoneNumber + "','"+ct.getName() +"'] " );
        CALL_TARGET_CACHE.put( phoneNumber, new CacheEntry( ct, System.currentTimeMillis() ) );
    }

    private boolean isFresh( CacheEntry cachedEntry ) {
        return cachedEntry.cachedTimestamp + CACHE_UPDATE_RATE > System.currentTimeMillis();
    }

    final class CacheEntry {

        public CallTarget dataItem;
        public long cachedTimestamp;

        public CacheEntry( CallTarget dataItem, long cachedTimestamp ) {
            this.dataItem = dataItem;
            this.cachedTimestamp = cachedTimestamp;
        }
    }
    
}
