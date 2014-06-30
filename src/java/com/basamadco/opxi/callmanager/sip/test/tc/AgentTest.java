package com.basamadco.opxi.callmanager.sip.test.tc;

import com.basamadco.opxi.callmanager.pool.AgentSelection;
import com.basamadco.opxi.callmanager.pool.SelectionEvent;
import com.basamadco.opxi.callmanager.sip.test.Test;

/**
 * @author Jrad
 *         Date: Mar 5, 2006
 *         Time: 2:13:34 PM
 */
public class AgentTest extends Test {

    public AgentTest() {
//        setEnabled();
    }

    public void run() {
        try {

//            AgentService as = AgentService.getInstance();
//            as.addAgentFor( new UserAgent( "sip:agent04@cc.basamad.acc" ) );
//            for (int i = 0; i < as.listAgents().size(); i++) {
//                Agent agent = (Agent) as.listAgents().get( i );
//                System.out.println( "Agent inf: " + agent.skillEfficiency( "sales" ) );
//                System.out.println( "Agent inf: " + agent.skillEfficiency( "english" ) );
//            }
//            as.addAgentFor( new UserAgent( "agent01", "cc.basamad.acc" ) );
//            as.addAgentFor( new UserAgent( "agent03", "cc.basamad.acc" ) );
//            as.addAgentFor( new UserAgent( "agent04", "cc.basamad.acc" ) );
//
////            as.removeAgentFor( new UserAgent( "agent04", "cc.basamad.acc" ) );
////            System.out.println( "=============== As agent size after remove: " + as.listAgents().size() );
//            List pools = as.pools();
////            System.out.println( "POOLS: " );
//            AgentPool agentPool = (AgentPool) pools.get( 1 );
//            AgentSelection selection = agentPool.select( CallService.TEST_CALL );
//            System.out.println( "After primary pool selection:" );
////            printSelection( selection );
//            for (int i = 0; i < 1/*pools.size()*/; i++) {
//                AgentPool secPool = (AgentPool) pools.get( i );
//                System.out.println( "\t secondary pool name: " + secPool.getName() );
//                secPool.select( selection );
//            }
////            printSelection( selection );
//            Agent agent = agentPool.hunt( selection );
//            System.out.println( "Hunted Agent from " + agentPool.getName() + " is " + agent.getName() );
//            System.out.println( "final invariant: " + agent.hasMaximumCallsReached() );
//            agent.decOpenCalls();
//            /************************************* ******************************************/
//
//            AgentPool agentPool2 = (AgentPool) pools.get( 1 );
//            AgentSelection selection2 = agentPool2.select( CallService.TEST_CALL );
//            System.out.println( "After primary pool selection:" );
////            printSelection( selection );
//            for (int i = 0; i < 1/*pools.size()*/; i++) {
//                AgentPool secPool = (AgentPool) pools.get( i );
//                System.out.println( "\t secondary pool name: " + secPool.getName() );
//                secPool.select( selection2 );
//            }
////            printSelection( selection );
//            Agent agent2 = agentPool2.hunt( selection2 );
//            System.out.println( "Hunted Agent from " + agentPool2.getName() + " is " + agent2.getName() );
//            System.out.println( "final invariant: " + agent2.hasMaximumCallsReached() );

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void printSelection(AgentSelection selection) {
        for (int i = 0; i < selection.selections().length; i++) {
            SelectionEvent sel = selection.selections()[i];
            System.out.println("Selection [agent:" + sel.getAgent().getAOR() + "],[score:" + sel.getSkillScore() + "]");
        }
    }

}
