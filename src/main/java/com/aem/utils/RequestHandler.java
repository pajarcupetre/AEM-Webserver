package com.aem.utils;

import com.aem.utils.PropertyHandler;
import com.aem.utils.Request;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;

/**
 * Created by alexandru-petrisorpajarcu on 02/05/2016.
 */
public class RequestHandler implements Runnable {
	private Socket clientSocket;
	private Properties properties;

	public RequestHandler(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public void run() {
		properties = PropertyHandler.loadProperties();
		Request request = new Request(clientSocket);
		Map<String, String> requestHeaders = request.getHeaderFields();
		Map<String, String> requestParams = request.getRequestParams();
		if (!requestHeaders.get("domain-location").equals("/")) {
			sendBadLocationResponse(clientSocket);
		} else {
			switch (requestHeaders.get("http-method")) {
				case "GET" :
					handleGetRequest(requestHeaders, requestParams, clientSocket);
					break;
				case "POST":
					handlePostRequest(requestHeaders, requestParams, request.getBody(), clientSocket);
					break;
				default:
					sendBadRequestResponse(clientSocket, "Unsupported HTPP method\r\n");
			}
		}
	}

	private void sendBadRequestResponse(Socket clientSocket, String response) {
		sendResponse(clientSocket, 406, "406 Not Acceptable", response);
	}

	private void handlePostRequest(Map<String, String> requestHeaders, Map<String, String> requestParams, String body, Socket clientSocket) {
		String contentType = requestHeaders.get("Content-Type");
		/*
		 *	"Content-Type" -> "multipart/form-data; boundary=AkAVcssdZzXLi5cHYbCjCCfqxh7nv6"
		 */
		String boundary = contentType.substring(contentType.indexOf("=")+1);
		boundary = "--"+boundary+"--";
		String outdir = properties.getProperty("aem.webserver.fileUploadPath");
		String[] bodyLines = body.split("\n");
		/* Format
		 * 	--AkAVcssdZzXLi5cHYbCjCCfqxh7nv6
		 *	Content-Disposition: form-data; name="filename"; filename="filename"
		 *  Content-Type: application/octet-stream
		 *	Content-Transfer-Encoding: binary
		 *
		 */
		String filename = bodyLines[1].split("; ")[2].split("=")[1];
		filename = filename.substring(1, filename.lastIndexOf("\""));
		File file = new File(outdir + File.separator + filename);
		File outdirFile = new File(outdir);
		if (!outdirFile.exists()) {
			outdirFile.mkdirs();
		}
		try {
			PrintWriter writer = new PrintWriter(file.getAbsolutePath());
			for (int i=5; i< bodyLines.length -1; i++) {
				writer.println(bodyLines[i]);
			}
			writer.close();
			sendResponseOK(clientSocket);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	private void handleGetRequest(Map<String, String> requestHeaders, Map<String, String> requestParams, Socket socket) {
		if (requestParams.containsKey("filename")) {
			String filename = requestParams.get("filename");
			String indir = properties.getProperty("aem.webserver.fileUploadPath");
			File fileToSend = new File(indir + File.separator + filename);
			if (fileToSend.exists()) {
				try {
					OutputStream outStream = socket.getOutputStream();
					outStream.write(("HTTP/1.1 200 OK \r\n").getBytes());
					outStream.write(("Content-Type: application/octet-stream\r\n").getBytes());
					outStream.write(("Content-Disposition: attachment; filename=\""+filename+"\"\r\n\r\n").getBytes());
					Files.copy(fileToSend.toPath(),outStream);
					outStream.flush();
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				sendResponse(clientSocket, 500, "Internal Server Error", "File not available under server\n");
			}
		} else {
			sendResponseOK(clientSocket);
		}
	}

	private void sendResponseOK(Socket clientSocket) {
		sendResponse(clientSocket, 200, "OK", "");
	}

	private void sendBadLocationResponse(Socket clientSocket) {
		sendResponse(clientSocket, 404, "Not Found", "Location not available\r\n");
	}

	private void sendResponse(Socket clientSocket, int code, String status, String responseString) {
		try {
			OutputStream outStream = clientSocket.getOutputStream();
			outStream.write(("HTTP/1.1 "+ code + " "+ status +"\r\n\r\n"+responseString+"\r\n").getBytes());
			outStream.flush();
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
