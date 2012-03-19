<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="formExport" scope="session" class="fr.paris.lutece.plugins.form.web.ExportFormatJspBean" />

<% 
	formExport.init( request, fr.paris.lutece.plugins.form.web.FormJspBean.RIGHT_MANAGE_FORM);
    response.sendRedirect( formExport.getConfirmRemoveExportFormat(request) );
%>
