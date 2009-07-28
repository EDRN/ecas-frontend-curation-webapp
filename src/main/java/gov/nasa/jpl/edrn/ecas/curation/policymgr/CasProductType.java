package gov.nasa.jpl.edrn.ecas.curation.policymgr;


import java.io.*;
import java.util.*;

public class CasProductType {
	// attributes
	public String id;
	public String name;
	
	// sub elements
	public String versionerClass;
	public String repositoryPath;
	public String description;
	
	// Stores metadata (key,val) pairs.
	public Hashtable<String, String> metadata;
	
	// stores className of extractor => configuration tuples
	public Hashtable<String, Hashtable> configuration;
	
	public CasProductType() {
		metadata = new Hashtable();
		configuration = new Hashtable();
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

	
	public void setMetaDataValue(String key, String value) {	
		metadata.put(key, value);
	}
	
	public String getMetaDataValue(String key) {
		return (String)metadata.get(key);
	}
	
	public Hashtable getMetaDataHT() {
		return metadata;
	}
	
	public String showHashTable(Hashtable t) {
		StringBuffer sb = new StringBuffer();
		if (t == null)
			return new String("");
		
		for (Object k : t.keySet()) {
			sb.append((String)k);
			sb.append("=>");
			sb.append((String)t.get(k));
			sb.append("\n");
		}
		return sb.toString();
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Product Type ");
		sb.append("id = \"" + id + "\" name = \"" + name);
		sb.append("\"\n");
		sb.append("\tRepository path: "+repositoryPath);
		sb.append("\n\tVersionerClass: "+versionerClass);
		sb.append("\n\tDesc: "+description);
		sb.append(showHashTable(metadata));
	
		return sb.toString();
	}
	
	public String toXMLString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<cas:producttypes xmlns:cas=\"http://oodt.jpl.nasa.gov/1.0/cas\">\n");
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
		
		if (configuration.size() > 0) {
			sb.append("<metExtractors>");
			for (Object o : configuration.keySet()) {
				String e = (String)o;
				Hashtable properties = (Hashtable)configuration.get(e);
				sb.append("<extractor class=\"" + e + "\">");
				
				if (properties.size() > 0) {
					sb.append("<configuration>");
					for (Object n: properties.keySet()) {
						String propName = (String) n;
						sb.append("<property name=\"" + propName +"\" ");
						sb.append("value=\"" + (String)properties.get(propName) + "\" />");
					}
					sb.append("</configuration>\n");
				}
			}
			sb.append("</metExtractors>\n");
		}
		
		if (metadata.size() > 0) {
			sb.append("<metadata>");
			for (Object m : metadata.keySet()) {
				String k = (String) m;
				sb.append("<keyval>");
				sb.append("<key>" + k + "</key>\n");
				sb.append("<val>" + (String)metadata.get(k) + "</val>\n");	
				sb.append("</keyval>\n");
			}
			sb.append("</metadata>\n");
		}
		sb.append("</type>");
		return sb.toString();
	}	
}
