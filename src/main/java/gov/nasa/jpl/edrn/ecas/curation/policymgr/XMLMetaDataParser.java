/* 
 *  Copyright (c) 2009, California Institute of Technology. 
 *  ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
 * 
 *  Author: Andrew Clark
 */
package gov.nasa.jpl.edrn.ecas.curation.policymgr;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XMLMetaDataParser handles reading configuration details
 * for elements and product types from their XML policy files.
 * 
 * @author aclark
 *
 */
public class XMLMetaDataParser {
	private Document doc;
	private Element root;
	
	/**
	 * The constructor loads a policy file for parsing by 
	 * either of the two utility methods, getElements or 
	 * getProductTypes.
	 * 
	 * @param filename	The name of the XML policy file to parse for 
	 * 					configuration details.
	 */
    XMLMetaDataParser(String filename) {
    	doc = parseXmlFile(filename);                     

    	if (doc == null) 
    		return;

    	// Get the XML root node 
        root = doc.getDocumentElement();
    }
 
    /**
     * getElements() will retrieve all of the elements from 
     * a policy file for a given dataset collection and 
     * instantiate a collection of objects with their details. 
     * 
     * @returns	Returns a Hashtable<String, CasElement> of all elements
     * 			found in the elements.xml policy file. The hashtable maps
     * 			element names to their objects. 
     */
    public Hashtable<String, CasElement> getElements() { 
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
    
    /**
     * getProductTypes() parses a product type policy
     * file to retrieve all available configuration information
     * for a given dataset. 
     * 
     * @returns	A Hashtable<String, CasProductType> of product
     * type information read from a policy file. Product type
     * metadata, extractor configuration, and file management 
     * information (e.g. versioner class, repository path) is 
     * stored in each object. Product type names are mapped to 
     * their corresponding objects in the hashtable.
     * 
     */
    public Hashtable<String, CasProductType> getProductTypes() { 
    	Hashtable<String, CasProductType> tmpProductTypes = null; 
    	CasProductType cpt = null;
    	
    	tmpProductTypes = new Hashtable<String, CasProductType>();
    	// <type> branch 
    	NodeList list = root.getElementsByTagName("type");

    	for (int i = 0; i < list.getLength(); i++) {
    		Node typeNode, childNode;
    		// instantiate new product type object
    		cpt = new CasProductType();
    		typeNode = (Node)list.item(i);    	
    		
    		Element e = (Element) typeNode;    		
    		cpt.setId(e.getAttribute("id"));
    		cpt.setName(e.getAttribute("name"));	
    		
    		// retrieve the product-type file management metadata and description
    		NodeList sublist = e.getElementsByTagName("repositoryPath");
    		
    		// set repository path
    		if (sublist.getLength() > 0) {
    			childNode = sublist.item(0);     		
    			cpt.setRepositoryPath(childNode.getTextContent());
    		}
    		
    		// set versioner class
    		sublist = e.getElementsByTagName("versionerClass");
    		if (sublist.getLength() > 0) {
    			childNode = sublist.item(0);     		
    			cpt.setVersionerClass(childNode.getTextContent());
    		}
    		
    		// set description
    		sublist = e.getElementsByTagName("description");
    		if (sublist.getLength() > 0) {
    			childNode = sublist.item(0);     		
    			cpt.setDescription(childNode.getTextContent());
    		}    	    		
    	 	
    		// parse any extractors
    		sublist = e.getElementsByTagName("metExtractors");
    		if (sublist.getLength() > 0) {
    			childNode = sublist.item(0);
    			Element tmp = (Element)childNode;

    			NodeList extractors = tmp.getElementsByTagName("extractor");
    			if (extractors.getLength() > 0) {
    				for (int m = 0; m < extractors.getLength(); m++) {
	    				Element tmpExtractor = (Element)extractors.item(m);
		    			String extractorKey = tmpExtractor.getAttribute("class");
			    		
			    		NodeList config = tmp.getElementsByTagName("configuration");
			    		// parse configuration properties
			    		if (config.getLength() > 0) {
				    		Node configNode = config.item(0);
				    		Element configElement = (Element) configNode;		
				    			
				       		NodeList props = configElement.getElementsByTagName("property");
				       		if (props.getLength() > 0) {		
					    		for (int k = 0; k < props.getLength(); k++) {
					    			Element tmpProperty = (Element)props.item(k);
					
					    			String propName = tmpProperty.getAttribute("name");
					    			String propValue = tmpProperty.getAttribute("value");  			
					    			cpt.setConfigurationPropertyValue(extractorKey, propName, propValue);
					    		}
				       		}
			    		}
    				}
    			}
    		}
    		
    		// parse metadata, if any
    		sublist = e.getElementsByTagName("metadata");
    		if (sublist.getLength() > 0) {
        		// get metadata tree
        		childNode = sublist.item(0);
    			Element metadataTag = (Element)childNode;
    			
        		// get keyval nodes from metadata 
        		NodeList keyValPairs = metadataTag.getElementsByTagName("keyval");
        		if (keyValPairs.getLength() > 0) {        		
	        		for (int n = 0; n < keyValPairs.getLength(); n++) {
	        			// retrieve each key/val pair
	        			Element keyVal = (Element) keyValPairs.item(n);
	        			
	        			String metaKey = new String();
	        			String metaVal = new String();
	        			
	        			NodeList keyValList = keyVal.getElementsByTagName("key");
	        			if (keyValList.getLength() > 0) {
	        				childNode = keyValList.item(0);
	        				metaKey = childNode.getTextContent();
	        	    				
		        			keyValList = keyVal.getElementsByTagName("val");
		        			if (keyValList.getLength() > 0) {
		        				childNode = keyValList.item(0);
		        				metaVal = childNode.getTextContent();
		        			}
		        			else {
		        				metaVal = new String("TBD");
		        			}
	        			} 
	        			else {
	        				// empty keyval node
	        				continue;
	        			}
        				cpt.setMetaDataValue(metaKey, metaVal);
	        		}
        		}
    		}
    		// populate tmpProductTypes {string => hashtable}
			tmpProductTypes.put(cpt.name, cpt);
    	}
    	// return CasProductType hashtable 
    	return tmpProductTypes; 
    }
        
    /**
     * Parses an XML file and returns a Document
     * object storing that XML structure.
     * 
     * @param filename	The name of the XML policy 
     * 					file to be parsed.
     * 
     * @returns A Document object storing the DOM tree
     * 			of the parsed XML file.
     */
    public static Document parseXmlFile(String filename) {
    	try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            
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