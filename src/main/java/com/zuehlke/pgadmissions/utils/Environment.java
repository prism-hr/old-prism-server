package com.zuehlke.pgadmissions.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Environment {

    private final Logger log = LoggerFactory.getLogger(Environment.class);
    
	private static Environment instance;
	
	private String applicationHost;
	private String emailFromAddress;
	private String emailToAddress;
	private String uclProspectusLink;
	private String admissionsOfferServieLevel;
	private String admissionsValidationServiceLevel;
	
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
			admissionsOfferServieLevel = environmentProperties.getProperty("admissions.servicelevel.offer");
			admissionsValidationServiceLevel = environmentProperties.getProperty("admissions.servicelevel.validation");
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				in.close();				
			} catch (Exception e) {
			    log.error(e.getMessage(), e);
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

	public String getAdmissionsOfferServiceLevel() {
		
		return admissionsOfferServieLevel;
	}

	public String getAdmissionsValidationServiceLevel() {		
		return admissionsValidationServiceLevel;
	}
}
