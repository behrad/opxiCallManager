<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.basamadco.opxi.callmanager.call.Leg"%>
<%@ page import="com.basamadco.opxi.callmanager.call.CallService"%>
<%@ page import="com.basamadco.opxi.callmanager.call.CallServiceFactory"%>
<% try { %>
    <%
        //        if( request.getParameter( "id" ) != null )
        com.basamadco.opxi.callmanager.call.CallService call = com.basamadco.opxi.callmanager.call.CallServiceFactory.getCallService( request.getParameter( "id" ) );
    %>
    <p class="title">Call Information for Call-Id: '<%=call.getId()%>'</p>
	<p>
    <table cellspacing="0" class="mainCon">
		<tr>
			<td class="titrecon">#</td>
			<td class="titrecon">Leg CallId</td>
			<td class="titrecon">Role</td>
			<td class="titrecon">Status</td>
		</tr>
        <%
        	Collection list = call.allLegs();
        	if( list.size() == 0 ) {
        %>
        		<tr align="center"><td colspan="5" align="center" style="border:0px solid #000099;">
						<div align="center"><font color="red">No call Leg available in this call.</font></div>
				</td></tr>
		<%
        	} else {
                int count = 1;
                for (Iterator i = list.iterator(); i.hasNext(); count++ ) {
                    Leg leg = (Leg)i.next();
//						String color = "#000000";
//						switch( inCall.getState() ) {
//							case CallService.QUEUED:
//								color="#DD5500";
//								break;
//							case CallService.TRANSFERING:
//								color="#DD5500";
//								break;
//							case CallService.MAKE_CALL:
//							case CallService.ASSIGNED:
//							case CallService.IN_GREETING:
//								color="#0000AA";
//								break;
//							case CallService.WAITING:
//								color="#008800";
//								break;
//						}
        %>
				<tr>
					<td class="rowdef"><%=count%></td>
					<td class="rowdef"><%= leg.getSession().getCallId() %></td>
					<td class="rowdef"><%= leg.getRoleName() %></td>
					<td class="rowdef"><%= leg.getState() %></td>
				</tr>
        <%
            	}
            }
        %>
    	</table>
    </p>
    <%
        } catch( Exception e ) {
    %>
            <div align="center">Error Message:<font color="red">&nbsp;<%=e.getMessage()%></font></div>
    <%
        }
    %>