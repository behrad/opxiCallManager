package com.basamadco.opxi.callmanager.logging;

import com.basamadco.opxi.callmanager.AbstractCallManagerService;
import com.basamadco.opxi.callmanager.ServiceFactory;
import com.basamadco.opxi.callmanager.call.TimerException;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.pool.Agent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogService extends AbstractCallManagerService {

    private final static Logger logger = Logger.getLogger( LogService.class.getName() );

    private LogManager logManager;

    private String serviceLoggerId;

    //     TODO Convert timers to SipServlet API (JSR116) based ServletTimer
    //    private Timer serviceLogDraftTimer;
    private LogServiceTimer timer;

    private AutoAALRenewTimer autoAALTimer;
//    private ServletTimer agentActivityLoggerTimerServlet;

//    private final static String AGENT_LOG_REPORT_RATE_MINUTE = PropertyUtil.getProperty("opxi.logReport.agentLog.report_rate");


    public LogService( ServiceFactory serviceFactory, LogManager logManager ) {
        setServiceFactory( serviceFactory );
        this.logManager = logManager;
        startServiceActivityLogger();
        timer = new LogServiceTimer( getServiceFactory(), serviceLoggerId );
        autoAALTimer = new AutoAALRenewTimer( getServiceFactory(), serviceLoggerId + "-AUTO-AAL-RENEWAL" );
//        initAgentActivityLoggerTimerServlet();
        /*serviceLogDraftTimer = new Timer( "LogServiceTimer-" + new Date() );
        serviceLogDraftTimer.schedule( new ServiceLoggerTask(), service_report_period, service_report_period );*/
    }

    public String openAgentActivityLogger( UserAgent agent ) {
        return logManager.registerLogger( new AgentActivityLogger( getServiceFactory(), agent ) );
    }

    public void closeAgentActivityLogger( String id, Object cause ) throws ForceActionException {
        logManager.closeLogger( id, cause );
    }

    public AgentActivityLogger getAgentActivityLogger( String id ) throws ActivityLogNotExistsException {
        return (AgentActivityLogger) logManager.getLogger( id );
    }

    public ServiceActivityLogger getServiceActivityLogger() throws ActivityLogNotExistsException {
        return (ServiceActivityLogger) logManager.getLogger( serviceLoggerId );
    }

    private void startServiceActivityLogger() {
        if ( serviceLoggerId == null ) {
            serviceLoggerId = logManager.registerLogger( new ServiceActivityLogger( getServiceFactory() ) );
            logger.finer( "ServiceActivityLog started... " + serviceLoggerId );
        } else {
            // error
        }
    }

    public void restart() throws TimerException {
        try {
            String oldServiceLoggerId = serviceLoggerId;
            serviceLoggerId = logManager.registerLogger( new ServiceActivityLogger( getServiceFactory() ) );
            logger.finer( "Register new ServiceActivityLog: " + serviceLoggerId );
            if ( oldServiceLoggerId != null ) {
                logger.finer( "Forcing old ServiceActivityLog: " + oldServiceLoggerId );
                logManager.closeLogger( oldServiceLoggerId, new Date().toString() );
            }
        } catch ( ForceActionException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new TimerException( e );
        } catch ( Throwable t ) {
            logger.log( Level.SEVERE, t.getMessage(), t );
            throw new TimerException( t );
        }
    }

    public void cancel() {
        if ( timer != null ) {
            timer.cancel();
        }
        if ( autoAALTimer != null ) {
            autoAALTimer.cancel();
        }
    }

    public void destroy() {
        if ( serviceLoggerId != null ) {
            try {
                logManager.closeLogger( serviceLoggerId, "Call Manager Destroy" );
            } catch ( ForceActionException e ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
            }
        }
        cancel();
        logManager.dispose();
        logger.info( "LogService destroyed successfully." );
    }

    /**
     * Checks an activity logger is empty or not.
     *
     * @param aal Agent Activity Logger
     * @return boolean
     */
    protected boolean isLoggable( AgentActivityLogger aal ) {

        return true;
//        try {
//            AgentActivity aa = aal.getLogVO().getAgentActivity();
//
//            if (aa.getAssignedCalls() != 0) return true;
//            if (aa.getRejectedCalls() != 0) return true;
//            if (aa.getForwardedCalls() != 0) return true;
//            if (aa.getHeldCalls() != 0) return true;
//            if (aa.getNotAnsweredCalls() != 0) return true;
//
//            if (aa.getAnsweredCalls().getCount() != 0) return true;
//            if (aa.getIncomingCalls().getCount() != 0) return true;
//            if (aa.getOutgoingCalls().getCount() != 0) return true;
//            if (aa.getTrunkSvc().getTrunkCalls().getCount() != 0) return true;
//
//            return false;
//        }
//        catch (Exception e) {
//            return false;
//        }
    }

    public void reNewAgentActivityLoggers( List listAgents ) {
        Iterator agentsIterator = listAgents.iterator();
        while ( agentsIterator.hasNext() ) {
            Agent agent = (Agent) agentsIterator.next();
            reNewAgentActivityLogger( agent, "AutoAALRenewal" );
        }
    }

    protected void reNewAgentActivityLogger( Agent agent, String reason ) {
        UserAgent ua = new UserAgent( agent.getAOR() );
        try {
            logger.finest( "Renew AAL for: " + agent.getAOR() + " with id: " + agent.getActivityLogId() );
            String agentActivityLogId = agent.getActivityLogId();
            //TODO : If the old agent activity log is full or not empty then close it.
            AgentActivityLogger oldLog = getServiceFactory().getLogService().getAgentActivityLogger( agentActivityLogId );
            if ( isLoggable( oldLog ) ) {
                agent.setActivityLogId( getServiceFactory().getLogService().openAgentActivityLogger( ua ) );
                oldLog.transformTo( getServiceFactory().getLogService().getAgentActivityLogger( agent.getActivityLogId() ) );
                getServiceFactory().getLogService().closeAgentActivityLogger( agentActivityLogId, reason );
            }
        }
        catch ( ActivityLogNotExistsException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
        catch ( ForceActionException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    public List listObjects() {
        List l = new ArrayList();
        l.add( "Service Log Id: '" + serviceLoggerId + "' to be forced @ '" + new Date( timer.getTimer().scheduledExecutionTime() ) );
        return l;
    }
}