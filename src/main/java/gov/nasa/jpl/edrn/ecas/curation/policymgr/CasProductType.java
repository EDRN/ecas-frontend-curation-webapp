/* 
 *  Copyright (c) 2009, California Institute of Technology. 
 *  ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
 * 
 *  Author: Andrew Clark
 */
package gov.nasa.jpl.edrn.ecas.curation.policymgr;

import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

// JSON formatting classes
import gov.nasa.jpl.edrn.ecas.curation.util.JSONObject;
import gov.nasa.jpl.edrn.ecas.curation.util.JSONException;
import gov.nasa.jpl.edrn.ecas.curation.util.JSONArray;

/**
 * CasProductType represents a product type from the policy configuration of a
 * dataset collection. This class includes methods to convert the current
 * instance of a product type into its JSON and XML representations.
 * 
 * @author aclark
 */
public class CasProductType {
    // attributes
    protected String id;
    protected String name;

    // sub-elements
    protected String versionerClass;
    protected String repositoryPath;
    protected String description;

    // Stores metadata (key,val) pair
    protected Hashtable<String, String> metadata;

    // stores {extractor class name => {property name => property value} }
    protected Hashtable<String, Hashtable<String, String>> configuration;

    /**
     * Constructor instantiates two internal hashtables for metadata and
     * extractor configuration properties.
     */
    public CasProductType() {
        metadata = new Hashtable<String, String>();
        configuration = new Hashtable<String, Hashtable<String, String>>();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setVersionerClass(String versionerClass) {
        this.versionerClass = versionerClass;
    }

    public String getVersionerClass() {
        return versionerClass;
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public String getRepositoryPath() {
        return repositoryPath;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Sets a metadata key/value pair in the internal storage hashtable for the
     * current product type.
     * 
     * @param key
     *            The metadata key
     * @param value
     *            The metadata value
     */
    public void setMetaDataValue(String key, String value) {
        if (value == null) {
            /*
             * System.out.print("in CasProductType.setMetaDataValue, attempt to 'put' a null value in HT"); 
             * System.out.println(" under key "+key);
             */
            metadata.put(key, new String());
        } else if (key.toLowerCase().equals("pubmedid")) {
            /*
             * System.out.println("[debug@CasProductType: setting a pubmed ID...is its value garbled?");
             * System.out.println(value);
             */
            metadata.put(key, value);
        } else
            metadata.put(key, value);
    }

    public String getMetaDataValue(String key) {
        return (String) metadata.get(key);
    }

    /**
     * @param key
     *            The metadata field to be removed from the current product
     *            type.
     */
    public void deleteMetaDataKey(String key) {
        if (metadata.containsKey(key)) {
            String deleted = metadata.remove(key);
            // System.out.println("[debug@CasProductType]: deleted key "+key+" from metadata");
        }
    }

    /**
     * Check if a current metadata item exists in the current product types
     * policy.
     * 
     * @return True if the metadata item exists in the metadata catalog. False
     *         otherwise.
     */
    public boolean containsMetaDataKey(String key) {
        return metadata.containsKey(key);
    }

    /**
     * Define a configuration property for a metadata extractor in the policy of
     * the product type.
     * 
     * @param extractor
     *            The class name of the extractor to be updated.
     * @param name
     *            The extractor configuration property name.
     * @param value
     *            The extractor configuration property value.
     */
    public void setConfigurationPropertyValue(String extractor, String name,
            String value) {
        if (configuration.containsKey(extractor)) {
            Hashtable<String, String> properties = (Hashtable<String, String>) configuration
                    .get(extractor);
            properties.put(name, value);
        } else {
            Hashtable<String, String> properties = new Hashtable<String, String>();
            properties.put(name, value);
            configuration.put(extractor, properties);
        }
    }

    public String getConfigurationPropertyValue(String extractor, String name) {
        Hashtable<String, String> properties = (Hashtable<String, String>) configuration
                .get(extractor);
        return (String) properties.get(name);
    }

    public Hashtable<String, String> getMetaDataHT() {
        return metadata;
    }

    public Hashtable<String, Hashtable<String, String>> getConfigurationPropertyHT() {
        return configuration;
    }

    /**
     * Construct a JSON string of all the CasProductType attributes.
     * 
     * @return A JSON-formatted String of the member attributes of the current
     *         product type.
     */
    public String toJSON() {
        String jOutput = null;
        try {
            jOutput = new JSONObject().put("versionerClass", versionerClass)
                    .put("repositoryPath", repositoryPath).put("description",
                            description).put("name", name).put("id", id)
                    .toString();
            return jOutput;
        } catch (JSONException je) {
            System.out.println("JSON Exception caught in toJSON()" + je);
            return "{ }";
        }
    }

    /**
     * Constructs a JSON-formatted String of only the product type metadata
     * items as key-value pairs.
     * 
     * @return The JSON-formatted String of all metadata items for this product
     *         type.
     */
    public String metaDataToJSON() {
        Iterator iter = metadata.keySet().iterator();
        JSONArray jsonMetaDataArray = new JSONArray();

        try {
            while (iter.hasNext()) {
                JSONObject jsonPair = new JSONObject();
                String key = (String) iter.next();
                String value = (String) metadata.get(key);

                jsonPair.put("key", key);
                jsonPair.put("value", value);

                jsonMetaDataArray.put(jsonPair);
            }
            return new JSONObject().put("metadata", jsonMetaDataArray)
                    .toString();
        } catch (JSONException e) {
            System.out.println("JSON Exception caught in metaDataToJSON()" + e);
            return "{ }";
        }
    }

    /**
     * Utility method to create a String representation of the contents of a
     * Hashtable storing <String, String>.
     * 
     * @param t
     *            Hashtable<String, String> instance to be converted to String.
     * @return String representation of the hashtable's content.
     * 
     */
    private String showHashTable(Hashtable<String, String> t) {
        StringBuffer sb = new StringBuffer();
        if (t.size() == 0)
            return new String("{ empty }");

        for (Object k : t.keySet()) {
            sb.append((String) k);
            sb.append(" => ");
            sb.append((String) t.get(k));
            sb.append("\n");
        }
        return sb.toString();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Product Type ");
        sb.append("id = \"" + id + "\" name = \"" + name);
        sb.append("\"\n");
        sb.append("\tRepository path: " + repositoryPath);
        sb.append("\n\tVersionerClass: " + versionerClass);
        sb.append("\n\tDesc: " + description);
        sb.append("\n");
        sb.append(showHashTable(metadata));

        return sb.toString();
    }

    /**
     * This method builds an XML representation of the current product type
     * instance, according to the XML structure used by the eCAS policy
     * configuration files. The resulting XML string must still be wrapped with
     * the top-level "cas:producttype" tags.
     * 
     * @return A Java String containing the eCAS policy file XML representation
     *         of the current CasProductType instance.
     */
    public Document toXMLDocument() {
        // borrowed from oodt.cas.commons.xml.XMLUtils
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document document = null;

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();

            Element typeElem = document.createElement("type");
            typeElem.setAttribute("id", id);
            typeElem.setAttribute("name", name);

            Element repositoryPathElem = document
                    .createElement("repositoryPath");
            repositoryPathElem.appendChild(document
                    .createTextNode(repositoryPath));
            typeElem.appendChild(repositoryPathElem);

            Element versionerClassPathElem = document
                    .createElement("versionerClass");
            versionerClassPathElem.appendChild(document
                    .createTextNode(versionerClass));
            typeElem.appendChild(versionerClassPathElem);

            Element descriptionElem = document.createElement("description");
            descriptionElem.appendChild(document.createTextNode(description));
            typeElem.appendChild(descriptionElem);

            Element metExtractor = document.createElement("metExtractors");

            typeElem.appendChild(metExtractor);

            return document;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return A Java String containing the XML representation of the metadata
     *         item set for the current Product Type.
     */
    public String metaDataToXMLString() {
        return toXML(false);
    }

    /**
     * @return A Java String containing the XML representation of the current
     *         Product Type.
     */
    public String toXMLString() {
        return toXML(true);
    }

    /**
     * @param complete
     *            If true, convert entire Product Type instance to its XML
     *            representation, else only convert its metadata items to XML
     *            format.
     * @return A Java String containing the XML structure of the current Product
     *         Type.
     */
    private String toXML(boolean complete) {
        // String comment;
        StringBuffer sb = new StringBuffer();

        if (complete) {
            /*
             * sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
             * sb.append("\n");sb.append(
             * "<cas:producttypes xmlns:cas=\"http://oodt.jpl.nasa.gov/1.0/cas\">\n"
             * ); sb.append("\n");
             */
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
                    String e = (String) o;
                    Hashtable<String, String> properties = (Hashtable) configuration
                            .get(e);
                    sb.append("<extractor class=\"" + e + "\">\n");

                    if (properties.size() > 0) {
                        sb.append("<configuration>\n");
                        sb.append("\n");
                        for (Object n : properties.keySet()) {
                            String propName = (String) n;
                            sb.append("<property name=\"" + propName + "\" ");
                            sb.append("value=\""
                                    + (String) properties.get(propName)
                                    + "\" />\n");
                        }
                        sb.append("</configuration>\n");
                    }
                    sb.append("</extractor>\n");
                }
                sb.append("</metExtractors>\n");
            }
            sb.append("\n");
        }
        if (metadata.size() > 0) {
            sb.append("<metadata>\n");
            for (Object m : metadata.keySet()) {
                String k = (String) m;
                sb.append("<keyval>\n");
                sb.append("<key>" + k + "</key>\n");
                sb.append("<val>" + (String) metadata.get(k) + "</val>\n");
                sb.append("</keyval>\n");
            }
            sb.append("</metadata>\n");
        }

        if (complete)
            sb.append("</type>\n");
        /*
         * sb.append("</cas:producttypes>\n");
         */
        return sb.toString();

    }
}
