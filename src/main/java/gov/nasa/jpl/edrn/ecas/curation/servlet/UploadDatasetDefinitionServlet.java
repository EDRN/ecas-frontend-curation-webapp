package gov.nasa.jpl.edrn.ecas.curation.servlet;

import gov.nasa.jpl.edrn.ecas.backend.EDRNFileManagerClient;
import gov.nasa.jpl.edrn.security.SingleSignOn;
import gov.nasa.jpl.oodt.cas.filemgr.structs.exceptions.ConnectionException;
import gov.nasa.jpl.oodt.cas.filemgr.structs.exceptions.RepositoryManagerException;
import gov.nasa.jpl.oodt.cas.metadata.util.PathUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

// Handles the form (POST) submission made from: 
// IngestData > Create new dataset > provideDatasetDefinitionFiles
// Redirects to:
// IngestData > Create new dataset > 
public class UploadDatasetDefinitionServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4607282147807134757L;

	public UploadDatasetDefinitionServlet() { }

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

		processRequest(req,res);

		// Transfer control to the next step in the process
		res.sendRedirect(req.getContextPath() + "/ingestData.jsp");
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
	
	private static final int BYTE_READ_SIZE = 200000;
	private static final int BYTE_THRESHOLD = 10000;
	private static final String TEMP_REPOSITORY = "/tmp/";

	private void processRequest(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			DiskFileItemFactory factory = new DiskFileItemFactory();

			factory.setSizeThreshold(BYTE_THRESHOLD);
			factory.setRepository(new File(TEMP_REPOSITORY));

			ServletFileUpload upload = new ServletFileUpload(factory);

			String pathKeyName = "gov.nasa.jpl.edrn.ecas.dataDefinition.uploadPath";
			String targetPath = PathUtils.replaceEnvVariables(getServletContext().getInitParameter(pathKeyName));
			String policyName = "undefined";
			
			List<FileItem> items = upload.parseRequest(request);

			for (FileItem item : items) {
				if (item.isFormField()) {
					String name = item.getFieldName();
					String value = item.getString();
					if (name.equals("policyName"))
						policyName = value;
				}
			}
			
			// make sure that the target (policy) directory exists first 
			try {
				String policyDirectory = targetPath + "/" + policyName + "/";
				boolean success = (new File(policyDirectory)).mkdirs();
				if (!success) {
					return; // no need to do anything if policy dir cannot be created
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			for (FileItem item : items) {
				if (!item.isFormField()) {
					InputStream is = item.getInputStream();
					String filename = targetPath + "/" + policyName + "/" + item.getName();
					
					System.out.println("[debug]: writing filename: " + filename);
					
					FileOutputStream os = new FileOutputStream(new File(filename));
					byte[] b = new byte[BYTE_READ_SIZE];
					int bytesRead = 0;
					while ((bytesRead = is.read(b)) != -1) {
						os.write(b, 0, bytesRead);
					}

					os.flush();
					os.close();
					is.close();
				} 
			}
			
			// need to update properties here...
			
			pathKeyName = "gov.nasa.jpl.edrn.ecas.url";
			String filemgrURL = PathUtils.replaceEnvVariables(getServletContext().getInitParameter(pathKeyName));
			
			System.out.println("[debug]: Getting ready to bounce: " + filemgrURL);
			System.out.println("[debug]: policyName: " + policyName);
			
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
			
			System.out.println("[debug]: bounce has completed!");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}