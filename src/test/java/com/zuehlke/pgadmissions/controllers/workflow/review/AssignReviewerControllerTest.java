package com.zuehlke.pgadmissions.controllers.workflow.review;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.zuehlke.pgadmissions.dao.ReviewerDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.propertyeditors.ReviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

public class AssignReviewerControllerTest {

	
	private AssignReviewerController controller;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;
	
	private NewUserByAdminValidator userValidatorMock;
	private ReviewService reviewServiceMock;
	
	private MessageSource messageSourceMock;
	private BindingResult bindingResultMock;
	
	protected static final String REVIEW_DETAILS_VIEW_NAME = "/private/staff/admin/assign_reviewers_to_appl_page";
	private RegisteredUser currentUserMock;
	private ReviewerPropertyEditor reviewerPropertyEditorMock;
	
	

	@Test
	public void shouldGetReviewRoundPageWithOnlyAssignTrueAssignReviewersFunctionality() {
		ModelMap modelMap = new ModelMap();
		String reviewRoundDetailsPage = controller.getAssignReviewersPage(modelMap);
		Assert.assertEquals(REVIEW_DETAILS_VIEW_NAME, reviewRoundDetailsPage);
		Assert.assertTrue((Boolean) modelMap.get("assignOnly"));
		
	}
	
	
	@Test
	public void shouldReturnLatestReviewRoundIfApplicationForm(){
		Program program = new ProgramBuilder().id(6).toProgram();
		ReviewRound reviewRound = new ReviewRoundBuilder().id(1).toReviewRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).latestReviewRound(reviewRound).toApplicationForm();
		
		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);		
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		
		assertEquals(reviewRound, controller.getReviewRound("5"));
	}

	@Test
	public void shouldSaveReviewRoundIfNoErrors() {		
		ReviewRound reviewRound = new ReviewRoundBuilder().id(4).toReviewRound();
		reviewServiceMock.save(reviewRound);
		EasyMock.replay(reviewServiceMock);
		String view = controller.assignReviewers(reviewRound, bindingResultMock);
		assertEquals("redirect:/applications", view);
		EasyMock.verify(reviewServiceMock);
		
	}
	
	
	@Test
	public void shouldSetToFlagForAdminNotifiesToNoIfNoErrors() {	
		Reviewer reviewer3 = new ReviewerBuilder().id(1).requiresAdminNotification(CheckedStatus.NO).toReviewer();
		Reviewer reviewer1 = new ReviewerBuilder().id(null).requiresAdminNotification(CheckedStatus.NO).toReviewer();
		Reviewer reviewer2 = new ReviewerBuilder().id(2).requiresAdminNotification(CheckedStatus.YES).toReviewer();
		ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(reviewer1, reviewer2, reviewer3).id(4).toReviewRound();
		reviewServiceMock.save(reviewRound);
		EasyMock.replay(reviewServiceMock);
		String view = controller.assignReviewers(reviewRound, bindingResultMock);
		assertEquals("redirect:/applications", view);
		EasyMock.verify(reviewServiceMock);
		Assert.assertEquals(CheckedStatus.YES, reviewer1.getRequiresAdminNotification());
		Assert.assertEquals(CheckedStatus.YES, reviewer2.getRequiresAdminNotification());
	}
	
	@Test
	public void shouldReturnToViewAndNotSaveIfErros() {
		EasyMock.reset(bindingResultMock);
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(true);
		EasyMock.replay(bindingResultMock);
		ReviewRound reviewRound = new ReviewRoundBuilder().id(4).toReviewRound();	
		EasyMock.replay(reviewServiceMock);
		String view = controller.assignReviewers(reviewRound, bindingResultMock);
		assertEquals(REVIEW_DETAILS_VIEW_NAME, view);
		EasyMock.verify(reviewServiceMock);
		
	}
	
	@Before
	public void setUp() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);
		userValidatorMock = EasyMock.createMock(NewUserByAdminValidator.class);
		reviewServiceMock = EasyMock.createMock(ReviewService.class);
		
		messageSourceMock = EasyMock.createMock(MessageSource.class);
		
		reviewerPropertyEditorMock = EasyMock.createMock(ReviewerPropertyEditor.class);
		bindingResultMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		EasyMock.replay(bindingResultMock);
		
		controller = new AssignReviewerController(applicationServiceMock, userServiceMock, userValidatorMock, null, reviewServiceMock, messageSourceMock, reviewerPropertyEditorMock, null);		
	}
			
}
