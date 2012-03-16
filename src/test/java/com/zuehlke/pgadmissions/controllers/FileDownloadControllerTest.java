package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.DocumentService;

public class FileDownloadControllerTest {
	private DocumentService documentServiceMock;
	private FileDownloadController controller;
	private RegisteredUser currentUser;

	@Test
	public void shouldGetDocumentFromServiceAndWriteContentToResponse() throws IOException{
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(3).toApplicationForm();
		Document document = new DocumentBuilder().applicationForm(applicationForm).content("aaaa".getBytes()).id(1).toDocument();
		EasyMock.expect(documentServiceMock.getDocumentById(1)).andReturn(document);
		EasyMock.replay(documentServiceMock);
		
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(currentUser);
		
		
		HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ServletOutputStream servletOutputStream = new ServletOutputStream() {			
			
			@Override
			public void write(int b) throws IOException {
				byteArrayOutputStream.write(b);			
			}
		};
		
		
		EasyMock.expect(responseMock.getOutputStream()).andReturn(servletOutputStream);
		EasyMock.replay(responseMock);
		controller.download(1, responseMock);
		
		EasyMock.verify(documentServiceMock);
		
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		assertEquals("aaaa", new String(byteArray));
	}
	
	
	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfCurrentUserCannotSeeApplicationForm() throws IOException{
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(3).toApplicationForm();
		Document document = new DocumentBuilder().applicationForm(applicationForm).content("aaaa".getBytes()).id(1).toDocument();
		EasyMock.expect(documentServiceMock.getDocumentById(1)).andReturn(document);
		EasyMock.replay(documentServiceMock);
		
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(false);
		EasyMock.replay(currentUser);
	

		controller.download(1, new MockHttpServletResponse());

	}
	
	@Before
	public void setup() {

		documentServiceMock = EasyMock.createMock(DocumentService.class);
		controller = new FileDownloadController( documentServiceMock);
		currentUser = EasyMock.createMock(RegisteredUser.class);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);

		authenticationToken.setDetails(currentUser);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
