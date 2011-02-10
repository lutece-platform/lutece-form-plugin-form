<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="formGraph" scope="request" class="fr.paris.lutece.plugins.form.web.DoDownloadGraph" />
<% 
 
	formGraph.doGenerateGraph( request , response );
 
%>
