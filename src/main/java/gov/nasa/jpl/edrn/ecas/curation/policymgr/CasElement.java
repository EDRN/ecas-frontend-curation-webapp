package gov.nasa.jpl.edrn.ecas.curation.policymgr;

/* 
 * TODO add generic types to Hashtable usage/eliminate unchecked exception warnings
 * TODO implement hashcode as: id string .hashcode()
 * TODO implement equals as: id string .equals()
 * TODO add toElement/DocumentFragment method?
 * TODO add toJSON method?
 * TODO handle exceptions?
 * TODO expand javadoc comments
 */
/**
 * Represents a single metadata element of an 
 * eCAS dataset metadata policy.
 * 
 * @author aclark
 */
public class CasElement {
	protected String id;
	protected String name;
	protected Description desc;
	
	public CasElement() { }
	
	/**
	 * Construct a CasElement with the attributes passed
	 * as String arguments.
	 * 
	 * @param id	The colon-separated id attribute of the CAS element.
	 * @param name	The name attribute of the CAS element.
	 * @param description	A description of the CAS metadata element.
	 */
	CasElement(String id, String name, String description) {
		this.id = id;
		this.name = name;		
		this.desc = new Description(description);
	}
	
	/**
	 * Sets the id attribute to a new value for the current CAS metadata 
	 * element.
	 * 
	 * @param id	The new id value as a Java String, 
	 * 				in colon-separated format. For example,
	 * 				<code>urn:edrn:ecas:hanash:LabAnalysticMethods</code>
	 */
	public void setID(String id) {
		this.id = id;
	}
	
	public String getID() {
		return this.id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setDescription(String desc) {
		this.desc.setDescription(desc);
	}
	
	public String getDescription() {
		return this.desc.getDescription();
	}
	
	/**
	 * Produce a human-readable summary of the CAS metadata
	 * element.
	 * 
	 * @return	A Java String representation of the CAS metadata 
	 * 			element with its attribute values.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Element ID: " + id + "\n");
		sb.append("Element Name: " + name + "\n");
		sb.append("Element Description: " + desc + "\n");

		return sb.toString();
	}
	
	/**
	 * Produce an XML fragment of the current CAS element,
	 * formatted in the element schema used by a metadata 
	 * policy file.
	 * 
	 * @return	A Java String storing an XML fragment of the current
	 * 			CasElement object in the schema of eCAS policy files.
	 */
	public String toXMLString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<element id=\"");
		sb.append(id);
		sb.append("\" name=\"");
		sb.append(name);
		sb.append("\">");
		sb.append(desc.toXMLString());
		sb.append("</element>");
		
		return sb.toString();
	}
	
	/**
	 * Store the description element of a CAS metadata item
	 * as a class nested within the CasElement wrapper.
	 * 
	 * @author aclark
	 */
	class Description {
		protected String description;
		
		public Description(String desc) {
			description = desc;
		}
		
		public void setDescription(String d) {
			description = d;
		}
		
		public String getDescription() {
			return description;
		}
		
		public String toString() {
			return getDescription();
		}
		
		public String toXMLString() {
			StringBuffer sb = new StringBuffer();
			sb.append("<description>");
			sb.append(description);
			sb.append("</description>");
			
			return sb.toString();
		}
	}
}
