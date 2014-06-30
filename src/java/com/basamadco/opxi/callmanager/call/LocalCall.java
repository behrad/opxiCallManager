package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.SipService;
import com.basamadco.opxi.callmanager.logging.ActivityLogNotExistsException;

import javax.servlet.sip.SipServletRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Aug 21, 2006
 *         Time: 3:49:47 PM
 */
public class LocalCall extends CallService {

    private static final Logger logger = Logger.getLogger( LocalCall.class.getName() );

    public LocalCall( SipServletRequest request, String role, SipService ctx ) {
        super( request, role, ctx );        
    }

    protected void release() {
        super.release();
        try {
            ctx.getServiceFactory().getLogService().getServiceActivityLogger().addLocalCall( duration() );
        } catch ( ActivityLogNotExistsException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

}
