package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.ServiceFactory;
import com.basamadco.opxi.callmanager.entity.DirectoryEntity;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.profile.OpxiCMEntityProfile;
import com.basamadco.opxi.callmanager.entity.profile.Rule;
import com.basamadco.opxi.callmanager.entity.profile.RuleSet;
import com.basamadco.opxi.callmanager.pool.rules.RuleInstantiationException;
import com.basamadco.opxi.callmanager.profile.ProfileException;
import com.basamadco.opxi.callmanager.rule.AbstractRule;
import com.basamadco.opxi.callmanager.rule.RuleNotInvolvedException;
import com.basamadco.opxi.callmanager.sip.SipCallController;

import javax.servlet.sip.SipServletRequest;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents any callable entity in OPXi Call Manager. Callable entities are them
 * that incomming SIP requests will be address directly or indirectly.
 * CallTargets conventionaly all are LDAP based entities.
 * There are a bunch of interesting common properties which are avaiable through this class.
 *
 * @author Jrad
 *         Date: Jul 26, 2006
 *         Time: 2:02:18 PM
 */
public abstract class CallTarget extends DirectoryEntity {

    private static final Logger logger = Logger.getLogger( CallTarget.class.getName() );


    private ServiceFactory serviceFactory;

    private String name;

    private String telephoneNumber;

    private SipServletRequest request;

    private transient OpxiCMEntityProfile profileToApply;

    private transient boolean profileIsDirty = false;

    private List applications = new ArrayList();

    protected List<AbstractRule> ruleSet = new ArrayList<AbstractRule>();


    protected CallTarget() {
//        this.name = name;
//        setCN( name );
    }

    /**
     * The copy constructor
     *
     * @param target The CallTarget object to copy from
     */
    protected CallTarget( CallTarget target ) {
        setCN( target.getCN() );
        setDN( target.getDN() );
        setName( target.getName() );
        setTelephoneNumber( target.getTelephoneNumber() );
        setRequest( target.getRequest() );
        setHomeURI( target.getHomeURI() );
        setServiceFactory( target.getServiceFactory() );
    }

    /**
     * Returns this CallTarget's access URI.
     * It may be SIP, HTTP or any other URI types
     *
     * @return String representation of the URI
     * @throws com.basamadco.opxi.callmanager.OpxiException
     *
     */
//    public abstract URI getTargetURIs() throws OpxiException;

    /**
     * Is responsible for handling the call with the correct logic
     *
     * @param call The CallService object to service
     * @return
     */
    public abstract void service( CallService call ) throws OpxiException;

//    public abstract String getTargetURIs( String requestURI ) throws OpxiException;

    /**
     * Checks if this callable entity should handle calls with a call Queue
     *
     * @return true if this calltarget will use a Queue to handle calls
     */
    public abstract boolean isQueueable();

    /**
     * Shows if this CallTarget is in stable state and can be updated.
     *
     * @return true if profile changes are to be applied
     */
    protected abstract boolean hasUpdatableState();


    /**
     * This method is called when corresponding call's state is changed
     * This can be used in proxy mode for proxyTargets to be aware of their call
     * state and to update their status regarding to call state.
     *
     * @param callState
     */
    public void callStateUpdated( int callState ) {

    }


    /**
     * Checks if the input event is supported against this entitie's rule profile
     *
     * @param event
     * @return false if event is against some rule, otherwise true
     * @throws OpxiException
     */
    public boolean approves( Object event ) throws OpxiException {
        logger.finer( "Check profile rules in " + this + " on " + event );
        if ( ruleSet.size() == 0 ) {
            logger.finest( "RuleSet size is zero (All events are accepted) in this profile" );
            return true;
        }
        for ( AbstractRule rule : ruleSet ) {
            try {
//                logger.finest("Check rule: " + rule);
                if ( !rule.evaluate( event ) ) {
                    logger.finest( "Event is against rule " + rule );
                    return false;
                }
                logger.finest( "Event satisfies rule " + rule );
            } catch ( RuleNotInvolvedException e ) {
                logger.finest( "Rule not involveable, check next rule..." );
            }
        }
        logger.finest( "No rule is against the event in profile " + this );
        return true;
    }

    public List<AbstractRule> getRules() {
        return Collections.unmodifiableList( new ArrayList<AbstractRule>( ruleSet ) );
    }

    /**
     * Applies the input profile schema to this CallTarget object properties
     *
     * @param profile The new profile to apply
     */
    protected void applyProfile( OpxiCMEntityProfile profile ) throws ProfileException {
        if ( profile.getApplicationIntegration() != null ) {
            applications.clear();
            for ( int i = 0; i < profile.getApplicationIntegration().getApplicationCount(); i++ ) {
                com.basamadco.opxi.callmanager.entity.profile.Application app =
                        profile.getApplicationIntegration().getApplication( i );
                try {
                    Application application = (Application) BaseDAOFactory.getDirectoryDAOFactory().
                            getCallTargetDAO().getCallTargetById( app.getName() );
                    application.setServiceFactory( getServiceFactory() );
                    application.setProfile( app );
                    logger.finest( "Add application integration for entity '" + getName() + "': " + app.getName() );
                    addApplication( application );
                } catch ( Exception e ) {
                    logger.log( Level.SEVERE, "Ignoring application profile: " + app.getName(), e );
                }
            }
            Collections.sort( applications );
            logger.finest( "ApplicationIntegration profile update is successfullly applied." );
        }
        for ( RuleSet rulesProfile : profile.getRuleSet() ) {
            try {
                for ( Rule rule : rulesProfile.getRule() ) {
                    AbstractRule theRule = AbstractRule.instantiate( rule, this );
                    theRule.setTargetService( rulesProfile.getTargetService() );
                    logger.finest( "add rule to[" + hashCode() + "] " + ruleSet.add( theRule ) );
                }
            } catch ( RuleInstantiationException e ) {
                logger.log( Level.SEVERE, "Could not successfully create rule ", e );

            }
        }
    }

    /**
     * Checks input CallService object application profile and assigns the first
     * evaluated application to the call
     *
     * @param call the CallService object for which application profile will be checked
     * @return True if an application evaluates to be assigned to the input call
     * @throws ApplicationIntegrationException
     *
     */
    public boolean doesApplicationInvolve( CallService call ) throws ApplicationIntegrationException {
        logger.finer( "Check application profile for entity '" + getName() + "': size=" + applications.size() );
        for ( int i = 0; i < applications.size(); i++ ) {
            Application application = (Application) applications.get( i );
            ApplicationIntegrationContext ctx = new ApplicationIntegrationContext( call, application, this );
            call.setApplicationContext( ctx );
            boolean result = ctx.evaluate();
            logger.finest( "******** Application '" + application.getName() + "' evaluated: " + result );
            if ( result ) {
                CallController cc = new SipCallController( call );
                try {
                    cc.involveApplications();
                } catch ( CallServiceException e ) {
                    logger.log( Level.SEVERE, e.getMessage(), e );
                    return false;
                }
                return true;
            } else {
//                ctx.destroy();
            }
        }
        return false;
    }


    public void setCN( String CN ) {
        super.setCN( CN );
        setName( CN );
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber( String telephoneNumber ) {
        this.telephoneNumber = telephoneNumber;
    }

    public ServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    public void setServiceFactory( ServiceFactory serviceFactory ) {
        this.serviceFactory = serviceFactory;
    }

    public SipServletRequest getRequest() {
        return request;
    }

    public void setRequest( SipServletRequest request ) {
        this.request = request;
    }

    private void addApplication( com.basamadco.opxi.callmanager.call.Application application ) {
        applications.add( application );
    }

    /**
     * Clients should call this to update this CallTarget properties with the new
     * profile schema values.
     * NOTE: Since callable entities are run-time managed objects, OPXi Call Manager
     * won't apply changes to properties values instantly, but the object will be
     * flaged to be updated with the new profile properties at the right time when
     * hasUpdatableState returns TRUE.
     *
     * @param profile The profile to update this CallTarget properties with
     */
    public void assignProfile( OpxiCMEntityProfile profile ) throws ProfileException {
        profileIsDirty = true;
        profileToApply = profile;
        checkApplyProfileUpdate();
    }

    /**
     * Conditionaly applies profile updates to this object based on it's state.
     * NOTE: It is the CallTarget implmentations' responsibility to call this method
     * when needed (e.g. after changes to object's internal state) to apply profile
     * waiting to be applied.
     */
    protected void checkApplyProfileUpdate() {
//        logger.finest("Profile is dirty: " + profileIsDirty);
//        logger.finest("Profile to apply: " + profileToApply);
//        logger.finest("Is updatable: " + hasUpdatableState());
        if ( profileIsDirty && profileToApply != null ) {
            if ( hasUpdatableState() ) {
                try {
                    applyProfile( profileToApply );
                    profileIsDirty = false;
                    profileToApply = null;
                } catch ( ProfileException e ) {
                    logger.log( Level.SEVERE, "Couldn't apply profile", e );
                }
            }
        }
    }

}
