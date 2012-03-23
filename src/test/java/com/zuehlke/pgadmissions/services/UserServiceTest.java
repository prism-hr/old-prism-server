package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class UserServiceTest {

	private UserDAO userDAOMock;
	private RegisteredUser user;
	private UserService userService;
	private RoleDAO roleDAOMock;

	@Test
	public void shouldGetUserFromDAO() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).toUser();
		EasyMock.expect(userDAOMock.get(1)).andReturn(user);
		EasyMock.replay(userDAOMock);
		assertEquals(user, userService.getUser(1));
	}

	@Test
	public void shouldgetListOfReviewersForApplication() {
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).username("tom").role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(reviewer));
		EasyMock.replay(userDAOMock);
		List<RegisteredUser> reviewersForApplication = userService.getReviewersForApplication(form);
		Assert.assertTrue(reviewersForApplication.contains(reviewer));
		Assert.assertEquals(1, reviewersForApplication.size());
	}

	@Test
	public void shouldgetEmptyListOfReviewersForApplication() {
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).username("tom").role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();

		Set<RegisteredUser> reviewers = new HashSet<RegisteredUser>();
		reviewers.add(reviewer);

		ApplicationForm form = new ApplicationFormBuilder().id(1).reviewers(reviewers).toApplicationForm();
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(reviewer));
		EasyMock.replay(userDAOMock);

		List<RegisteredUser> reviewersForApplication = userService.getReviewersForApplication(form);

		Assert.assertFalse(reviewersForApplication.contains(reviewer));
		Assert.assertEquals(0, reviewersForApplication.size());
	}

	@Test
	public void shouldGetAllUsersWithAuthority() {
		RegisteredUser userOne = new RegisteredUserBuilder().id(1).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(2).toUser();
		Authority auth = Authority.ADMINISTRATOR;
		Role role = new RoleBuilder().id(1).toRole();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(auth)).andReturn(role);
		EasyMock.expect(userDAOMock.getUsersInRole(role)).andReturn(Arrays.asList(userOne, userTwo));
		EasyMock.replay(roleDAOMock, userDAOMock);
		
		List<RegisteredUser>users = userService.getUsersInRole(auth);
		assertEquals(2, users.size());
		assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));
	}

	@Test
	public void shouldGetSuperAdministrators(){
		Role super1 = new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).id(1).toRole();
		Role reviewer = new RoleBuilder().authorityEnum(Authority.REVIEWER).id(2).toRole();
		Role super2 = new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).id(3).toRole();
		Role applicant = new RoleBuilder().authorityEnum(Authority.APPLICANT).id(4).toRole();
		RegisteredUser userOne = new RegisteredUserBuilder().id(1).role(applicant).toUser();
		RegisteredUser superAdmin1 = new RegisteredUserBuilder().id(2).role(super2).toUser();
		RegisteredUser superAdmin2 = new RegisteredUserBuilder().id(3).roles(super1, reviewer).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(4).role(reviewer).toUser();
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(userOne, userTwo, superAdmin1, superAdmin2));
		EasyMock.replay(userDAOMock);
		List<RegisteredUser> superAdmins = userService.getSuperAdmins();
		assertEquals(2, superAdmins.size());
		assertTrue(superAdmins.containsAll(Arrays.asList(superAdmin1, superAdmin2)));
	}
	
	@Before
	public void setUp() {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		user = new RegisteredUserBuilder().id(1).username("bob").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		authenticationToken.setDetails(user);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		userDAOMock = EasyMock.createMock(UserDAO.class);
		roleDAOMock = EasyMock.createMock(RoleDAO.class);
		userService = new UserService(userDAOMock, roleDAOMock);

	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
