<%@ page
	import="java.io.File"
	import="java.util.List"
	import="java.util.ArrayList"
	import="java.util.Iterator"
	import="java.util.Map"
	import="java.util.Hashtable"
	import="gov.nasa.jpl.edrn.ecas.curation.policymgr.CurationPolicyManager"
	import="gov.nasa.jpl.edrn.ecas.curation.policymgr.CasProductType"
%>

<%!
	/* 
	A simple function to convert special characters in a string
	to their HTML entity codes, so the text fields are displayed
	properly in the eCAS metata management tool.
	
	Accepts one parameter, the original plain text string.
	Returns the converted plain text string.
	*/
	public String text2HtmlEntity(String s) {
		char [] orig = s.toCharArray();
		StringBuffer result = new StringBuffer();
		
		for (int i=0; i < orig.length; i++) {
			if (orig[i] == '"')
				result.append("&#34");
			else if (orig[i] == '\'')
				result.append("&#39");
			else if (orig[i] == '&')
				result.append("&#38");
			else if (orig[i] == '<')
				result.append("&#60");
			else if (orig[i] == '>')
				result.append("&#62");
			else if (orig[i] == '%')
				result.append("$#37");
			else
				result.append(orig[i]);
		}
		return result.toString();	
	}
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
			
			Hashtable metaDataItems = cpm.getProductTypeMetaData(policyName, productTypeName);
		
			CasProductType cpt = (CasProductType) metaDataItems.get(productTypeName);
			
			Hashtable metaData = new Hashtable(cpt.metadata);
		
			/*
			String tableOpen = "<table id=\"metadataEditor\">";
			String tableClose = "</table>";			
			String tableHeader = "\t\t\t<thead><tr><th>Key</th><th>Value</th></thead>";
			
			out.println(tableOpen);
			out.println(tableHeader);
			*/
			if (metaData.size() > 0) {		
				int even_row = 1;
				for (Object k : metaData.keySet()) {
					String key = (String)k;
					String value = text2HtmlEntity((String)metaData.get(k));
							
					String keyField = "\t\t\t\t<td class=\"key\">" + key + "</td>";
					
					String valueField = "\t\t\t\t<td class=\"value\"><input type=\"text\" id=\"value_" + key +
										"\" name=\"value_" + key + "\" value=\"" + value + "\" /></td>";
						
					out.print("\t\t\t<tr class=\"");
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
			/*
			out.println(tableClose);
			out.println("<input type=\"hidden\" name=\"action\" value=\"SAVEALL\"/>");
			out.println("<br><input type=\"submit\" id=\"submitButton\" value=\"Save changes\"/>");
			out.println("</form>");	
			*/
		%> 
		</tbody></table>
		<input type="hidden" name="dsCollection" value="<%=session.getAttribute("dsCollection") %>" />
		<input type="hidden" name="ds" value="<%=session.getAttribute("ds") %>" />
		<input type="hidden" name="step" value="displayDatasetMetadata"/>
		<input type="hidden" name="action" value="SAVEALL"/>
		<input type="submit" id="submitButton" value="Save changes"/>
		</form>
	</div>
</div>