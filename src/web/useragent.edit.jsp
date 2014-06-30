<%@ include file="template/taglibs.jsp"%>

<table class="mainCon" cellspacing="0" cellpadding="3">
<html:form action="u/agent">
    <html:hidden property="id"/>
    <html:hidden property="action"/>
	<tr>
		<td colspan="2" class="titrecon" align="left">Input Agent profile information down below:</td>
	</tr>
	<tr>
		<td class="titrecon">
		    Name
		</td>
		<td class="rowdef">
		    <html:text styleClass="textarea" property="name"/>
		</td>
	</tr>
	<tr>
		<td class="titrecon">
			Basic
		</td>
		<td class="rowdef">
		    <html:text styleClass="textarea" property="basic"/>
		</td>
	</tr>
	<tr>
		<td class="titrecon">
			Note
		</td>
		<td class="rowdef">
		    <html:text styleClass="textarea" property="note"/>
		</td>
	</tr>
	<tr>
		<td class="titrecon">
			&nbsp;
		</td>
		<td class="rowdef">
			<html:submit styleClass="searchbutton" />
		</td>
	</tr>
</html:form>
</table>