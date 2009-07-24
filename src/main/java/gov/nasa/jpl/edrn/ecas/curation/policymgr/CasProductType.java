package gov.nasa.jpl.edrn.ecas.curation.policymgr;


import java.io.*;
import java.util.*;

public class CasProductType {
	// attributes
	public String id;
	public String name;
	
	// sub elements
	public String versionerClass;
	public String repositoryPath; // use URL class instead?
	public String description;
	
	// Stores metadata (key,val) pairs.
	public Hashtable<String, String> metadata;
	
	public CasProductType() {
		metadata = new Hashtable();
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
	
}
