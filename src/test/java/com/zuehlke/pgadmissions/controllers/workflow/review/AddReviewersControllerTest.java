package com.zuehlke.pgadmissions.controllers.workflow.review;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

public class AddReviewersControllerTest {
	private static final String VIEW_RESULT = "private/staff/admin/assign_reviewers_to_appl_page";
	private ReviewService reviewServiceMock;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;
	private NewUserByAdminValidator userValidatorMock;
	private MessageSource messageSourceMock;
	private AddReviewersController controller;

	@Before
	public void setUp(){
		reviewServiceMock = EasyMock.createMock(ReviewService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userValidatorMock = EasyMock.createMock(NewUserByAdminValidator.class);

		messageSourceMock = EasyMock.createMock(MessageSource.class);

		
		controller = new AddReviewersController(applicationServiceMock, reviewServiceMock, userServiceMock,	userValidatorMock, messageSourceMock);
	}
	
	@Test
	public void shouldGetLatestReviewRoundForApplication(){
		ReviewRound reviewRound = new ReviewRoundBuilder().id(5).toReviewRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(4).latestReviewRound(reviewRound).toApplicationForm();
		EasyMock.expect(applicationServiceMock.getApplicationById(4)).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock);
		
		assertEquals(reviewRound, controller.getReviewRound(4));
		
	}
	
	@Test
	public void shouldReturnShowPageTemplate() {
		ModelMap modelMap = new ModelMap();
		Assert.assertEquals(VIEW_RESULT, controller.getAddReviewsPage(modelMap));
		Assert.assertTrue((Boolean) modelMap.get("assignOnly"));
		
	}

}
