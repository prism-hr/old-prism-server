package com.zuehlke.pgadmissions.controllers.usermanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.controllers.usermanagement.ManageUsersController;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.NewAdminUserDTO;
import com.zuehlke.pgadmissions.dto.NewRolesDTO;
import com.zuehlke.pgadmissions.exceptions.AccessDeniedException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.RolePropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;

public class ManageUsersControllerTest {

	private RegisteredUser currentUser;
	private ProgramsService programsServiceMock;
	private ManageUsersController manageUsersController;
	private UserService userServiceMock;
	private RolePropertyEditor rolePropertyEditorMock;
	private ManageUsersController manageUsersControllerWithCurrentUserOverride;
	

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
	
	@Test
	public void shouldNotCreateNewUserIfInvalidDTO() {
		ModelMap modelMap = new ModelMap();
		NewAdminUserDTO adminUser = new NewAdminUserDTO();
		adminUser.setNewUserEmail("test");
		ModelAndView addNewUserModelAndView = manageUsersController.addNewUser(adminUser, null, null, modelMap);
		Assert.assertEquals("private/staff/superAdmin/create_new_user_in_role_page", addNewUserModelAndView.getViewName());
		Assert.assertEquals(4, ((BindingResult)modelMap.get("result")).getErrorCount());
		Assert.assertNull(modelMap.get("selectedProgram"));
		Assert.assertNull(modelMap.get("newUserFirstName"));
		Assert.assertNull(modelMap.get("newUserLastName"));
		Assert.assertEquals("test", modelMap.get("newUserEmail"));
	}
	
	@Test
	public void shouldUpdateExistingEmailUserWithSuperAdminRole() {
		ModelMap modelMap = new ModelMap();
		NewAdminUserDTO adminUser = new NewAdminUserDTO();
		adminUser.setNewUserFirstName("mark");
		adminUser.setNewUserLastName("jones");
		adminUser.setNewUserEmail("test@gmail.com");
		NewRolesDTO newRolesDTO = new NewRolesDTO();
		
		Role superAdminRole = new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole();
		newRolesDTO.getNewRoles().add(superAdminRole);
		
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
		
		RegisteredUser selectedUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getUserByEmail("test@gmail.com")).andReturn(selectedUser);
		EasyMock.expect(selectedUser.isInRole(Authority.APPLICANT)).andReturn(false);
		EasyMock.expect(selectedUser.isInRole(Authority.REVIEWER)).andReturn(false);
		EasyMock.expect(selectedUser.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(selectedUser.isInRole(Authority.APPROVER)).andReturn(false);
		EasyMock.expect(selectedUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
		EasyMock.expect(selectedUser.isInRole(Authority.REFEREE)).andReturn(false);
		EasyMock.expect(selectedUser.isInRole(Authority.INTERVIEWER)).andReturn(false);
		EasyMock.expect(selectedUser.getProgramsOfWhichAdministrator()).andReturn(new ArrayList<Program>());
		EasyMock.expect(selectedUser.getProgramsOfWhichApprover()).andReturn(new ArrayList<Program>());
		EasyMock.expect(selectedUser.getProgramsOfWhichReviewer()).andReturn(new ArrayList<Program>());
		EasyMock.expect(selectedUser.getProgramsOfWhichInterviewer()).andReturn(new ArrayList<Program>());
		EasyMock.expect(selectedUser.getRoles()).andReturn(new ArrayList<Role>());
		
		userServiceMock.save(selectedUser);
		
		EasyMock.replay(currentUser, selectedUser, userServiceMock);

		ModelAndView addNewUserModelAndView = manageUsersControllerWithCurrentUserOverride.addNewUser(adminUser, -1, newRolesDTO, modelMap);
		Assert.assertEquals("redirect:/manageUsers/showPage?programId=", addNewUserModelAndView.getViewName());
	}
	
	@Test
	public void shouldCreateNewUserWithSuperAdminRole() {
		ModelMap modelMap = new ModelMap();
		NewAdminUserDTO adminUser = new NewAdminUserDTO();
		adminUser.setNewUserFirstName("mark");
		adminUser.setNewUserLastName("jones");
		adminUser.setNewUserEmail("test@gmail.com");
		NewRolesDTO newRolesDTO = new NewRolesDTO();
		
		Role superAdminRole = new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole();
		newRolesDTO.getNewRoles().add(superAdminRole);
		
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
		
		EasyMock.expect(userServiceMock.getUserByEmail("test@gmail.com")).andReturn(null);
		userServiceMock.save(EasyMock.anyObject(RegisteredUser.class));
		EasyMock.replay(currentUser, userServiceMock);

		ModelAndView addNewUserModelAndView = manageUsersControllerWithCurrentUserOverride.addNewUser(adminUser, -1, newRolesDTO, modelMap);
		Assert.assertEquals("redirect:/manageUsers/showPage?programId=", addNewUserModelAndView.getViewName());
	}

	@Test(expected = AccessDeniedException.class)
	public void shouldThrowExceptionForNonAdministrators() {
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUser.getId()).andReturn(1);
		EasyMock.replay(currentUser);
		manageUsersControllerWithCurrentUserOverride.getUsersPage();
	}

	@Test
	public void shouldReturnCorrectView() {
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUser);
		assertEquals("private/staff/superAdmin/assign_roles_page", manageUsersControllerWithCurrentUserOverride.getUsersPage());
	}

	
	@Test
	public void shouldReturnEmptyUserInRoleListIfNoProgram() {
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.expect(currentUser.getId()).andReturn(1);
		EasyMock.replay(currentUser);
		
		List<RegisteredUser> users = manageUsersControllerWithCurrentUserOverride.getUsersInRoles(null);
		assertTrue(users.isEmpty());
	}

	
	@Test
	public void shouldReturnAllUsersForProgramIfProgamProvided() {
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUser);
		Program program = new ProgramBuilder().id(5).toProgram();
		RegisteredUser userOne = new RegisteredUserBuilder().id(3).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(4).toUser();
		EasyMock.expect(userServiceMock.getAllUsersForProgram(program)).andReturn(Arrays.asList(userOne, userTwo));
		EasyMock.replay(userServiceMock);		
		EasyMock.expect(programsServiceMock.getProgramById(5)).andReturn(program);
		EasyMock.replay(programsServiceMock);
		List<RegisteredUser> users = manageUsersControllerWithCurrentUserOverride.getUsersInRoles(5);		
		assertEquals(2, users.size());
		assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));
	}

	@Test
	public void shouldReloadCurrentUserToAttachToHibernateSession() {
		RegisteredUser user = new RegisteredUserBuilder().id(5).toUser();
		EasyMock.expect(currentUser.getId()).andReturn(5);
		EasyMock.expect(userServiceMock.getUser(5)).andReturn(user);
		EasyMock.replay(currentUser, userServiceMock);
		assertEquals(user, manageUsersController.getCurrentUser());
		EasyMock.verify(userServiceMock);
	}

	@Test
	public void shouldReturnCorrectPossibleRolesForSuperadmin() {
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUser);
		List<Authority> authorities = manageUsersControllerWithCurrentUserOverride.getAuthorities();
		assertEquals(5, authorities.size());
		assertTrue(authorities.containsAll(Arrays.asList(Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER, Authority.SUPERADMINISTRATOR, Authority.INTERVIEWER)));
	}

	@Test
	public void shouldReturnCorrectPossibleRolesForAdmin() {
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUser);		
		List<Authority> authorities = manageUsersControllerWithCurrentUserOverride.getAuthorities();
		assertEquals(4, authorities.size());
		assertTrue(authorities.containsAll(Arrays.asList(Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER,  Authority.INTERVIEWER)));
	}

	@Test
	public void shouldReturnAllProgramsForSuperAdmin() {
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUser);

		Program programOne = new ProgramBuilder().id(1).toProgram();
		Program programTwo = new ProgramBuilder().id(2).toProgram();
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(programOne, programTwo));
		EasyMock.replay(programsServiceMock);

		List<Program> allPrograms = manageUsersControllerWithCurrentUserOverride.getPrograms();

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

		List<Program> allPrograms = manageUsersControllerWithCurrentUserOverride.getPrograms();

		assertEquals(2, allPrograms.size());
		assertTrue(allPrograms.containsAll(Arrays.asList(programOne, programTwo)));
	}

	@Test
	public void shouldBindPropertyEditor() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.registerCustomEditor(Role.class, "newRoles", rolePropertyEditorMock);
		EasyMock.replay(binderMock);
		manageUsersController.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}

		
	@Test
	public void shouldUpdateUserAndRedirectToPageForProgram(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();		
		RegisteredUser selectedUser = new RegisteredUserBuilder().programsOfWhichReviewer(selectedProgram).id(1).toUser();
		NewRolesDTO newRolesDTO = new NewRolesDTO();
		userServiceMock.updateUserWithNewRoles(selectedUser, selectedProgram, newRolesDTO);
		EasyMock.replay(userServiceMock);
		assertEquals("redirect:/manageUsers/showPage?programId=1", manageUsersControllerWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, newRolesDTO, new ModelMap()).getViewName());
		EasyMock.verify(userServiceMock);
	
	}
	@Before
	public void setup() {

		currentUser = EasyMock.createMock(RegisteredUser.class);

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		programsServiceMock = EasyMock.createMock(ProgramsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		rolePropertyEditorMock = EasyMock.createMock(RolePropertyEditor.class);

		manageUsersController = new ManageUsersController(programsServiceMock, userServiceMock, rolePropertyEditorMock);
		
		manageUsersControllerWithCurrentUserOverride = new ManageUsersController(programsServiceMock, userServiceMock, rolePropertyEditorMock){

			@Override
			public RegisteredUser getCurrentUser() {
				return currentUser;
			}
			
		};

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
