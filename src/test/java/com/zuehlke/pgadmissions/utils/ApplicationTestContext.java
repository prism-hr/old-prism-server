package com.zuehlke.pgadmissions.utils;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ApplicationTestContext {

	private static ApplicationTestContext instance;
	private ClassPathXmlApplicationContext classPathXmlApplicationContext;
	
	private ApplicationTestContext() {
		 classPathXmlApplicationContext = new ClassPathXmlApplicationContext("testHibernateContext.xml");
	}

	public static synchronized ApplicationTestContext getInstance() {
		if (instance == null) {
			instance = new ApplicationTestContext();
		}
		return instance;
	}

	public ClassPathXmlApplicationContext getClassPathXmlApplicationContext() {
		return classPathXmlApplicationContext;
	}

	public void setClassPathXmlApplicationContext(ClassPathXmlApplicationContext classPathXmlApplicationContext) {
		this.classPathXmlApplicationContext = classPathXmlApplicationContext;
	}
}
