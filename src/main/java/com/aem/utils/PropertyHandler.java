package com.aem.utils;

import java.io.*;
import java.util.Properties;

/**
 * Created by alexandru-petrisorpajarcu on 05/05/2016.
 */
public class PropertyHandler {
	public static Properties loadProperties() {

		InputStream inputStream = null;
		Properties properties =  new Properties();
		try {
			//Get file from resources folder
			ClassLoader classLoader = PropertyHandler.class.getClassLoader();
			File file = new File(classLoader.getResource("application.properties").getFile());
			inputStream = new FileInputStream(file);
			properties.load(inputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return properties;
	}
}
