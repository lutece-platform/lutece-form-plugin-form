<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />
<jsp:useBean id="listFormPortlet" scope="session" class="fr.paris.lutece.plugins.form.web.portlet.ListFormPortletJspBean" />
<% listFormPortlet.init( request, fr.paris.lutece.plugins.form.web.FormJspBean.RIGHT_MANAGE_FORM); %>
<%= listFormPortlet.getCreate(request) %>

<%@ include file="../../AdminFooter.jsp" %>
