package com.aem.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.aem.utils.ContentParser.getHeaderFieldsFromContent;
import static com.aem.utils.ContentParser.readBody;
import static com.aem.utils.ContentParser.readBodyInChuncks;

/**
 * Created by alexandru-petrisorpajarcu on 03/05/2016.
 */
public class Response {
	Map<String, String> headerFields;
	String body;

	public Response(BufferedReader inputStreamReader) {
		this.headerFields = getHeaderFieldsFromContent(inputStreamReader);
		if (headerFields.containsKey("Content-Length")) {
			body = readBody(inputStreamReader, Integer.parseInt(headerFields.get("Content-Length")));
		} else if (headerFields.containsKey("Transfer-Encoding") && headerFields.get("Transfer-Encoding").equals("Chunked")) {
			body = readBodyInChuncks(inputStreamReader);
		} else {
			body = "";
		}
	}

	public Map<String, String> getHeaderFields() {
		return headerFields;
	}

	public String getBody() {
		return body;
	}

}
