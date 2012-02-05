package com.zuehlke.pgadmissions.spring;

import java.io.File;

import javax.servlet.ServletContext;

import org.junit.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class SpringConfigTest {

	@Test
	public void shouldLoadSpringConfigWithoutError() throws Exception {

		ServletContext servletContext = new MockServletContext();

		XmlWebApplicationContext context = new XmlWebApplicationContext();
		context.setServletContext(servletContext);
		File file = new File("testContext.xml" );
		System.out.print(file.getAbsolutePath() + ", exists " + file.exists());
		
		context.setConfigLocations(new String[] { "testContext.xml" });

		context.refresh();


	}
}
