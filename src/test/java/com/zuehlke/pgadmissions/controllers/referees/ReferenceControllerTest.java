package com.zuehlke.pgadmissions.controllers.referees;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.Reference;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ReferenceValidator;

public class ReferenceControllerTest {

	private ApplicationsService applicationsServiceMock;
	private ReferenceController controller;
	private UsernamePasswordAuthenticationToken authenticationToken;
	private RegisteredUser currentUser;
	private DocumentPropertyEditor documentPropertyEditor;
	private ReferenceValidator referenceValidator;
	private RefereeService refereeServiceMock;
	private UserService userServiceMock;

	@Test
	public void shouldReturnApplicationForm() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(currentUser);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock, currentUser);
		controller = new ReferenceController(applicationsServiceMock, refereeServiceMock, userServiceMock, documentPropertyEditor, referenceValidator) {
			@Override
			RegisteredUser getCurrentUser(){
				return currentUser;
			}
		};
		ApplicationForm returnedApplicationForm = controller.getApplicationForm("1");
		assertEquals(applicationForm, returnedApplicationForm);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNoFoundExceptionIfApplicationFormDoesNotExist() {
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm("1");
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNoFoundExceptionIfUserNotRefereeForForm() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(currentUser);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(false);
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock, currentUser);
		controller = new ReferenceController(applicationsServiceMock, refereeServiceMock, userServiceMock, documentPropertyEditor, referenceValidator) {
			@Override
			RegisteredUser getCurrentUser(){
				return currentUser;
			}
		};
		controller.getApplicationForm("1");
	}

	@Test
	public void shouldReturnCurrrentUserReloadedToBindToSessionAsUser() {
		EasyMock.expect(userServiceMock.getUser(currentUser.getId())).andReturn(currentUser);
		EasyMock.replay(userServiceMock);
		assertEquals(currentUser, controller.getUser());
		EasyMock.verify(userServiceMock);

	}

	@Test
	public void shouldReturnUploadReferencePage() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		assertEquals("private/referees/upload_references", controller.getUploadReferencesPage(applicationForm));
	}

	@Test
	public void shouldReturnExpiredViewIfApplicationAlreadyDecided() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.APPROVED).toApplicationForm();
		assertEquals("private/referees/upload_references_expired", controller.getUploadReferencesPage(applicationForm));
	}

	@Test
	public void shouldReturnReferenceIfFAlreadyExists() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(currentUser);
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		controller = new ReferenceController(applicationsServiceMock, refereeServiceMock, userServiceMock, documentPropertyEditor, referenceValidator) {

			@Override
			public ApplicationForm getApplicationForm(String application) {
				return applicationForm;
			}
			@Override
			RegisteredUser getCurrentUser(){
				return currentUser;
			}
		};
		Reference reference = new ReferenceBuilder().id(2).toReference();
		Referee referee = new RefereeBuilder().id(8).reference(reference).toReferee();
		EasyMock.expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(referee);
		EasyMock.replay(currentUser);
		Reference returnedReference = controller.getReference( "1");
		assertEquals(reference, returnedReference);
	}

	@Test
	public void shouldReturnNewReferenceIfNotAlredyExists() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(currentUser);
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		controller = new ReferenceController(applicationsServiceMock, refereeServiceMock, userServiceMock, documentPropertyEditor, referenceValidator) {

			@Override
			public ApplicationForm getApplicationForm(String application) {
				return applicationForm;
			}
			@Override
			RegisteredUser getCurrentUser(){
				return currentUser;
			}
		};

		Referee referee = new RefereeBuilder().id(8).toReferee();
		EasyMock.expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(referee);
		EasyMock.replay(currentUser);
		Reference returnedReference = controller.getReference( "1");
		assertNull(returnedReference.getId());
		assertEquals(referee, returnedReference.getReferee());
	}

	@Test
	public void shouldBindPRopertyEditorAndValidator() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(referenceValidator);
		binderMock.registerCustomEditor(Document.class, documentPropertyEditor);
		EasyMock.replay(binderMock);
		controller.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldReturnToFormViewIfValidationErrors() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
		EasyMock.replay(errorsMock);
		assertEquals("private/referees/upload_references", controller.handleReferenceSubmission(new Reference(), errorsMock));
	}

	@Test
	public void shouldSaveReferenceAndRedirectToSaveViewIfNoErrors() {
		Referee referee = new RefereeBuilder().id(1).toReferee();
		Reference reference = new ReferenceBuilder().referee(referee).id(4).toReference();
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		refereeServiceMock.saveReferenceAndSendMailNotifications(referee);
		EasyMock.replay(errorsMock, refereeServiceMock);
		assertEquals("redirect:/addReferences/referenceuploaded", controller.handleReferenceSubmission(reference, errorsMock));
		EasyMock.verify(refereeServiceMock);
		assertEquals(reference, referee.getReference());
	}
	


	@Before
	public void setUp() {
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		documentPropertyEditor = EasyMock.createMock(DocumentPropertyEditor.class);
		referenceValidator = EasyMock.createMock(ReferenceValidator.class);
		refereeServiceMock = EasyMock.createMock(RefereeService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		controller = new ReferenceController(applicationsServiceMock, refereeServiceMock, userServiceMock, documentPropertyEditor, referenceValidator);

		authenticationToken = new UsernamePasswordAuthenticationToken(null, null);

		currentUser = new RegisteredUserBuilder().id(1).toUser();
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
