package gov.nasa.jpl.edrn.ecas.curation.policymgr;

/**
 * TODO XML validation against a DTD/schema?
 * 
 */
import java.io.*;
import java.util.Hashtable;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * Reads an eCAS metadata configuration file and constructs
 * a collection of objects to represent the dataset policies
 * mapping product types and elements to each other.
 * 
 * @author aclark
 *
 */
public class XMLMetaDataParser {
	private Element root;
	
    XMLMetaDataParser(String filename) {
    	Document doc = parseXmlFile(filename);                     

    	if (doc == null) 
    		return;

    	// Get the XML root node 
        root = doc.getDocumentElement();
    }
 
    //public Hashtable<String, CasElement> getElements() { return null; }    
    public Hashtable getElements() { 
    	Hashtable tmpElements = new Hashtable();
    	String id, name, strDesc;
    	CasElement c;
    	
    	// Get a list of all elements in the document
        NodeList list = root.getElementsByTagName("element");
        for (int i = 0; i < list.getLength(); i++) {
        	id = name = strDesc = null;
        	c = null;
        	
            Element el = (Element)list.item(i);
            
            // get the element id and name attributes
        	id = el.getAttribute("id");
        	name = el.getAttribute("name");
            
            NodeList sublist = el.getChildNodes();   
            Element desc = (Element)sublist.item(1);
            
            strDesc = desc.getTextContent();
            
            c = new CasElement(id, name, strDesc);
            tmpElements.put(id, c);
        }
    	return tmpElements; 
    }
    
    public Hashtable getProductTypes() { 
    	//Hashtable<String, Hashtable> tmpProductTypes;
    	Hashtable<String, CasProductType> tmpProductTypes;
    	CasProductType cpt;
    	// integer index to the sub-list of items
    	// from the product-type DOM tree
    	int j;
    	
    	tmpProductTypes = new Hashtable();
    	
    	// <type>
    	NodeList list = root.getElementsByTagName("type");
    	for (int i = 0; i < list.getLength(); i++) {
    		
    		cpt = new CasProductType();
    		Element e = (Element)list.item(i);
    		
    		cpt.setId(e.getAttribute("id"));
    		cpt.setName(e.getAttribute("name"));
    		
    		// retrieve the product-type file management metadata and description
    		NodeList sublist = e.getChildNodes();
    		
    		j = 1; 
    		Element el = (Element)sublist.item(j);
    		// set repository path
    		cpt.setRepositoryPath(el.getTextContent());
    		
    		// set versioner class
    		j += 2;
    		el = (Element)sublist.item(j);
    		cpt.setVersionerClass(el.getTextContent());
    		
    		// description
    		j += 2;
    		el = (Element)sublist.item(j);
    		cpt.setDescription(el.getTextContent());

    		//---------------------------------------------
    		// metExtractor structure parsed starting here
    		j += 2;
    		for ( ; j < sublist.getLength(); j += 2) {
    			
	    		//String extractorKey;
	    		
	    		Element metExtractors = (Element)sublist.item(j);
	    		NodeList extractors = metExtractors.getElementsByTagName("extractor");
	    		
	    		if (sublist.getLength() > 0 && ((Element)sublist.item(j)).getTagName().equals("metExtractor")) {
		    		Element extractClass = (Element)extractors.item(0);
		    		String extractorKey = extractClass.getAttribute("class");
		    		
		    		Hashtable tmpMetExtractors = new Hashtable();    		
		   		
		    		NodeList cfgPropert = extractClass.getElementsByTagName("configuration");
		    		Element cfgElement = (Element) cfgPropert.item(0);
		    		Element cfg = (Element)cfgPropert.item(0);		
		    			
		       		NodeList cfgs = cfg.getElementsByTagName("property");
		       		
		       		Hashtable configuration = new Hashtable();		
		       		
		    		for (int k = 0; k < cfgs.getLength(); k++) {
		    			Element tmpProperty = (Element)cfgs.item(k);
		
		    			String propName = tmpProperty.getAttribute("name");
		    			String propValue = tmpProperty.getAttribute("value");  			
		    			configuration.put(propName, propValue);
		    		}
		    		
		    		tmpMetExtractors.put(extractorKey, configuration);
		    		cpt.configuration = tmpMetExtractors;
		    		
		    		
	    		} else if (sublist.getLength() > 0 && ((Element)sublist.item(j)).getTagName().equals("metadata")) {
	    			// parse metaData
	        		String metaKey, metaVal;

	        		// grab metadata tree
	        		Element metadataTag = (Element) sublist.item(j);
	    			
	        		// grab key,value nodes from metadata branch
	        		NodeList keyValPairs = metadataTag.getChildNodes();
	        		for (int n = 1; n < keyValPairs.getLength(); ) {
	        			// retrieve each key/val pair
	        			Element keyVal = (Element)keyValPairs.item(n);
	        			NodeList keyValList = keyVal.getChildNodes();
	        			
	        			Element key = (Element)keyValList.item(1);
	        			Element val = (Element)keyValList.item(3);
	        			
	        			metaKey = key.getTextContent();
	        			metaVal = val.getTextContent();
	        			
	        			cpt.setMetaDataValue(metaKey, metaVal);
	        			n += 2;
	        		}
	        		// populate tmpProductTypes string => hashtable
	        		//tmpProductTypes.put(cpt.name, cpt.metadata);
	        		tmpProductTypes.put(cpt.name, cpt);
	    		}
	    		
	    		
    		}
    		if (cpt.metadata.size() == 0) {
    			//System.out.println("no metadata definitions found");
    			//tmpProductTypes.put(cpt.name, cpt.metadata);
        		tmpProductTypes.put(cpt.name, cpt);
    		}
    		//---------------------------------------------
    		// Metadata extraction starts here
    		j += 2;
		/*
    		String metaKey, metaVal;

    		// grab metadata tree
    		Element metadataTag = (Element) sublist.item(j);
			
    		// grab key,value nodes from metadata branch
    		NodeList keyValPairs = metadataTag.getChildNodes();
    		for (int n = 1; n < keyValPairs.getLength(); ) {
    			// retrieve each key/val pair
    			Element keyVal = (Element)keyValPairs.item(n);
    			NodeList keyValList = keyVal.getChildNodes();
    			
    			Element key = (Element)keyValList.item(1);
    			Element val = (Element)keyValList.item(3);
    			
    			metaKey = key.getTextContent();
    			metaVal = val.getTextContent();
    			
    			cpt.setMetaDataValue(metaKey, metaVal);
    			n += 2;
    		}
    		// populate tmpProductTypes string => hashtable
    		tmpProductTypes.put(cpt.name, cpt.metadata);
*/
    	}
    	System.out.println("tmpProductTypes size: " + tmpProductTypes.size());
    	// return CasProductType hashtable 
    	return tmpProductTypes; 
    }
    
    public static Document parseXmlFile(String filename) {
    	try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            factory.setIgnoringComments(true);
            
            // parse the XML file
            Document doc = factory.newDocumentBuilder().parse(new File(filename));
            return doc;
        } 
    	catch (SAXException e) {
            // A parsing error occurred; the xml input is not valid
    		e.printStackTrace();
        } 
    	catch (ParserConfigurationException e) {
    		e.printStackTrace();
        } 
    	catch (IOException e) {
    		// file I/O error
    		e.printStackTrace();
        }
    	return null;
    }
    
}