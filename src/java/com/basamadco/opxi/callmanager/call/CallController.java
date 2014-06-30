package com.basamadco.opxi.callmanager.call;

import javax.servlet.sip.Address;
import javax.servlet.sip.URI;

/**
 * To fastly overcome the call control functionality application-wide in OPXi Call Manager,
 * we need a very sophisticated and characterized call handling logic in the application
 * design with the SIP call handling protocol as one of the implemented control stacks.
 * A CallController is responsible for the logic of call stablishment and progress.
 * So, for any connection to be established for a call, an implemented controller is needed
 * which knows how to handle underlying call signaling.
 * CallControllers are not B2BUA machines but are logical wrappers for them. They know how
 * to use B2BUA machines.
 * There should exist call state machines usable as building service blocks and not at
 * the servlet level as currently is in this application.
 *
 * @author Jrad
 *         Date: Nov 23, 2006
 *         Time: 10:08:12 AM
 */
public interface CallController {


    /**
     * Returns the name of the B2BUA machine that will handle the connection
     * @return name of the SIP B2BUA machine handler class
     */
    public String getMachineType();

    public void setMachineType( String machineClassName );


    /**
     * Callee's role name in call participation
     * @return name of the role that callee will play in
     * @see Leg
     */
    public String getCalleeRoleName();

    public void setCalleeRoleName( String calleeRoleName );


    /**
     * The target URI that this controller should establish a call connection to with the specified machine and role
     * @return the URI of the callee
     */
    public URI getCalleeURI();

    public void setCalleeURI( URI calleeURI );

    
    public Address getCalleeAddress();

    public void setCalleeAddress( Address address );


    public UASLeg getUASLeg();

    public void setUASLeg( UASLeg UAS );

    
    /**
     * the call object that will be involved in the connection
     * @return the CallService
     */
    public CallService getCallService();

    /**
     * Connects the controlled CallService to the specified calleeURI as calleeRoleName with the help of
     * the B2BUA machineType 
     * 
     * @throws CallServiceException
     */
    public void connect() throws CallServiceException;

    /**
     * Does the dynamic application envolvement for the call being controlled 
     * @throws CallServiceException
     */
    public void involveApplications() throws CallServiceException;

}