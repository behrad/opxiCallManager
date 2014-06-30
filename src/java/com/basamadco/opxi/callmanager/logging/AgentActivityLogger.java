package com.basamadco.opxi.callmanager.logging;

import com.basamadco.opxi.activitylog.schema.*;
import com.basamadco.opxi.activitylog.schema.Presence;
import com.basamadco.opxi.callmanager.ServiceFactory;
import com.basamadco.opxi.callmanager.call.CallService;
import com.basamadco.opxi.callmanager.entity.*;
import com.basamadco.opxi.callmanager.entity.Registration;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.DAOFactoryException;
import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.webdav.StorageDAOFactory;
import com.basamadco.opxi.callmanager.sip.util.SipUtil;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgentActivityLogger extends OpxiActivityLogger {

    private static final Logger logger = Logger.getLogger( AgentActivityLogger.class.getName() );


    private static final String TRANSFER_SERVICE = "TransferService";

    private static final String IM_SERVICE = "IMService";

    private AgentTrunkActivityLogger _trunkLogger = null;

    private AgentRegistrationLogger _registrationLogger = null;

//    private Map<String, ReportItem> reportItems = new HashMap<String, ReportItem>();

//    private Map statisticsMap = new ConcurrentHashMap();


    public AgentActivityLogger( ServiceFactory serviceFactory, Serializable object ) {
        super( serviceFactory, object );
    }

    protected OpxiActivityLog initLogVO( final Serializable object ) {
        UserAgent ua = (UserAgent) object;
        OpxiActivityLog oal = new OpxiActivityLog();
        AgentActivity aa = new AgentActivity();
        aa.setAgent( ua.getSipURIString() );
        aa.setBegin( new Date() );
        aa.setAssignedCalls( 0 );
        aa.setRejectedCalls( 0 );
        aa.setNotAnsweredCalls( 0 );
        aa.setAnsweredCalls( (AnsweredCalls) initStatistics( new AnsweredCalls() ) );
        aa.setIncomingCalls( (IncomingCalls) initStatistics( new IncomingCalls() ) );
        aa.setOutgoingCalls( (OutgoingCalls) initStatistics( new OutgoingCalls() ) );

        addStatistics( AnsweredCalls.class, SummaryStatistics.newInstance() );
        addStatistics( IncomingCalls.class, SummaryStatistics.newInstance() );
        addStatistics( OutgoingCalls.class, SummaryStatistics.newInstance() );

        aa.setTrunkSvc( new TrunkSvc() );

        aa.setRegistrarSvc( new RegistrarSvc() );
        // Agent has not sent a PUBLISH event yet!
        /*Presence p = new Presence();
        p.setOpen(ua.isActive());
        p.setState(ua.getNote());
        p.setDate(new Date());
        aa.addPresence(p);*/
        aa.setReports( new Reports() );
        aa.setPolls( new Polls() );
        oal.setAgentActivity( aa );

        return oal;
    }

    public void incAssignedCalls() throws ActivityLogNotExistsException {
        AgentActivity aa = getLogVO().getAgentActivity();
        aa.setAssignedCalls( aa.getAssignedCalls() + 1 );
    }

    public void incRejectedCalls() {
        AgentActivity aa = getLogVO().getAgentActivity();
        aa.setRejectedCalls( aa.getRejectedCalls() + 1 );
    }

    public void incNotAnsweredCalls() {
        getLogVO().getAgentActivity().setNotAnsweredCalls( getLogVO().getAgentActivity().getNotAnsweredCalls() + 1 );
    }

    public void addIncomingCallTime( long duration ) {
        if ( duration > 0 ) {
            AgentActivity aa = getLogVO().getAgentActivity();
            getStatistics( IncomingCalls.class ).addValue( duration / 1000f );
            aa.setIncomingCalls( (IncomingCalls) StatisticsTransformer.getInstance().transform(
                    getStatistics( IncomingCalls.class ), aa.getIncomingCalls() ) );
        }
    }

    public void addAnsweredCallTime( long answerTime ) {
        if ( answerTime >= 0 ) {
            AgentActivity aa = getLogVO().getAgentActivity();
            getStatistics( AnsweredCalls.class ).addValue( answerTime / 1000f );
            aa.setAnsweredCalls( (AnsweredCalls) StatisticsTransformer.getInstance().transform(
                    getStatistics( AnsweredCalls.class ), aa.getAnsweredCalls() ) );
        }
    }

    public void addOutgoingCallTime( long duration ) {
        if ( duration > 0 ) {
            AgentActivity aa = getLogVO().getAgentActivity();
            getStatistics( OutgoingCalls.class ).addValue( duration / 1000f );
            aa.setOutgoingCalls( (OutgoingCalls) StatisticsTransformer.getInstance().transform(
                    getStatistics( OutgoingCalls.class ), aa.getOutgoingCalls() ) );
        }
    }

    public void addPresence( com.basamadco.opxi.callmanager.entity.Presence presence ) {
        AgentActivity aa = getLogVO().getAgentActivity();
        Presence p = new Presence();
        p.setDate( new Date() );
        p.setOpen( presence.isActive() );
        p.setState( presence.getNote() );
        p.setContact( presence.getContactURI() );
        p.setMsgIndex( presence.getMsgIndex() );
        aa.addPresence( p );
    }

    public void addPresenceComment( com.basamadco.opxi.callmanager.entity.Presence presence, String msg ) {
        AgentActivity aa = getLogVO().getAgentActivity();
        Comment comment = new Comment();
        comment.setMessage( msg );
        comment.setTime( new Date() );
        for ( Presence p : aa.getPresence() ) {
            if ( p.getMsgIndex() == presence.getMsgIndex() ) {
                p.addComment( comment );
            }
        }

    }

    public void addTransferService( String callId, String referTo ) {
        AgentActivity aa = getLogVO().getAgentActivity();
        Service service = getService( aa, TRANSFER_SERVICE );
        service.setCount( service.getCount() + 1 );
        Usage usage = new Usage();
        usage.setDate( new Date() );
        RefItem transferred = new RefItem();
        transferred.setName( "Call-Id" );
        transferred.setValue( callId );
        usage.addRefItem( transferred );
        RefItem transferee = new RefItem();
        transferee.setName( "Transferee" );
        transferee.setValue( referTo );
        usage.addRefItem( transferee );
        service.addUsage( usage );
    }

    public void addTransferTarget( String callId, String referredBy ) {
        AgentActivity aa = getLogVO().getAgentActivity();
        Service service = getService( aa, TRANSFER_SERVICE );
        service.setCount( service.getCount() + 1 );
        Usage usage = new Usage();
        usage.setDate( new Date() );
        RefItem transferred = new RefItem();
        transferred.setName( "Call-Id" );
        transferred.setValue( callId );
        usage.addRefItem( transferred );
        RefItem transferee = new RefItem();
        transferee.setName( "TransferredBy" );
        transferee.setValue( referredBy );
        usage.addRefItem( transferee );
        service.addUsage( usage );
    }

    public void addIMService( String to, String content ) {
        AgentActivity aa = getLogVO().getAgentActivity();
        Service service = getService( aa, IM_SERVICE );
        service.setCount( service.getCount() + 1 );
        Usage usage = new Usage();
        usage.setDate( new Date() );
        RefItem dest = new RefItem();
        dest.setName( "Destination" );
        dest.setValue( to );
        usage.addRefItem( dest );
        RefItem messageContent = new RefItem();
        messageContent.setName( "Text" );
        messageContent.setValue( content );
        usage.addRefItem( messageContent );
        service.addUsage( usage );
    }

    public void addTrunkUsage( CallService call ) {
        getOrAddTrunkActivityLogger().logTrunkUsage( call );
    }


    public void transformTo( AgentActivityLogger newLog ) {
//        getLogVO().getAgentActivity().getPresenceCount()

        for ( int i = 0; i < getLogVO().getAgentActivity().getRegistrarSvc().getRegistrationCount(); i++ ) {
            if ( getLogVO().getAgentActivity().getRegistrarSvc().getRegistration( i ).getEndDate() == null ) {
                newLog.getOrAddRegistrationLogger().transformFrom( getLogVO().getAgentActivity().getRegistrarSvc().getRegistration( i ) );
            }
        }
    }

    private AgentTrunkActivityLogger getOrAddTrunkActivityLogger() {
        if ( _trunkLogger == null ) {
            _trunkLogger = new AgentTrunkActivityLogger( this );
        }
        return _trunkLogger;
    }

    public void addRegistration( Registration reg ) {
        getOrAddRegistrationLogger().logRegistration( reg );
    }

    public void addUnregistration( Registration reg ) {
        getOrAddRegistrationLogger().logUnregistration( reg );
    }

    private AgentRegistrationLogger getOrAddRegistrationLogger() {
        if ( _registrationLogger == null ) {
            _registrationLogger = new AgentRegistrationLogger( this );
        }
        return _registrationLogger;
    }


    public void addAgentReportItem( String[] ids ) {
        synchronized ( this ) {
            updateCategory( ids );
        }
    }


    private void updateCategory( String[] catNames ) {
        for ( Category cat : getLogVO().getAgentActivity().getReports().getCategory() ) {
            if ( cat.getName().equals( catNames[0] ) ) {
                cat.setCount( cat.getCount() + 1 );
                getOrCreateCategory( 1, cat, catNames );
                return;
            }
        }
        Category newCat = new Category();
        newCat.setCount( 1 );
        newCat.setName( catNames[0] );
        getLogVO().getAgentActivity().getReports().addCategory( newCat );
        getOrCreateCategory( 1, newCat, catNames );
    }

    private void getOrCreateCategory( int level, Category subCat, String[] catNames ) {
        if ( level < catNames.length ) {
            for ( Category cat : subCat.getCategory() ) {
                if ( cat.getName().equals( catNames[level] ) ) {
                    cat.setCount( cat.getCount() + 1 );
                    getOrCreateCategory( level + 1, cat, catNames );
                    return;
                }
            }
            Category newCat = new Category();
            newCat.setCount( 1 );
            newCat.setName( catNames[level] );
            subCat.addCategory( newCat );
            getOrCreateCategory( level + 1, newCat, catNames );
            return;
        } else {
            return;
        }
    }


    public void addPoll( String caller, String poll ) {
        Poll pollItem = new Poll();
        pollItem.setCallerId( caller );
        pollItem.setPoll( poll );
        getLogVO().getAgentActivity().getPolls().addPoll( pollItem );
    }

    private Service getService( AgentActivity aa, String serviceName ) {
        Service service = null;
        for ( int i = 0; i < aa.getServiceCount(); i++ ) {
            service = aa.getService( i );
            if ( service.getName().equals( serviceName ) ) {
                return service;
            }
        }
        service = new Service();
        service.setCount( 0 );
        service.setName( serviceName );
        aa.addService( service );
        return service;
    }

    protected void beforeForceLog( Object cause ) {
        AgentActivity aa = getLogVO().getAgentActivity();
        aa.setEnd( new Date() );
        aa.setCause( cause.toString() );
//        for ( ReportItem item : reportItems.values() ) {
//            aa.getReports().addReportItem( item );
//        }
    }

    protected void doForceSpecific() throws ForceActionException {
        try {

            DirectoryDAOFactory adao = BaseDAOFactory.getDirectoryDAOFactory();
            String agentManager = adao.getAgentDAO().getManagerNameFor(
                    SipUtil.getName( getLogVO().getAgentActivity().getAgent() )
            );
            forceLog( new String[]{agentManager} );
        } catch ( DAOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ForceActionException( e.getMessage(), e );
        } catch ( DAOFactoryException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new ForceActionException( e.getMessage(), e );
        }
    }


}