<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="formApp" scope="request" class="fr.paris.lutece.plugins.form.web.FormApp" />

<% formApp.doRemoveAsynchronousUploadedFile( request ); %>