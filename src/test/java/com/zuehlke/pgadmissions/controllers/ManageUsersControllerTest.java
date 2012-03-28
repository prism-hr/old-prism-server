package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.AccessDeniedException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;

public class ManageUsersControllerTest {

	private RegisteredUser currentUser;
	private ProgramsService programsServiceMock;
	private ManageUsersController manageUsersController;
	private UserService userServiceMock;

	@Test
	public void shouldGetSelectedUserIfIdProvided() {
		RegisteredUser user = new RegisteredUserBuilder().id(5).toUser();
		EasyMock.expect(userServiceMock.getUser(5)).andReturn(user);
		EasyMock.replay(userServiceMock);

		assertEquals(user, manageUsersController.getSelectedUser(5));
	}

	@Test
	public void shoudlReturnNullIfUserIdNotProvided() {
		assertNull(manageUsersController.getSelectedUser(null));
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoucnExceptionIfNoSuchUser() {

		EasyMock.expect(userServiceMock.getUser(5)).andReturn(null);
		EasyMock.replay(userServiceMock);
		manageUsersController.getSelectedUser(5);
	}

	@Test
	public void shouldGetSelectedProgramfIdProvided() {
		Program program = new ProgramBuilder().id(5).toProgram();
		EasyMock.expect(programsServiceMock.getProgramById(5)).andReturn(program);
		EasyMock.replay(programsServiceMock);

		assertEquals(program, manageUsersController.getSelectedProgram(5));
	}

	@Test
	public void shoudlReturnNullIfProgramIdNotProvided() {
		assertNull(manageUsersController.getSelectedProgram(null));
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoucnExceptionIfNoSuchProgram() {

		EasyMock.expect(programsServiceMock.getProgramById(5)).andReturn(null);
		EasyMock.replay(programsServiceMock);
		manageUsersController.getSelectedProgram(5);
	}

	@Test
	public void shouldReturnAllInternalusers() {

		RegisteredUser userOne = new RegisteredUserBuilder().id(1).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(2).toUser();
		EasyMock.expect(userServiceMock.getAllInternalUsers()).andReturn(Arrays.asList(userOne, userTwo));
		EasyMock.replay(userServiceMock);
		List<RegisteredUser> internalUsers = manageUsersController.getavailableUsers();
		assertEquals(2, internalUsers.size());
		assertTrue(internalUsers.containsAll(Arrays.asList(userOne, userTwo)));
	}

	@Test(expected = AccessDeniedException.class)
	public void shouldThrowExceptionForNonAdministrators() {
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(false).anyTimes();

		EasyMock.replay(currentUser);
		manageUsersController.getUsersPage(null, null);
	}

	@Test
	public void shouldReturnCorrectView() {
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUser);
		assertEquals("private/staff/superAdmin/assign_roles_page", manageUsersController.getUsersPage(null,  new ModelMap()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldReturnEmptyUserInRoleListIfNoProgram() {
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUser);
		ModelMap modelMap = new ModelMap();
		manageUsersController.getUsersPage(null, modelMap);
		List<RegisteredUser> users = (List<RegisteredUser>) modelMap.get("usersInRoles");
		assertTrue(users.isEmpty());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldReturnAllUsersForProgramIfProgamProvided() {
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUser);
		Program program = new ProgramBuilder().id(5).toProgram();
		RegisteredUser userOne = new RegisteredUserBuilder().id(3).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(4).toUser();
		EasyMock.expect(userServiceMock.getAllUsersForProgram(program)).andReturn(Arrays.asList(userOne, userTwo));
		EasyMock.replay(userServiceMock);
		ModelMap modelMap = new ModelMap();
		manageUsersController.getUsersPage(program,modelMap);
		List<RegisteredUser> users = (List<RegisteredUser>) modelMap.get("usersInRoles");
		assertEquals(2, users.size());
		assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));
	}

	@Test
	public void shouldGetCurrentUserFromSecurityContext() {
		assertEquals(currentUser, manageUsersController.getCurrentUser());
	}

	@Test
	public void shouldReturnCorrectPossibleRolesForSuperadmin() {
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUser);
		List<Authority> authorities = manageUsersController.getAuthorities();
		assertEquals(4, authorities.size());
		assertTrue(authorities.containsAll(Arrays.asList(Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER, Authority.SUPERADMINISTRATOR)));
	}

	@Test
	public void shouldReturnCorrectPossibleRolesForAdmin() {
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUser);
		List<Authority> authorities = manageUsersController.getAuthorities();
		assertEquals(3, authorities.size());
		assertTrue(authorities.containsAll(Arrays.asList(Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER)));
	}

	@Test
	public void shouldReturnAllProgramsForSuperAdmin() {
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUser);

		Program programOne = new ProgramBuilder().id(1).toProgram();
		Program programTwo = new ProgramBuilder().id(2).toProgram();
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(programOne, programTwo));
		EasyMock.replay(programsServiceMock);

		List<Program> allPrograms = manageUsersController.getPrograms();

		assertEquals(2, allPrograms.size());
		assertTrue(allPrograms.containsAll(Arrays.asList(programOne, programTwo)));
	}

	@Test
	public void shouldReturnProgramsOfWhichUserAdministratorForAdmin() {
		Program programOne = new ProgramBuilder().id(1).toProgram();
		Program programTwo = new ProgramBuilder().id(2).toProgram();

		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.expect(currentUser.getProgramsOfWhichAdministrator()).andReturn(Arrays.asList(programOne, programTwo));
		EasyMock.replay(currentUser);

		List<Program> allPrograms = manageUsersController.getPrograms();

		assertEquals(2, allPrograms.size());
		assertTrue(allPrograms.containsAll(Arrays.asList(programOne, programTwo)));
	}
	
	

	@Before
	public void setup() {

		currentUser = EasyMock.createMock(RegisteredUser.class);

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		programsServiceMock = EasyMock.createMock(ProgramsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		manageUsersController = new ManageUsersController(programsServiceMock, userServiceMock);

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
