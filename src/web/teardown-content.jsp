<%@ page import="com.basamadco.opxi.callmanager.sip.listener.SipSessionManager"%>
<%@ page import="com.basamadco.opxi.callmanager.call.Leg"%>
<%@ page import="com.basamadco.opxi.callmanager.call.CallServiceFactory"%>
<table width="100%" class="mainCon" cellpadding="0" cellspacing="0" height="90%">
        <tr valign="top"><td>
            <%
                try {
                    if( request.getParameter( "callId" ) != null ) {
                        CallServiceFactory.getCallService( request.getParameter( "callId" ) ).teardown( "Web Console Event" );
            %>
                        <p class="msg_green">Call Has Successfully Bean Tear Down</p>
                        <p class="msg_green"><a href="javascript:history.back();">Back</a></p>
                        <p class="msg_green"><a href="<%=request.getContextPath()%>/index.jsp">Home</a></p>
            <%
                    }
                } catch( Throwable e ) {
            %>
                <p class="msg">Error Message:&nbsp;<%=e.getMessage()%></p>
                <p class="msg_green"><a href="javascript:history.back();">Back</a></p>
                <p class="msg_green"><a href="<%=request.getContextPath()%>/index.jsp">Home</a></p>
            <%
                }
            %>
            </td>
        </tr>
    </table>
