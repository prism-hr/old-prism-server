package com.zuehlke.pgadmissions.services;

import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
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
	private RegisteredUser reviewer1;
	private Role reviewerRole;
	private ApplicationForm application;



	@Before
	public void setUp() {
		userDaoMock = EasyMock.createMock(UserDAO.class);
		roleDaoMock = EasyMock.createMock(RoleDAO.class);
		programmeDaoMock = EasyMock.createMock(ProgramDAO.class);
		applicationDaoMock = EasyMock.createMock(ApplicationFormDAO.class);
		reviewerDaoMock = EasyMock.createMock(ReviewerDAO.class);

		reviewerRole = new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole();
		reviewer1 = new RegisteredUserBuilder().id(100).email("rev1@bla.com")//
				.role(reviewerRole)//
				.username("rev 1")//
				.toUser();
		programme = new ProgramBuilder().id(1).title("super prog").reviewers(reviewer1).toProgram();
		application = new ApplicationFormBuilder().id(200).program(programme).status(ApplicationFormStatus.VALIDATION).toApplicationForm();

		reviewService = new ReviewService(userDaoMock, roleDaoMock, programmeDaoMock, applicationDaoMock, reviewerDaoMock);
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
		Assert.assertTrue(programme.getProgramReviewers().contains(reviewer1));
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
		Assert.assertTrue(programme.getProgramReviewers().contains(reviewer1));
		Assert.assertTrue(programme.getProgramReviewers().contains(reviewer2));

		Assert.assertTrue(reviewer2.getProgramsOfWhichReviewer().contains(programme));
		Assert.assertTrue(reviewer2.isInRole(Authority.REVIEWER));
	}

	@Test
	public void shouldAddReviewersToApplication() {
		RegisteredUser reviewer2 = new RegisteredUserBuilder().id(101).email("rev2@bla.com")//
				.role(reviewerRole)//
				.toUser();
		programme.getProgramReviewers().add(reviewer2);

		reviewerDaoMock.save((Reviewer) EasyMock.anyObject());
		EasyMock.expectLastCall().times(2);

		applicationDaoMock.save(application);
		EasyMock.expectLastCall().andDelegateTo(new CheckReviewersAndSimulateSaveDAO(reviewer1, reviewer2));

		EasyMock.replay(userDaoMock, roleDaoMock, applicationDaoMock, reviewerDaoMock);
		reviewService.moveApplicationToReview(application, reviewer1, reviewer2);
		EasyMock.verify(userDaoMock, roleDaoMock, applicationDaoMock, reviewerDaoMock);

		List<Reviewer> reviewers = application.getLatestReviewRound().getReviewers();
		Assert.assertNotNull(reviewers);
		Assert.assertEquals(2, reviewers.size());
		for (Reviewer reviewer : reviewers) {
			Assert.assertEquals(application, reviewer.getApplication());
		}
		Assert.assertTrue(reviewer1.isReviewerInLatestReviewRoundOfApplicationForm(application));
		Assert.assertTrue(reviewer2.isReviewerInLatestReviewRoundOfApplicationForm(application));
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
					reviewService.moveApplicationToReview(application, reviewer1);
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
		Reviewer reviewer = new ReviewerBuilder().user(reviewer1).id(1).toReviewer();		
		ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(reviewer).toReviewRound();
		application.setLatestReviewRound(reviewRound);
		
		applicationDaoMock.save(application);
		EasyMock.expectLastCall().andDelegateTo(new CheckReviewersAndSimulateSaveDAO(reviewer1));
		EasyMock.replay(userDaoMock, roleDaoMock, applicationDaoMock);
		reviewService.moveApplicationToReview(application, reviewer1);

		EasyMock.verify(userDaoMock, roleDaoMock, applicationDaoMock);
		Assert.assertEquals(1, application.getLatestReviewRound().getReviewers().size());
		Assert.assertEquals(reviewer, application.getLatestReviewRound().getReviewers().get(0));
		Assert.assertEquals(ApplicationFormStatus.REVIEW, application.getStatus());
	}

	@Test
	public void shouldAddOnlyReviewersNotAlreadyInApplication() {
		 ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewer1).toReviewer()).toReviewRound();
		application.setLatestReviewRound(reviewRound);

		RegisteredUser reviewer2 = new RegisteredUserBuilder().id(101).email("rev2@bla.com")//
				.role(reviewerRole)//
				.toUser();
		programme.getProgramReviewers().add(reviewer2);

		applicationDaoMock.save(application);
		EasyMock.expectLastCall().andDelegateTo(new CheckReviewersAndSimulateSaveDAO(reviewer1, reviewer2));
		EasyMock.replay(userDaoMock, roleDaoMock, applicationDaoMock);

		reviewService.moveApplicationToReview(application, reviewer1, reviewer2);

		EasyMock.verify(userDaoMock, roleDaoMock, applicationDaoMock);
		Assert.assertTrue(reviewer1.isReviewerInLatestReviewRoundOfApplicationForm(application));
		Assert.assertTrue(reviewer2.isReviewerInLatestReviewRoundOfApplicationForm(application));
		List<Reviewer> reviewers = application.getLatestReviewRound().getReviewers();
		Assert.assertNotNull(reviewers);
		Assert.assertEquals(2, reviewers.size());
		Assert.assertEquals(ApplicationFormStatus.REVIEW, application.getStatus());
	}

	@Test
	public void shouldNotAddReviewerIfNotInProgramme() {
		programme.getProgramReviewers().clear();

		EasyMock.replay(userDaoMock, roleDaoMock, applicationDaoMock);
		boolean threwException = false;
		try {
			reviewService.moveApplicationToReview(application, reviewer1);
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
			reviewService.moveApplicationToReview(application, anyUser);
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
