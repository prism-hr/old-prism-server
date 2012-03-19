package com.zuehlke.pgadmissions.controllers;

import java.util.Arrays;

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
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
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
		EasyMock.expect(currentUser.getAuthorities()).andReturn(Arrays.asList(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()));
		EasyMock.expect(currentUser.getAuthorities()).andReturn(Arrays.asList(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()));
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
		EasyMock.replay(currentUser);
		Assert.assertEquals("private/staff/superAdmin/assign_roles_page", manageUsersController.getUsersPage().getViewName());
	}
	
	@Test(expected=AccessDeniedException.class)
	public void shouldThrowExceptionForNonAdministrators() {
		EasyMock.expect(currentUser.getAuthorities()).andReturn(Arrays.asList(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()));
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
		EasyMock.replay(currentUser);
		manageUsersController.getUsersPage();
	}
	
	@Test
	public void shouldReturnAllUsersAndProgramsForSuperAdmin() {
		
		EasyMock.expect(currentUser.getAuthorities()).andReturn(Arrays.asList(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()));
		EasyMock.expect(currentUser.getAuthorities()).andReturn(Arrays.asList(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()));
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
		EasyMock.replay(currentUser);
		
		RegisteredUser approverOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("usernameOne").password("password").accountNonExpired(false).accountNonLocked(false)
		.credentialsNonExpired(false).enabled(false).toUser();
		RegisteredUser approverTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("usernameTwo").password("password").accountNonExpired(false).accountNonLocked(false)
		.credentialsNonExpired(false).enabled(false).toUser();
		
		Program program1 = new ProgramBuilder().approver(approverOne, approverTwo).toProgram();
		Program program2 = new ProgramBuilder().approver(approverOne, approverTwo).toProgram();
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
		
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(currentUser, approverOne, approverTwo));
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(currentUser, approverOne, approverTwo));
		
		EasyMock.replay(programsServiceMock, userDAOMock);
		Assert.assertEquals("private/staff/superAdmin/assign_roles_page", manageUsersController.getUsersPage().getViewName());
		ManageUsersModel model = (ManageUsersModel) manageUsersController.getUsersPage().getModel().get("model");
		Assert.assertEquals(2, model.getPrograms().size());
		Assert.assertEquals(3, model.getUsersInRoles().size());
	}
	
	@Test
	public void shouldReturnProgramUsersAndProgramForSetAdmin() {
		
		EasyMock.expect(currentUser.getAuthorities()).andReturn(Arrays.asList(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()));
		EasyMock.expect(currentUser.getAuthorities()).andReturn(Arrays.asList(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()));
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
		EasyMock.replay(currentUser);
		
		RegisteredUser approverOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("usernameOne").password("password").accountNonExpired(false).accountNonLocked(false)
		.credentialsNonExpired(false).enabled(false).toUser();
		RegisteredUser approverTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("usernameTwo").password("password").accountNonExpired(false).accountNonLocked(false)
		.credentialsNonExpired(false).enabled(false).toUser();
		
		Program program1 = new ProgramBuilder().approver(approverOne, approverTwo).toProgram();
		Program program2 = new ProgramBuilder().approver(approverOne).administrator(currentUser).toProgram();
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
		
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(currentUser, approverOne, approverTwo));
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(currentUser, approverOne, approverTwo));
		
		EasyMock.replay(programsServiceMock, userDAOMock);
		Assert.assertEquals("private/staff/superAdmin/assign_roles_page", manageUsersController.getUsersPage().getViewName());
		ManageUsersModel model = (ManageUsersModel) manageUsersController.getUsersPage().getModel().get("model");
		Assert.assertEquals(1, model.getPrograms().size());
		Assert.assertEquals(2, model.getUsersInRoles().size());
	}
	
	@Test
	public void shouldNotReturnProgramUsersAndProgramForUnSetAdmin() {
		
		EasyMock.expect(currentUser.getAuthorities()).andReturn(Arrays.asList(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()));
		EasyMock.expect(currentUser.getAuthorities()).andReturn(Arrays.asList(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()));
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(true);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
		EasyMock.replay(currentUser);
		
		RegisteredUser approverOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("usernameOne").password("password").accountNonExpired(false).accountNonLocked(false)
		.credentialsNonExpired(false).enabled(false).toUser();
		RegisteredUser approverTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("usernameTwo").password("password").accountNonExpired(false).accountNonLocked(false)
		.credentialsNonExpired(false).enabled(false).toUser();
		
		Program program1 = new ProgramBuilder().approver(approverOne, approverTwo).toProgram();
		Program program2 = new ProgramBuilder().approver(approverOne).toProgram();
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
		
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(currentUser, approverOne, approverTwo));
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(currentUser, approverOne, approverTwo));
		
		EasyMock.replay(programsServiceMock, userDAOMock);
		Assert.assertEquals("private/staff/superAdmin/assign_roles_page", manageUsersController.getUsersPage().getViewName());
		ManageUsersModel model = (ManageUsersModel) manageUsersController.getUsersPage().getModel().get("model");
		Assert.assertEquals(0, model.getPrograms().size());
		Assert.assertEquals(0, model.getUsersInRoles().size());
	}
	
	@Before
	public void setup() {

		currentUser = EasyMock.createMock(RegisteredUser.class);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		programsServiceMock = EasyMock.createMock(ProgramsService.class);
		userDAOMock = EasyMock.createMock(UserDAO.class);
		roleDAOMock = EasyMock.createMock(RoleDAO.class);
		userService = new UserService(userDAOMock, roleDAOMock);
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
