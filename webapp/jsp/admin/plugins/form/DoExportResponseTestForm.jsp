<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="formForm" scope="session" class="fr.paris.lutece.plugins.form.web.FormJspBean" />


<% 

	formForm.init( request, fr.paris.lutece.plugins.form.web.ManageFormJspBean.RIGHT_MANAGE_FORM );
   String strResult =  formForm.doExportResponseTestForm( request , response );
  if (!response.isCommitted())
{
  response.sendRedirect(strResult);
}
%>
