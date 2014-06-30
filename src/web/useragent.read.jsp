<%@ include file="template/taglibs.jsp"%>

<table class="mainCon" cellspacing="0" cellpadding="3">
	<tr>
		<td colspan="2" class="titrecon" align="left">Agent profile:</td>
	</tr>
	<tr>
		<td class="titrecon">
		    Name
		</td>
		<td class="rowdef">
			<bean:write name="valueObject" property="name"/>
		</td>
	</tr>
	<tr>
		<td class="titrecon">
			Basic
		</td>
		<td class="rowdef">
		    <bean:write name="valueObject" property="basic"/>
		</td>
	</tr>
	<tr>
		<td class="titrecon">
			Note
		</td>
		<td class="rowdef">
		    <bean:write name="valueObject" property="note"/>
		</td>
	</tr>
	<tr>
		<td class="titrecon">&nbsp;
		</td>
		<td class="rowdef">
			<a href="<%=request.getContextPath()%>/home.do"><bean:message key="home"/></a>
		</td>
	</tr>
</table>