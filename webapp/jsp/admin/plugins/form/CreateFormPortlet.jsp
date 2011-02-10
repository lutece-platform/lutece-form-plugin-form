<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />
<jsp:useBean id="formPortlet" scope="session" class="fr.paris.lutece.plugins.form.web.portlet.FormPortletJspBean" />
<% formPortlet.init( request,fr.paris.lutece.plugins.form.web.ManageFormJspBean.RIGHT_MANAGE_FORM); %>
<%= formPortlet.getCreate(request) %>

<%@ include file="../../AdminFooter.jsp" %>