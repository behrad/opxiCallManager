<%
    String msg = (String) request.getAttribute( "msg" );
    if ( msg != null ) {
%>
<p class="msg_green"><%= msg %>
</p>
<%
    }
%>

<table class="mainCon" cellspacing="0" cellpadding="5">
    <form action="<%=request.getContextPath()%>/webIMServlet" method="get">
        <tr>
            <td colspan="2" class="titrecon" align="left">Send instant message with information down below:</td>
        </tr>
        <tr>
            <td class="titrecon" align="right">To</td>
            <td class="rowdef">
                <input class="textarea" size="50" name="target" type="text"/>
            </td>
        </tr>
        <tr>
            <td class="titrecon" align="right">Text</td>
            <td class="rowdef">
                <textarea class="textarea" cols="50" rows="5" name="text"></textarea>
            </td>
        </tr>
        <tr>
            <td class="titrecon">&nbsp;</td>
            <td class="rowdef">
                <input class="searchbutton" type="submit" value=" Send "/>
            </td>
        </tr>
    </form>
</table>