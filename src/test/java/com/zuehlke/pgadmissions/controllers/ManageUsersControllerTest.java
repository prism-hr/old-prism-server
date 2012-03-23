package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.PersonalDetail;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.AccessDeniedException;
import com.zuehlke.pgadmissions.pagemodels.ManageUsersModel;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;

public class ManageUsersControllerTest {

	private RegisteredUser currentUser;
	private ProgramsService programsServiceMock;
	private ManageUsersController manageUsersController;
	private UserDAO userDAOMock;
	private RoleDAO roleDAOMock;
	private UserService userService;

	@Test
	public void shouldReturnCorrectView() {
		Role super1 = new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).id(1).toRole();
		RegisteredUser superAdmin1 = new RegisteredUserBuilder().id(2).role(super1).toUser();
		
		EasyMock.expect(currentUser.getAuthorities()).andReturn(
				Arrays.asList(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()));
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(superAdmin1));
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(new ArrayList<Program>());
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(new ArrayList<Program>());
		EasyMock.replay(currentUser, programsServiceMock, userDAOMock);
		Assert.assertEquals("private/staff/superAdmin/assign_roles_page", manageUsersController.getUsersPage(null)
				.getViewName());
	}

	@Test(expected = AccessDeniedException.class)
	public void shouldThrowExceptionForNonAdministrators() {
		EasyMock.expect(currentUser.getAuthorities()).andReturn(
				Arrays.asList(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()));
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(currentUser));

		EasyMock.replay(currentUser);
		manageUsersController.getUsersPage(null);
	}

	@Test
	public void shouldReturnAllUsersAndProgramsForSuperAdmin() {

		EasyMock.expect(currentUser.getAuthorities()).andReturn(
				Arrays.asList(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()));
		EasyMock.expect(currentUser.getAuthorities()).andReturn(
				Arrays.asList(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()));
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
		EasyMock.replay(currentUser);

		RegisteredUser approverOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
				.email("email@test.com").username("usernameOne").password("password").accountNonExpired(false)
				.accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		RegisteredUser approverTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
				.email("email@test.com").username("usernameTwo").password("password").accountNonExpired(false)
				.accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		Program program1 = new ProgramBuilder().id(1).approver(approverOne, approverTwo).toProgram();
		Program program2 = new ProgramBuilder().approver(approverOne, approverTwo).toProgram();
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
		EasyMock.expect(programsServiceMock.getProgramById(1)).andReturn(program1);

		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(currentUser, approverOne, approverTwo));
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(currentUser, approverOne, approverTwo));
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(currentUser, approverOne, approverTwo));

		EasyMock.replay(programsServiceMock, userDAOMock);
		Assert.assertEquals("private/staff/superAdmin/assign_roles_page", manageUsersController.getUsersPage(null)
				.getViewName());
		ManageUsersModel model = (ManageUsersModel) manageUsersController.getUsersPage(1).getModel().get("model");
		Assert.assertEquals(2, model.getPrograms().size());
		Assert.assertEquals(2, model.getUsersInRoles().size());
		Assert.assertTrue(model.getRoles().contains(Authority.SUPERADMINISTRATOR));
	}

	@Test
	public void shouldReturnProgramUsersAndProgramForSetAdmin() {

		EasyMock.expect(currentUser.getAuthorities()).andReturn(
				Arrays.asList(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()));
		EasyMock.expect(currentUser.getAuthorities()).andReturn(
				Arrays.asList(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()));
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
		EasyMock.replay(currentUser);
		RegisteredUser approverOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
				.email("email@test.com").username("usernameOne").password("password").accountNonExpired(false)
				.accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		RegisteredUser approverTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
				.email("email@test.com").username("usernameTwo").password("password").accountNonExpired(false)
				.accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		Program program1 = new ProgramBuilder().id(1).approver(approverOne, approverTwo).toProgram();
		Program program2 = new ProgramBuilder().approver(approverOne).administrator(currentUser).toProgram();
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
	
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(currentUser, approverOne, approverTwo));
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(currentUser, approverOne, approverTwo));
		Role admin = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole();
		Role approver = new RoleBuilder().authorityEnum(Authority.APPROVER).toRole();
		EasyMock.expect(programsServiceMock.getProgramById(1)).andReturn(program1);
		EasyMock.replay(programsServiceMock, userDAOMock);
		Assert.assertEquals("private/staff/superAdmin/assign_roles_page", manageUsersController.getUsersPage(null)
				.getViewName());
		ManageUsersModel model = (ManageUsersModel) manageUsersController.getUsersPage(1).getModel().get("model");
		Assert.assertEquals(1, model.getPrograms().size());
		Assert.assertEquals(2, model.getUsersInRoles().size());
	}

	@Test
	public void shouldNotReturnProgramUsersAndProgramForUnSetAdmin() {

		EasyMock.expect(currentUser.getAuthorities()).andReturn(
				Arrays.asList(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()));
		EasyMock.expect(currentUser.getAuthorities()).andReturn(
				Arrays.asList(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()));
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
		EasyMock.replay(currentUser);

		RegisteredUser approverOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
				.email("email@test.com").username("usernameOne").password("password").accountNonExpired(false)
				.accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		RegisteredUser approverTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
				.email("email@test.com").username("usernameTwo").password("password").accountNonExpired(false)
				.accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		Program program1 = new ProgramBuilder().approver(approverOne, approverTwo).toProgram();
		Program program2 = new ProgramBuilder().approver(approverOne).toProgram();
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));

		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(currentUser, approverOne, approverTwo));
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(currentUser, approverOne, approverTwo));

		EasyMock.replay(programsServiceMock, userDAOMock);
		Assert.assertEquals("private/staff/superAdmin/assign_roles_page", manageUsersController.getUsersPage(null)
				.getViewName());
		ManageUsersModel model = (ManageUsersModel) manageUsersController.getUsersPage(null).getModel().get("model");
		Assert.assertEquals(0, model.getPrograms().size());
		Assert.assertEquals(0, model.getUsersInRoles().size());
		Assert.assertTrue(!model.getRoles().contains(Authority.SUPERADMINISTRATOR));
	}

	@Test
	public void shouldReturnUsersOfAspecificProgram() {
		EasyMock.expect(currentUser.getAuthorities()).andReturn(
				Arrays.asList(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()));
		EasyMock.expect(currentUser.getAuthorities()).andReturn(
				Arrays.asList(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()));
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
		EasyMock.replay(currentUser);

		RegisteredUser approverOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
				.email("email@test.com").username("usernameOne").password("password").accountNonExpired(false)
				.accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		RegisteredUser approverTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
				.email("email@test.com").username("usernameTwo").password("password").accountNonExpired(false)
				.accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		Program program1 = new ProgramBuilder().id(1).approver(approverOne).toProgram();
		Program program2 = new ProgramBuilder().approver(approverOne, approverTwo).toProgram();
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
		EasyMock.expect(programsServiceMock.getProgramById(1)).andReturn(program1);

		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(currentUser, approverOne));
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(currentUser, approverOne, approverTwo));

		EasyMock.replay(programsServiceMock, userDAOMock);
		Assert.assertEquals("private/staff/superAdmin/assign_roles_page", manageUsersController.getUsersPage(null)
				.getViewName());
		ManageUsersModel model = (ManageUsersModel) manageUsersController.getUsersPage(1).getModel().get("model");
		Assert.assertEquals(2, model.getPrograms().size());
		Assert.assertEquals(1, model.getUsersInRoles().size());
	}
	
	@Test
	public void shouldProcessReviewersForProgram(){
		Role super1 = new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).id(1).toRole();
		Role super2 = new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).id(3).toRole();
		Role reviewer = new RoleBuilder().authorityEnum(Authority.REVIEWER).id(2).toRole();
		Role applicant = new RoleBuilder().authorityEnum(Authority.APPLICANT).id(4).toRole();
		RegisteredUser userOne = new RegisteredUserBuilder().id(1).role(applicant).toUser();
		RegisteredUser superAdmin1 = new RegisteredUserBuilder().id(2).role(super2).toUser();
		RegisteredUser superAdmin2 = new RegisteredUserBuilder().id(3).roles(super1, reviewer).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(4).role(reviewer).toUser();
		RegisteredUser reviewerUser = new RegisteredUserBuilder().id(1).role(reviewer).toUser();
		Program program = new ProgramBuilder().reviewers(reviewerUser).id(1).toProgram();
		List<RegisteredUser> reviewers = manageUsersController.processReviewersForProgram(Arrays.asList(userTwo, superAdmin1, superAdmin2), program);
		Assert.assertEquals(1, reviewers.size());
	}
	
	@Test
	public void shouldProcessApproversForProgram(){
		Role super1 = new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).id(1).toRole();
		Role super2 = new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).id(3).toRole();
		Role reviewer = new RoleBuilder().authorityEnum(Authority.REVIEWER).id(2).toRole();
		Role applicant = new RoleBuilder().authorityEnum(Authority.APPLICANT).id(4).toRole();
		RegisteredUser superAdmin1 = new RegisteredUserBuilder().id(2).role(super2).toUser();
		RegisteredUser superAdmin2 = new RegisteredUserBuilder().id(3).roles(super1, reviewer).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(4).role(reviewer).toUser();
		Role approver = new RoleBuilder().authorityEnum(Authority.APPROVER).id(2).toRole();
		RegisteredUser approverUser = new RegisteredUserBuilder().id(1).role(approver).toUser();
		Program program = new ProgramBuilder().approver(approverUser).id(1).toProgram();
		List<RegisteredUser> approvers = manageUsersController.processApproversForProgram(Arrays.asList(userTwo, superAdmin1, superAdmin2),program);
		Assert.assertEquals(1, approvers.size());
	}

	@Test
	public void shouldProcessAdminsForProgram(){
		Role reviewer = new RoleBuilder().authorityEnum(Authority.REVIEWER).id(2).toRole();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(4).role(reviewer).toUser();
		Role admin = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).id(2).toRole();
		RegisteredUser adminUser = new RegisteredUserBuilder().id(1).role(admin).toUser();
		Program program = new ProgramBuilder().administrator(adminUser).id(1).toProgram();
		List<RegisteredUser> admins = manageUsersController.processAdministratorsForProgram(Arrays.asList( userTwo),program);
		Assert.assertEquals(1, admins.size());
	}

	@Before
	public void setup() {

		currentUser = EasyMock.createMock(RegisteredUser.class);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		programsServiceMock = EasyMock.createMock(ProgramsService.class);
		userDAOMock = EasyMock.createMock(UserDAO.class);
		roleDAOMock = EasyMock.createMock(RoleDAO.class);
		userService = new UserService(userDAOMock, roleDAOMock){
			@Override
			public
			List<RegisteredUser> getSuperAdmins(){
				return new ArrayList<RegisteredUser>();
			}
		};
		manageUsersController = new ManageUsersController(programsServiceMock, userService);

		authenticationToken.setDetails(currentUser);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
