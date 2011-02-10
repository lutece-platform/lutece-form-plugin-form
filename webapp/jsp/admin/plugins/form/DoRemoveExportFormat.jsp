<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="formExport" scope="session" class="fr.paris.lutece.plugins.form.web.ExportFormatJspBean" />


<% 
	formExport.init( request, fr.paris.lutece.plugins.form.web.ManageFormJspBean.RIGHT_MANAGE_FORM );
    response.sendRedirect( formExport.doRemoveExportFormat( request ) );
%>