package com.aem.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InterfaceAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexandru-petrisorpajarcu on 03/05/2016.
 */
public class Request {
	Map<String, String> headerFields;
	Map<String, String> requestParams;
	String body;

	public Request(Socket socket) {
		InputStream inputStream = null;
		requestParams = new HashMap<>();
		try {
			inputStream = socket.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			this.headerFields = getHeaderFields(in);
			if (headerFields.containsKey("Content-Length")) {
				body = readBody(in, Integer.parseInt(headerFields.get("Content-Length")));
			} else {
				body = "";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String readBody(BufferedReader in, int numberOfChars) {
		String bodyString = "";
		char bodyStringBuffer[] = new char[numberOfChars];
		try {
			in.read(bodyStringBuffer);
			bodyString = new String(bodyStringBuffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bodyString;
	}

	private Map<String, String> getHeaderFields(BufferedReader in) {
		Map<String, String> headerFields = new HashMap();
		try {
			ArrayList<String> headerLines = new ArrayList<String>();
			String inputLine;
			while ((inputLine = in.readLine()) != null)
			{
				if (inputLine.equals("")) break;
				headerLines.add(inputLine);
			}
			if (headerLines.size() > 0) {
				String requestTypeLine = headerLines.remove(0);
				System.out.println(requestTypeLine);
				String[] requestTypeDetails = requestTypeLine.split(" ");
				headerFields.put("http-method", requestTypeDetails[0]);
				String location;
				if (requestTypeDetails[1].contains("?")) {
					location = requestTypeDetails[1].substring(0, requestTypeDetails[1].indexOf("?"));
					parseRequestParams(requestTypeDetails[1].substring(requestTypeDetails[1].indexOf("?")+1));
				} else {
					location = requestTypeDetails[1];
				}
				headerFields.put("domain-location", location);
				headerFields.put("http-version", requestTypeDetails[2]);
			}
			headerLines.forEach(line -> {
				String[] fieldWithValue = line.split(": ");
				headerFields.put(fieldWithValue[0], fieldWithValue[1]);
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
		return headerFields;
	}

	private void parseRequestParams(String params) {
		String[] paramsWithValue = params.split("&");
		for (int i = 0; i < paramsWithValue.length; i++) {
			String[] paramWithValue = paramsWithValue[i].split("=");
			this.requestParams.put(paramWithValue[0], paramWithValue[1]);
		}
	}

	public Map<String, String> getHeaderFields() {
		return headerFields;
	}

	public String getBody() {
		return body;
	}

	public Map<String, String> getRequestParams() {
		return requestParams;
	}
}
