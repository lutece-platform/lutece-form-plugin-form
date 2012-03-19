<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:useBean id="formCategory" scope="session" class="fr.paris.lutece.plugins.form.web.CategoryJspBean" />

<% 
	formCategory.init( request, formForm.RIGHT_MANAGE_FORM);
    response.sendRedirect( formCategory.doCreateCategory(request) );
%>
