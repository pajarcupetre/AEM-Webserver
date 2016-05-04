package com.aem.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexandru-petrisorpajarcu on 03/05/2016.
 */
public class Request {
	Map<String, String> headerFields;
	String body;

	public Request(Socket socket) {
		InputStream inputStream = null;
		try {
			inputStream = socket.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			this.headerFields = getHeaderFields(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				String[] requestTypeDetails = requestTypeLine.split(" ");
				headerFields.put("request-type", requestTypeDetails[0]);
				headerFields.put("domain-location", requestTypeDetails[1]);
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
}
