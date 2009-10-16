package gov.nasa.jpl.edrn.ecas.curation.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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

    public void testBackendApp()
    {
    	assertTrue( true );
    }
}
