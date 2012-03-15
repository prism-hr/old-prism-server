package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.DocumentService;

public class DeleteFileControllerTest {

	
	private DocumentService documentServiceMock;
	private DeleteFileController controller;
	private RegisteredUser currentUser;
	
	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfCurrentUserNotApplicant() throws IOException{
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(new RegisteredUserBuilder().id(1).toUser()).id(3).submissionStatus(SubmissionStatus.UNSUBMITTED).toApplicationForm();
		Document document = new DocumentBuilder().applicationForm(applicationForm).content("aaaa".getBytes()).id(1).toDocument();
		EasyMock.expect(documentServiceMock.getDocumentById(1)).andReturn(document);
		EasyMock.replay(documentServiceMock);
		controller.delete(1);
		EasyMock.verify(documentServiceMock);

	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIDocumentDoesNotExist() throws IOException{	
		EasyMock.expect(documentServiceMock.getDocumentById(1)).andReturn(null);
		EasyMock.replay(documentServiceMock);
		controller.delete(1);
		EasyMock.verify(documentServiceMock);
	}
	
	
	@Test(expected=CannotUpdateApplicationException.class)
	public void shouldCannotUpdateApplicationExceptionIfApplicationFormSubmitted() throws IOException{	
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(currentUser).id(3).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		Document document = new DocumentBuilder().applicationForm(applicationForm).content("aaaa".getBytes()).id(1).toDocument();
		EasyMock.expect(documentServiceMock.getDocumentById(1)).andReturn(document);
		EasyMock.replay(documentServiceMock);
		controller.delete(1);
		EasyMock.verify(documentServiceMock);
	}
	
	@Test
	public void shouldGetDocumentFromServiceAndDelete(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(currentUser).id(3).submissionStatus(SubmissionStatus.UNSUBMITTED).toApplicationForm();
		Document document = new DocumentBuilder().applicationForm(applicationForm).content("aaaa".getBytes()).id(1).toDocument();
		EasyMock.expect(documentServiceMock.getDocumentById(1)).andReturn(document);
		documentServiceMock.delete(document);
		EasyMock.replay(documentServiceMock);
		ModelAndView modelAndView = controller.delete(1);
		assertEquals("redirect:/application", modelAndView.getViewName());
		assertEquals(3, modelAndView.getModel().get("id"));		
	}
	
	@Before
	public void setup() {

		documentServiceMock = EasyMock.createMock(DocumentService.class);
		controller = new DeleteFileController( documentServiceMock);
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
