<%@ page
	import="java.io.File"
	import="java.util.List"
	import="java.util.ArrayList"
	import="java.util.Iterator"
	import="java.util.Map"
	import="java.util.Hashtable"
	import="gov.nasa.jpl.edrn.ecas.curation.policymgr.CurationPolicyManager"
%>
<script type="text/javascript" src="js/jquery/jquery.js"></script>

<div class="wizardContent">
	<h4>Dataset Metadata For: <%=session.getAttribute("dsCollection") %> / <%=session.getAttribute("ds") %></h4>

	<div>
		<h5>Defined metadata key/value pairs in <%=session.getAttribute("dsCollection") %> : <%=session.getAttribute("ds") %> </h5>
		<%
			String policyName = (String)session.getAttribute("dsCollection");
			String productTypeName = (String)session.getAttribute("ds");
			
			CurationPolicyManager cpm = new CurationPolicyManager();
			Hashtable metaData = (Hashtable)cpm.getProductTypeMetaData(policyName, productTypeName);
			
			String tableOpen = "<table>";
			String tableClose = "</table>";

			out.println(tableOpen);
			
			for (Object k : metaData.keySet()) {
				out.println("<tr><td>");
				out.print( (String)k);
				out.println("</td><td>");
				out.println(metaData.get(k).toString());
				out.println("</td></tr>");
				
			}
			out.println(tableClose);

		%>

	</div>
</div>