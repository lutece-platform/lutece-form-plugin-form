<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="formForm" scope="session" class="fr.paris.lutece.plugins.form.web.FormJspBean" />


<% 
	formForm.init( request,fr.paris.lutece.plugins.form.web.ManageFormJspBean.RIGHT_MANAGE_FORM );
    response.sendRedirect( formForm.doModifyField( request ) );
%>
