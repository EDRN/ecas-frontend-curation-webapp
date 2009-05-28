<%
// Copyright (c) 2008, California Institute of Technology.
// ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.

// $Id$
%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
%>

<jsp:include page="views/common/edrn-informatics-header.jsp" />

<h1 class="sectionTitle" style="width: auto; font-size: 22px; font-weight: bold; margin-top: 24px; text-align: center;">Please Log In to the eCAS Curation Interface</h1>
<p>&nbsp;</p>

  
<form id="login-form" method="post" action="ldap_login.jsp" name="login_form">
 <input name="from" value="<%=request.getParameter("from")%>" type="hidden">
 <center>
  <%
   if(request.getParameter("loginFail") != null){
  %>
   	<div class="error" style="margin:0px;margin-bottom:5px;width:230px;font-size:90%">Invalid Credentials...<br/>Please try again.</div>
  <%
   }
 
   if(request.getParameter("loginConnectFail") != null){
   %>
 	<div class="error" style="margin:0px;margin-bottom:5px;width:230px;font-size:90%">Unable to contact LDAP authentication server...<br/>Please try again later.</div>
   <% 
   }
   %>
  <table>
   <tbody><tr>
     <td>Username</td>
     <td><input id="login-username" name="username" value="" size="20" maxlength="255" type="text"></td>
   </tr>
   <tr>
     <td>Password</td>
     <td><input name="password" value="" size="20" maxlength="255" type="password"></td>
   </tr>
   <tr>
     <td>&nbsp;</td>
     <td><input name="login_submit" value="Log In" type="submit"></td>
   </tr>
 </tbody></table>
 </center>
</form>



<div style=""></div>
<script type="text/javascript">
	document.getElementById('login-username').focus();
</script>

<jsp:include page="views/common/edrn-informatics-footer.jsp" />