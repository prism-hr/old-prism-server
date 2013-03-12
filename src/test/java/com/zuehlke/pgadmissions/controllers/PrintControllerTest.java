package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.ServletRequestBindingException;

import com.itextpdf.text.DocumentException;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.pdf.PdfModelBuilder;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

public class PrintControllerTest {

	private PrintController controller;
	private ApplicationsService applicationSevice;
	private PdfDocumentBuilder pdfDocumentBuilderMock;
	private UserService userServiceMock;
	private RegisteredUser currentUser;

	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUserCannotSeeApplicationForm() throws ServletRequestBindingException, IOException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("applicationFormId", "23");
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setOutputStreamAccessAllowed(true);	

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).build();
		EasyMock.expect(applicationSevice.getApplicationByApplicationNumber("23")).andReturn(applicationForm).anyTimes();
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(false);
		EasyMock.replay(currentUser);
		controller.printPage(request, response);
		
	}
	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicationFormDoesNotExist() throws ServletRequestBindingException, IOException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("applicationFormId", "23");
		MockHttpServletResponse response = new MockHttpServletResponse();				
		EasyMock.expect(applicationSevice.getApplicationByApplicationNumber("23")).andReturn(null).anyTimes();
		EasyMock.replay(applicationSevice);
		controller.printPage(request, response);
	}

	@Test
	public void shouldBuildPDFForApplicationAndSend() throws IOException, ServletRequestBindingException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("applicationFormId", "23");
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setOutputStreamAccessAllowed(true);		

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).applicant(currentUser).build();
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(currentUser);
		EasyMock.expect(applicationSevice.getApplicationByApplicationNumber("23")).andReturn(applicationForm).anyTimes();
		byte[] bytes = "pdf".getBytes();
		EasyMock.expect(pdfDocumentBuilderMock.build(EasyMock.isA(PdfModelBuilder.class), EasyMock.eq(applicationForm))).andReturn(bytes);

		EasyMock.replay(applicationSevice, pdfDocumentBuilderMock);

		controller.printPage(request, response);

		assertArrayEquals(bytes, response.getContentAsByteArray());
		assertEquals("0", response.getHeader("Expires"));
		assertEquals("must-revalidate, post-check=0, pre-check=0", response.getHeader("Cache-Control"));
		assertEquals("public", response.getHeader("Pragma"));
		assertEquals("inline; filename=\"UCL_PRISM_23.pdf\"", response.getHeader("Content-Disposition"));
		assertEquals("application/pdf", response.getContentType());
		assertEquals(bytes.length, response.getContentLength());
	}

    @Test
    @SuppressWarnings("unchecked")
	public void shouldBuildPDFForAllSelectedApplicationsAndSend() throws IOException, ServletRequestBindingException, DocumentException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("appList", "23;34;");
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setOutputStreamAccessAllowed(true);		

		ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(2).applicant(new RegisteredUserBuilder().id(4).build()).build();
		ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(3).applicant(new RegisteredUserBuilder().id(5).build()).build();
		EasyMock.expect(currentUser.canSee(applicationFormOne)).andReturn(true);
		EasyMock.expect(currentUser.canSee(applicationFormTwo)).andReturn(true);
		EasyMock.expect(currentUser.hasAdminRightsOnApplication(EasyMock.eq(applicationFormOne))).andReturn(true);
		EasyMock.expect(currentUser.hasAdminRightsOnApplication(EasyMock.eq(applicationFormTwo))).andReturn(true);
		EasyMock.replay(currentUser);
		EasyMock.expect(applicationSevice.getApplicationByApplicationNumber("23")).andReturn(applicationFormOne).anyTimes();
		EasyMock.expect(applicationSevice.getApplicationByApplicationNumber("34")).andReturn(applicationFormTwo).anyTimes();
		byte[] bytes = "pdf".getBytes();
        EasyMock.expect(pdfDocumentBuilderMock.build(EasyMock.isA(HashMap.class))).andReturn(bytes);

		EasyMock.replay(applicationSevice, pdfDocumentBuilderMock);

		controller.printAll(request, response);

		assertArrayEquals(bytes, response.getContentAsByteArray());
		assertEquals("0", response.getHeader("Expires"));
		assertEquals("must-revalidate, post-check=0, pre-check=0", response.getHeader("Cache-Control"));
		assertEquals("public", response.getHeader("Pragma"));
		assertEquals("inline; filename=\"UCL_PRISM_timestamp.pdf\"", response.getHeader("Content-Disposition"));
		assertEquals("application/pdf", response.getContentType());
		assertEquals(bytes.length, response.getContentLength());
	}

    @Test
    @SuppressWarnings("unchecked")
	public void shouldSkipNullApplications() throws ServletRequestBindingException, DocumentException, IOException{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("appList", "23;34;");
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setOutputStreamAccessAllowed(true);		

		ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(3).applicant(currentUser).build();
		
		EasyMock.expect(currentUser.canSee(applicationFormTwo)).andReturn(true);
		EasyMock.replay(currentUser);
		EasyMock.expect(applicationSevice.getApplicationByApplicationNumber("23")).andReturn(null).anyTimes();
		EasyMock.expect(applicationSevice.getApplicationByApplicationNumber("34")).andReturn(applicationFormTwo).anyTimes();
		byte[] bytes = "pdf".getBytes();
        EasyMock.expect(pdfDocumentBuilderMock.build(EasyMock.isA(HashMap.class))).andReturn(bytes);
		
		EasyMock.replay(applicationSevice, pdfDocumentBuilderMock);

		controller.printAll(request, response);
		
		assertArrayEquals(bytes, response.getContentAsByteArray());
		assertEquals("0", response.getHeader("Expires"));
		assertEquals("must-revalidate, post-check=0, pre-check=0", response.getHeader("Cache-Control"));
		assertEquals("public", response.getHeader("Pragma"));
		assertEquals("inline; filename=\"UCL_PRISM_timestamp.pdf\"", response.getHeader("Content-Disposition"));
		assertEquals("application/pdf", response.getContentType());
		assertEquals(bytes.length, response.getContentLength());
	}
	
	@Test
	public void shouldSkipApplicationsUserCannotSees() throws ServletRequestBindingException, DocumentException, IOException{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("appList", "23;34;");
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setOutputStreamAccessAllowed(true);		

		ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(2).applicant(currentUser).build();
		ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(3).applicant(currentUser).build();
		EasyMock.expect(currentUser.canSee(applicationFormOne)).andReturn(false);
		EasyMock.expect(currentUser.canSee(applicationFormTwo)).andReturn(true);
		EasyMock.replay(currentUser);
		EasyMock.expect(applicationSevice.getApplicationByApplicationNumber("23")).andReturn(applicationFormOne).anyTimes();
		EasyMock.expect(applicationSevice.getApplicationByApplicationNumber("34")).andReturn(applicationFormTwo).anyTimes();
        byte[] bytes = "pdf".getBytes();
        EasyMock.expect(pdfDocumentBuilderMock.build(EasyMock.isA(HashMap.class))).andReturn(bytes);

		EasyMock.replay(applicationSevice, pdfDocumentBuilderMock);

		controller.printAll(request, response);

		assertArrayEquals(bytes, response.getContentAsByteArray());
		assertEquals("0", response.getHeader("Expires"));
		assertEquals("must-revalidate, post-check=0, pre-check=0", response.getHeader("Cache-Control"));
		assertEquals("public", response.getHeader("Pragma"));
		assertEquals("inline; filename=\"UCL_PRISM_timestamp.pdf\"", response.getHeader("Content-Disposition"));
		assertEquals("application/pdf", response.getContentType());
		assertEquals(bytes.length, response.getContentLength());
	}
	
	@Before
	public void setUp() {
		applicationSevice = EasyMock.createMock(ApplicationsService.class);
		pdfDocumentBuilderMock = EasyMock.createMock(PdfDocumentBuilder.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		controller = new PrintController(applicationSevice, pdfDocumentBuilderMock, userServiceMock){
			@Override
			protected String getTimestamp() {
				return "timestamp";
			}
		};
		currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(currentUser.getId()).andReturn(1).anyTimes();
		
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
