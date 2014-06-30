package com.basamadco.opxi.callmanager.call;

/**
 * @author Jrad
 *         Date: Jul 16, 2006
 *         Time: 4:20:22 PM
 */
public class LegState {


    public static final LegState IDLE    = new LegState( "Idle" );

    public static final LegState TRYING  = new LegState( "Trying" );

    public static final LegState RINGING = new LegState( "Ringing" );

    public static final LegState FAILURE = new LegState( "Failure" );

    public static final LegState IN_CALL = new LegState( "In Call" );

    public static final LegState END     = new LegState( "End" );

    public static final LegState ACK_PENDING = new LegState( "Ack Pending" );

//    public static final LegState UNI_LEG  = new LegState( "UniLeg" );

    /*public static final LegState REINVITE_PENDING = new LegState( "ReInvite Pending" );


    public static final LegState INVITING_AGENT = new LegState( "Inviting Agent" );*/

    public static final LegState REFRESH_INVITE_SENT = new LegState( "Refresh Invite Sent To Caller" );
    

    private String state;

    protected LegState( String state ) {
        this.state = state;
    }

    public boolean equals( Object object ) {
        LegState st = (LegState) object;
        return state.equalsIgnoreCase( st.state );
    }

    public String toString() {
        return state;
    }

}
