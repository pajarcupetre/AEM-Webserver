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

import static com.aem.utils.ContentParser.*;

/**
 * Created by alexandru-petrisorpajarcu on 03/05/2016.
 */
public class Request {
	Map<String, String> headerFields;
	Map<String, String> requestParams;
	String body;

	public Request(BufferedReader inputStreamReader) {
		requestParams = new HashMap<>();
		this.headerFields = getHeaderFieldsFromContent(inputStreamReader);
		String location = headerFields.get("domain-location");
		if (location.contains("?")) {
			parseRequestParams(location.substring(location.indexOf("?")+1));
			headerFields.put("domain-location", location.substring(0, location.indexOf("?")));
		}
		if (headerFields.containsKey("Content-Length")) {
			body = readBody(inputStreamReader, Integer.parseInt(headerFields.get("Content-Length")));
		} else if (headerFields.containsKey("Transfer-Encoding") && headerFields.get("Transfer-Encoding").equals("Chunked")) {
			body = readBodyInChuncks(inputStreamReader);
		} else {
			body = "";
		}
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
