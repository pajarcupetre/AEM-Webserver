package com.aem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by alexandru-petrisorpajarcu on 02/05/2016.
 */
public class ClientHandler implements Runnable {
	private Socket clientSocket;

	public ClientHandler(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public void run() {
		try {
			InputStream inputStream = clientSocket.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

			String inputLine;

			while ((inputLine = in.readLine()) != null)
			{
				System.out.println(inputLine);
			}
			System.out.println("out");
			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
