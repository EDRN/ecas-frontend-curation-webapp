/* 
 *  Copyright (c) 2009, California Institute of Technology. 
 *  ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
 * 
 *  Author: Andrew Clark
 */
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

// JTidy utility class
import gov.nasa.jpl.edrn.ecas.curation.util.HTMLEncode;

/**
 * This servlet processes form data submitted via POST from the dataset metadata
 * management tool under development for the eCAS Curator.
 * 
 * Product type metadata values can be edited or replaced from the web
 * interface. Form values are saved into their corresponding fields in a
 * CasProductType instance.
 * 
 * @author aclark
 * 
 */
public class UpdateDatasetMetaDataServlet extends HttpServlet {
    private Hashtable<String, CasProductType> metaDataItems;

    public UpdateDatasetMetaDataServlet() {
    }

    public void init(ServletConfig conf) throws ServletException {
        super.init(conf);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Redirect if no valid user logged in
        SingleSignOn auth = new SingleSignOn(res, req);
        if (!auth.isLoggedIn()) {
            res.sendRedirect("/login.jsp?from=" + req.getRequestURL());
            return;
        }

        HttpSession session = req.getSession();

        String policyName = req.getParameter("dsCollection");
        String productTypeName = req.getParameter("ds");
        String action = req.getParameter("action");
        String step = req.getParameter("step");

        // check for session timeout or missing parameter
        if (policyName == null || productTypeName == null || action == null
                || step == null) {
            res.sendRedirect(req.getContextPath() + "/home.jsp");
        }
        // ----------------------------------------
        // instantiate product type object
        CurationPolicyManager cpm = new CurationPolicyManager();
        metaDataItems = cpm.getProductTypeMetaData(policyName, productTypeName);

        // get product type metadata for this dataset
        CasProductType cpt = (CasProductType) metaDataItems
                .get(productTypeName);

        if (action != null) {
            String key = req.getParameter("key");
            String value = req.getParameter("value");

            if (action.toLowerCase().equals("newkey")) {
                if (key != null && !cpt.containsMetaDataKey(key)) {
                    if (value.equals(""))
                        cpt.setMetaDataValue(key, "TBD");
                    else
                        cpt.setMetaDataValue(key, value);
                } else {
                    // System.out.println("[debug]: key "+key+" already exists in metadata");
                }
            } else if (action.toLowerCase().equals("deletekey")) {
                if (key != null && cpt.containsMetaDataKey(key)) {
                    cpt.deleteMetaDataKey(key);
                    // System.out.println("[debug]: key "+key+" is deleted from metadata");
                } else {
                    // System.out.println("[debug]: no such key as "+key);
                }
            } else if (action.toLowerCase().equals("savekey")) {
                if (key != null && cpt.containsMetaDataKey(key)) {
                    cpt.setMetaDataValue(key, value);
                    // System.out.println("[debug]: value for key "+key+", "+value+", is updated in metadata");
                }
            } else {
                // System.out.println("in SaveAll now...");
                session.setAttribute("action", "SaveAll");

                // get all submitted form values
                Enumeration formKeys = req.getParameterNames();

                // update metadata value from POST form
                String keyName;
                while (formKeys.hasMoreElements()) {
                    String keyField = (String) formKeys.nextElement();
                    // extract the metadata id from the form field
                    String[] tokens = keyField.split("_");
                    if (tokens.length == 2 && "value".equals(tokens[0])
                            && cpt.containsMetaDataKey(tokens[1])) {
                        keyName = tokens[1];
                        // get the submitted values for this key
                        String[] formValues = req.getParameterValues(keyField);
                        // System.out.println(keyName + ", "+formValues[0]);
                        cpt.setMetaDataValue(keyName, formValues[0]);
                    }
                }
            }

            // build policy file path
            String policyDirectory = "/usr/local/ecas/filemgr/policy";
            String policyPath = policyDirectory;
            String policyFile = policyPath + "/" + policyName
                    + "/product-types.xml";

            // serialize all CasProductType instances from metaDataItems
            // hashtable
            PrintWriter pw = new PrintWriter(new File(policyFile));
            pw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            pw.write("\n");
            pw
                    .write("<cas:producttypes xmlns:cas=\"http://oodt.jpl.nasa.gov/1.0/cas\">\n");

            for (String s : metaDataItems.keySet()) {
                // generate XML string
                CasProductType tmpCpt = metaDataItems.get(s);
                String xmlString = tmpCpt.toXMLString();
                pw.write(xmlString);
            }
            pw.flush();
            pw.write("</cas:producttypes>\n");
            pw.close();
        }

        // Transfer control to the next step in the process
        if (req.getParameter("output").equals("json"))
            return;
        else
            res.sendRedirect(req.getContextPath() + "/manageDataset.jsp?step="
                    + step);
    }

    // Handle HTTP GET requests by forwarding to a common processor
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // call GET handler for POST request
        doPost(req, res);
        /*
         * HttpSession session = req.getSession();
         * session.setAttribute("errorMsg"
         * ,"You must use POST to access this page"); RequestDispatcher
         * dispatcher = getServletContext().getRequestDispatcher("/error.jsp");
         * dispatcher.forward(req,res);
         */
    }

}
