package com.zuehlke.pgadmissions.utils;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ApplicationContext {

	private static ApplicationContext instance;
	private ClassPathXmlApplicationContext classPathXmlApplicationContext;
	
	private ApplicationContext() {
		 classPathXmlApplicationContext = new ClassPathXmlApplicationContext("testHibernateContext.xml");
	}

	public static synchronized ApplicationContext getInstance() {
		if (instance == null) {
			instance = new ApplicationContext();
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
