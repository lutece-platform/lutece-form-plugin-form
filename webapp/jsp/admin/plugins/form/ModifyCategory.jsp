<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />
<jsp:useBean id="formCategory" scope="session" class="fr.paris.lutece.plugins.form.web.CategoryJspBean" />
<% formCategory.init( request, fr.paris.lutece.plugins.form.web.ManageFormJspBean.RIGHT_MANAGE_FORM);%>
<%= formCategory.getModifyCategory( request ) %>
<%@ include file="../../AdminFooter.jsp" %>