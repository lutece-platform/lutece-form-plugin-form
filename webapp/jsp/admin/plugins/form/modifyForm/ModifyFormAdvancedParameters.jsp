<jsp:useBean id="formAdvancedParametersForm" scope="session" class="fr.paris.lutece.plugins.form.web.FormAdvancedParametersJspBean" />

<% String strContent = formAdvancedParametersForm.processController ( request , response ); %>

<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../../AdminFooter.jsp" %>