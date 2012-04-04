package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.NewRolesDTO;
import com.zuehlke.pgadmissions.exceptions.AccessDeniedException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.RolePropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.MimeMessagePreparatorFactory;

public class ManageUsersControllerTest {

	private RegisteredUser currentUser;
	private ProgramsService programsServiceMock;
	private ManageUsersController manageUsersController;
	private UserService userServiceMock;
	private RolePropertyEditor rolePropertyEditorMock;
	private ManageUsersController manageUsersControllerWithCurrentUserOverride;
	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;

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
	public void shouldReturnCorrectViewForAddingNewUser() {
		ModelAndView createNewUserView = manageUsersControllerWithCurrentUserOverride.createNewUser(null, new ModelMap());
		Assert.assertEquals("private/staff/superAdmin/create_new_user_in_role_page", createNewUserView.getViewName());
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
		EasyMock.expect(currentUser.getId()).andReturn(1);
		EasyMock.replay(currentUser);
		manageUsersControllerWithCurrentUserOverride.getUsersPage(null, null);
	}

	@Test
	public void shouldReturnCorrectView() {
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUser);
		assertEquals("private/staff/superAdmin/assign_roles_page", manageUsersControllerWithCurrentUserOverride.getUsersPage(null, new ModelMap()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldReturnEmptyUserInRoleListIfNoProgram() {
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.expect(currentUser.getId()).andReturn(1);
		EasyMock.replay(currentUser);
		ModelMap modelMap = new ModelMap();
		manageUsersControllerWithCurrentUserOverride.getUsersPage(null, modelMap);
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
		manageUsersControllerWithCurrentUserOverride.getUsersPage(program, modelMap);
		List<RegisteredUser> users = (List<RegisteredUser>) modelMap.get("usersInRoles");
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
		assertEquals(4, authorities.size());
		assertTrue(authorities.containsAll(Arrays.asList(Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER, Authority.SUPERADMINISTRATOR)));
	}

	@Test
	public void shouldReturnCorrectPossibleRolesForAdmin() {
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUser.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUser);		
		List<Authority> authorities = manageUsersControllerWithCurrentUserOverride.getAuthorities();
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
	public void shouldSaveSelectedUser() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();
		userServiceMock.save(selectedUser);
		EasyMock.replay(userServiceMock);
		manageUsersControllerWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, new Program(), new NewRolesDTO());
		EasyMock.verify(userServiceMock);
	}

	@Test
	public void shouldAddUserRoleAdminIfNotAlreadyAdminAndAdminInNewRoles() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();		
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.ADMINISTRATOR).toRole();
		NewRolesDTO newRolesDTO = new NewRolesDTO();
		newRolesDTO.getNewRoles().add(role);		
		manageUsersControllerWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, new Program(), newRolesDTO);
		assertTrue(selectedUser.isInRole(Authority.ADMINISTRATOR));
	}	
	
	@Test
	public void shouldAddUserRoleApproverIfNotAlreadyApproverAndAproverInNewRoles() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();		
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.APPROVER).toRole();
		NewRolesDTO newRolesDTO = new NewRolesDTO();
		newRolesDTO.getNewRoles().add(role);		
		manageUsersControllerWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, new Program(), newRolesDTO);
		assertTrue(selectedUser.isInRole(Authority.APPROVER));
	}

	@Test
	public void shouldAddUserRoleReviewerIfNotAlreadyRevieweAndRevieweInNewRoles() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();		
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.REVIEWER).toRole();
		NewRolesDTO newRolesDTO = new NewRolesDTO();
		newRolesDTO.getNewRoles().add(role);		
		manageUsersControllerWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, new Program(), newRolesDTO);
		assertTrue(selectedUser.isInRole(Authority.REVIEWER));
	}
	
	@Test
	public void shouldAddUserRoleSuperAdmimnIfNotAlreadySuperadminAndSuperadminInNewRoles() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();		
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.SUPERADMINISTRATOR).toRole();
		NewRolesDTO newRolesDTO = new NewRolesDTO();
		newRolesDTO.getNewRoles().add(role);		
		manageUsersControllerWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, new Program(), newRolesDTO);
		assertTrue(selectedUser.isInRole(Authority.SUPERADMINISTRATOR));
	}
	
	@Test
	public void shouldRemoveSuperadminRoleIfNotInNewListAndUserIsSuperadmin() {
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUser);
		Role role= new RoleBuilder().id(1).authorityEnum(Authority.SUPERADMINISTRATOR).toRole();
		RegisteredUser selectedUser = new RegisteredUserBuilder().role(role).id(1).toUser();		
		NewRolesDTO newRolesDTO = new NewRolesDTO();

		manageUsersControllerWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, new Program(), newRolesDTO);
		assertFalse(selectedUser.isInRole(Authority.SUPERADMINISTRATOR));
	}
	
	@Test
	public void shouldNotRemoveSuperadminRoleIfNotInNewListAndUserIsNotSuperadmin() {
		EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.replay(currentUser);
		Role role= new RoleBuilder().id(1).authorityEnum(Authority.SUPERADMINISTRATOR).toRole();
		RegisteredUser selectedUser = new RegisteredUserBuilder().role(role).id(1).toUser();		
		NewRolesDTO newRolesDTO = new NewRolesDTO();

		manageUsersControllerWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, new Program(), newRolesDTO);
		assertTrue(selectedUser.isInRole(Authority.SUPERADMINISTRATOR));
	}
	
	@Test
	public void shouldAddProgramToAdminlistIfNew(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();		
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.ADMINISTRATOR).toRole();
		NewRolesDTO newRolesDTO = new NewRolesDTO();
		newRolesDTO.getNewRoles().add(role);		
		manageUsersControllerWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, newRolesDTO);
		assertTrue(selectedUser.getProgramsOfWhichAdministrator().contains(selectedProgram));
	}
	
	@Test
	public void shouldAddProgramToApproverlistIfNew(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();		
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.APPROVER).toRole();
		NewRolesDTO newRolesDTO = new NewRolesDTO();
		newRolesDTO.getNewRoles().add(role);		
		manageUsersControllerWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, newRolesDTO);
		assertTrue(selectedUser.getProgramsOfWhichApprover().contains(selectedProgram));
	}
	
	
	@Test
	public void shouldAddProgramToRevieerListIfNew(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();		
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.REVIEWER).toRole();
		NewRolesDTO newRolesDTO = new NewRolesDTO();
		newRolesDTO.getNewRoles().add(role);		
		manageUsersControllerWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, newRolesDTO);
		assertTrue(selectedUser.getProgramsOfWhichReviewer().contains(selectedProgram));
	}
	
	
	@Test
	public void shouldRemoveFromProgramsOfWhichAdministratorIfNoLongerInList(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();		
		RegisteredUser selectedUser = new RegisteredUserBuilder().programsOfWhichAdministrator(selectedProgram).id(1).toUser();
		NewRolesDTO newRolesDTO = new NewRolesDTO();
		manageUsersControllerWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, newRolesDTO);
		assertFalse(selectedUser.getProgramsOfWhichAdministrator().contains(selectedProgram));
	}
	
	@Test
	public void shouldRemoveFromProgramsOfWhichApproverIfNoLongerInList(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();		
		RegisteredUser selectedUser = new RegisteredUserBuilder().programsOfWhichApprover(selectedProgram).id(1).toUser();
		NewRolesDTO newRolesDTO = new NewRolesDTO();
		manageUsersControllerWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, newRolesDTO);
		assertFalse(selectedUser.getProgramsOfWhichApprover().contains(selectedProgram));
	}
	
	
	@Test
	public void shouldRemoveFromProgramsOfWhichReviewerIfNoLongerInList(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();		
		RegisteredUser selectedUser = new RegisteredUserBuilder().programsOfWhichReviewer(selectedProgram).id(1).toUser();
		NewRolesDTO newRolesDTO = new NewRolesDTO();
		manageUsersControllerWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, newRolesDTO);
		assertFalse(selectedUser.getProgramsOfWhichReviewer().contains(selectedProgram));
	}
	
	@Test
	public void shouldRedirectToPageForProgram(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();		
		RegisteredUser selectedUser = new RegisteredUserBuilder().programsOfWhichReviewer(selectedProgram).id(1).toUser();
		NewRolesDTO newRolesDTO = new NewRolesDTO();
		assertEquals("redirect:/manageUsers/showPage?programId=1", manageUsersControllerWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, newRolesDTO));
	
	}
	@Before
	public void setup() {

		currentUser = EasyMock.createMock(RegisteredUser.class);

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		programsServiceMock = EasyMock.createMock(ProgramsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		rolePropertyEditorMock = EasyMock.createMock(RolePropertyEditor.class);
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		manageUsersController = new ManageUsersController(programsServiceMock, userServiceMock, rolePropertyEditorMock,
				mimeMessagePreparatorFactoryMock, javaMailSenderMock);
		
		manageUsersControllerWithCurrentUserOverride = new ManageUsersController(programsServiceMock, userServiceMock, rolePropertyEditorMock,
				mimeMessagePreparatorFactoryMock,javaMailSenderMock){

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
