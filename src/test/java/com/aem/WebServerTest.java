package com.aem;

import junit.framework.TestCase;
import org.junit.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by alexandru-petrisorpajarcu on 05/05/2016.
 */
public class WebServerTest {

	static WebServer server;
	HashMap<String, String> params = new HashMap<String, String>();
	HashMap<String, String> headers = new HashMap<String, String>();

	@BeforeClass
	public static void setUp() {
		server = new WebServer(9000,10);
		new Thread(server).start();
	}

	@AfterClass
	public static void tearDown() {
		System.out.println("Stopping Server");
		server.stopServer();
	}

	@Test
	public void testBadFilenameRequest()
	{
		try {
			Client testClient = new Client(InetAddress.getLocalHost(), 9000);
			params.clear();
			params.put("filename", "test");
			testClient.sendRequest("GET", headers, params, "test", "/");
			String response = testClient.receiveResponse();
			assertEquals("File not available under server\r\n", response);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testBadLocationRequest()
	{
		try {
			Client testClient = new Client(InetAddress.getLocalHost(), 9000);
			params.clear();
			testClient.sendRequest("GET", headers, params, "test", "/badLocation");
			String response = testClient.receiveResponse();
			assertEquals("Location not available\r\n", response);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
