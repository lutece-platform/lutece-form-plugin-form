<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />
<jsp:useBean id="formExport" scope="session" class="fr.paris.lutece.plugins.form.web.ExportFormatJspBean" />
<% formExport.init( request,fr.paris.lutece.plugins.form.web.ManageFormJspBean.RIGHT_MANAGE_FORM ); %>
<%= formExport.getCreateExportFormat( request ) %>
<%@ include file="../../AdminFooter.jsp" %>