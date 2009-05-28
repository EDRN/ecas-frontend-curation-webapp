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
public class UploadPolicyServlet extends HttpServlet {

	public UploadPolicyServlet() { }

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
		
		// Transfer control to the next step in the process
		res.sendRedirect(req.getContextPath() + "/ingestData.jsp?step=choosePolicy");
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
	private static final String STORAGE_LOCATION = "/"; /* will be STORAGE_LOCATION + /policy/ + filename */
	private static final String TEMP_REPOSITORY = "/tmp/";

	private void processRequest(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			DiskFileItemFactory factory = new DiskFileItemFactory();

			factory.setSizeThreshold(10000);
			factory.setRepository(new File(TEMP_REPOSITORY));

			ServletFileUpload upload = new ServletFileUpload(factory);

			String target = "";
			
			List<FileItem> items = upload.parseRequest(request);

			for (FileItem item : items) {
				if (item.isFormField()) {
					String name = item.getFieldName();
					String value = item.getString();
					if (name.equals("target"))
						target = value;
				}
			}
			
			for (FileItem item : items) {
				if (!item.isFormField()) {
					InputStream is = item.getInputStream();
					String filename = STORAGE_LOCATION + "/" + target + "/" + item.getName();
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}