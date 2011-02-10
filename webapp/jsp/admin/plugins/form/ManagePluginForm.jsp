<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="formManagePluginForm" scope="session" class="fr.paris.lutece.plugins.form.web.ManageFormJspBean" />

<% formManagePluginForm.init( request, fr.paris.lutece.plugins.form.web.ManageFormJspBean.RIGHT_MANAGE_FORM ); %>
<%= formManagePluginForm.getManageForm( request ) %>

<%@ include file="../../AdminFooter.jsp" %>