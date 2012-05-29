package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.bind.ServletRequestBindingException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.services.ApplicationsService;

import cucumber.annotation.After;


public class PrintControllerTest {

	
	private PrintController controller;
	private ApplicationsService applicationSevice;

	@Test
	@Ignore
	public void shouldReturnNotNullDocument() throws IOException {
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);  
		HttpServletResponse response = EasyMock.createControl().createMock(HttpServletResponse.class);
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");
		response.setHeader("Content-Disposition", "inline; filename=\"application23.pdf\"");
		response.setContentType("application/pdf");
		response.setContentLength(EasyMock.anyInt());
		EasyMock.expect(response.getOutputStream()).andReturn(new ServletOutputStream() {
			@Override
			public void write(int b) throws IOException {}
		});

		EasyMock.expect(request.getParameter("applicationFormId")).andReturn("23");
		EasyMock.expect(request.getParameter("applicationFormId")).andReturn("23");
		
		ApplicationForm applicationForm = new ApplicationForm();
		applicationForm.setApplicant(new RegisteredUser());
		
		applicationForm.setProgram(new Program());
		
		EasyMock.expect(applicationSevice.getApplicationByApplicationNumber("23")).andReturn(applicationForm);
		
		EasyMock.replay(request, response, applicationSevice);
		
		Document pdf = controller.printPage(request, response);
		Assert.assertNotNull(pdf);
	}
	
	@Test
	@Ignore
	public void shouldReturnNotNullDocs() throws IOException, ServletRequestBindingException, DocumentException {
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);  
		HttpServletResponse response = EasyMock.createControl().createMock(HttpServletResponse.class);
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");
		response.setHeader("Content-Disposition", "attachment; filename=\"applications.pdf\"");
		response.setContentType("application/pdf");
		response.setContentLength(EasyMock.anyInt());
		EasyMock.expect(response.getOutputStream()).andReturn(new ServletOutputStream() {
			@Override
			public void write(int b) throws IOException {}
		});

		EasyMock.expect(request.getParameter("appList")).andReturn("23;34;");
		EasyMock.expect(request.getParameter("appList")).andReturn("23;34;");
		
		ApplicationForm applicationForm = new ApplicationForm();
		applicationForm.setApplicant(new RegisteredUser());
		
		applicationForm.setProgram(new Program());
		
		EasyMock.expect(applicationSevice.getApplicationByApplicationNumber("23")).andReturn(applicationForm);
		EasyMock.expect(applicationSevice.getApplicationByApplicationNumber("34")).andReturn(applicationForm);
		
		EasyMock.replay(request, response, applicationSevice);
		
		Document pdf = controller.printAll(request, response);
		Assert.assertNotNull(pdf);
	}
	
	@Before
	public void setUp() {
		applicationSevice = EasyMock.createMock(ApplicationsService.class);
		controller = new PrintController(applicationSevice, null);
		RegisteredUser currentUser = new RegisteredUserBuilder().id(1).toUser();
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);

		authenticationToken.setDetails(currentUser);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
	}
	@After
	public void tearDown(){
		SecurityContextHolder.clearContext();
	}
}
