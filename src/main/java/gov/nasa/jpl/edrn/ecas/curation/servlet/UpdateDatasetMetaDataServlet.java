package gov.nasa.jpl.edrn.ecas.curation.servlet;

import gov.nasa.jpl.edrn.ecas.backend.EDRNFileManagerClient;
import gov.nasa.jpl.edrn.ecas.curation.policymgr.CurationPolicyManager;
import gov.nasa.jpl.edrn.ecas.curation.policymgr.CasProductType;
import gov.nasa.jpl.edrn.security.SingleSignOn;

import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// JTidy utility class
import gov.nasa.jpl.edrn.ecas.curation.util.HTMLEncode;
import gov.nasa.jpl.oodt.cas.filemgr.structs.exceptions.ConnectionException;
import gov.nasa.jpl.oodt.cas.filemgr.structs.exceptions.RepositoryManagerException;
import gov.nasa.jpl.oodt.cas.metadata.util.PathUtils;

/**
 * This servlet processes form data submitted via
 * POST from the dataset metadata management tool
 * under development for the eCAS Curator. 
 * 
 * Product type metadata values can be edited or
 * replaced from the web interface. Form values are
 * saved into their corresponding fields in a CasProductType 
 * instance for the current product type.
 * 
 * @author aclark
 *
 */
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

		// read API parameters from session
		String action = req.getParameter("action");
		String step = req.getParameter("step");
		String policyName = req.getParameter("dsCollection");
		String productTypeName = req.getParameter("ds");
				
		// debug messages go to stdout
//		System.out.println("[debug]: POST processing method reached in UpdateDatasetMetaDataServlet");
//		System.out.println("[debug]: current context path " + req.getContextPath());

		// instantiate product type object 
		CurationPolicyManager cpm = new CurationPolicyManager();
		Hashtable<String, CasProductType> metaDataItems = cpm.getProductTypeMetaData(policyName, productTypeName);
		
		// get metadata hash table
		CasProductType cpt = (CasProductType) metaDataItems.get(productTypeName);
		
		// get all submitted form values
		Enumeration formKeys = req.getParameterNames();
		
		// update metadata value from POST form
		String keyName;
	    while (formKeys.hasMoreElements()) {
	      String keyField = (String)formKeys.nextElement();

	      // extract the metadata id from the form field
	      String [] tokens = keyField.split("_");
	      if (tokens.length == 2 && cpt.containsMetaDataKey(tokens[1])) {
	    	  keyName = tokens[1];
			  // get the submitted values for that key
		      String [] formValues = req.getParameterValues(keyField);

		      // save new value	
		      // PubMedID requires HTML entity encoding because 
		      // of hyperlinks in the metadata field.
		      if (keyName.equals("PubMedID")) {
	    		  cpt.setMetaDataValue(keyName, HTMLEncode.encode(formValues[0]));
		      }
		      else
		    	  cpt.setMetaDataValue(keyName, formValues[0]);
	      }
	    }
	    
	    // build policy file path
		String policyDirectory = "/usr/local/ecas/filemgr/policy";
		String policyPath = policyDirectory;
		String policyFile = policyPath + "/" + policyName + "/product-types.xml";
		
		// serialize all CasProductType instances from metaDataItems hashtable
		PrintWriter pw = new PrintWriter(new File(policyFile));
		pw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		pw.write("\n");
		pw.write("<cas:producttypes xmlns:cas=\"http://oodt.jpl.nasa.gov/1.0/cas\">\n");

		for ( String s : metaDataItems.keySet()) {
			// generate XML string
			CasProductType tmpCpt = metaDataItems.get(s); 
			String xmlString = tmpCpt.toXMLString();
			pw.write(xmlString);
		}
		pw.flush();
		pw.write("</cas:producttypes>\n");
		pw.close();
        
		// when we update the metadata we need to bounce the FM so that the browser 
		// knows about it
		
		// need to update properties here...
		
		String pathKeyName = "gov.nasa.jpl.edrn.ecas.url";
		String filemgrURL = PathUtils.replaceEnvVariables(getServletContext().getInitParameter(pathKeyName));
		
	    // bounce filemanager here
		
	    URL url = new URL(filemgrURL);
	    try {
			EDRNFileManagerClient FmClient = new EDRNFileManagerClient(url);
			FmClient.bounce();
		} catch (ConnectionException e) {
			e.printStackTrace();
		} catch (RepositoryManagerException e) {
			e.printStackTrace();
		}
		
		
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
