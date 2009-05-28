<%@page import="gov.nasa.jpl.edrn.security.SingleSignOn"%>
<%

//Copyright (c) 2009, California Institute of Technology.
//ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
//$Id$

// use new single sign on API
SingleSignOn auth = new SingleSignOn(response, request);
String refererUrl = request.getParameter("from");
auth.logout();
response.sendRedirect(refererUrl);
%>