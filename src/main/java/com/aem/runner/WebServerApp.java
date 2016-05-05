package com.aem.runner;

import com.aem.WebServer;
import com.aem.utils.PropertyHandler;

import java.util.Properties;

/**
 * Created by alexandru-petrisorpajarcu on 05/05/2016.
 */
public class WebServerApp {

	public static void main(String[] args) {
		Properties properties = PropertyHandler.loadProperties();
		int threads = Integer.parseInt(properties.getProperty("aem.webserver.numberOfThreads"));
		int port = Integer.parseInt(properties.getProperty("aem.webserver.port"));
		WebServer webServer = new WebServer(port, threads);
		new Thread(webServer).start();
	}
}
