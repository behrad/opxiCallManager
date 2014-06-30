<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.basamadco.opxi.callmanager.queue.Queue"%>
<%@ page import="com.basamadco.opxi.callmanager.ServiceFactory"%>
<%@ page import="com.basamadco.opxi.callmanager.util.OpxiToolBox"%>
<%@ page import="com.basamadco.opxi.callmanager.call.CallService"%>
<%@ page import="com.basamadco.opxi.callmanager.sip.listener.SipSessionManager"%>
<%@ page import="com.basamadco.opxi.callmanager.call.Leg"%>
<% try { %>
    <%
    try {
        if( request.getParameter( "callId" ) != null ) {
            ((com.basamadco.opxi.callmanager.call.Leg)SipSessionManager.getByCallId( request.getParameter( "callId" ) ).getAttribute( com.basamadco.opxi.callmanager.call.Leg.class.getName() ))
            .getCallService().teardown( "Web Console Event" );
        }
    } catch( Throwable e ) {
    %>
        <p>Error Message:<font color="red">&nbsp;<%=e.getMessage()%></font></p>
    <%
    }
    ServiceFactory sf = (ServiceFactory)application.getAttribute( "com.basamadco.opxi.callmanager.ServiceFactory" );
    Queue queue = sf.getQueueManagementService().queueForName( request.getParameter( "id" ) );
    %>
    <p class="title">Call Information for queue: <%=queue.getName()%></p>
    <table width="90%" align="center" cellspacing="0" class="mainCon">
			<tr>
				<td class="titrecon">#</td>
				<td class="titrecon">Call Id</td>
				<td class="titrecon">Caller</td>
				<td class="titrecon">Status</td>
				<td class="titrecon">Wait Time</td>
				<td class="titrecon">Duration</td>
                <td class="titrecon">Handler Agent</td>
                <td class="titrecon">Actions</td>
            </tr>
        <%
        	Collection list = queue.totalCallList();
        	if( list.size() == 0 ) {
        %>
        		</table>
				<p class="msg">No call available in queue</p>
		<%
        	} else {
                int count = 1;
                for (Iterator i = list.iterator(); i.hasNext(); count++ ) {
                    com.basamadco.opxi.callmanager.call.CallService inCall = (CallService)i.next();
						String color = "def";
						switch( inCall.getState() ) {
							case CallService.QUEUED:
								color = "gray";
								break;
							case com.basamadco.opxi.callmanager.call.CallService.TRANSFERING:
								color = "gray";
								break;
							case CallService.MAKE_CALL:
							case CallService.ASSIGNED:
							case CallService.IN_GREETING:
								color = "swing";
								break;
                            case com.basamadco.opxi.callmanager.call.CallService.RINGING:
                                color = "yellow";
                                break;
                            case com.basamadco.opxi.callmanager.call.CallService.WAITING:
								color = "blue";
								break;
						}
        %>

				<tr>
					<td class="row<%=color%>"><%=count%></td>
					<td class="row<%=color%>"><a href="<%=request.getContextPath()%>/showCall.jsp?id=<%=inCall.getId()%>"><%=inCall.getId()%></a></td>
					<td class="row<%=color%>"><%=inCall.getInitialRequest().getFrom().getURI()%></td>
					<td class="row<%=color%>"><%=inCall.getStateString()%></td>
                    <td class="row<%=color%>"><%=OpxiToolBox.duration( inCall.waitTime() )%></td>
                    <td class="row<%=color%>"><%=OpxiToolBox.duration( inCall.duration() )%></td>
                    <td class="row<%=color%>" align="center"><%= (inCall.getHandlerAgent()==null)?"---":inCall.getHandlerAgent()%></td>
                    <td class="row<%=color%>">
                        <a href="<%=request.getContextPath()%>/transfer.jsp?callId=<%=inCall.getId()%>">transfer</a>
                        ,
                        <a href="<%=request.getContextPath()%>/teardown.jsp?callId=<%=inCall.getId()%>">teardown</a>
                    </td>
                </tr>
        <%
            	}
                %>
            </table>
        <%
            }
        %>
    </p>
    <%
        } catch( Exception e ) {
    %>
            <p align="center">Error Message:<font color="red">&nbsp;<%=e.getMessage()%></font></p>
    <%
        }
    %>