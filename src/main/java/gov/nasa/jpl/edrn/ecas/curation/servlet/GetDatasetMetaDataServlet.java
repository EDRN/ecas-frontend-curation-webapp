package gov.nasa.jpl.edrn.ecas.curation.servlet;

import gov.nasa.jpl.edrn.ecas.curation.policymgr.CurationPolicyManager;
import gov.nasa.jpl.edrn.ecas.curation.policymgr.CasProductType;
import gov.nasa.jpl.edrn.security.SingleSignOn;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Hashtable;

/**
 * Responds to GET requests sent from the eCAS Curator frontend to 
 * return a set of metadata key-value pairs for the requested 
 * dataset and dataset collection in either JSON or XML format. 
 * 
 * @author aclark
 *
 */
public class GetDatasetMetaDataServlet extends HttpServlet {
	// Need Serial UID
	
	public GetDatasetMetaDataServlet() { } 
	
	public void init(ServletConfig conf) throws ServletException {
    	super.init(conf);
	} 
	
	/** 
	 * POST requests are not supported but will be passed
	 * to the GET handler unofficially.
	 */
	public void doPost (HttpServletRequest req, HttpServletResponse res) 
	  throws ServletException, IOException {
		// call GET handler for POST request
		doGet(req, res);
	}
		
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	  throws ServletException, IOException {
		
		// Redirect if no valid user logged in
		SingleSignOn auth = new SingleSignOn(res, req);
		if (! auth.isLoggedIn()) {
			res.sendRedirect("/login.jsp?from=" + req.getRequestURL());
			return;
		}
		HttpSession session = req.getSession();

		// read API parameters from session
		String policyName = req.getParameter("dsCollection");
		String productTypeName = req.getParameter("ds");
		String returnFormat = req.getParameter("format");
		String step = req.getParameter("step");

		// instantiate product type object 
		CurationPolicyManager cpm = new CurationPolicyManager();
		Hashtable<String, CasProductType> metaDataItems = cpm.getProductTypeMetaData(policyName, productTypeName);
		
		CasProductType cpt = (CasProductType) metaDataItems.get(productTypeName);		
		
		PrintWriter out = new PrintWriter(res.getWriter());

		// check return format
		if ("json".equals(returnFormat.toLowerCase())) {
			res.setContentType("application/json");
			out.println(getProductTypeJSONString(cpt));
		}
		
		if ("xml".equals(returnFormat.toLowerCase())) {
			res.setContentType("text/xml");
			//XMLUtils.writeXmlToStream(cpt.toXMLDocument(), res.getOutputStream());
			out.println(getProductTypeXMLString(cpt));
		}
	}

	/**
	 * Gets a String in XML format containing the metdata items for the
	 * Product Type passed as an argument.
	 * 
	 * @param c	The CasProductType containing metadata items.
	 * @return	A String containing the XML structure of metadata key-value 
	 * 			pairs from the CasProductType.
	 */
	private static String getProductTypeXMLString(CasProductType c) { 
		// retrieve the XML formatted string for this product type branch
		// XML fragment does not have top level cas:producttype tags or
		// XML DTD.
		return c.metaDataToXMLString(); 
	}
	
	/**
	 * Get a String in JSON format containing the metadata items
	 * for the current Product Type.
	 * 
	 * @param c	The CasProductType object containing metadata to be
	 * 			converted to JSON output.
	 * @return	A JSON representation of all the dataset metadata 
	 * 			key-value pairs.
	 */
	private static String getProductTypeJSONString(CasProductType c) {
		// retrieve the JSON formatted metaData string
		return c.metaDataToJSON();
	}
	
}
