package com.aem;

import com.aem.utils.ServerStatus;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by alexandru-petrisorpajarcu on 30/04/2016.
 */
public class WebServer implements Runnable {
	private int serverPort;
	private ExecutorService threadPool;
	private ServerStatus status;
	private ServerSocket serverSocket;

	public WebServer(int serverPort, int numberOfThreads) {
		this.serverPort = serverPort;
		this.threadPool = Executors.newFixedThreadPool(numberOfThreads);
	}


	public void run() {
		openServerSocket();
		while (isServerRunning()) {
			Socket clientSocket = null;
			try {
				clientSocket = this.serverSocket.accept();
			} catch (IOException e) {
				if (!isServerRunning()) {
					System.out.println("Server is down");
					break;
				}
				throw new RuntimeException("Couldn't accept new client");
			}
			this.threadPool.execute(new ClientHandler(clientSocket));
		}
		this.threadPool.shutdown();
		System.out.println("Server stopped");

	}

	private synchronized boolean isServerRunning() {
		return status.equals(ServerStatus.RUNNING);
	}

	public synchronized void stopServer(){
		this.status = ServerStatus.STOPPED;
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			throw new RuntimeException("Couldn't close server");
		}
	}

	private void openServerSocket() {
		try {
			this.serverSocket = new ServerSocket(this.serverPort);
			this.status = ServerStatus.RUNNING;
		} catch (IOException e) {
			throw new RuntimeException("Could start server on port:"+ this.serverPort);
		}
	}
}
