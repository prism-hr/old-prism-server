package com.zuehlke.pgadmissions.controllers.workflow.review;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.propertyeditors.MoveToReviewReviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ReviewRoundValidator;

public class MoveToReviewControllerTest {

	private MoveToReviewController controller;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;

	private ReviewService reviewServiceMock;

	private BindingResult bindingResultMock;

	private static final String REVIEW_DETAILS_VIEW_NAME = "/private/staff/reviewer/assign_reviewers_to_appl_page";
	private static final String REVIEWERS_SECTION_NAME = "/private/staff/reviewer/assign_reviewers_section";
	private RegisteredUser currentUserMock;	
	private MoveToReviewReviewerPropertyEditor reviewerPropertyEditorMock;
	private ReviewRoundValidator reviewRoundValidatorMock;

	@Test
	public void shouldGetReviewRoundPageWithOnlyAssignFalseNewReviewersFunctionality() {
		ModelMap modelMap = new ModelMap();
		String reviewRoundDetailsPage = controller.getReviewRoundDetailsPage(modelMap);
		Assert.assertEquals(REVIEW_DETAILS_VIEW_NAME, reviewRoundDetailsPage);
		Assert.assertFalse((Boolean) modelMap.get("assignOnly"));

	}	
	
	@Test
	public void shouldGetReviewesSectionWithOnlyAssignFalseNewReviewersFunctionality() {
		ModelMap modelMap = new ModelMap();
		String reviewersDetailsSection = controller.getReviewersSectionView(modelMap);
		Assert.assertEquals(REVIEWERS_SECTION_NAME, reviewersDetailsSection);
		Assert.assertFalse((Boolean) modelMap.get("assignOnly"));

	}

	@Test
	public void shouldGetApplicationFromId() {
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
	public void shouldReturnNewReviewRoundWithExistingRoundsReviewersIfAny() {
		Reviewer reviewerOne = new ReviewerBuilder().id(1).toReviewer();
		Reviewer reviewerTwo = new ReviewerBuilder().id(2).toReviewer();
		final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").latestReviewRound(new ReviewRoundBuilder().reviewers(reviewerOne, reviewerTwo).toReviewRound()).toApplicationForm();
		
		controller = new MoveToReviewController(applicationServiceMock, userServiceMock,reviewServiceMock, reviewRoundValidatorMock,  reviewerPropertyEditorMock){
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if(applicationId.equals("bob")){
					return application;
				}
				return null;
			}

		};	
		ReviewRound returnedReviewRound = controller.getReviewRound("bob");
		assertNull(returnedReviewRound.getId());
		assertEquals(2, returnedReviewRound.getReviewers().size());
		assertTrue(returnedReviewRound.getReviewers().containsAll(Arrays.asList(reviewerOne, reviewerTwo)));
	}
	@Test
	public void shouldReturnNewReviewRoundWithEmtpyReviewersIfNoLatestReviewRound() {
	
		final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").toApplicationForm();
		
		controller = new MoveToReviewController(applicationServiceMock, userServiceMock,reviewServiceMock, reviewRoundValidatorMock,  reviewerPropertyEditorMock){
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if(applicationId.equals("bob")){
					return application;
				}
				return null;
			}

		};	
		ReviewRound returnedReviewRound = controller.getReviewRound("bob");
		assertNull(returnedReviewRound.getId());
		assertTrue(returnedReviewRound.getReviewers().isEmpty());

	}
	@Test
	public void shouldMoveApplicationToReview() {
		ReviewRound reviewRound = new ReviewRoundBuilder().id(4).toReviewRound();
		final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").toApplicationForm();
		
		controller = new MoveToReviewController(applicationServiceMock, userServiceMock,reviewServiceMock, reviewRoundValidatorMock,  reviewerPropertyEditorMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				return application;
			}

		};	
		
		reviewServiceMock.moveApplicationToReview(application, reviewRound);
		EasyMock.replay(reviewServiceMock);
		
		String view = controller.moveToReview("abc", reviewRound, bindingResultMock);
		assertEquals("/private/common/ajax_OK", view);
		EasyMock.verify(reviewServiceMock);
		
	}

	@Test
	public void shouldNotSaveReviewRoundAndReturnToReviewRoundPageIfHasErrors() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		controller = new MoveToReviewController(applicationServiceMock, userServiceMock,reviewServiceMock, reviewRoundValidatorMock,  reviewerPropertyEditorMock){
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				return applicationForm;
			}

		};
		ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationForm).toReviewRound();
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
		EasyMock.replay(errorsMock, applicationServiceMock);
		assertEquals(REVIEWERS_SECTION_NAME, controller.moveToReview("1", reviewRound, errorsMock));

	}

	@Test
	public void shouldAddReviewRoundValidator() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(reviewRoundValidatorMock);		
		binderMock.registerCustomEditor(Reviewer.class, reviewerPropertyEditorMock);
		binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
		
		EasyMock.replay(binderMock);
		controller.registerReviewRoundValidator(binderMock);
		EasyMock.verify(binderMock);
	}
	
	@Before
	public void setUp() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);
		reviewServiceMock = EasyMock.createMock(ReviewService.class);
		
		reviewerPropertyEditorMock = EasyMock.createMock(MoveToReviewReviewerPropertyEditor.class);
		bindingResultMock = EasyMock.createMock(BindingResult.class);
		
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		EasyMock.replay(bindingResultMock);
		reviewRoundValidatorMock = EasyMock.createMock(ReviewRoundValidator.class);
		controller = new MoveToReviewController(applicationServiceMock, userServiceMock,reviewServiceMock, reviewRoundValidatorMock,  reviewerPropertyEditorMock);

	}

}
