package gov.nasa.jpl.edrn.ecas.curation.servlet;

import gov.nasa.jpl.edrn.ecas.curation.policymgr.CurationPolicyManager;
import gov.nasa.jpl.edrn.security.SingleSignOn;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// Displays the contents of the staging area as an HTML unordered list (<ul></ul>): 
// This servlet is meant to be invoked via an AJAX call, the generated HTML output
// will be dynamically inserted into a DOM element on the requesting page.
//

public class ShowStagingAreaServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9161865618189131978L;

	public ShowStagingAreaServlet() { }

	public void init(ServletConfig conf) throws ServletException {
    	super.init(conf);
	}
	
	
	// hardcode for tumor 
	private final String staging_area = "/data/ingest";
	
	public void doPost (HttpServletRequest req, HttpServletResponse res) 
	  throws ServletException, IOException {
	  
    HttpSession session = req.getSession();
    session.setAttribute("errorMsg","You must use GET to access this page");
    RequestDispatcher dispatcher = 
      getServletContext().getRequestDispatcher("/error.jsp");
    dispatcher.forward(req,res);
	}

	// Handle HTTP GET requests by forwarding to a common processor
	public void doGet(HttpServletRequest req, HttpServletResponse res) 
	  throws ServletException, IOException {
    
    
    // Redirect if no valid user logged in
    SingleSignOn auth = new SingleSignOn(res, req);
    if (! auth.isLoggedIn()) {
      res.sendRedirect("/login.jsp?from=" + req.getRequestURL());
      return;
    }
    String root = req.getParameter("root");
    PrintWriter out = res.getWriter();

    // we return with blank page with error
    if (root == null) {
      out.println("");
      return;
    }
    
    CurationPolicyManager pm = new CurationPolicyManager(staging_area);
    out.println(pm.htmlGetStagingArea(root));
    return;
	}
}