package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.SipService;
import com.basamadco.opxi.callmanager.pool.Agent;
import com.basamadco.opxi.callmanager.sip.util.SipUtil;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipURI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Aug 21, 2006
 *         Time: 3:49:24 PM
 */
public class OutboundCall extends CallService {

    private static final Logger logger = Logger.getLogger( OutboundCall.class.getName() );


    public OutboundCall( SipServletRequest request, String role, SipService ctx ) {
        super( request, role, ctx );
    }

    protected void release() {
        super.release();
        // since the only users of outbound calls are agents then this should return an agent's name
        String userName = ((SipURI)getCallerAddress().getURI()).getUser();
        try {
            Agent callerAgent = ctx.getServiceFactory().getAgentService().getAgentByAOR( SipUtil.toSipURIString( userName ) );
            ctx.getServiceFactory().getLogService().getAgentActivityLogger( callerAgent.getActivityLogId() ).addOutgoingCallTime( duration() );

            ctx.getServiceFactory().getLogService().getServiceActivityLogger().addOutgoingCall( duration() );
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, "No Activity Log exists to log outgoing call...", e );
        }
    }

}
