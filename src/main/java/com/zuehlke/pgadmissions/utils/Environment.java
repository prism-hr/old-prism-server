package com.zuehlke.pgadmissions.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Environment {

	private static Environment instance;
	private String applicationHost;

	private Environment() {
		InputStream in = null;
		try {
			Properties environmentProperties = new Properties();
			in = this.getClass().getResourceAsStream("/environment.properties");
			environmentProperties.load(in);
			applicationHost = environmentProperties.getProperty("application.host");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				;
			} catch (Exception e) {
				// ignore
			}
		}
		
	}

	public static synchronized Environment getInstance() {
		if (instance == null) {
			instance = new Environment();

		}
		return instance;
	}

	public String getApplicationHostName() {		
		return applicationHost;
	}
}
