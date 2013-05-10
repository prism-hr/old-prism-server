package com.zuehlke.pgadmissions.controllers.workflow.review;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.SuggestedSupervisorBuilder;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;

public class ReviewControllerTest {

	private ReviewController controller;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;

	private ReviewService reviewServiceMock;
	private BindingResult bindingResultMock;
	private RegisteredUser currentUserMock;

	@Test
	public void shouldGetNominatedReviewers() {
		EasyMock.reset(userServiceMock);
		final RegisteredUser interUser1 = new RegisteredUserBuilder().id(7).build();
		final RegisteredUser interUser2 = new RegisteredUserBuilder().id(6).build();

		String emailOfSupervisor1 = "1@ucl.ac.uk";
		String emailOfSupervisor2 = "2@ucl.ac.uk";
		SuggestedSupervisor applicantNominatedSupervisor1 = new SuggestedSupervisorBuilder().id(1).email(emailOfSupervisor1).build();
		SuggestedSupervisor applicantNominatedSupervisor2 = new SuggestedSupervisorBuilder().id(2).email(emailOfSupervisor2).build();

		ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().suggestedSupervisors(applicantNominatedSupervisor1, applicantNominatedSupervisor2)
		                .build();

		final Program program = new ProgramBuilder().reviewers(interUser1, interUser2).id(6).build();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).programmeDetails(programmeDetails).build();

		controller = new ReviewController(applicationServiceMock, userServiceMock, reviewServiceMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if (applicationId.equals("5")) {
					return applicationForm;
				}
				return null;
			}

			@Override
			public ReviewRound getReviewRound(Object applicationId) {
				return null;
			}

		};

		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor1)).andReturn(interUser1);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor2)).andReturn(interUser2);
		EasyMock.replay(userServiceMock);
		List<RegisteredUser> reviewersUsers = controller.getNominatedSupervisors("5");
		assertEquals(2, reviewersUsers.size());
		assertTrue(reviewersUsers.containsAll(Arrays.asList(interUser1, interUser2)));
	}

	@Test
	public void shouldGetProgrammeReviewersAndRemoveApplicantNominatedSupervisors() {
		EasyMock.reset(userServiceMock);
		final RegisteredUser interUser1 = new RegisteredUserBuilder().id(7).build();
		final RegisteredUser interUser2 = new RegisteredUserBuilder().id(6).build();
		final RegisteredUser interUser3 = new RegisteredUserBuilder().id(5).build();
		
		String emailOfSupervisor1 = "1@ucl.ac.uk";
		String emailOfSupervisor2 = "2@ucl.ac.uk";
		SuggestedSupervisor applicantNominatedSupervisor1 = new SuggestedSupervisorBuilder().id(1).email(emailOfSupervisor1).build();
		SuggestedSupervisor applicantNominatedSupervisor2 = new SuggestedSupervisorBuilder().id(2).email(emailOfSupervisor2).build();
		
		ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().suggestedSupervisors(applicantNominatedSupervisor1, applicantNominatedSupervisor2)
		                .build();

		final Program program = new ProgramBuilder().reviewers(interUser1, interUser2,interUser3).id(6).build();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).programmeDetails(programmeDetails).build();
		controller = new ReviewController(applicationServiceMock, userServiceMock, reviewServiceMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if (applicationId.equals("5")) {
					return applicationForm;
				}
				return null;
			}

			@Override
			public ReviewRound getReviewRound(Object applicationId) {
				return null;
			}

		};
		
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor1)).andReturn(interUser1);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor2)).andReturn(interUser2);
		EasyMock.replay(userServiceMock);

		List<RegisteredUser> reviewersUsers = controller.getProgrammeReviewers("5");
		assertEquals(1, reviewersUsers.size());
		assertTrue(reviewersUsers.containsAll(Arrays.asList(interUser3)));
	}

	@Test
	public void shouldGetApplicationFromIdForAdmin() {
		Program program = new ProgramBuilder().id(6).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();

		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
		EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		ApplicationForm returnedForm = controller.getApplicationForm("5");
		assertEquals(applicationForm, returnedForm);

	}

	@Test(expected = MissingApplicationFormException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicatioDoesNotExist() {
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(null);
		EasyMock.replay(applicationServiceMock);

		controller.getApplicationForm("5");
	}

	@Test(expected = InsufficientApplicationFormPrivilegesException.class)
	public void shouldThrowExceptionIfUserNotAdminOrReviewerOfApplicationProgram() {

		Program program = new ProgramBuilder().id(6).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();

		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
		EasyMock.expect(currentUserMock.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm)).andReturn(false);

		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		controller.getApplicationForm("5");
	}

	@Test
	public void shouldGetListOfPreviousReviewersAndRemoveDefaultReviewersAndApplicantNominatedSupervisors() {
		EasyMock.reset(userServiceMock);
		final RegisteredUser applicantNominatedSupervisor = new RegisteredUserBuilder().id(5).build();
		final RegisteredUser defaultReviewer = new RegisteredUserBuilder().id(7).build();
		final RegisteredUser reviewer = new RegisteredUserBuilder().id(6).build();
		
		String emailOfSupervisor1 = "1@ucl.ac.uk";
		String emailOfSupervisor2 = "2@ucl.ac.uk";
		SuggestedSupervisor applicantNominatedSupervisor1 = new SuggestedSupervisorBuilder().id(1).email(emailOfSupervisor1).build();
		SuggestedSupervisor applicantNominatedSupervisor2 = new SuggestedSupervisorBuilder().id(2).email(emailOfSupervisor2).build();
		
		ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().suggestedSupervisors(applicantNominatedSupervisor1, applicantNominatedSupervisor2)
		                .build();

		final Program program = new ProgramBuilder().reviewers(defaultReviewer).id(6).build();

		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).programmeDetails(programmeDetails).build();
		controller = new ReviewController(applicationServiceMock, userServiceMock, reviewServiceMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if (applicationId.equals("5")) {
					return applicationForm;
				}
				return null;
			}

			@Override
			public ReviewRound getReviewRound(Object applicationId) {
				return null;
			}

		};

		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor1)).andReturn(applicantNominatedSupervisor).times(2);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor2)).andReturn(defaultReviewer).times(2);
		List<RegisteredUser> reviewerList = new ArrayList<RegisteredUser>();
		reviewerList.add(defaultReviewer);
		reviewerList.add(reviewer);
		EasyMock.expect(userServiceMock.getAllPreviousReviewersOfProgram(program)).andReturn(reviewerList);
		EasyMock.replay(userServiceMock);
		List<RegisteredUser> reviewersUsers = controller.getPreviousReviewers("5");
		assertEquals(1, reviewersUsers.size());
		assertTrue(reviewersUsers.contains(reviewer));
	}

	@Test
	public void shouldGetCurrentUserAsUser() {
		assertEquals(currentUserMock, controller.getUser());
	}

	@Before
	public void setUp() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);

		reviewServiceMock = EasyMock.createMock(ReviewService.class);

		bindingResultMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		EasyMock.replay(bindingResultMock);

		controller = new ReviewController(applicationServiceMock, userServiceMock, reviewServiceMock) {

			@Override
			public ReviewRound getReviewRound(Object applicationId) {
				return null;
			}
		};

	}
}
