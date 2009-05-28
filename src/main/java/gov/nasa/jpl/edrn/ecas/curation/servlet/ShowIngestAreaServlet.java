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

// Displays the contents of the ingest area as an HTML unordered list (<ul></ul>): 
// This servlet is meant to be invoked via an AJAX call, the generated HTML output
// will be dynamically inserted into a DOM element on the requesting page.
//

public class ShowIngestAreaServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5972805576883827168L;

	public ShowIngestAreaServlet() { }

	public void init(ServletConfig conf) throws ServletException {
    	super.init(conf);
	}
	
	// hardcode for tumor 
	private final String ingest_area = "/data/archive";
	
	public void doPost (HttpServletRequest req, HttpServletResponse res) 
	  throws ServletException, IOException {

		// Redirect if no valid user logged in
		SingleSignOn auth = new SingleSignOn(res, req);
		if (! auth.isLoggedIn()) {
			res.sendRedirect("/login.jsp?from=" + req.getRequestURL());
			return;
		}
		
		String target = req.getParameter("target");
		PrintWriter out = res.getWriter();

		// we return with blank page with error
		if (target == null) {
			out.println("");
			return;
		}
		
		CurationPolicyManager pm = new CurationPolicyManager(ingest_area);
		out.println(pm.htmlGetIngestArea(target));
		
		return;
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