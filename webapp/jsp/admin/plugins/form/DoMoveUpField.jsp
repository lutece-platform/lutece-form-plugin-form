<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="formForm" scope="session" class="fr.paris.lutece.plugins.form.web.FormJspBean" />

<% 
	formForm.init( request, formForm.RIGHT_MANAGE_FORM );
    response.sendRedirect( formForm.doMoveUpField( request ) );
%>
