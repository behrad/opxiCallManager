<%
    String msg = (String)request.getAttribute( "msg" );
    if( msg != null ) {
%>
    <p class="msg_green"><%= msg %></p>
<%
    }
%>
	</br>
    <table class="mainCon" cellspacing="0" cellpadding="5">
        <form action="<%=request.getContextPath()%>/callTransfer">
        	<tr>
		      	<td colspan="2" class="titrecon" align="left">Input call transfer information down below:</td>
            </tr>
            <tr>
            	<td class="titrecon" align="right">Transfer Call Id </td>
            	<td class="rowdef"><input class="textarea" size="50" name="callId" type="text"
                                          value="<%=request.getParameter( "callId" ) == null ? "" : request.getParameter( "callId" ) %>"/>
                </td>
            </tr>
            <tr>
            	<td class="titrecon" align="right">Transferee </td>
            	<td class="rowdef">
                	<input class="textarea" size="50" name="target" type="text" value="sip:voiceApp@cc.basamad.acc;123456"/>
            	</td>
            </tr>
            <tr>
            	<td class="titrecon" td>&nbsp;</td>
            	<td class="rowdef">
            		<input class="searchbutton" type="submit" value=" Transfer "/>
            	</td>
            </tr>
        </form>
	</table>