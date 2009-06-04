package gov.nasa.jpl.edrn.ecas.curation.servlet;

//EDRN imports
import gov.nasa.jpl.edrn.security.SingleSignOn;

//OODT imports
import gov.nasa.jpl.oodt.cas.metadata.util.PathUtils;

//JDK imports
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//APACHE imports
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

// Handles the form (POST) submission made from: 
// IngestData > Create new dataset > provideDatasetDefinitionFiles
// Redirects to:
// IngestData > Create new dataset > 
public class UploadMetExtractorConfigServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UploadMetExtractorConfigServlet() {
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

		// Call the upload method
		processRequest(req, res);

		// Transfer control to the next step in the process
		res.sendRedirect(req.getContextPath()
				+ "/addData.jsp?step=specifyIngestOptions");
	}

	// Handle HTTP GET requests by forwarding to a common processor
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		session.setAttribute("errorMsg",
				"You must use POST to access this page");
		RequestDispatcher dispatcher = getServletContext()
				.getRequestDispatcher("/error.jsp");
		dispatcher.forward(req, res);
	}

	private static final int BYTE_THRESHOLD = 10000;
	private static final String TEMP_REPOSITORY = "/tmp/";

	private void processRequest(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			DiskFileItemFactory factory = new DiskFileItemFactory();

			factory.setSizeThreshold(BYTE_THRESHOLD);
			factory.setRepository(new File(TEMP_REPOSITORY));

			ServletFileUpload upload = new ServletFileUpload(factory);

			List<FileItem> items = upload.parseRequest(request);

			String pathKeyName = "gov.nasa.jpl.edrn.ecas.metExtractorConf.uploadPath";
			String targetPath = PathUtils
					.replaceEnvVariables(getServletContext().getInitParameter(
							pathKeyName));

			HttpSession session = request.getSession();
			session.removeAttribute("metextConfigFilePath");
			
			// make sure that the target (policy) directory exists first 
			
			try {
				String policyDirectory = targetPath;
				File d = new File(policyDirectory);
				
				if (!d.isDirectory()) {
					System.out.println("[debug] mkdir '" + targetPath + "'");
					boolean success = (new File(policyDirectory)).mkdirs();
					if (!success) {
						return; // no need to do anything if policy dir cannot be created
					}
				} else {
					System.out.println("[debug] directory '" + targetPath + "' exists");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			for (FileItem item : items) {
				if (!item.isFormField()) {
					String fullFilePath = targetPath + "/" + item.getName();
					item.write(new File(fullFilePath));

					// only set this if it hasn't been set yet
					// assumption: the first conf file is the gold source
					if (session.getAttribute("metextConfigFilePath") == null) {
						session.setAttribute("metextConfigFilePath",
								fullFilePath);
					}

				} else {
					// it's a simple field, check if it's name is metext, and if
					// so
					// get and set everything

					if (item.getFieldName().equals("metext")) {
						// Store the type of metadata extractor in the session
						// Get the full name for use by the cas
						String metextFullName = item.getString();
						session.setAttribute("metext", metextFullName);
						// Get the pretty name for display on the browser
						session
								.setAttribute("metextPrettyName",
										metextFullName.substring(metextFullName
												.lastIndexOf(".") + 1));
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}