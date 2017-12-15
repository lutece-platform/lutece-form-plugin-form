<jsp:useBean id="formTestForm" scope="session" class="fr.paris.lutece.plugins.form.web.TestFormJspBean" />

<% String strContent = formTestForm.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>