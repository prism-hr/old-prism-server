package com.zuehlke.pgadmissions.services;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class ReviewServiceTest {

	private ReviewService reviewService;

	private UserDAO userDaoMock;
	private RoleDAO roleDaoMock;
	private ProgramDAO programmeDaoMock;
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

		reviewerRole = new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole();
		reviewer1 = new RegisteredUserBuilder().id(100).email("rev1@bla.com")//
				.role(reviewerRole)//
				.username("rev 1")//
				.toUser();
		programme = new ProgramBuilder().id(1).title("super prog").reviewers(reviewer1).toProgram();
		application = new ApplicationFormBuilder().id(200).program(programme).status(ApplicationFormStatus.VALIDATION).toApplicationForm();

		reviewService = new ReviewService(userDaoMock, roleDaoMock, programmeDaoMock, applicationDaoMock);
	}

	@Test(expected = IllegalStateException.class)
	public void throwISEwhenUserAlreadyExists() {
		EasyMock.expect(userDaoMock.getUserByEmail("some@email.com")).andReturn(reviewer1);
		EasyMock.replay(userDaoMock, roleDaoMock, applicationDaoMock);
		reviewService.createNewReviewerForProgramme(programme, "la", "le", "some@email.com");
	}

	@Test
	public void createUserAndAddToProgramme() {
		EasyMock.expect(userDaoMock.getUserByEmail("some@email.com")).andReturn(null);
		EasyMock.expect(roleDaoMock.getRoleByAuthority(Authority.REVIEWER)).andReturn(reviewerRole);
		userDaoMock.save(EasyMock.anyObject(RegisteredUser.class));
		EasyMock.expectLastCall().andDelegateTo(new CheckProgrammeAndSimulateSaveDAO(340, programme));

		EasyMock.replay(userDaoMock, roleDaoMock, applicationDaoMock);
		RegisteredUser newReviewer = reviewService.createNewReviewerForProgramme(programme, "la", "le", "some@email.com");

		EasyMock.verify(userDaoMock, roleDaoMock, applicationDaoMock);
		Assert.assertEquals(2, programme.getReviewers().size());
		Assert.assertTrue(programme.getReviewers().contains(reviewer1));
		Assert.assertTrue(programme.getReviewers().contains(newReviewer));
		Assert.assertTrue(newReviewer.getProgramsOfWhichReviewer().contains(programme));
		Assert.assertTrue(newReviewer.isInRole(Authority.REVIEWER));
	}

	@Test
	public void addReviewerToProgramme() {
		RegisteredUser reviewer2 = new RegisteredUserBuilder().id(101).role(reviewerRole).toUser();
		userDaoMock.save(reviewer2);
		EasyMock.expectLastCall();

		EasyMock.replay(userDaoMock, roleDaoMock, applicationDaoMock);
		reviewService.addUserToProgramme(programme, reviewer2);

		EasyMock.verify(userDaoMock, roleDaoMock, applicationDaoMock);
		Assert.assertEquals(2, programme.getReviewers().size());
		Assert.assertTrue(programme.getReviewers().contains(reviewer1));
		Assert.assertTrue(programme.getReviewers().contains(reviewer2));
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
		Assert.assertEquals(2, programme.getReviewers().size());
		Assert.assertTrue(programme.getReviewers().contains(reviewer1));
		Assert.assertTrue(programme.getReviewers().contains(reviewer2));

		Assert.assertTrue(reviewer2.getProgramsOfWhichReviewer().contains(programme));
		Assert.assertTrue(reviewer2.isInRole(Authority.REVIEWER));
	}

	@Test
	public void shouldAddReviewersToApplication() {
		RegisteredUser reviewer2 = new RegisteredUserBuilder().id(101).email("rev2@bla.com")//
				.role(reviewerRole)//
				.toUser();
		programme.getReviewers().add(reviewer2);

		applicationDaoMock.save(application);
		EasyMock.expectLastCall().andDelegateTo(new CheckReviewersAndSimulateSaveDAO(reviewer1, reviewer2));

		EasyMock.replay(userDaoMock, roleDaoMock, applicationDaoMock);
		reviewService.moveApplicationToReview(application, reviewer1, reviewer2);
		EasyMock.verify(userDaoMock, roleDaoMock, applicationDaoMock);

		Assert.assertTrue(reviewer1.isReviewerOfApplicationForm(application));
		Assert.assertTrue(reviewer2.isReviewerOfApplicationForm(application));
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
		application.getReviewerUsers().add(reviewer1);

		applicationDaoMock.save(application);
		EasyMock.expectLastCall().andDelegateTo(new CheckReviewersAndSimulateSaveDAO(reviewer1));
		EasyMock.replay(userDaoMock, roleDaoMock, applicationDaoMock);
		reviewService.moveApplicationToReview(application, reviewer1);

		EasyMock.verify(userDaoMock, roleDaoMock, applicationDaoMock);
		Assert.assertEquals(1, application.getReviewerUsers().size());
		Assert.assertEquals(ApplicationFormStatus.REVIEW, application.getStatus());
	}

	@Test
	public void shouldAddOnlyReviewersNotAlreadyInApplication() {
		application.getReviewerUsers().add(reviewer1);

		RegisteredUser reviewer2 = new RegisteredUserBuilder().id(101).email("rev2@bla.com")//
				.role(reviewerRole)//
				.toUser();
		programme.getReviewers().add(reviewer2);

		applicationDaoMock.save(application);
		EasyMock.expectLastCall().andDelegateTo(new CheckReviewersAndSimulateSaveDAO(reviewer1, reviewer2));
		EasyMock.replay(userDaoMock, roleDaoMock, applicationDaoMock);

		reviewService.moveApplicationToReview(application, reviewer1, reviewer2);

		EasyMock.verify(userDaoMock, roleDaoMock, applicationDaoMock);
		Assert.assertTrue(reviewer1.isReviewerOfApplicationForm(application));
		Assert.assertTrue(reviewer2.isReviewerOfApplicationForm(application));
		Assert.assertEquals(ApplicationFormStatus.REVIEW, application.getStatus());
	}

	@Test
	public void shouldNotAddReviewerIfNotInProgramme() {
		programme.getReviewers().clear();

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
		programme.getReviewers().clear();
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

	class CheckProgrammeAndSimulateSaveDAO extends UserDAO {
		private final Integer id;
		private final Program expectedProgramme;

		public CheckProgrammeAndSimulateSaveDAO(Integer id, Program programme) {
			super(null);
			this.id = id;
			this.expectedProgramme = programme;
		}

		@Override
		public void save(RegisteredUser user) {
			user.setId(id);
			Assert.assertTrue(user.getProgramsOfWhichReviewer().contains(expectedProgramme));
		}
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
				Assert.assertTrue(applToSave.getReviewerUsers().contains(expectedReviewer));
			}
		}
	}
}
