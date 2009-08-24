package gov.nasa.jpl.edrn.ecas.curation.policymgr;

import java.io.*;
import java.util.Hashtable;

/**
 * CasProductType represents a product type from
 * the policy configuration of a dataset collection.
 * 
 * @author aclark
 *
 */
public class CasProductType {
	// attributes
	public String id;
	public String name;
	
	// sub-elements
	public String versionerClass;
	public String repositoryPath;
	public String description;
	
	// Stores metadata (key,val) pair
	public Hashtable<String, String> metadata;
	
	// stores {extractor class name => {property name => property value} }
	public Hashtable<String, Hashtable<String, String>> configuration;
	
	/**
	 * Constructor instantiates two internal hashtables
	 * for metadata and extractor configuration properties.
	 */
	public CasProductType() {
		metadata = new Hashtable<String, String>();
		configuration = new Hashtable<String, Hashtable<String, String>>();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getVersionerClass() {
		return versionerClass;
	}

	public void setVersionerClass(String versionerClass) {
		this.versionerClass = versionerClass;
	}

	public String getRepositoryPath() {
		return repositoryPath;
	}

	public void setRepositoryPath(String repositoryPath) {
		this.repositoryPath = repositoryPath;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/** 
	 * Sets a metadata key/value pair in the internal storage hashtable
	 * for the current product type.
	 * 
	 * @param key	The metadata key
	 * @param value	The metadata value field
	 * 
	 */
	public void setMetaDataValue(String key, String value) {
		if (value == null) 
			metadata.put(key, new String());
		else
			metadata.put(key, value);
	}
	
	/**
	 * Check if a current metadata item exists in the 
	 * current product types policy.
	 * 
	 * @return	True if the metadata item exists in the 
	 * 			metadata catalog. False otherwise.
	 * 
	 */
	public boolean containsMetaDataKey(String key) {
		return metadata.containsKey(key);
	}
	
	public String getMetaDataValue(String key) {
		return (String)metadata.get(key);
	}
	
	/**
	 * Define a configuration property for a metadata extractor in 
	 * the policy of the product type.
	 * 
	 *  @param	extractor	The class name of the extractor to be updated.
	 *  @param	name		The extractor configuration property name.
	 *  @param	value		The extractor configuration property value.
	 * 
	 */
	public void setConfigurationPropertyValue(String extractor, String name, String value) {
		if (configuration.containsKey(extractor)) {
			Hashtable<String, String> properties = (Hashtable<String, String>)configuration.get(extractor);
			properties.put(name, value); 	
		}
		else {
			Hashtable<String, String> properties = new Hashtable<String, String>();
			properties.put(name, value);
			configuration.put(extractor, properties);
		}
	}
	
	public String getConfigurationPropertyValue(String extractor, String name) {
		Hashtable<String, String> properties = (Hashtable<String, String>) configuration.get(extractor);
		return (String) properties.get(name);
	}
	
	public Hashtable<String, String> getMetaDataHT() {
		return metadata;
	}

	public Hashtable<String, Hashtable<String, String>> getConfigurationPropertyHT() {
		return configuration;
	}
	
	/**
	 * Utility method to create a String representation of the 
	 * contents of a Hashtable storing <String, String>.
	 * 
	 * @param t	Hashtable<String, String> instance to be 
	 * 			converted to String. 
	 * @return	String representation of the hashtable's content.
	 * 
	 */
	private String showHashTable(Hashtable<String, String> t) {
		StringBuffer sb = new StringBuffer();
		if (t == null)
			return new String("{ empty }");
		
		for (Object k : t.keySet()) {
			sb.append((String) k);
			sb.append(" => ");
			sb.append((String) t.get(k));
			sb.append("\n");
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * @return	A summary of the current CasProductType
	 * 			instance in Java String form. 
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Product Type ");
		sb.append("id = \"" + id + "\" name = \"" + name);
		sb.append("\"\n");
		sb.append("\tRepository path: "+repositoryPath);
		sb.append("\n\tVersionerClass: "+versionerClass);
		sb.append("\n\tDesc: "+description);
		sb.append("\n");
		sb.append(showHashTable(metadata));
	
		return sb.toString();
	}
	
	
	/**
	 * This method builds an XML representation of the 
	 * current product type instance, according to the
	 * XML structure used by the eCAS policy configuration
	 * files. The resulting XML string must still be 
	 * wrapped with the top-level "cas:producttype" 
	 * tags.
	 *  
	 * @return	A Java String containing the eCAS policy file XML 
	 * 			representation of the current CasProductType instance.
	 */
	public String toXMLString() {
		/* 
		 * TODO Convert this method to use the Document class from Java API to generate XML.
		 */		
		String comment;
		StringBuffer sb = new StringBuffer();
//		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
//		sb.append("\n");
//		sb.append("<cas:producttypes xmlns:cas=\"http://oodt.jpl.nasa.gov/1.0/cas\">\n");
		sb.append("\n");
		sb.append("<type id=\"");
		sb.append(id);
		sb.append("\" name=\"");
		sb.append(name);
		sb.append("\">\n");
		sb.append("<repositoryPath>");
		sb.append(repositoryPath);
		sb.append("</repositoryPath>\n");

		sb.append("<versionerClass>");
		sb.append(versionerClass);
		sb.append("</versionerClass>\n");		

		sb.append("<description>");
		sb.append(description);
		sb.append("</description>\n");

		sb.append("\n");
		if (configuration.size() > 0) {
			sb.append("<metExtractors>\n");
			for (Object o : configuration.keySet()) {
				String e = (String)o;
				Hashtable<String, String> properties = (Hashtable)configuration.get(e);
				sb.append("<extractor class=\"" + e + "\">\n");
				
				if (properties.size() > 0) {
					sb.append("<configuration>\n");
					sb.append("\n");					
					for (Object n: properties.keySet()) {
						String propName = (String) n;
						sb.append("<property name=\"" + propName +"\" ");
						sb.append("value=\"" + (String)properties.get(propName) + "\" />\n");
					}
					sb.append("</configuration>\n");
				}
				sb.append("</extractor>\n");
			}
			sb.append("</metExtractors>\n");
		}
		sb.append("\n");	
		if (metadata.size() > 0) {
			sb.append("<metadata>\n");
			for (Object m : metadata.keySet()) {
				String k = (String) m;
				sb.append("<keyval>\n");
				sb.append("<key>" + k + "</key>\n");
				sb.append("<val>" + (String)metadata.get(k) + "</val>\n");	
				sb.append("</keyval>\n");
			}
			sb.append("</metadata>\n");
		}
		sb.append("</type>\n");
		//sb.append("</cas:producttypes>\n");
		return sb.toString();
	}	
}
