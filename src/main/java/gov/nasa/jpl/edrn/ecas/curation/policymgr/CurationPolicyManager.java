package gov.nasa.jpl.edrn.ecas.curation.policymgr;

//JDK imports
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.util.Arrays;

//APACHE imports
import org.apache.commons.lang.StringUtils;

public class CurationPolicyManager {

	private String rootDirectory = "/";
	
	/* this is a little obnoxious but we'll fix later */
	private final String policyDirectory = "/usr/local/ecas/filemgr/policy";
	
	/* this is a little obnoxious but we'll fix later */

	public CurationPolicyManager() {
		setRootDirectory("/usr/local/ecas"); 
	}

	public CurationPolicyManager(String root) {
		setRootDirectory(root);
	}

	public void setRootDirectory(String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	public String getRootDirectory() {
		return rootDirectory;
	}
	
	public String htmlGetStagingArea(String directory) {
	  
	  String startingPath = (rootDirectory + "/" + directory);
	  startingPath = StringUtils.replace(startingPath, "source", "/");
		String f[] = getFilesInDirectory(startingPath);
		
		if (f == null)
			return "";
		
		String buf = "[\r\n";
		for (int i = 0; i < f.length; i++) {
			String children[] = getFilesInDirectory(startingPath + "/" + f[i]);
			boolean bHasChildren = children != null && (children.length > 0);
			buf += "\t{\r\n"
					+ "\t\t\"text\" : \""+f[i]+"\",\r\n"
					+ "\t\t\"id\"   : \""+directory+"/"+f[i]+"\",\r\n"
					+ "\t\t\"expanded\" : false,\r\n"
					+ "\t\t\"hasChildren\" : "+((bHasChildren) ? "true" : "false")+"\r\n"
					+ "\t},\r\n";
		}
		buf += "]\n";
		
		return buf;
	}

	public String htmlGetIngestArea(String directory) {
		String f[] = getFilesInDirectory(rootDirectory + "/" + directory);
		
		if (f == null)
			return "";
		
		String buf = "[\r\n";
		for (int i = 0; i < f.length; i++) {
			//String children[] = getFilesInDirectory(rootDirectory + "/" + directory + "/" + f[i]);
			//boolean bHasChildren = (children.length > 0);
			buf += "\t{\r\n"
					+ "\t\t\"text\" : \""+f[i]+"\",\r\n"
					+ "\t\t\"id\"   : \""+directory+"/"+f[i]+"\",\r\n"
					+ "\t\t\"expanded\" : false,\r\n"
					+ "\t\t\"hasChildren\" : false\r\n"
					+ "\t},\r\n";
		}
		buf += "]\n";
		
		return buf;
	}

	public String htmlGetExistingPolicies() {
		String f[] = getFilesInDirectory(policyDirectory);
		
		if (f == null)
			return "";

		String buf = "[\r\n";
		for (int i = 0; i < f.length; i++) {
			//String children[] = getFilesInDirectory(rootDirectory + "/" + directory + "/" + f[i]);
			//boolean bHasChildren = (children.length > 0);
			buf += "\t{\r\n"
					+ "\t\t\"text\" : \"<a href=\\\""+f[i]+"\\\" onclick=\\\"return treeSelection(this);\\\">"+f[i]+"</a>\",\r\n"
					+ "\t\t\"id\"   : \""+f[i]+"\",\r\n"
					+ "\t\t\"expanded\" : false,\r\n"
					+ "\t\t\"hasChildren\" : false\r\n"
					+ "\t},\r\n";
		}
		buf += "]\n";
		
		return buf;
	}

	public String htmlGetDatasetsForAGivenPolicy(String policy) {
		String buf = "";
		String url = "file:///" + policyDirectory + "/" + policy + "/product-types.xml";

		List<String> names = parseDocumentForName(url);

		if (names == null)
			return "";

		Iterator<String> it = names.iterator();

		
		buf = "[";
		while (it.hasNext()) {
			String f = (String) it.next();
			buf += "\t{\r\n"
				+ "\t\t\"text\" : \"<a href=\\\""+f+"\\\" onclick=\\\"return treeSelection(this);\\\">"+f+"</a>\",\r\n"
				+ "\t\t\"id\"   : \""+f+"\",\r\n"
				+ "\t\t\"expanded\" : false,\r\n"
				+ "\t\t\"hasChildren\" : false\r\n"
				+ "\t},\r\n";
		}
		buf += "]";

		return buf;
	}	
	
	public Hashtable getProductTypeMetaData(String policyName, String ProductTypeName) {
		String policyPath = policyDirectory;
		String policyFile = policyPath + "/" + policyName + "/product-types.xml";
	
		// parse the policy file
		XMLMetaDataParser xmp = new XMLMetaDataParser(policyFile);
		// retrieve a hashtable of {product type name => product type policy}
		// for all datasets from the dataset collection.
		Hashtable<String, CasProductType> productTypeMetaData = xmp.getProductTypes();
		
		// retrieve the product type to be updated. store as CasProductType instance.
		CasProductType cpt = (CasProductType) productTypeMetaData.get(ProductTypeName);
		
		return productTypeMetaData;
	}
	
	private String[] getFilesInDirectory(String directory) {
		File dir = new File(directory);

		String[] children = dir.list();
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return !name.startsWith(".");
			}
		};
		children = dir.list(filter);

		/*
		List<String> l = Arrays.asList(children);
	    Collections.sort(l);
	    return (String[]) l.toArray();
	    */
		
		return children;
	    
	}

	private Document parseXmlFile(String address){
		Document dom = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			URL url = new URL(address);
			URLConnection urlConnection = url.openConnection();
			InputStream urlStream = url.openStream();
			urlConnection.setAllowUserInteraction(false);
			dom = db.parse(urlStream);
			urlStream.close();
		} catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
		return dom;
	}

	private List<String> parseDocumentForName(String url) {
		Document dom = parseXmlFile(url);
		
		if (dom == null)
			return null;
		
        NodeList nodelist = dom.getElementsByTagName("type");
        List<String> p = new ArrayList<String>();
        
        int count = nodelist.getLength();
        for (int i = 0; i < count; i++) {
            Element e = (Element)nodelist.item(i);
            NamedNodeMap nnm = e.getAttributes();

            if (nnm != null) {
            	String name = nnm.getNamedItem("name").getNodeValue();
            	p.add(name);
            }
        }
        
        return p;
	}
	
}
