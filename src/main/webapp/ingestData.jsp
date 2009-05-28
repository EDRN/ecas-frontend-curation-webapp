<%@ page
	import="java.io.File"
	import="java.util.List"
	import="java.util.ArrayList"
	import="java.util.Iterator"
	import="java.util.Map"
	import="gov.nasa.jpl.edrn.ecas.curation.policymgr.CurationPolicyManager"
%>
<%@ include file="tools/requireLogin.jsp" %>
<%
// Build Breadcrumbs for the page
// Breadcrumb specification format: label or label:url
// if :url not provided, breadcrumb will not be clickable
// if url begins with '/', context path will be prepended
session.setAttribute("breadcrumbs",new String[] {"Ingest Data"});
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
		$("#policyView").treeview({
			url: "showExistingPolicies"
			
		});
	});
	
	function treeSelection(a) {
		id = $(a).attr('href');
		$("#dsCollection").attr('value',id);
		$("#submitButton").attr('disabled',false);
		return false;
	}
</script>
<!--  end tree view css + js -->
<div style="min-width:1040px;">
	<h4>Determine Dataset</h4>

	<div class="wizardContent" >
		<p>There are two ways to add data to eCAS. Please choose an option below to continue:
		</p>
		<div style="background:url(media/img/icon-new-product.png) scroll left top no-repeat;min-height:150px;padding-left:90px;width:400px;float:left;margin-right:15px;">
			<h5 style="margin-top:10px;">Add products to an existing dataset collection.</h5>
			<form action="addData.jsp" method="POST"/>
				<input type="hidden" name="step" value="choosePolicy"/>
				<input type="text" id="dsCollection" name="dsCollection" value="choose from the list below..." style="width:300px;border:solid 1px #888;padding:2px;">
				<input type="submit" id="submitButton" disabled="true" value="Select"/>
				<div id="policyView" style="width:390px;border:solid 0px red;margin-top:5px;background-color:#fff;">
				
				</div>
				
			</form>
		</div>
		<div style="background:url(media/img/icon-new-dataset.png) scroll left top no-repeat;height:150px;padding-left:100px;width:300px;float:left;">
			<h5 style="margin-top:10px;"><a href="createDataset.jsp">Create a new Dataset Collection.</a></h5>
		</div>
		<div class="clr"><!-- CLEAR --></div>

		
		<br/><br/>
	</div>
</div>
<jsp:include page="views/common/edrn-informatics-footer.jsp" />