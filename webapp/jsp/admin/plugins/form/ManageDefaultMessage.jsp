<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />
<jsp:useBean id="formDefaultMessage" scope="session" class="fr.paris.lutece.plugins.form.web.DefaultMessageJspBean" />

<% formDefaultMessage.init( request,fr.paris.lutece.plugins.form.web.ManageFormJspBean.RIGHT_MANAGE_FORM ); %>
<%= formDefaultMessage.getManageDefaultMessage( request ) %>

<%@ include file="../../AdminFooter.jsp" %>