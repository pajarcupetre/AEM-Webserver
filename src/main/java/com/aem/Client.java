package com.aem;

import com.aem.utils.Response;

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
	BufferedReader bufferedReader;

	Client(InetAddress host, Integer port){
		this.host = host;
		this.port = port;
		try {
			socket = new Socket(host.getHostName(), port.intValue());
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
		Response response = new Response(bufferedReader);
		return response.getBody();
	}

}
