package gov.nasa.jpl.edrn.ecas.curation.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ServletTest 
    extends TestCase
{

	public ServletTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( ServletTest.class );
    }

    public void testBackendApp()
    {
    	assertTrue( true );
    }
}
