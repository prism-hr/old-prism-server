package com.zuehlke.pgadmissions.controllers.workflow.review;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ReviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;
import com.zuehlke.pgadmissions.validators.ReviewRoundValidator;

public class ReviewControllerTest {
	private ReviewController controller;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;

	private NewUserByAdminValidator userValidatorMock;
	
	private ReviewService reviewServiceMock;
	private MessageSource messageSourceMock;
	private BindingResult bindingResultMock;
	
	private RegisteredUser currentUserMock;
	private ReviewerPropertyEditor reviewerPropertyEditorMock;
	private ReviewRoundValidator reviewRoundValidator;
	private EncryptionHelper encryptionHelperMock;

	@Test
	public void shouldAddRegisteredUserValidatorAndReviewerPropertyEditor() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(userValidatorMock);

		EasyMock.replay(binderMock);
		controller.registerReviewerValidators(binderMock);
		EasyMock.verify(binderMock);
	}
	@Test
	public void shouldAddReviewRoundValidator() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(reviewRoundValidator);		
		binderMock.registerCustomEditor(Reviewer.class, reviewerPropertyEditorMock);
		EasyMock.replay(binderMock);
		controller.registerReviewRoundValidator(binderMock);
		EasyMock.verify(binderMock);
	}

	
	@Test
	public void shouldGetProgrammeReviewers() {
		final RegisteredUser interUser1 = new RegisteredUserBuilder().id(7).toUser();
		final RegisteredUser interUser2 = new RegisteredUserBuilder().id(6).toUser();

		final Program program = new ProgramBuilder().reviewers(interUser1, interUser2).id(6).toProgram();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		controller =  new ReviewController(applicationServiceMock, userServiceMock, userValidatorMock,reviewRoundValidator,reviewServiceMock, messageSourceMock, reviewerPropertyEditorMock, encryptionHelperMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if(applicationId == "5"){
					return applicationForm;
				}
				return null;
			}
		

			@Override
			public ReviewRound getReviewRound(Object applicationId) {
				// TODO Auto-generated method stub
				return null;
			}

		};


		List<RegisteredUser> reviewersUsers = controller.getProgrammeReviewers("5");
		assertEquals(2, reviewersUsers.size());
		assertTrue(reviewersUsers.containsAll(Arrays.asList(interUser1, interUser2)));
	}

	@Test
	public void shouldReturnNewUser() {
		assertNotNull(controller.getReviewer());
		assertNull(controller.getReviewer().getId());
	}

	@Test
	public void shouldGetApplicationFromIdForAdmin() {
		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
		EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		ApplicationForm returnedForm = controller.getApplicationForm("5");
		assertEquals(applicationForm, returnedForm);

	}

	@Test
	public void shouldGetApplicationFromIdForReviewer() {
		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
		EasyMock.expect(currentUserMock.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm)).andReturn(true);
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		ApplicationForm returnedForm = controller.getApplicationForm("5");
		assertEquals(applicationForm, returnedForm);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicatioDoesNotExist() {
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(null);
		EasyMock.replay(applicationServiceMock);

		controller.getApplicationForm("5");
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUserNotAdminOrReviewerOfApplicationProgram() {

		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
		EasyMock.expect(currentUserMock.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm)).andReturn(false);

		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		controller.getApplicationForm("5");
	}

	
	@Test
	public void shouldGetListOfPreviousReviewersAndRemoveDefaultReviewers(){
		EasyMock.reset(userServiceMock);
		final RegisteredUser defaultReviewer = new RegisteredUserBuilder().id(7).toUser();
		final RegisteredUser reviewer = new RegisteredUserBuilder().id(6).toUser();


		final Program program = new ProgramBuilder().reviewers(defaultReviewer).id(6).toProgram();

		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		controller = new  ReviewController(applicationServiceMock, userServiceMock, userValidatorMock,reviewRoundValidator,reviewServiceMock, messageSourceMock, reviewerPropertyEditorMock, encryptionHelperMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if(applicationId == "5"){
					return applicationForm;
				}
				return null;
			}

		
			@Override
			public ReviewRound getReviewRound(Object applicationId) {
				// TODO Auto-generated method stub
				return null;
			}

		};
		
		EasyMock.expect(userServiceMock.getAllPreviousReviewersOfProgram(program)).andReturn(Arrays.asList(defaultReviewer, reviewer));
		EasyMock.replay(userServiceMock);
		List<RegisteredUser> reviewersUsers = controller.getPreviousReviewers("5");
		assertEquals(1, reviewersUsers.size());
		assertTrue(reviewersUsers.contains(reviewer));
	}

	@Before
	public void setUp() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);
		userValidatorMock = EasyMock.createMock(NewUserByAdminValidator.class);
		reviewerPropertyEditorMock = EasyMock.createMock(ReviewerPropertyEditor.class);
		reviewServiceMock = EasyMock.createMock(ReviewService.class);
		messageSourceMock = EasyMock.createMock(MessageSource.class);
		reviewRoundValidator = EasyMock.createMock(ReviewRoundValidator.class);
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		bindingResultMock = EasyMock.createMock(BindingResult.class);		
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		EasyMock.replay(bindingResultMock);

		controller = new ReviewController(applicationServiceMock, userServiceMock, userValidatorMock,reviewRoundValidator, reviewServiceMock, messageSourceMock, reviewerPropertyEditorMock, encryptionHelperMock) {

			@Override
			public ReviewRound getReviewRound(Object applicationId) {
				// TODO Auto-generated method stub
				return null;
			}
		};

	}
}
