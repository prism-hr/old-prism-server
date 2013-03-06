package com.zuehlke.pgadmissions.spring;

import javax.servlet.ServletContext;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class SpringConfigTest {

	@Test
	@Ignore
	public void shouldLoadSpringTestContextWithoutError() throws Exception {
		ServletContext servletContext = new MockServletContext();	
		XmlWebApplicationContext context = new XmlWebApplicationContext();
		context.setServletContext(servletContext);
		context.setConfigLocation("/testContext.xml");
		context.refresh();
	}
	
	@Test
	@Ignore
    public void shouldLoadSpringApplicationContextWithoutError() throws Exception {
        ServletContext servletContext = new MockServletContext();   
        XmlWebApplicationContext context = new XmlWebApplicationContext();
        context.setServletContext(servletContext);
        context.setConfigLocation("file:src/main/webapp/WEB-INF/spring/application-context.xml");
        context.refresh();
    }
}
