package com.zuehlke.pgadmissions.controllers.referees;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.controllers.ReviewCommentController;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;
import com.zuehlke.pgadmissions.validators.ReferenceValidator;

public class ReferenceControllerTest {

	private ApplicationsService applicationsServiceMock;
	private ReferenceController controller;
	private UsernamePasswordAuthenticationToken authenticationToken;
	private RegisteredUser currentUser;
	private DocumentPropertyEditor documentPropertyEditor;
	private FeedbackCommentValidator referenceValidator;
	private RefereeService refereeServiceMock;
	private CommentService commentServiceMock;
	private UserService userServiceMock;

	@Test
	public void shouldReturnApplicationForm() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(currentUser);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock, currentUser);
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
		authenticationToken.setDetails(currentUser);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(false);
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock, currentUser);
		controller = new ReferenceController(applicationsServiceMock, refereeServiceMock, userServiceMock, documentPropertyEditor, referenceValidator, commentServiceMock) {
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
		ReferenceComment reference = new ReferenceCommentBuilder().id(2).toReferenceComment();
		Referee referee = new RefereeBuilder().id(8).reference(reference).toReferee();
		EasyMock.expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(referee);
		EasyMock.replay(currentUser);
		ReferenceComment returnedReference = controller.getComment( "1");
		assertEquals(reference, returnedReference);
	}

	@Test
	public void shouldReturnNewReferenceIfNotAlredyExists() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(currentUser);
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
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
		EasyMock.replay(currentUser);
		ReferenceComment returnedReference = controller.getComment( "1");
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
		assertEquals("private/referees/upload_references", controller.handleReferenceSubmission(new ReferenceComment(), errorsMock));
	}

	@Test
	public void shouldSaveReferenceAndRedirectToSaveViewIfNoErrors() {
		Referee referee = new RefereeBuilder().id(1).toReferee();
		ReferenceComment reference = new ReferenceCommentBuilder().referee(referee).id(4).toReferenceComment();
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		commentServiceMock.save(reference);
		refereeServiceMock.saveReferenceAndSendMailNotifications(referee);
		EasyMock.replay(errorsMock, refereeServiceMock);
		assertEquals("redirect:/addReferences/referenceuploaded", controller.handleReferenceSubmission(reference, errorsMock));
		EasyMock.verify(refereeServiceMock);
	}
	
	@Test
	public void shouldCreateNewReferenceCommentForApplicationForm(){
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
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
