package com.aem;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        WebServer server = new WebServer(9000,10);
        new Thread(server).start();

        try {
            Thread.sleep(20 * 6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stopping Server");
        server.stopServer();
        assertTrue( true );
    }
}