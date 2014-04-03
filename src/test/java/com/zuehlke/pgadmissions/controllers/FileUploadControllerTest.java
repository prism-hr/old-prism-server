package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.multipart.MultipartFile;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.DocumentValidator;

public class FileUploadControllerTest {

	private FileUploadController controller;
	private ApplicationFormService applicationsServiceMock;
	private RegisteredUser currentUser;
	private DocumentValidator documentValidatorMock;
	private BindingResult errors;
	private Document document;
	private DocumentService documentServiceMock;
	private UserService userServiceMock;

	@Test
	public void shouldGetApplicationFormFromService() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(currentUser).id(2).build();

		EasyMock.expect(applicationsServiceMock.getByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);

		ApplicationForm returnedForm = controller.getApplicationForm("1");
		assertEquals(applicationForm, returnedForm);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicationFormDoNotExist() {

		EasyMock.expect(applicationsServiceMock.getByApplicationNumber("5")).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm("5");

	}

	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldThrowCannotUpdateApplicationExceptionIfApplicationFormNotInUnsubmmitedState() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(currentUser).id(2).status(new State().withId(ApplicationFormStatus.APPROVED))
				.build();
		EasyMock.expect(applicationsServiceMock.getByApplicationNumber("2")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm("2");
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionIfCurrentUserNotApplicant() {
		RegisteredUser applicant = new RegisteredUserBuilder().id(6).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).applicant(applicant)
				.build();
		EasyMock.expect(applicationsServiceMock.getByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm("5");

	}

	
	
	@Test
	public void shouldCreateDocumentFromFile() throws IOException{
		MultipartFile multipartFileMock = EasyMock.createMock(MultipartFile.class);
		EasyMock.expect(multipartFileMock.getOriginalFilename()).andReturn("filename");
		EasyMock.expect(multipartFileMock.getContentType()).andReturn("ContentType");
		EasyMock.expect(multipartFileMock.getBytes()).andReturn("lala".getBytes());
		EasyMock.replay(multipartFileMock);

		Document document  = controller.getDocument(multipartFileMock, DocumentType.SUPPORTING_FUNDING);
		assertNull(document.getId());
		assertEquals("filename", document.getFileName());
		assertEquals("ContentType", document.getContentType());
		assertEquals("lala", new String(document.getContent()));
		assertEquals(DocumentType.SUPPORTING_FUNDING, document.getType());
		
	}
	
	@Test
	public void shouldReturnNullIfMultiPartFileIsNull() throws IOException{		
		assertNull(controller.getDocument(null, null));		
	}
	
	@Test
	public void shouldRegisterValidator(){
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(documentValidatorMock);
		EasyMock.replay(binderMock);
		controller.initBinder(binderMock);
		EasyMock.verify(binderMock);
	}
	
	@Test
	public void shouldSaveValidDocument(){
		Document doc = new DocumentBuilder().id(1).build();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(false);
		documentServiceMock.save(doc);
		EasyMock.replay(errors, documentServiceMock);
		String viewName = controller.uploadFileAsynchronously(doc, errors);
		EasyMock.verify(documentServiceMock);
		assertEquals("/private/common/parts/supportingDocument", viewName);
	}
	
	@Test
	public void shouldNotSaveInValidDocument(){
		Document doc = new DocumentBuilder().id(1).build();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(true);		
		EasyMock.replay(errors, documentServiceMock);
		String viewName = controller.uploadFileAsynchronously(doc, errors);
		EasyMock.verify(documentServiceMock);
		assertEquals("/private/common/parts/supportingDocument", viewName);
	}
	@Before
	public void setup() {
		errors = EasyMock.createMock(BindingResult.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationFormService.class);
		documentValidatorMock = EasyMock.createMock(DocumentValidator.class);
		documentServiceMock = EasyMock.createMock(DocumentService.class);
		document = new DocumentBuilder().id(1).build();
		userServiceMock = EasyMock.createMock(UserService.class);
//		controller = new FileUploadController(applicationsServiceMock, documentValidatorMock, documentServiceMock, userServiceMock) {
//			@Override
//			Document newDocument() {
//				return document;
//			}
//			@Override
//			BindingResult newErrors(Document document) {
//				return errors;
//			}
//
//		};

		currentUser = new RegisteredUserBuilder().id(1).build();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.replay(userServiceMock);
	}


}
