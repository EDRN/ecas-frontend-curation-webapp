<%@ page
	import="java.io.File"
	import="java.io.BufferedReader"
	import="java.io.InputStreamReader"
	import="java.net.URL"
%>
<%
	/*
	 *
	 * Simple behind-the-scenes translation service to query
	 * ecas-services on tumor and pass along the translated 
	 * return value from the corresponding service to the 
	 * eCAS Curator frontend. 
	 *
	 * This is a workaround for the problem of trying 
	 * to make a cross-site GET/POST request with jQuery 
	 * from the curator app to ecas-services.
	 *
	 * Author: ahart, aclark
	 */
	 
	 /*
	  *	getTranslation requires two parameters be passed 
	  *	in a GET request:
	  *		id : the ID value to be translated (a ProtocolID, SiteNameID, etc)
	  *		uri: the name of the service to be invoked from 
	  *			 ecas-services, e.g. 'protocols.php'. The 
	  *			 full URL to the service will be formed 
	  *			 by getTranslation, just pass the name of
	  *			 the actual module for now.
	  *
	  */
	String id = (String)request.getParameter("id");
	String uri = (String)request.getParameter("uri");
	
	try {
		URL serviceCall = new URL("http://tumor.jpl.nasa.gov/ecas-services/" 
		        				   + uri + "?id=" + id);
	    BufferedReader br = new BufferedReader(
    	        			new InputStreamReader(
    	        			 serviceCall.openStream()));
	    
    	String line = br.readLine();
		if (line != null && line.trim().length()!=0) {
			out.print(line.trim());	
		}
		br.close();
	}
	catch (Exception e) {
		e.printStackTrace();	
	}	
%>