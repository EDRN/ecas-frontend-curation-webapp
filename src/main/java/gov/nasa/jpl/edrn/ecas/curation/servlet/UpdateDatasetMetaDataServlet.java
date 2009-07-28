package gov.nasa.jpl.edrn.ecas.curation.servlet;

import gov.nasa.jpl.edrn.ecas.curation.policymgr.CurationPolicyManager;
import gov.nasa.jpl.edrn.ecas.curation.policymgr.CasProductType;
import gov.nasa.jpl.edrn.security.SingleSignOn;

import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UpdateDatasetMetaDataServlet extends HttpServlet {
	
	public UpdateDatasetMetaDataServlet() { } 
	
	public void init(ServletConfig conf) throws ServletException {
    	super.init(conf);
	} 
	
	public void doPost (HttpServletRequest req, HttpServletResponse res) 
	  throws ServletException, IOException {

		// Redirect if no valid user logged in
		SingleSignOn auth = new SingleSignOn(res, req);
		if (! auth.isLoggedIn()) {
			res.sendRedirect("/login.jsp?from=" + req.getRequestURL());
			return;
		}
		HttpSession session = req.getSession();
		String action = req.getParameter("action");
		String step = req.getParameter("step");
		
		String policyName = req.getParameter("dsCollection");
		String productTypeName = req.getParameter("ds");
				
		// print a status message to stdout for now
//		System.out.println("[debug]: POST processing method reached in UpdateDatasetMetaDataServlet");
//		System.out.println("[debug]: current context path " + req.getContextPath());

		// instantiate product type object 
		CurationPolicyManager cpm = new CurationPolicyManager();
		Hashtable metaDataItems = cpm.getProductTypeMetaData(policyName, productTypeName);
		
		// get metadata hash table
		CasProductType cpt = (CasProductType) metaDataItems.get(productTypeName);
		Hashtable metaData = new Hashtable(cpt.metadata);		
		
		// get all submitted form values
		Enumeration formKeys = req.getParameterNames();
		
		// update metadata values from form fields
		String keyName;
	    while (formKeys.hasMoreElements()) {
	      String keyField = (String)formKeys.nextElement();

	      // extract the part we want, the metadata id
	      String [] tokens = keyField.split("_");
	      if (tokens.length == 2 && metaData.containsKey(tokens[1])) {
	    	  keyName = tokens[1];
		      //System.out.print("key: "+ keyName);
	      
			  // get the submitted values for that key
		      String [] formValues = req.getParameterValues(keyField);
			  // save new value
		      metaData.put(keyName, formValues[0]);
	      }
	     }
		
	    // build path policy file
		String policyDirectory = "/usr/local/ecas/filemgr/policy";
		String policyPath = policyDirectory;
		String policyFile = policyPath + "/" + policyName + "/product-types.xml";
	    
		// generate XML string, write to file.
		String xmlString = cpt.toXMLString();
		
		PrintWriter pw = new PrintWriter(new File(policyFile));
        pw.write(xmlString);
        pw.flush();
        pw.close();
        
		// Transfer control to the next step in the process
		res.sendRedirect(req.getContextPath() + "/manageDataset.jsp?step=" + step);
	}

	// Handle HTTP GET requests by forwarding to a common processor
	public void doGet(HttpServletRequest req, HttpServletResponse res) 
	  throws ServletException, IOException {
		HttpSession session = req.getSession();
		session.setAttribute("errorMsg","You must use POST to access this page");
		RequestDispatcher dispatcher = 
			getServletContext().getRequestDispatcher("/error.jsp");
		dispatcher.forward(req,res);
	}
	
	
}
