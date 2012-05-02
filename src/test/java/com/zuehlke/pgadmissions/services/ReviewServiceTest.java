package com.zuehlke.pgadmissions.services;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class ReviewServiceTest {

	private ReviewService reviewService;

	private UserDAO userDaoMock;
	private RoleDAO roleDaoMock;

	private Program programme;
	private RegisteredUser reviewer1;
	private Role reviewerRole;

	@Before
	public void setUp() {
		userDaoMock = EasyMock.createMock(UserDAO.class);
		roleDaoMock = EasyMock.createMock(RoleDAO.class);

		reviewerRole = new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole();
		reviewer1 = new RegisteredUserBuilder().id(100).email("rev1@bla.com")//
				.role(reviewerRole)//
				.toUser();
		programme = new ProgramBuilder().id(1).title("super prog").reviewers(reviewer1).toProgram();

		reviewService = new ReviewService(userDaoMock, roleDaoMock);
	}

	@Test(expected = IllegalStateException.class)
	public void throwISEwhenUserAlreadyExists() {
		EasyMock.expect(userDaoMock.getUserByEmail("some@email.com")).andReturn(reviewer1);
		EasyMock.replay(userDaoMock, roleDaoMock);
		reviewService.createNewReviewerForProgramme(programme, "la", "le", "some@email.com");
	}

	@Test
	public void createUserAndAddToProgramme() {
		EasyMock.expect(userDaoMock.getUserByEmail("some@email.com")).andReturn(null);
		EasyMock.expect(roleDaoMock.getRoleByAuthority(Authority.REVIEWER)).andReturn(reviewerRole);
		userDaoMock.save(EasyMock.anyObject(RegisteredUser.class));
		EasyMock.expectLastCall().andDelegateTo(new SimulateIDSetting(340));

		EasyMock.replay(userDaoMock, roleDaoMock);
		RegisteredUser newReviewer = reviewService.createNewReviewerForProgramme(programme, "la", "le", "some@email.com");

		EasyMock.verify(userDaoMock, roleDaoMock);
		Assert.assertEquals(2, programme.getReviewers().size());
		Assert.assertTrue(programme.getReviewers().contains(reviewer1));
		Assert.assertTrue(programme.getReviewers().contains(newReviewer));
		Assert.assertTrue(newReviewer.getProgramsOfWhichReviewer().contains(programme));
		Assert.assertTrue(newReviewer.isInRole(Authority.REVIEWER));
	}

	@Test
	public void ignoreExistingReviewerInProgramme() {
		EasyMock.replay(userDaoMock, roleDaoMock);
		reviewService.addUserToProgramme(programme, reviewer1);

		EasyMock.verify(userDaoMock, roleDaoMock);
		// still same amount of reviewers:
		Assert.assertEquals(1, programme.getReviewers().size());
		Assert.assertTrue(programme.getReviewers().contains(reviewer1));
		Assert.assertTrue(programme.getReviewers().contains(reviewer1));
	}

	@Test
	public void addReviewerToProgramme() {
		RegisteredUser reviewer2 = new RegisteredUserBuilder().id(101).role(reviewerRole).toUser();
		userDaoMock.save(reviewer2);
		EasyMock.expectLastCall();

		EasyMock.replay(userDaoMock, roleDaoMock);
		reviewService.addUserToProgramme(programme, reviewer2);

		EasyMock.verify(userDaoMock, roleDaoMock);
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

		EasyMock.replay(userDaoMock, roleDaoMock);
		reviewService.addUserToProgramme(programme, reviewer2);

		EasyMock.verify(userDaoMock, roleDaoMock);
		Assert.assertEquals(2, programme.getReviewers().size());
		Assert.assertTrue(programme.getReviewers().contains(reviewer1));
		Assert.assertTrue(programme.getReviewers().contains(reviewer2));

		Assert.assertTrue(reviewer2.getProgramsOfWhichReviewer().contains(programme));
		Assert.assertTrue(reviewer2.isInRole(Authority.REVIEWER));
	}
	
	class SimulateIDSetting extends UserDAO {
		private final Integer id;
		public SimulateIDSetting(Integer id) {
			super(null);
			this.id = id;
		}
		@Override
		public void save(RegisteredUser user) {
			user.setId(id);
		}
	}
}
