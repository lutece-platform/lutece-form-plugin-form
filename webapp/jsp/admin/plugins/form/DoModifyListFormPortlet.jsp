<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="listFormPortlet" scope="session" class="fr.paris.lutece.plugins.form.web.portlet.ListFormPortletJspBean" />
<% 
	listFormPortlet.init( request, fr.paris.lutece.plugins.form.web.FormJspBean.RIGHT_MANAGE_FORM );
    response.sendRedirect( listFormPortlet.doModify( request ) );
%>
