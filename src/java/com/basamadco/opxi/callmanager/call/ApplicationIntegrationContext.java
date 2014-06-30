package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.ServiceObject;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.context.HashMapContext;

import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Nov 22, 2006
 *         Time: 11:34:55 AM
 */
public class ApplicationIntegrationContext implements JexlContext, ServiceObject {

    private static final Logger logger = Logger.getLogger( ApplicationIntegrationContext.class.getName() );


    private HashMapContext context;

    private CallTarget target;

    private CallService call;

    private Application application;

    private ApplicationTimerContext timerContext;


    public ApplicationIntegrationContext( CallService call, Application application, CallTarget target ) {
        this.context = new HashMapContext();
        this.target = target;
        this.call = call;
        this.application = application;

        logger.finest( "Create context[" + target.getName() + ", " + call.getId() + "]" );
        context.getVars().put( "this", target );
        context.getVars().put( "call", call );
        context.getVars().put( "request", call.getInitialRequest() );
        context.getVars().put( "date", new Date() );
        context.getVars().put( "timer", this );
        application.setIntegrationCtx( this );
    }


    public void setVars( Map map ) {
        context.setVars( map );
    }


    public Map getVars() {
        return context.getVars();
    }

    public ApplicationTimerContext create( long timeout ) throws TimerException {
        if ( timerContext == null ) {
            try {
                timerContext = new ApplicationTimerContext( this, timeout );
                return timerContext;
            } catch ( Throwable e ) {
                logger.log( Level.SEVERE, "Couldn't create timer ", e );
                throw new com.basamadco.opxi.callmanager.call.TimerException( e );
            }
        } else {
            return timerContext;
        }
    }

    public boolean evaluate() throws ApplicationIntegrationException {
        return application.evaluate( this );
    }


    public CallService getCall() {
        return call;
    }

    public Application getApplication() {
        return application;
    }


    public ApplicationTimerContext getTimerContext() {
        return timerContext;
    }

    public void setTimerContext( ApplicationTimerContext timerContext ) {
        this.timerContext = timerContext;
    }

    public void destroy() {
        getCall().setApplicationContext( null );
        context.clear();
        if ( timerContext != null )
            timerContext.destroy();
    }
}