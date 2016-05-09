package com.aem;

import com.aem.utils.HTTPMethods;
import com.aem.utils.Response;
import junit.framework.TestCase;
import org.junit.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
			headers.put("Connection", "close");
			testClient.sendRequest(HTTPMethods.GET, headers, params, "test", "/");
			String response = testClient.receiveResponse(true).getBody();
			assertEquals("File not available under server", response);
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
			headers.put("Connection", "close");
			testClient.sendRequest(HTTPMethods.GET, headers, params, "test", "/badLocation");
			Response response = testClient.receiveResponse(true);

			assertEquals("Location not available", response.getBody());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testUploadAndDownload()
	{
		try {
			Client testClient = new Client(InetAddress.getLocalHost(), 9000);
			params.clear();
			headers = postHeadersForUpload();
			String body = postBody();
			headers.put("Content-Length", body.length()+"");
			testClient.sendRequest(HTTPMethods.POST, headers, params, body, "/");
			Response response = testClient.receiveResponse(true);
			assertEquals("", response.getBody());
			params.clear();
			params.put("filename", "Test.java");
			headers.clear();
			headers.put("Connection", "keep-alive");
			testClient.sendRequest(HTTPMethods.GET, headers, params, "", "/");
			response = testClient.receiveResponse(true);
			Map<String, String> responseHeaderValues = response.getHeaderFields();
			String responseExpected = "/**\n" +
					" * Created by alexandru-petrisorpajarcu on 09/03/2016.\n" +
					" */\n" +
					"public class Test {\n" +
					"\tpublic int add(int a, int b) {\n" +
					"\t\treturn a+b;\n" +
					"\t}\n" +
					"\tpublic String add(int a, int b){\n" +
					"\t\treturn a+\"+\"+b;\n" +
					"\t}\n" +
					"}\n\n";
			assertEquals(responseExpected, response.getBody());

			headers.put("Connection", "close");
			testClient.sendRequest(HTTPMethods.HEAD, headers, params, "", "/");
			response = testClient.receiveResponse(false);
			Map<String, String> headResponseHeader = response.getHeaderFields();
			assertEquals(headResponseHeader.get("Connection"),"close");

			headResponseHeader.remove("Connection");
			responseHeaderValues.remove("Connection");
			assertEquals(response.getHeaderFields(), responseHeaderValues);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private HashMap<String,String> postHeadersForUpload() {
		HashMap<String, String> uploadHeaders = new HashMap<>();
		uploadHeaders.put("Cache-Control","no-cache");
		uploadHeaders.put("Content-Type", "multipart/form-data; boundary=jjSqLsGKRdCp_YL4YogVi4HakyhrmB9PrWBf");
		uploadHeaders.put("Host", "localhost:8080");
		uploadHeaders.put("Connection", "Keep-Alive");
		uploadHeaders.put("User-Agent", "Apache-HttpClient/4.4.1 (Java/1.8.0_40-release)");
		uploadHeaders.put("Accept-Encoding", "gzip,deflate");
		return uploadHeaders;
	}

	private String postBody() {
		String body = "--jjSqLsGKRdCp_YL4YogVi4HakyhrmB9PrWBf\n";
		body += "Content-Disposition: form-data; name=\"Test.java\"; filename=\"Test.java\"\n" +
				"Content-Type: application/octet-stream\n" +
				"Content-Transfer-Encoding: binary\n";
		body += "\n";
		body += "/**\n" +
				" * Created by alexandru-petrisorpajarcu on 09/03/2016.\n" +
				" */\n" +
				"public class Test {\n" +
				"\tpublic int add(int a, int b) {\n" +
				"\t\treturn a+b;\n" +
				"\t}\n" +
				"\tpublic String add(int a, int b){\n" +
				"\t\treturn a+\"+\"+b;\n" +
				"\t}\n" +
				"}\n" +
				"\n";
		body += "--jjSqLsGKRdCp_YL4YogVi4HakyhrmB9PrWBf--\n";
		return body;
	}
}
