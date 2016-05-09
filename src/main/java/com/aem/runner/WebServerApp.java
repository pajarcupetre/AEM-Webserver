package com.aem.runner;

import com.aem.WebServer;
import com.aem.utils.PropertyHandler;

import java.util.Properties;

/**
 * Created by alexandru-petrisorpajarcu on 05/05/2016.
 */
public class WebServerApp {

	public static void main(String[] args) {
		int threads;
		int port;
		if (args.length == 2) {
			port = Integer.parseInt(args[0]);
			threads	= Integer.parseInt(args[1]);
		} else {
			Properties properties = PropertyHandler.loadProperties();
			threads = Integer.parseInt(properties.getProperty("aem.webserver.numberOfThreads"));
			port = Integer.parseInt(properties.getProperty("aem.webserver.port"));
		}
		WebServer webServer = new WebServer(port, threads);
		new Thread(webServer).start();
	}
}
