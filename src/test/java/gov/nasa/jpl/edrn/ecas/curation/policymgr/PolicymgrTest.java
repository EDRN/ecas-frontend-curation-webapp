package gov.nasa.jpl.edrn.ecas.curation.policymgr;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import gov.nasa.jpl.edrn.ecas.curation.policymgr.CasElement;
import gov.nasa.jpl.edrn.ecas.curation.policymgr.CasProductType;
import gov.nasa.jpl.edrn.ecas.curation.policymgr.CurationPolicyManager;
// import gov.nasa.jpl.edrn.ecas.curation.policymgr.CurationPolicyValidator;
import gov.nasa.jpl.edrn.ecas.curation.policymgr.XMLMetaDataParser;

public class PolicymgrTest 
    extends TestCase
{

	public PolicymgrTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( PolicymgrTest.class );
    }

    public void testPolicyMgr()
    {
    	CasElement e = new CasElement();
    	CasProductType p = new CasProductType();
    	CurationPolicyManager cpm = new CurationPolicyManager();
    	// CurationPolicyValidator cpv = new CurationPolicyValidator();
    	// XMLMetaDataParser xmdp = new XMLMetaDataParser("");
    	
    	assertTrue( true );
    }
}
