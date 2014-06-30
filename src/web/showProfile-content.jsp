<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.basamadco.opxi.callmanager.call.CallService"%>
<%@ page import="com.basamadco.opxi.callmanager.call.CallServiceFactory"%>
<%@ page import="com.basamadco.opxi.callmanager.call.Leg"%>
<% try { %>
    <%
        //        if( request.getParameter( "id" ) != null )
        String customer = request.getParameter( "customerId" ).trim();
    %>
    <p class="title">Customer Information Page: '<%=customer%>'</p></h2>
	    <p>
        <table cellspacing="0" class="mainCon">
                
        </table>
    </p>
    <%
        } catch( Exception e ) {
    %>
            <div align="center">Error Message:<font color="red">&nbsp;<%=e.getMessage()%></font></div>
    <%
        }
    %>