package com.zuehlke.pgadmissions.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Environment {

	private static Environment instance;
	private String applicationHost;
	private String emailFromAddress;
	private String emailToAddress;
	private String uclProspectusLink;
	
	private Environment() {
		InputStream in = null;
		try {
			Properties environmentProperties = new Properties();
			in = this.getClass().getResourceAsStream("/environment.properties");
			environmentProperties.load(in);
			applicationHost = environmentProperties.getProperty("application.host");
			emailFromAddress = environmentProperties.getProperty("email.address.from");
			emailToAddress = environmentProperties.getProperty("email.address.to");
			uclProspectusLink = environmentProperties.getProperty("ucl.prospectus.url");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();				
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

	public String getEmailFromAddress() {
		return emailFromAddress;
	}

	public String getEmailToAddress() {	
		return emailToAddress;
	}

	public String getUCLProspectusLink() {
		return uclProspectusLink;
	}
}
