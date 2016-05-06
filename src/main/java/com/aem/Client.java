package com.aem;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by alexandru-petrisorpajarcu on 05/05/2016.
 */
public class Client {
	InetAddress host;
	Integer port;
	Socket socket;

	Client(InetAddress host, Integer port){
		this.host = host;
		this.port = port;
		try {
			socket = new Socket(host.getHostName(), port.intValue());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void sendRequest(String http_method, HashMap<String, String> httpHeaders, HashMap<String,String> params, String body, String domainLocation){
		try {
			OutputStream outputStream = socket.getOutputStream();
			String location = domainLocation+"?";
			for (String key : params.keySet()) {
				location += key + "=" + params.get(key) + "&";
			}
			location = location.substring(0, location.length()-1);
			outputStream.write((http_method+" "+location+" HTTP/1.1\r\n").getBytes());
			httpHeaders.keySet().forEach(key -> {
				try {
					outputStream.write((key + ":" + httpHeaders.get(key) + "\r\n").getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			outputStream.write("\r\n".getBytes());
			outputStream.write(body.getBytes());
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	String receiveResponse() {
		String response = "";
		try {
			InputStream inputStream = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(inputStream);
			BufferedReader br = new BufferedReader(isr);
			StringBuilder out = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				out.append(line);
				System.out.println(line);
			}
			response = out.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

}
