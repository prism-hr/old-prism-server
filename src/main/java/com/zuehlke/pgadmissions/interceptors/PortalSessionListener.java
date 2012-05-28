package com.zuehlke.pgadmissions.interceptors;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.lang.RandomStringUtils;

public class PortalSessionListener implements HttpSessionListener {

	
	@Override
	public void sessionCreated(HttpSessionEvent se) {
		String random = RandomStringUtils.randomAlphanumeric(16);
		System.err.println("Creating key for " +  random);
		SecretKeySpec key = new SecretKeySpec(random.getBytes(), "AES");
		se.getSession().setAttribute("key", key);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		

	}

}
