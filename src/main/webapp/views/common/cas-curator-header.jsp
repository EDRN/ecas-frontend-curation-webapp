<%@page import="org.apache.oodt.security.sso.SingleSignOn"%>
<!-- 
eCAS curator Release Information:
$Id$
 -->
<%
// Copyright (c) 2008, California Institute of Technology.
// ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.

// $Id$

	// Set up parameters for single signon authentication
	SingleSignOn auth = SSOUtils.getWebSingleSignOn(application, request, response);
	String refererUrl = request.getRequestURI();
	String userDetails = "";

	// Determine text to show in the user details area 
	if(auth.isLoggedIn()){
    	userDetails = "Logged in as " + auth.getCurrentUsername() 
    		+ " &nbsp;<a href=\"" + request.getContextPath() + "/tools/logout.jsp?from=" 
    		+ refererUrl + "\">Log Out</a>";
	} else { 
		userDetails = "Not logged in. <a href=\"" + request.getContextPath() 
			+ "/login.jsp?from=" + refererUrl + "\">Log In</a>";
	}
 	
	// Retrieve the breadcrumbs to display on this page
	String breadcrumbString = "";
	if (session.getAttribute("breadcrumbs") != null) {
 		String breadcrumbs[] = (String[]) session.getAttribute("breadcrumbs");
		for (int i = 0; i < breadcrumbs.length; i++) {
			// Breadcrumbs are specified on individual pages as an array
			// of String objects, each with the following format:
			// either:	label (for a non-clickable breadcrumb)
			// or:		label:url (for a clickable link breadcrumb)
			String[] parts = (String[]) ((String)breadcrumbs[i]).split(":");
			if (parts.length == 2) {
				if (parts[1].startsWith("/")) {
					// Process a breadcrumb with an absolute clickable link
					breadcrumbString += (" / " + "<a href=\"" 
						+ request.getContextPath() + parts[1] + "\">" 
						+ parts[0] + "</a>");
				} else {
					// Process a breadcrumb with a relative clickable link
					breadcrumbString += (" / " + "<a href=\"" 
						+ parts[1] + "\">" + parts[0] + "</a>");
				}
			} else {
				// Process a non-clickable breadcrumb
				breadcrumbString += (" / " + parts[0]);	
			}
		}
		// Clear the breadcrumb data from the session after processing.
		// This prevents a page which does not explicitly set its own breadcrumb data from
		// displaying breadcrumbs left over from a previous page
		session.setAttribute("breadcrumbs",null);
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="org.apache.oodt.cas.curation.util.SSOUtils"%>
<%@page import="org.apache.oodt.security.sso.AbstractWebBasedSingleSignOn"%>
<%@page import="org.apache.oodt.security.sso.SingleSignOn"%>
<%@page import="org.apache.oodt.cas.curation.servlet.CuratorConfMetKeys"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<!-- CSS Stylesheets -->
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/media/skin/main.css" />
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/media/skin/ui.tabs.css"/>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/media/skin/fileTree.css"/>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/media/skin/jquery.alerts.css"/>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/media/skin/custom/css/edrn-informatics.css"/>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/media/skin/curator/css/curator.css"/>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/media/skin/editing.css"/>


	<!-- JavaScript Libraries -->
	<script type="text/javascript" src="js/jquery-1.3.2.js"></script>
	<script type="text/javascript" src="js/jquery-ui/jquery-ui-1.7.2.full.min.js"></script>
	<script type="text/javascript" src="js/fileTree.js"></script>
	<script type="text/javascript" src="js/jquery.alerts.js"></script>
	<script type="text/javascript" src="js/main.js"></script>
	<script type="text/javascript" src="js/editing.js"></script>
	<script type="text/javascript" src="js/jquery.periodicalupdater.js"></script>
	

	
	<% String projectName =  application.getInitParameter(CuratorConfMetKeys.PROJECT_DISPLAY_NAME);%>
	<title><%=(projectName != null ? projectName+" :: ":"")%>CAS</title>
</head>
<body onload="refreshIngestTaskList();registerIngestionTaskListener();">
<div id="ncibanner">
	<div id="ncibanner-inner">
	  <a href="http://www.cancer.gov/"><h2 class="ncilogo">National Cancer Institute</h2></a>
	  <a href="http://www.cancer.gov/"><h2 class="cdglogo">www.cancer.gov</h2></a>
	  <a href="http://www.nih.gov/"><h2 class="nihlogo">National Institutes of Health</h2></a>
	</div>
</div>
<br class="clr"/>
<div id="edrnlogo">
	<h1 class="header-title">
		<img id="edrnlogo-logo" src="./media/img/edrn-logo.png"/>Early Detection Research Network</h1>
	<h2 class="header-title">Research and development of biomarkers and technologies for the clinical application of early cancer detection strategies</h2>
</div>
<div id="dcplogo">
	<a href="http://prevention.cancer.gov"><h2 class="dcplogo">Division of Cancer Prevention</h2></a>
</div>
<div class="userdetails"><%=userDetails %></div>
<div id="edrninformatics">
	<h2 class="app-title">EDRN eCAS Curation</h2>
	<div id="breadcrumbs" class="cas-breadcrumbs">
	  <a href="./">Home</a>&nbsp;<%=breadcrumbString %>
    </div>

<!-- begin main content -->
	<div style="position:relative;min-height:300px;">