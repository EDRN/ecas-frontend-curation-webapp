<%@ page
	import="java.io.File"
	import="java.io.*"
	import="java.net.URL"
	import="java.net.MalformedURLException"
	import="java.util.List"
	import="java.util.ArrayList"
	import="java.util.Iterator"
	import="java.util.Map"
	import="java.util.Hashtable"
	import="java.util.Collections"
	import="java.util.Set"
	import="gov.nasa.jpl.edrn.ecas.curation.policymgr.CurationPolicyManager"
	import="gov.nasa.jpl.edrn.ecas.curation.policymgr.CasProductType"
	import="gov.nasa.jpl.edrn.ecas.curation.util.HTMLEncode"
%>

<%! 
	/**
	 *	getServices retrieves a list of translation services available
	 *	from ecas-services and checks incoming metadata items for 
	 * 	any fields with ID values that can be mapped to names using
	 *	a correspoding service. 
	 *
	 *	@param URL The path to the services.txt file containing 
	 *				the eCAS service to URI definitions.
	 *	@return	A hashtable mapping service names (field names) 
	 *			to the corresponding service module offered through
	 *			ecas-services.
	 */
	public static Hashtable<String, String> getServices(String URL) {
    	Hashtable<String, String> serviceRegistry = new Hashtable<String, String>();
		try {
	    	URL serviceFile = new URL(URL);
	    	BufferedReader br = new BufferedReader(
	    	        			new InputStreamReader(
	    	        			 serviceFile.openStream()));
	    	String line = br.readLine();
			while (line != null) {
				if (!line.startsWith("#") && line.length()!=0) {
					String[] tokens = line.split("[|]");
					String serviceName = tokens[0];
	
					// split up the service URI and take
					// the last fragment '*.php'
					String [] URI = tokens[1].split("[/]");
					String completeURI = URI[1];			
	
					serviceRegistry.put(serviceName, completeURI);
					
				}
				line = br.readLine();	
			}
			br.close();
		}
		catch (Exception e) {
			e.printStackTrace();	
		}	
		return serviceRegistry;
}
%>
<script type="text/javascript" src="js/jquery/jquery.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	initial_translation();
	$("td.value.translatable > input:text").focus(do_focus_translation);	
	$("td.value.translatable > input:text").blur(do_blur_translation);	
});


function do_focus_translation() {
	myId = $(this).attr('id');
	fieldname = myId.split("_")[1];
	$('#'+myId).val($("#value_" + fieldname).val());
}

function do_blur_translation() {
	myId = $(this).attr('id');
	fieldname = myId.split("_")[1];
	newvalue  = $('#'+myId).val();
	$('#'+myId).val('validating...');
	//translate(fieldname, newvalue, lookup_uri(fieldname));
	translateAndSave(fieldname,newvalue,lookup_uri(fieldname));
}

function translateAndSave(fn, id, uri) {
	ds = '<%=session.getAttribute("ds") %>';
	dsCollection = '<%=session.getAttribute("dsCollection") %>';
	step = 'displayDatasetMetadata';
	action= 'savekey';
	output= 'json';

	key   = fn;
	value = id;

	$.post('updateDatasetMetaData',{
		'ds':ds,
		'dsCollection':dsCollection,
		'step'   : step,
		'action' : action,
		'key'    : key,
		'value'  : value,
		'output' : output}, function() {
			translate(fn, id, uri);
		});
	
}
	
function translate(fn, id, uri) {
	//resource = 'manageDataset.jsp?step=getTranslation&id='+id+'&uri='+uri;
	resource = 'views/manageDataset/getTranslation.jsp?id='+id+'&uri='+uri;
	$.get(resource,  function(data) {
		  		$("input#scratch_" + fn).val(data);
		  		$("span#value_" + fn + "_translated").html(data);
		  		$("input#value_" + fn).val(id);
		});
}	

function confirmDelete(keyName) {
	var mesg = "Delete the "+keyName+" field from Metadata?";
	return confirm(mesg);
}
</script>

<div class="wizardContent">
	<h4>Dataset Metadata For: <%=session.getAttribute("dsCollection") %> / <%=session.getAttribute("ds") %></h4>
	<div>
		<form action="updateDatasetMetaData" method="post"> 
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
				
				// Hashtable storing translation service names and URIs			
				Hashtable<String,String> serviceRegistry = new Hashtable();
				
				// list to hold sorted field names, to enforce the order
				// in which they are displayed in the editor
				List<String> sortedKeys = new ArrayList<String>();
				
				//System.out.println("[debug]: metaData table size: " + metaData.size());

				String tableHeader = "<thead><tr><th id=\"tableHeaderKey\">Key</th><th id=\"tableHeaderValue\">Value</th></thead><tbody>";
				String tableClose = "\t\t</tbody></table>";			
				
				if (metaData.size() > 0) {
				    // count/classify even and odd numbered rows in the 
				    // results table for style sheet purposes
				    int even_row = 0;
				    
				    out.println(tableHeader);
				    
				    // Sort the metadata key names alphabetically
				    sortedKeys = new ArrayList<String>(metaData.keySet());
				    Collections.sort(sortedKeys);
				    
				    // Get service/URI listing 
					serviceRegistry = this.getServices("http://tumor.jpl.nasa.gov/ecas/services/services.txt");
				    
				    for (Object k : sortedKeys) {
						String key = (String)k;
						String value = (String)metaData.get(k);
						String encodedValue = HTMLEncode.encode(value);
						
						String keyField = "\t\t\t\t<td class=\"label\">" + key + "</td>";
						
						String origValueField = "\t\t\t\t<td class=\"value\">\r\n";
						
						String valueField = new String(origValueField);
						
						// Set the form element for the metadata item value
						// to either a text input field or a textarea 
						// depending on the length of the string that
						// will populate it.
						if (value.length() < 100) {
						    valueField += "\t\t\t\t<input type=\"text\" id=\"value_" + key 
						    			  +	"\" name=\"value_" + key  
						    			  + "\" value=\"" + encodedValue 
										  + "\" />"; //+ deleteURL +"\r\n\t\t\t\t</td>";
						}
						else {
						    int cols = 80;
						    int rows = value.length() / cols;
						    valueField += "\t\t\t\t<textarea id=\"value_" + key + "\" "
						    			  + "name=\"value_" + key + "\" "
						    			  + "rows=\"" + rows + "\" cols=\"" + cols + "\">" 
						    			  + value + "</textarea>"; //+ deleteURL +"</td>";
						}

						String scratchField = "\t\t\t\t<input type=\"text\" id=\"scratch_" + key + "\""
							  + " name=\"scratch_"+ key + "\" value=\"" + encodedValue + "\" />";
		
						String spanField = "\t\t\t\t<span id=\"value_"+ key +"_translated\">" + "" + "</span>";
		
						String hiddenValueField = "\t\t\t\t<input type=\"hidden\"" + "id=\"value_" + key +"\""
								  + " name=\"value_" + key + "\"" 
								  + " value=\""+ encodedValue +"\" />";
		
						String deleteURL = "<a href=\"updateDatasetMetaData?action=deleteKey"
		        			+ "&dsCollection=" + policyName 
		        			+ "&ds=" + productTypeName 
		        			+ "&key=" + key
		        			+ "&step=displayDatasetMetadata\""
		        			+ " id=\"delete_" + key + "\""	
						    + " onclick=\"return confirmDelete('"+ key +"')\"" 
						    + " class=\"deleteURL\">delete</a>";						
						
						out.print("\t\t<tr class=\"metadata ");
						
						// set even/odd row class for styling
						if (even_row==0) 
							out.print("odd\" ");
						else 
							out.print("even\" ");
						out.print("id=\"met_" + key + "\">\r\n");
						
						out.println(keyField);				
					    // add translation service elements if 
					    // the service registry has a web service
					    // defined for the current metadata type.
						if (serviceRegistry.size() > 0 && serviceRegistry.containsKey(key)) {
						    out.println("<td class=\"value translatable\">");
							out.println(scratchField);
							out.println(spanField);
							out.println(hiddenValueField);
						}
						else {
						    out.println(valueField);
						}
						 
						out.println(deleteURL);
						out.println("</td>");
						out.println("\t\t</tr>");
						
						// switch even/odd row 
						even_row = 1 - even_row;
					}
				} else {
				    out.println("<thead></thead><tbody>");
					out.println("\t\t<tr class=\"odd\"><td>No metadata values defined.");
					out.println("\t\t</td></tr>");
				}			
				out.println(tableClose);

				// generate service translation javascript calls in
			    // <script></script> block
			    out.println("<script type=\"text/javascript\">");
			    out.println("function initial_translation() {");
			    // initial translation 
			    for (Object o : sortedKeys) {
			        String fieldname = (String)o;
			        String id = null;
			        String uri = null;
			        if (serviceRegistry.containsKey(fieldname)) {
			            id = (String)metaData.get(fieldname);
			            uri = (String)serviceRegistry.get(fieldname);
			            out.println("translate('"+fieldname+"','"+id+"','"+uri+"');");
			        }
			    }
			    out.println("}");
			    // URI lookup
			    out.println("function lookup_uri(fieldname) {");
			    out.println("switch (fieldname) {");
			    for (Object o: serviceRegistry.keySet()) {
					String fieldname = (String)o;
			        String uri = (String)serviceRegistry.get(o);
			        out.println("case \"" + fieldname + "\": return \""+ uri +"\"; break;");
			    }
			    out.println("default: break; }");
			    out.println("} </script>");					
				
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
