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
		<form action="updateDatasetMetaData" method="POST"> 
		<table id="metaDataEditor">
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

				String tableHeader = "<thead><tr><th>Key</th><th>Value</th></thead><tbody>";
				String tableClose = "\t\t</tbody></table>";			
				/*
				String tableOpen = "<table id=\"metadataEditor\">";
				out.println(tableOpen);
				*/
				
				if (metaData.size() > 0) {		
				    int even_row = 0;

				    out.println(tableHeader);
				    for (Object k : metaData.keySet()) {
						String key = (String)k;
						String value = (String)metaData.get(k);
						
						String encodedValue = HTMLEncode.encode(value);
						
						String keyField = "\t\t\t\t<td class=\"label\">" + key + "</td>";
						
						String valueField = "\t\t\t\t<td class=\"value\">\r\n";
						
						if (value.length() < 100) {
						    valueField += "\t\t\t\t<input type=\"text\" id=\"value_" + key 
						    			  +	"\" name=\"value_" + key  
						    			  + "\" value=\"" + encodedValue 
										  + "\" />\r\n\t\t\t\t</td>";
						}
						else {
						    int cols = 80;
						    int rows = value.length() / cols;
						    valueField += "\t\t\t\t<textarea id=\"value_" + key + "\" "
						    			  + "name=\"value_" + key + "\" "
						    			  + "rows=\"" + rows + "\" cols=\"" + cols + "\">" 
						    			  + value + "</textarea></td>";
						}
							
						out.print("\t\t<tr class=\"metadata\" id=\"met_" + key + "\">\r\n");
						
						// set even/odd row class for styling
						/*
						if (even_row == 0) 
							out.println("odd\">");
						else 
							out.println("even\">");
						*/
						out.println(keyField);				
						out.println(valueField);
						
						out.println("\t\t</tr>");
						even_row = 1 - even_row;
					}
				} else {
				    out.println("<thead></thead><tbody>");
					out.println("\t\t<tr class=\"odd\"><td>No metadata values defined.");
					out.println("\t\t</td></tr>");
				}			
				out.println(tableClose);
				
				// hide the Save Changes button if no metadata is found
				if (metaData.size() > 0) {
					out.println("\t\t<input type=\"hidden\" name=\"dsCollection\" value=\""+ policyName +"\"/>");
					out.println("\t\t<input type=\"hidden\" name=\"ds\" value=\""+ productTypeName + "\"/>");
					out.println("\t\t<input type=\"hidden\" name=\"step\" value=\"displayDatasetMetadata\"/>");
					out.println("\t\t<input type=\"hidden\" name=\"action\" value=\"SaveAll\"/>");
					out.println("\t\t<br><input type=\"submit\" id=\"submitButton\" value=\"Save all changes\"/>");
				}
				out.println("\t\t</form>");	
			%> 			
	</div>
</div>

<div class="wizardContent">
	<h4>Add New Metadata Key</h4>
	<form action="updateDatasetMetaData" method="POST">
		<table>
			<thead><tr><th>KeyName</th><th>Value</th></thead>
			<tbody>
			<tr class="odd"><td><input type="text" id="key" name="key" /></td>
			<td><input type="text" id="value" name="value" /></td></tr>
			</tbody>
		</table>
		<input type="hidden" name="dsCollection" value="<%=policyName %>"/>
		<input type="hidden" name="ds" value="<%=productTypeName %>"/>
		<input type="hidden" name="step" value="displayDatasetMetadata"/>
		<input type="hidden" name="action" value="NewKey" /><br>
		<input type="submit" id="newKeyButton" value="Create new key" />
	</form>
</div>
