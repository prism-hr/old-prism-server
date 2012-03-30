package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.itextpdf.text.Document;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.ApplicationsService;


public class PrintControllerTest {

	
	private PrintController controller;
	private ApplicationsService applicationSevice;

	@Test
	public void shouldReturnNotNullDocument() throws IOException {
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);  
		HttpServletResponse response = EasyMock.createControl().createMock(HttpServletResponse.class);
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");
		response.setHeader("Content-Disposition", "inline; filename=\"application23.pdf\"");
		response.setContentType("application/pdf");
		response.setContentLength(1995);
		EasyMock.expect(response.getOutputStream()).andReturn(new ServletOutputStream() {
			@Override
			public void write(int b) throws IOException {}
		});

		EasyMock.expect(request.getParameter("applicationFormId")).andReturn("23");
		EasyMock.expect(request.getParameter("applicationFormId")).andReturn("23");
		
		ApplicationForm applicationForm = new ApplicationForm();
		applicationForm.setApplicant(new RegisteredUser());
		Project project = new Project();
		project.setProgram(new Program());
		applicationForm.setProject(project);
		EasyMock.expect(applicationSevice.getApplicationById(23)).andReturn(applicationForm);
		
		EasyMock.replay(request, response, applicationSevice);
		
		Document pdf = controller.printPage(request, response);
		Assert.assertNotNull(pdf);
		
	}
	
	@Before
	public void setUp() {
		applicationSevice = EasyMock.createMock(ApplicationsService.class);
		controller = new PrintController(applicationSevice);
	}
}
