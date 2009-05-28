<%@page import="gov.nasa.jpl.edrn.security.SingleSignOn"%>
<%
SingleSignOn auth = new SingleSignOn(response, request);
String refererUrl = request.getRequestURI();

 if(!auth.isLoggedIn()){
   response.sendRedirect("login.jsp?from="+refererUrl);
 }
%>