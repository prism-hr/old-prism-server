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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.validators.DocumentValidator;

public class FileUploadControllerTest {

	private FileUploadController controller;
	private ApplicationsService applicationsServiceMock;
	private RegisteredUser currentUser;
	private DocumentValidator documentValidatorMock;
	private BindingResult errors;
	private Document document;

	@Test
	public void shouldGetApplicationFormFromService() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(currentUser).id(2).submissionStatus(SubmissionStatus.UNSUBMITTED)
				.toApplicationForm();

		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);

		ApplicationForm returnedForm = controller.getApplicationForm(1);
		assertEquals(applicationForm, returnedForm);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicationFormDoNotExist() {

		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm(1);

	}

	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldThrowCannotUpdateApplicationExceptionIfApplicationFormNotInUnsubmmitedState() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(currentUser).id(2).submissionStatus(SubmissionStatus.SUBMITTED)
				.toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm(2);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionIfCurrentUserNotApplicant() {
		RegisteredUser applicant = new RegisteredUserBuilder().id(6).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.UNSUBMITTED).applicant(applicant)
				.toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm(2);

	}

	@Test
	public void shouldCreateDocumentFromFile() throws IOException {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		MultipartFile multipartFileMock = EasyMock.createMock(MultipartFile.class);
		EasyMock.expect(multipartFileMock.getOriginalFilename()).andReturn("filename");
		EasyMock.expect(multipartFileMock.getContentType()).andReturn("ContentType");
		EasyMock.expect(multipartFileMock.getBytes()).andReturn("lala".getBytes());
		EasyMock.replay(multipartFileMock);

		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		controller.uploadFile(applicationForm, DocumentType.CV, multipartFileMock);

		EasyMock.verify(applicationsServiceMock);
		assertEquals(1, applicationForm.getSupportingDocuments().size());
		Document document = applicationForm.getSupportingDocuments().get(0);
		assertEquals("filename", document.getFileName());
		assertEquals("ContentType", document.getContentType());
		assertEquals("lala", new String(document.getContent()));
		assertEquals(DocumentType.CV, document.getType());

	}

	@Test
	public void shouldReturnCorrectModelAndView() throws IOException {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(8).toApplicationForm();
		MultipartFile multipartFileMock = EasyMock.createMock(MultipartFile.class);
		EasyMock.expect(multipartFileMock.getOriginalFilename()).andReturn("filename");
		EasyMock.expect(multipartFileMock.getContentType()).andReturn("ContentType");
		EasyMock.expect(multipartFileMock.getBytes()).andReturn("lala".getBytes());
		EasyMock.replay(multipartFileMock);

		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		ModelAndView modelAndView = controller.uploadFile(applicationForm, DocumentType.CV, multipartFileMock);
		assertEquals("redirect:/application", modelAndView.getViewName());
		assertEquals(8, modelAndView.getModel().get("id"));
	}

	@Test
	public void shouldValidateAndAddErrorsToModelIfExists() throws IOException {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(8).toApplicationForm();

		MultipartFile multipartFileMock = EasyMock.createMock(MultipartFile.class);
		EasyMock.expect(multipartFileMock.getOriginalFilename()).andReturn("filename");
		EasyMock.expect(multipartFileMock.getContentType()).andReturn("ContentType");
		EasyMock.expect(multipartFileMock.getBytes()).andReturn("lala".getBytes());
		EasyMock.replay(multipartFileMock);

		documentValidatorMock.validate(document, errors);
		EasyMock.replay(documentValidatorMock);

		EasyMock.expect(errors.hasFieldErrors("fileName")).andReturn(true);
		FieldError fieldError = EasyMock.createMock(FieldError.class);
		EasyMock.expect(fieldError.getCode()).andReturn("bob");
		EasyMock.expect(errors.getFieldError("fileName")).andReturn(fieldError);
		EasyMock.replay(fieldError, errors);
	
		EasyMock.replay(applicationsServiceMock);
		ModelAndView modelAndView = controller.uploadFile(applicationForm, DocumentType.CV, multipartFileMock);
		assertEquals("redirect:/application", modelAndView.getViewName());
		assertEquals(8, modelAndView.getModel().get("id"));
		assertEquals("bob", modelAndView.getModel().get("uploadErrorCode"));
		EasyMock.verify(applicationsServiceMock);
	}

	@Before
	public void setup() {
		errors = EasyMock.createMock(BindingResult.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		documentValidatorMock = EasyMock.createMock(DocumentValidator.class);
		document = new DocumentBuilder().id(1).toDocument();
		controller = new FileUploadController(applicationsServiceMock, documentValidatorMock) {
			@Override
			Document newDocument() {
				return document;
			}
			@Override
			BindingResult newErrors(Document document) {
				return errors;
			}

		};

		currentUser = new RegisteredUserBuilder().id(1).toUser();
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
