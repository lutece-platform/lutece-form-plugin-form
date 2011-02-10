<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="formDefaultMessage" scope="session" class="fr.paris.lutece.plugins.form.web.DefaultMessageJspBean" />


<% 
	formDefaultMessage.init( request,fr.paris.lutece.plugins.form.web.ManageFormJspBean.RIGHT_MANAGE_FORM );
    response.sendRedirect( formDefaultMessage.doModifyDefaultMessage( request ) );
%>
