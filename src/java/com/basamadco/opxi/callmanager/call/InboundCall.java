package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.SipService;
import com.basamadco.opxi.callmanager.logging.ActivityLogNotExistsException;

import javax.servlet.sip.SipServletRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Aug 21, 2006
 *         Time: 3:48:55 PM
 */
public class InboundCall extends CallService {

    private final static Logger logger = Logger.getLogger( InboundCall.class.getName() );


    public InboundCall( SipServletRequest request, String role, SipService ctx ) {
        super( request, role, ctx );
        try {
            ctx.getServiceFactory().getLogService().getServiceActivityLogger().incCallAttempts();
        } catch ( ActivityLogNotExistsException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    protected void release() {
        super.release();
        try {
            ctx.getServiceFactory().getLogService().getServiceActivityLogger().addIncomingCall( duration() );
        } catch ( ActivityLogNotExistsException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

}
