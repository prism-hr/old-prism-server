package com.zuehlke.pgadmissions.controllers.referees;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;

public class ReferenceControllerTest {

	private ApplicationsService applicationsServiceMock;
	private ReferenceController controller;
	private RegisteredUser currentUser;
	private DocumentPropertyEditor documentPropertyEditor;
	private FeedbackCommentValidator referenceValidator;
	private RefereeService refereeServiceMock;
	private CommentService commentServiceMock;
	private UserService userServiceMock;

	@Test
	public void shouldReturnApplicationForm() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
		EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock, currentUser, userServiceMock);
		controller = new ReferenceController(applicationsServiceMock, refereeServiceMock, userServiceMock, documentPropertyEditor, referenceValidator, commentServiceMock) {
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
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
		EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(false);
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock, currentUser, userServiceMock);
		controller = new ReferenceController(applicationsServiceMock, refereeServiceMock, userServiceMock, documentPropertyEditor, referenceValidator, commentServiceMock) {
			@Override
			RegisteredUser getCurrentUser(){
				return currentUser;
			}
		};
		controller.getApplicationForm("1");
	}



	@Test
	public void shouldReturnUploadReferencePage() {
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REVIEW).build();
		controller = new ReferenceController(applicationsServiceMock, refereeServiceMock, userServiceMock, documentPropertyEditor, referenceValidator, commentServiceMock) {

			@Override
			public ApplicationForm getApplicationForm(String application) {
				return applicationForm;
			}
			@Override
			RegisteredUser getCurrentUser(){
				return currentUser;
			}
		};
		Referee refereeMock = EasyMock.createMock(Referee.class);
		EasyMock.expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(refereeMock);
		EasyMock.expect(refereeMock.hasResponded()).andReturn(false);
		EasyMock.replay(currentUser, refereeMock);
		assertEquals("private/referees/upload_references", controller.getUploadReferencesPage(applicationForm));
	}

	@Test
	public void shouldReturnExpiredViewIfApplicationAlreadyDecided() {
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.APPROVED).build();
		controller = new ReferenceController(applicationsServiceMock, refereeServiceMock, userServiceMock, documentPropertyEditor, referenceValidator, commentServiceMock) {

			@Override
			public ApplicationForm getApplicationForm(String application) {
				return applicationForm;
			}
			@Override
			RegisteredUser getCurrentUser(){
				return currentUser;
			}
		};
		Referee refereeMock = EasyMock.createMock(Referee.class);
		EasyMock.expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(refereeMock);
		EasyMock.expect(refereeMock.hasResponded()).andReturn(false);
		EasyMock.replay(currentUser, refereeMock);
		assertEquals("private/referees/upload_references_expired", controller.getUploadReferencesPage(applicationForm));
	}

	@Test(expected=ResourceNotFoundException.class)
	public void shoulThrowResourceNotFoundExceptionIfRefereeHasAlreadyProvidedReferecne() {
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REVIEW).build();
		controller = new ReferenceController(applicationsServiceMock, refereeServiceMock, userServiceMock, documentPropertyEditor, referenceValidator, commentServiceMock) {

			@Override
			public ApplicationForm getApplicationForm(String application) {
				return applicationForm;
			}
			@Override
			RegisteredUser getCurrentUser(){
				return currentUser;
			}
		};
		Referee refereeMock = EasyMock.createMock(Referee.class);
		EasyMock.expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(refereeMock);
		EasyMock.expect(refereeMock.hasResponded()).andReturn(true);
		EasyMock.replay(currentUser, refereeMock);
	
		controller.getUploadReferencesPage(applicationForm);
		
	}

	@Test
	public void shouldReturnReferenceIfFAlreadyExists() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
		controller = new ReferenceController(applicationsServiceMock, refereeServiceMock, userServiceMock, documentPropertyEditor, referenceValidator, commentServiceMock) {

			@Override
			public ApplicationForm getApplicationForm(String application) {
				return applicationForm;
			}
			@Override
			RegisteredUser getCurrentUser(){
				return currentUser;
			}
		};
		ReferenceComment reference = new ReferenceCommentBuilder().id(2).build();
		Referee referee = new RefereeBuilder().id(8).reference(reference).toReferee();
		EasyMock.expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(referee);
		EasyMock.replay(currentUser, userServiceMock);
		ReferenceComment returnedReference = controller.getComment( "1");
		assertEquals(reference, returnedReference);
	}

	@Test
	public void shouldReturnNewReferenceIfNotAlredyExists() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
		controller = new ReferenceController(applicationsServiceMock, refereeServiceMock, userServiceMock, documentPropertyEditor, referenceValidator, commentServiceMock) {

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
		EasyMock.replay(currentUser, userServiceMock);
		ReferenceComment returnedReference = controller.getComment( "1");
		assertNull(returnedReference.getId());
		assertEquals(referee, returnedReference.getReferee());
	}

	@Test
	public void shouldBindPRopertyEditorAndValidator() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(referenceValidator);
		binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
		
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
		assertEquals("private/referees/upload_references", controller.handleReferenceSubmission(new ReferenceComment(), errorsMock));
	}

	@Test
	public void shouldSaveReferenceAndRedirectToSaveViewIfNoErrors() {
		Referee referee = new RefereeBuilder().id(1).toReferee();
		ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("12").build();
		ReferenceComment reference = new ReferenceCommentBuilder().application(application).referee(referee).id(4).build();
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		commentServiceMock.save(reference);
		refereeServiceMock.saveReferenceAndSendMailNotifications(referee);
		EasyMock.replay(errorsMock, commentServiceMock, refereeServiceMock);
		assertEquals("redirect:/applications?messageCode=reference.uploaded&application=12", controller.handleReferenceSubmission(reference, errorsMock));
		EasyMock.verify(commentServiceMock, refereeServiceMock);
	}
	
	@Test
	public void shouldPreventFromSavingDuplicateReferences() {
		Referee referee = new RefereeBuilder().id(1).toReferee();
		ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("12").build();
		ReferenceComment reference = new ReferenceCommentBuilder().application(application).referee(referee).id(4).build();
		referee.setReference(reference);
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		EasyMock.replay(errorsMock, commentServiceMock, refereeServiceMock);
		assertEquals("redirect:/applications?messageCode=reference.uploaded&application=12", controller.handleReferenceSubmission(reference, errorsMock));
		EasyMock.verify(commentServiceMock, refereeServiceMock);
	}	
	
	@Test
	public void shouldCreateNewReferenceCommentForApplicationForm(){
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).build();
		final RegisteredUser currentUser =EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		Referee referee = new RefereeBuilder().id(5).toReferee();
		EasyMock.expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(referee);
		EasyMock.replay(userServiceMock, currentUser);
		controller = new ReferenceController(applicationsServiceMock, refereeServiceMock, userServiceMock, documentPropertyEditor, referenceValidator, commentServiceMock) {

			@Override
			public ApplicationForm getApplicationForm(String application) {
				return applicationForm;
			}
			@Override
			RegisteredUser getCurrentUser(){
				return currentUser;
			}
		};
		ReferenceComment comment = controller.getComment("5");
		
		assertNull(comment.getId());
		assertEquals(applicationForm, comment.getApplication());
		assertEquals(currentUser, comment.getUser());
		assertEquals(CommentType.REFERENCE, comment.getType());
		assertEquals(referee, comment.getReferee());
		
		
	}
	


	@Before
	public void setUp() {
		commentServiceMock = EasyMock.createMock(CommentService.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		documentPropertyEditor = EasyMock.createMock(DocumentPropertyEditor.class);
		referenceValidator = EasyMock.createMock(FeedbackCommentValidator.class);
		refereeServiceMock = EasyMock.createMock(RefereeService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		controller = new ReferenceController(applicationsServiceMock, refereeServiceMock, userServiceMock, documentPropertyEditor, referenceValidator, commentServiceMock);

		currentUser = EasyMock.createMock(RegisteredUser.class);
		

	}


}
