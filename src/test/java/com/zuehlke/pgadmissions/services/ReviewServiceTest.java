package com.zuehlke.pgadmissions.services;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ReviewRoundDAO;
import com.zuehlke.pgadmissions.dao.ReviewerDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class ReviewServiceTest {

	private ReviewService reviewService;

	private UserDAO userDaoMock;
	private RoleDAO roleDaoMock;
	private ProgramDAO programmeDaoMock;
	private ReviewerDAO reviewerDaoMock;
	private ApplicationFormDAO applicationDaoMock;

	private Program programme;
	private RegisteredUser reviewerUser1;
	private Role reviewerRole;
	private ApplicationForm application;

	private ReviewRoundDAO reviewRoundDAOMock;



	@Before
	public void setUp() {
		userDaoMock = EasyMock.createMock(UserDAO.class);
		roleDaoMock = EasyMock.createMock(RoleDAO.class);
		programmeDaoMock = EasyMock.createMock(ProgramDAO.class);
		applicationDaoMock = EasyMock.createMock(ApplicationFormDAO.class);
		reviewerDaoMock = EasyMock.createMock(ReviewerDAO.class);

		reviewerRole = new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole();
		reviewerUser1 = new RegisteredUserBuilder().id(100).email("rev1@bla.com")//
				.role(reviewerRole)//
				.username("rev 1")//
				.toUser();
		programme = new ProgramBuilder().id(1).title("super prog").reviewers(reviewerUser1).toProgram();
		application = new ApplicationFormBuilder().id(200).program(programme).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		reviewRoundDAOMock = EasyMock.createMock(ReviewRoundDAO.class);
		reviewService = new ReviewService(userDaoMock, roleDaoMock, programmeDaoMock, applicationDaoMock, reviewerDaoMock, reviewRoundDAOMock);
	}

	
	@Test
	public void addReviewerToProgramme() {
		RegisteredUser reviewer2 = new RegisteredUserBuilder().id(101).role(reviewerRole).toUser();
		userDaoMock.save(reviewer2);
		EasyMock.expectLastCall();

		EasyMock.replay(userDaoMock, roleDaoMock, applicationDaoMock);
		reviewService.addUserToProgramme(programme, reviewer2);

		EasyMock.verify(userDaoMock, roleDaoMock, applicationDaoMock);
		Assert.assertEquals(2, programme.getProgramReviewers().size());
		Assert.assertTrue(programme.getProgramReviewers().contains(reviewerUser1));
		Assert.assertTrue(programme.getProgramReviewers().contains(reviewer2));
		Assert.assertTrue(reviewer2.getProgramsOfWhichReviewer().contains(programme));
	}

	@Test
	public void addUserWhichIsntReviewerYetToProgramme() {
		RegisteredUser reviewer2 = new RegisteredUserBuilder().id(101).toUser();
		userDaoMock.save(reviewer2);
		EasyMock.expectLastCall();
		EasyMock.expect(roleDaoMock.getRoleByAuthority(Authority.REVIEWER)).andReturn(reviewerRole);

		EasyMock.replay(userDaoMock, roleDaoMock, applicationDaoMock);
		reviewService.addUserToProgramme(programme, reviewer2);

		EasyMock.verify(userDaoMock, roleDaoMock, applicationDaoMock);
		Assert.assertEquals(2, programme.getProgramReviewers().size());
		Assert.assertTrue(programme.getProgramReviewers().contains(reviewerUser1));
		Assert.assertTrue(programme.getProgramReviewers().contains(reviewer2));

		Assert.assertTrue(reviewer2.getProgramsOfWhichReviewer().contains(programme));
		Assert.assertTrue(reviewer2.isInRole(Authority.REVIEWER));
	}

	@Test
	public void shouldAddReviewRoundWithReviewersToApplication() {
		ReviewRound reviewRound = new ReviewRoundBuilder().id(1).toReviewRound();
		RegisteredUser reviewerUser2 = new RegisteredUserBuilder().id(101).email("rev2@bla.com")//
				.role(reviewerRole)//
				.toUser();
		
		programme.getProgramReviewers().add(reviewerUser2);
		
		applicationDaoMock.save(application);
		reviewRoundDAOMock.save(reviewRound);
		EasyMock.replay(userDaoMock, roleDaoMock, applicationDaoMock, reviewerDaoMock, reviewRoundDAOMock);
		
		reviewService.moveApplicationToReview(application,reviewRound, reviewerUser1, reviewerUser2);
		
		EasyMock.verify(userDaoMock, roleDaoMock, applicationDaoMock, reviewerDaoMock, reviewRoundDAOMock);	
		List<Reviewer> reviewers = reviewRound.getReviewers();
		Assert.assertEquals(application,reviewRound.getApplication());
		Assert.assertEquals(reviewRound, application.getLatestReviewRound());
		
		Assert.assertNotNull(reviewers);
		Assert.assertEquals(2, reviewers.size());
		Assert.assertEquals(reviewerUser1, reviewers.get(0).getUser());
		Assert.assertEquals(reviewerUser2, reviewers.get(1).getUser());
		
		
		Assert.assertEquals(ApplicationFormStatus.REVIEW, application.getStatus());
	}

	@Test
	public void shouldFailIfApplicationInInvalidState() {
		ApplicationFormStatus[] values = ApplicationFormStatus.values();
		for (ApplicationFormStatus status : values) {
			if (status != ApplicationFormStatus.VALIDATION && status != ApplicationFormStatus.REVIEW) {
				application.setStatus(status);
				boolean threwException = false;
				try {
					reviewService.moveApplicationToReview(application,  new ReviewRoundBuilder().id(1).toReviewRound(), reviewerUser1);
				} catch (IllegalStateException ise) {
					if (ise.getMessage().equals("Application in invalid status: '" + status + "'!")) {
						threwException = true;
					}
				}
				Assert.assertTrue(threwException);
			}
		}
	}

	@Test
	public void shouldNotAddReviewerIfAlreadyInApplication() {
		Reviewer reviewer = new ReviewerBuilder().user(reviewerUser1).id(1).toReviewer();		
		ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(reviewer).toReviewRound();
		application.setLatestReviewRound(reviewRound);
		
		applicationDaoMock.save(application);
		EasyMock.expectLastCall().andDelegateTo(new CheckReviewersAndSimulateSaveDAO(reviewerUser1));
		EasyMock.replay(userDaoMock, roleDaoMock, applicationDaoMock);
		reviewService.moveApplicationToReview(application, reviewRound, reviewerUser1);

		EasyMock.verify(userDaoMock, roleDaoMock, applicationDaoMock);
		Assert.assertEquals(1, application.getLatestReviewRound().getReviewers().size());
		Assert.assertEquals(reviewer, application.getLatestReviewRound().getReviewers().get(0));
		Assert.assertEquals(ApplicationFormStatus.REVIEW, application.getStatus());
	}

	@Test
	public void shouldAddOnlyReviewersNotAlreadyInApplication() {
		 ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUser1).toReviewer()).toReviewRound();
		application.setLatestReviewRound(reviewRound);

		RegisteredUser reviewerUser2 = new RegisteredUserBuilder().id(101).email("rev2@bla.com")//
				.role(reviewerRole)//
				.toUser();
		programme.getProgramReviewers().add(reviewerUser2);

		applicationDaoMock.save(application);
		EasyMock.expectLastCall().andDelegateTo(new CheckReviewersAndSimulateSaveDAO(reviewerUser1, reviewerUser2));
		EasyMock.replay(userDaoMock, roleDaoMock, applicationDaoMock);

		reviewService.moveApplicationToReview(application, reviewRound, reviewerUser1, reviewerUser2);

		EasyMock.verify(userDaoMock, roleDaoMock, applicationDaoMock);
		
		List<Reviewer> reviewers = application.getLatestReviewRound().getReviewers();
		Assert.assertNotNull(reviewers);
		Assert.assertEquals(2, reviewers.size());
		Assert.assertEquals(reviewerUser1, reviewers.get(0).getUser());
		Assert.assertEquals(reviewerUser2, reviewers.get(1).getUser());
	}

	@Test
	public void shouldNotAddReviewerIfNotInProgramme() {
		programme.getProgramReviewers().clear();

		EasyMock.replay(userDaoMock, roleDaoMock, applicationDaoMock);
		boolean threwException = false;
		try {
			reviewService.moveApplicationToReview(application, new ReviewRoundBuilder().id(1).toReviewRound(), reviewerUser1);
		} catch (IllegalStateException ise) {
			if (ise.getMessage().equals("User 'rev 1' is not a reviewer in programme 'super prog'!")) {
				threwException = true;
			}
		}
		Assert.assertTrue(threwException);
		Assert.assertNotSame(ApplicationFormStatus.REVIEW, application.getStatus());
	}

	@Test
	public void shouldNotAddUserIfNotReviewer() {
		programme.getProgramReviewers().clear();
		RegisteredUser anyUser = new RegisteredUserBuilder().id(23904)//
				.username("some other user")//
				.role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole())//
				.toUser();

		EasyMock.replay(userDaoMock, roleDaoMock, applicationDaoMock);
		boolean threwException = false;
		try {
			reviewService.moveApplicationToReview(application, new ReviewRoundBuilder().id(1).toReviewRound(), anyUser);
		} catch (IllegalStateException ise) {
			if (ise.getMessage().equals("User 'some other user' is not a reviewer!")) {
				threwException = true;
			}
		}
		Assert.assertTrue(threwException);
		Assert.assertNotSame(ApplicationFormStatus.REVIEW, application.getStatus());
	}

	

	class CheckReviewersAndSimulateSaveDAO extends ApplicationFormDAO {

		private final RegisteredUser[] expectedReviewers;

		CheckReviewersAndSimulateSaveDAO(RegisteredUser... reviewer) {
			super(null);
			this.expectedReviewers = reviewer;
		}

		@Override
		public void save(ApplicationForm applToSave) {
			for (RegisteredUser expectedReviewer : expectedReviewers) {
				Assert.assertTrue(expectedReviewer.isReviewerInLatestReviewRoundOfApplicationForm(applToSave));
			}
		}
	}
}
