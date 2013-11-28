<%@ page errorPage="../../ErrorPage.jsp" %>
<%@ page trimDirectiveWhitespaces="true" %>
<jsp:useBean id="formForm" scope="session" class="fr.paris.lutece.plugins.form.web.FormEntryJspBean" />
<% 
	formForm.init( request, formForm.RIGHT_MANAGE_FORM );
	formForm.doGenerateGraph( request , response );
%>