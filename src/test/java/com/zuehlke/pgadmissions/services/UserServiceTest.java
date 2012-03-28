package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
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
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
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
		Role superAdminRole = new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).id(1).toRole();		
		
		RegisteredUser superAdmin1 = new RegisteredUserBuilder().id(2).role(superAdminRole).toUser();
		RegisteredUser superAdmin2 = new RegisteredUserBuilder().id(3).roles(superAdminRole).toUser();
		
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.SUPERADMINISTRATOR)).andReturn(superAdminRole);
		EasyMock.replay(roleDAOMock);
		EasyMock.expect(userDAOMock.getUsersInRole(superAdminRole)).andReturn(Arrays.asList(superAdmin1, superAdmin2));
		EasyMock.replay(userDAOMock);
		List<RegisteredUser> superAdmins = userService.getSuperAdmins();
		assertEquals(2, superAdmins.size());
		assertTrue(superAdmins.containsAll(Arrays.asList(superAdmin1, superAdmin2)));
	}
	
	@Test
	public void shouldGetAllUsersForProgram(){
		RegisteredUser userOne = new RegisteredUserBuilder().id(2).toUser();
		RegisteredUser userTow = new RegisteredUserBuilder().id(3).toUser();
		Program program = new ProgramBuilder().id(7).toProgram();
		EasyMock.expect(userDAOMock.getUsersForProgram(program)).andReturn(Arrays.asList(userOne, userTow));
		EasyMock.replay(userDAOMock);
		List<RegisteredUser> users = userService.getAllUsersForProgram(program);
		assertEquals(2, users.size());
		assertTrue(users.containsAll(Arrays.asList(userOne, userTow)));
	}
	
	@Test
	public void shouldGetAllInternalUsers(){
		final RegisteredUser userOne = new RegisteredUserBuilder().id(1).toUser();
		final RegisteredUser userTwo = new RegisteredUserBuilder().id(2).toUser();
		final RegisteredUser userThree = new RegisteredUserBuilder().id(3).toUser();
		final RegisteredUser userFour = new RegisteredUserBuilder().id(4).toUser();
		final RegisteredUser userFive = new RegisteredUserBuilder().id(5).toUser();
		userService = new UserService(userDAOMock, roleDAOMock){

			@SuppressWarnings("unchecked")
			@Override
			public List<RegisteredUser> getUsersInRole(Authority auth) {
				if(auth == Authority.ADMINISTRATOR){
					return Arrays.asList(userOne, userTwo);
				}
				if(auth == Authority.APPROVER){
					 return Arrays.asList(userTwo, userThree);
				}
				if(auth == Authority.REVIEWER){
					 return Arrays.asList(userThree, userFour);
				}
				if(auth == Authority.SUPERADMINISTRATOR){
					 return Arrays.asList(userFour, userOne);
				}
				if(auth == Authority.APPLICANT){
					 return Arrays.asList(userFour, userFive);
				}
				return Collections.EMPTY_LIST;
			}
			
		};
		

		List<RegisteredUser> internalUsers= userService.getAllInternalUsers();
	
		assertEquals(4, internalUsers.size());
		assertTrue(internalUsers.containsAll(Arrays.asList(userOne, userTwo, userThree, userFour)));
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
