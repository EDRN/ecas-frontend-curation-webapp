package gov.nasa.jpl.edrn.ecas.curation.servlet;

//EDRN imports
import gov.nasa.jpl.edrn.security.SingleSignOn;

//OODT imports
import gov.nasa.jpl.oodt.cas.crawl.MetExtractorProductCrawler;
import gov.nasa.jpl.oodt.cas.metadata.exceptions.MetExtractionException;

//JDK imports
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.support.FileSystemXmlApplicationContext;

// Handles the form (POST) submission made from: 
// IngestData > Create new dataset > provideDatasetDefinitionFiles
// Redirects to:
// IngestData > Create new dataset > 
public class BeginIngestionTaskServlet extends HttpServlet {

  /**
	 * 
	 */
  private static final long serialVersionUID = -4607282147807134757L;

  public BeginIngestionTaskServlet() {
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

    // TODO: Take the parameters from the POST variables and set the crawlers in
    // motion
    // 
    // POST Variables to expect:
    //
    // dsCollection - data set collection (Grizzle, Aliu, etc)
    // ds - data set (EGFRTranslocation, PreInvasiveNeoplasia, etc)
    // metext - CopyOnRewrite, etc
    // ingestActionUnique - crawler pre-ingest action
    // ingestActionDeleteDataFile - crawler post-ingest action
    // ingestionRootPath - prepend /data/ingest to this for full path
    //
    //

    MetExtractorProductCrawler ecasCrawler = this
        .configureEDRNCrawler(getServletContext().getInitParameter(
            "gov.nasa.jpl.edrn.ecas.url"));
    ecasCrawler.setActionIds(Arrays.asList(req
        .getParameterValues("ingestAction")));
    String productRootPath = "/data/ingest/"
        + req.getParameter("ingestionRootPath");
    ecasCrawler.setProductPath(productRootPath);
    try {
      ecasCrawler.setMetExtractor(req.getParameter("metext"));
    } catch (Exception e) {
      throw new ServletException(e.getMessage());
    }

    try {
		ecasCrawler
			.setMetExtractorConfig(req.getParameter("metextConfigFilePath"));
	} catch (MetExtractionException e) {
		throw new ServletException(e.getMessage());
	}

    // turn em' loose
    ecasCrawler.crawl();
    
    // now we should explicitly destroy various sessions for cleanup
    HttpSession session = req.getSession();
    session.removeAttribute("metextPrettyName");
    session.removeAttribute("metext");
    session.removeAttribute("metextConfigFilePath");

    // Transfer control to the next step in the process
    res.sendRedirect(req.getContextPath() + "/home.jsp");
  }

  // Handle HTTP GET requests by forwarding to a common processor
  public void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    HttpSession session = req.getSession();
    session.setAttribute("errorMsg", "You must use POST to access this page");
    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(
        "/error.jsp");
    dispatcher.forward(req, res);
  }

  private MetExtractorProductCrawler configureEDRNCrawler(String ecasUrlStr)
      throws MalformedURLException {
    MetExtractorProductCrawler crawler = new MetExtractorProductCrawler();
    crawler
        .setClientTransferer("gov.nasa.jpl.oodt.cas.filemgr.datatransfer.LocalDataTransferFactory");
    crawler.setApplicationContext(new FileSystemXmlApplicationContext("classpath:/gov/nasa/jpl/oodt/cas/crawl/crawler-config.xml"));
    crawler.setCrawlForDirs(false);
    crawler.setFilemgrUrl(ecasUrlStr);
    crawler.setNoRecur(false);
    return crawler;
  }
}