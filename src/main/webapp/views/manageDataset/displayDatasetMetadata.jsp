<%@ page
	import="java.io.File"
	import="java.util.List"
	import="java.util.ArrayList"
	import="java.util.Iterator"
	import="java.util.Map"
	import="java.util.Hashtable"
	import="gov.nasa.jpl.edrn.ecas.curation.policymgr.CurationPolicyManager"
	import="gov.nasa.jpl.edrn.ecas.curation.policymgr.CasProductType"
	import="gov.nasa.jpl.edrn.ecas.curation.util.HTMLEncode"
%>
<script type="text/javascript" src="js/jquery/jquery.js"></script>

<div class="wizardContent">
	<h4>Dataset Metadata For: <%=session.getAttribute("dsCollection") %> / <%=session.getAttribute("ds") %></h4>
	<div>
		<h5>Defined metadata key/value pairs in <%=session.getAttribute("dsCollection") %> : <%=session.getAttribute("ds") %> </h5>
		
		<form action="updateDatasetMetaData" method="POST"> 
		<table id="metaDataEditor">
		<thead><tr><th>Key</th><th>Value</th></thead><tbody>
<%
			String policyName = (String)session.getAttribute("dsCollection");
			String productTypeName = (String)session.getAttribute("ds");
			
			CurationPolicyManager cpm = new CurationPolicyManager();
			// retrieve a hashtable of all product types with their metadata
			Hashtable<String, CasProductType> metaDataItems = cpm.getProductTypeMetaData(policyName, productTypeName);
			// get the product type requested in this session
			CasProductType cpt = (CasProductType) metaDataItems.get(productTypeName);
			// get the metadata for this product type 
			Hashtable<String, String> metaData = cpt.getMetaDataHT(); 
		
			//Hashtable<String, String> metaData = (Hashtable<String, String>)session.getAttribute("metaDataHT");
			//System.out.println("[debug]: metaData table size: " + metaData.size());
			
			String tableClose = "</tbody></table>";			
			/*
			String tableOpen = "<table id=\"metadataEditor\">";
			String tableHeader = "\t\t\t<thead><tr><th>Key</th><th>Value</th></thead>";
			out.println(tableOpen);
			out.println(tableHeader);
			*/
			if (metaData.size() > 0) {		
				int even_row = 1;
				for (Object k : metaData.keySet()) {
					String key = (String)k;
					String value = (String)metaData.get(k);
					
					/*
					 * PubMedID field requires special
					 * handling because of HTML hyperlink
					 * content. 
					 * (disabled 08/10/2009 - not necessary if only
					 * PubMedID value is entered instead of 
					 * an HTML hyperlink)
					 */
					//if (key.equals("PubMedID"))
						value = HTMLEncode.encode(value);
						
					String keyField = "\t\t\t\t<td class=\"key\">" + key + "</td>";
					
					String valueField = "\t\t\t\t<td class=\"value\"><input type=\"text\" id=\"value_" + key +
										"\" name=\"value_" + key + "\" value=\"" + value + "\" /></td>";
						
					out.print("\t\t\t<tr class=\"");
					// set even/odd row class for styling
					if (even_row == 1) 
						out.println("odd\">");
					else 
						out.println("even\">");
					
					out.println(keyField);				
					out.println(valueField);
					
					out.println("\t\t\t</tr>");
					even_row = 1 - even_row;
				}
			} else {
				out.println("<tr class=\"odd\"><td>");
				out.println("No metadata values defined.");
				out.println("</td></tr>");			
			}			
			out.println(tableClose);
			
			// hide the Save Changes button if no metadata is found
			if (metaData.size() > 0) {
				out.println("<input type=\"hidden\" name=\"dsCollection\" value=\""+ policyName +"\"/>");
				out.println("<input type=\"hidden\" name=\"ds\" value=\""+ productTypeName + "\"/>");
				out.println("<input type=\"hidden\" name=\"step\" value=\"displayDatasetMetadata\"/>");
				out.println("<input type=\"hidden\" name=\"action\" value=\"SaveAll\"/>");
				out.println("<br><input type=\"submit\" id=\"submitButton\" value=\"Save changes\"/>");
			}
			out.println("</form>");	
		%> 
	</div>
</div>