<%
// Copyright (c) 2008, California Institute of Technology.
// ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.

// $Id$
%>
<% /* 
	* External libraries that might be used for this JSP (maybe none) 
	*/ 
%>
<%@ page
	import="java.util.List"
	import="java.util.ArrayList"
	import="java.util.Iterator"
	import="java.util.Map"
	import="gov.nasa.jpl.edrn.ecas.curation.policymgr.CurationPolicyManager"
%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
%>
<jsp:include page="views/common/edrn-informatics-header.jsp" />

<!-- tree view css + js -->
<link rel="stylesheet" type="text/css" href="js/jquery-treeview/jquery.treeview.css"/>
<script type="text/javascript" src="js/jquery/jquery.js"></script>
<script type="text/javascript" src="js/jquery-treeview/jquery.treeview.js"></script>
<script type="text/javascript" src="js/jquery-treeview/jquery.treeview.async.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		// Load up the tree view for the staging area
		$("#stagingArea").treeview({
			url: "showStagingArea"
		});
	});
</script>
<!--  end tree view css + js -->

<!-- column two -->
<div style="background-color:#fff;position:absolute;left:11%;height:100%;margin-top:10px;border-left:solid 1px #999;border-right:solid 1px #999;width:32%;">
	<center>
	<div style="padding-left:10px;padding:10px;">
	<img src="media/img/ingestion.png" style="border-style: none"/><br/>
	<a href=ingestData.jsp?step=2>Add Data to eCAS</a>
	</div>
	<div style="padding-left:10px;padding:10px;">
	<img src="media/img/curation.png" style="border-style: none"/><br/>
	<a href="manageDataSet.jsp">Manage Dataset Definitions</a>
	</div>
	</center>
	<div class="clr"><!--  --></div>
</div>

<!--  column three -->	
<style type="text/css">
	div#stagingArea, div#ingestArea {
	
	}
	div#stagingArea ul, div#ingestArea ul {
		list-style:none;
	}
	div#stagingArea ul li, div#ingestArea ul li {
	
	}
	div#stagingArea ul li a, div#ingestArea ul li a {
		color:#48a;
	}
</style>
<div style="background-color:#fff;position:absolute;left:45%;height:100%;width:40%;margin-top:10px;">
	<div style="padding-left:2px;padding-right:2px;font-size:9pt">
		<h4 style="margin-bottom:2px;">Staging Area:</h4>
		<div style="margin-bottom:10px;">The files shown below have not yet been ingested into eCAS</div>
		<div id="stagingArea" style="border:solid 1px #ccc;margin:5px;padding:5px;background-color:#fff;">
		
		</div>

	</div>
</div>

<jsp:include page="views/common/edrn-informatics-footer.jsp"/>

