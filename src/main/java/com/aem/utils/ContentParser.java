package com.aem.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexandru-petrisorpajarcu on 09/05/2016.
 */
public class ContentParser {


	public static String readBody(BufferedReader in, int numberOfChars) {
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

	public static String readBodyInChuncks(BufferedReader in) {
		int chunckSize = 0;
		String bodyString = "";
		do {
			try {
				String chunckSizeLine = in.readLine();
				if (chunckSizeLine.contains(";")){
					chunckSizeLine = chunckSizeLine.substring(0,chunckSizeLine.indexOf(";"));
				}
				chunckSize = Integer.parseInt(chunckSizeLine, 16);
				if (chunckSize == 0 ){
					break;
				} else {
					char bodyStringBuffer[] = new char[chunckSize];
					in.read(bodyStringBuffer);
					bodyString += new String(bodyStringBuffer);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (chunckSize>0);
		return bodyString;
	}

	public static Map<String, String> getHeaderFieldsFromContent(BufferedReader in) {
		Map<String, String> headerFields = new HashMap();
		try {
			ArrayList<String> headerLines = new ArrayList<String>();
			String inputLine;
			while ((inputLine = in.readLine()) != null)
			{
				if (inputLine.equals("") && headerLines.size()>0) {
					break;
				} else if (inputLine.equals("")){
					continue;
				} else {
					headerLines.add(inputLine);
				}
			}
			if (headerLines.size() > 0) {
				String requestTypeLine = headerLines.remove(0);
				String[] requestTypeDetails = requestTypeLine.split(" ");
				headerFields.put("http-method", requestTypeDetails[0]);
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
