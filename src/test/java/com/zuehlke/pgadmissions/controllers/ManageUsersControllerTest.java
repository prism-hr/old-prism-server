package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.Program;
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
	private UserService userServiceMock;

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
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.SUPERADMINISTRATOR).toRole();
		EasyMock.expect(currentUser.getAuthorities()).andReturn(Arrays.asList(role)).anyTimes();		
		EasyMock.replay(currentUser);
		 ModelAndView modelAndView = manageUsersController.getUsersPage(null, null);
		 ManageUsersModel model = (ManageUsersModel) modelAndView.getModel().get("model");
		 assertNull(model.getSelectedProgram());
		 assertNull(model.getSelectedUser());
		assertEquals("private/staff/superAdmin/assign_roles_page",modelAndView.getViewName());
	}
	
	@Test
	public void shouldGetCurrentUserFromSecurityContextAndSetOnModel() {
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();	
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.SUPERADMINISTRATOR).toRole();
		EasyMock.expect(currentUser.getAuthorities()).andReturn(Arrays.asList(role)).anyTimes();	
		EasyMock.replay(currentUser);
		 ModelAndView modelAndView = manageUsersController.getUsersPage(null, null);
		 ManageUsersModel model = (ManageUsersModel) modelAndView.getModel().get("model");
		 assertEquals(currentUser, model.getUser());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldAddProgramAndUsesInProgramToModelIfProvided(){
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();	
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.SUPERADMINISTRATOR).toRole();
		EasyMock.expect(currentUser.getAuthorities()).andReturn(Arrays.asList(role)).anyTimes();	
		EasyMock.replay(currentUser);
		Program program = new ProgramBuilder().id(5).toProgram();
		EasyMock.expect(programsServiceMock.getProgramById(5)).andReturn(program);
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Collections.EMPTY_LIST);
		EasyMock.replay(programsServiceMock);
		RegisteredUser userOne = new RegisteredUserBuilder().id(1).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(2).toUser();
		EasyMock.expect(userServiceMock.getAllUsersForProgram(program)).andReturn(Arrays.asList(userOne, userTwo));
		EasyMock.expect(userServiceMock.getAllInternalUsers()).andReturn(Collections.EMPTY_LIST);
		EasyMock.replay(userServiceMock);
		
		 ModelAndView modelAndView = manageUsersController.getUsersPage(5, null);
		 ManageUsersModel model = (ManageUsersModel) modelAndView.getModel().get("model");
		 assertEquals(program, model.getSelectedProgram());
		assertEquals(2, model.getUsersInRoles().size());
		assertTrue(model.getUsersInRoles().containsAll(Arrays.asList(userOne, userTwo)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldAddUserToModelIfProvided(){
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();	
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.SUPERADMINISTRATOR).toRole();
		EasyMock.expect(currentUser.getAuthorities()).andReturn(Arrays.asList(role)).anyTimes();	
		EasyMock.replay(currentUser);
		RegisteredUser user = new RegisteredUserBuilder().id(5).toUser();
		EasyMock.expect(userServiceMock.getUser(5)).andReturn(user);
		EasyMock.expect(userServiceMock.getAllInternalUsers()).andReturn(Collections.EMPTY_LIST);
		EasyMock.replay(userServiceMock);
		ModelAndView modelAndView = manageUsersController.getUsersPage(null, 5);
		ManageUsersModel model = (ManageUsersModel) modelAndView.getModel().get("model");
		assertEquals(user, model.getSelectedUser());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldReturnCorrectPossibleRolesForSuperadmin(){
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();	
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.SUPERADMINISTRATOR).toRole();
		EasyMock.expect(currentUser.getAuthorities()).andReturn(Arrays.asList(role)).anyTimes();	
		EasyMock.replay(currentUser);	
		
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Collections.EMPTY_LIST);
		EasyMock.replay(programsServiceMock);
		ModelAndView modelAndView = manageUsersController.getUsersPage(null, null);
		ManageUsersModel model = (ManageUsersModel) modelAndView.getModel().get("model");
		assertEquals(4, model.getRoles().size());
		assertTrue(model.getRoles().containsAll(Arrays.asList(Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER, Authority.SUPERADMINISTRATOR)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldReturnCorrectPossibleRolesForAdmin(){
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.SUPERADMINISTRATOR).toRole();
		EasyMock.expect(currentUser.getAuthorities()).andReturn(Arrays.asList(role)).anyTimes();	
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.expect(currentUser.getProgramsOfWhichAdministrator()).andReturn(Collections.EMPTY_LIST);
		EasyMock.replay(currentUser);	
		ModelAndView modelAndView = manageUsersController.getUsersPage(null, null);
		ManageUsersModel model = (ManageUsersModel) modelAndView.getModel().get("model");
		assertEquals(3, model.getRoles().size());
		assertTrue(model.getRoles().containsAll(Arrays.asList(Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER)));
	}
	
	@Test
	public void shouldReturnAllProgramsForSuperAdmin(){
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();			
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.SUPERADMINISTRATOR).toRole();
		EasyMock.expect(currentUser.getAuthorities()).andReturn(Arrays.asList(role)).anyTimes();	
		EasyMock.replay(currentUser);	
		
		Program programOne = new ProgramBuilder().id(1).toProgram();
		Program programTwo = new ProgramBuilder().id(2).toProgram();
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(programOne, programTwo));
		EasyMock.replay(programsServiceMock);
		
		ModelAndView modelAndView = manageUsersController.getUsersPage(null, null);
		
		ManageUsersModel model = (ManageUsersModel) modelAndView.getModel().get("model");
		assertEquals(2, model.getPrograms().size());
		assertTrue(model.getPrograms().containsAll(Arrays.asList(programOne, programTwo)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldReturnAllInternalUsers(){
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();		
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.SUPERADMINISTRATOR).toRole();
		EasyMock.expect(currentUser.getAuthorities()).andReturn(Arrays.asList(role)).anyTimes();	
		EasyMock.replay(currentUser);	
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Collections.EMPTY_LIST);
		EasyMock.replay(programsServiceMock);
		
		RegisteredUser userOne = new RegisteredUserBuilder().id(1).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(2).toUser();
		
		EasyMock.expect(userServiceMock.getAllInternalUsers()).andReturn(Arrays.asList(userOne, userTwo));
		
		EasyMock.replay(userServiceMock);
		ModelAndView modelAndView = manageUsersController.getUsersPage(null, null);
		EasyMock.verify(userServiceMock);
		ManageUsersModel model = (ManageUsersModel) modelAndView.getModel().get("model");
		assertEquals(2, model.getAvailableUsers().size());
		assertTrue(model.getAvailableUsers().containsAll(Arrays.asList(userOne, userTwo)));
		
	}
	@Test
	public void shouldReturnProgramsOfWhichUserAdministratorForAdmin(){
		Program programOne = new ProgramBuilder().id(1).toProgram();
		Program programTwo = new ProgramBuilder().id(2).toProgram();
		
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();	
		EasyMock.expect(currentUser.getProgramsOfWhichAdministrator()).andReturn(Arrays.asList(programOne, programTwo));
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.SUPERADMINISTRATOR).toRole();
		EasyMock.expect(currentUser.getAuthorities()).andReturn(Arrays.asList(role)).anyTimes();	
		EasyMock.replay(currentUser);	
		
		
		ModelAndView modelAndView = manageUsersController.getUsersPage(null, null);
		
		ManageUsersModel model = (ManageUsersModel) modelAndView.getModel().get("model");
		assertEquals(2, model.getPrograms().size());
		assertTrue(model.getPrograms().containsAll(Arrays.asList(programOne, programTwo)));
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
