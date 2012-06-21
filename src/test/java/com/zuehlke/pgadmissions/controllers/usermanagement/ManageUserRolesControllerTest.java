package com.zuehlke.pgadmissions.controllers.usermanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.UpdateUserRolesDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.PlainTextUserPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.UpdateUserRolesDTOValidator;

public class ManageUserRolesControllerTest {

	private UserService userServiceMock;
	private ProgramsService programsServiceMock;
	private ManageUserRolesController controller;
	private RegisteredUser currentUserMock;
	private PlainTextUserPropertyEditor userPropertyEditorMock;
	private ProgramPropertyEditor programPropertyEditorMock;
	private UpdateUserRolesDTOValidator updateUserRolesDTOValidatorMock;
	private EncryptionHelper encryptionHelperMock;
	
	@Test
	public void shouldReturnAllProgramsForSuperAdmin() {
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUserMock);

		Program programOne = new ProgramBuilder().id(1).toProgram();
		Program programTwo = new ProgramBuilder().id(2).toProgram();
		EasyMock.expect(programsServiceMock.getAllPrograms()).andReturn(Arrays.asList(programOne, programTwo));
		EasyMock.replay(programsServiceMock);

		List<Program> allPrograms = controller.getPrograms();

		assertEquals(2, allPrograms.size());
		assertTrue(allPrograms.containsAll(Arrays.asList(programOne, programTwo)));
	}

	@Test
	public void shouldReturnProgramsOfWhichUserAdministratorForAdmin() {
		Program programOne = new ProgramBuilder().id(1).toProgram();
		Program programTwo = new ProgramBuilder().id(2).toProgram();

		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.expect(currentUserMock.getProgramsOfWhichAdministrator()).andReturn(Arrays.asList(programOne, programTwo));
		EasyMock.replay(currentUserMock);

		List<Program> allPrograms = controller.getPrograms();

		assertEquals(2, allPrograms.size());
		assertTrue(allPrograms.containsAll(Arrays.asList(programOne, programTwo)));
	}
	
	@Test
	public void shouldReturnCorrectPossibleRolesForSuperadmin() {
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUserMock);
		List<Authority> authorities = controller.getAuthorities();
		assertEquals(6, authorities.size());
		assertTrue(authorities.containsAll(Arrays.asList(Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER, Authority.SUPERADMINISTRATOR,
				Authority.INTERVIEWER, Authority.SUPERVISOR)));
	}

	@Test
	public void shouldReturnCorrectPossibleRolesForAdmin() {
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.replay(currentUserMock);

		List<Authority> authorities = controller.getAuthorities();
		assertEquals(5, authorities.size());
		assertTrue(authorities.containsAll(Arrays.asList(Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER, Authority.INTERVIEWER, Authority.SUPERVISOR)));
	}
	
	@Test
	public void shouldReturnNewUpdateUserRolesDTO(){
		assertNotNull(controller.getUpdateUserRolesDTO());
		assertTrue(controller.getUpdateUserRolesDTO() instanceof UpdateUserRolesDTO);
	}
	
	@Test
	public void shouldGetSelectedUserIfIdProvided(){
		EasyMock.reset(userServiceMock);
		RegisteredUser user = new RegisteredUserBuilder().id(5).toUser();
		
		EasyMock.expect(encryptionHelperMock.decryptToInteger("enc")).andReturn(5);
		EasyMock.expect(userServiceMock.getUser(5)).andReturn(user);
		EasyMock.replay(userServiceMock, encryptionHelperMock);

		assertEquals(user, controller.getSelectedUser("enc"));
		EasyMock.verify(userServiceMock, encryptionHelperMock);
	}

	@Test
	public void shoudlReturnNullIfUserIdNotProvided() {
		assertNull(controller.getSelectedUser(null));
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoucnExceptionIfNoSuchUser() {
		EasyMock.reset(userServiceMock);
		EasyMock.expect(encryptionHelperMock.decryptToInteger("enc")).andReturn(5);
		EasyMock.expect(userServiceMock.getUser(5)).andReturn(null);
		EasyMock.replay(userServiceMock, encryptionHelperMock);
		controller.getSelectedUser("enc");
	}

	@Test
	public void shouldGetSelectedProgramfIdProvided() {
		Program program = new ProgramBuilder().id(5).toProgram();
		EasyMock.expect(programsServiceMock.getProgramByCode("CODE ABC")).andReturn(program);
		EasyMock.replay(programsServiceMock);

		assertEquals(program, controller.getSelectedProgram("CODE ABC"));
		EasyMock.verify(programsServiceMock);
	}

	@Test
	public void shoudlReturnNullIfProgramIdNotProvided() {
		assertNull(controller.getSelectedProgram(null));
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoucnExceptionIfNoSuchProgram() {
		EasyMock.expect(programsServiceMock.getProgramByCode("ABC")).andReturn(null);

		EasyMock.replay(programsServiceMock);
		controller.getSelectedProgram("ABC");
	}
	

	@Test
	public void shouldReturnAllInternalusers() {
		EasyMock.reset(userServiceMock);
		RegisteredUser userOne = new RegisteredUserBuilder().id(1).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(2).toUser();
		EasyMock.expect(userServiceMock.getAllInternalUsers()).andReturn(Arrays.asList(userOne, userTwo));
		EasyMock.replay(userServiceMock);
		List<RegisteredUser> internalUsers = controller.getavailableUsers();
		assertEquals(2, internalUsers.size());
		assertTrue(internalUsers.containsAll(Arrays.asList(userOne, userTwo)));
	}
	
	@Test
	public void shouldReturnEmptyUserInRoleListIfNoProgram() {
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.expect(currentUserMock.getId()).andReturn(1);
		EasyMock.replay(currentUserMock);
		
		List<RegisteredUser> users = controller.getUsersInRoles(null);
		assertTrue(users.isEmpty());
	}

	
	@Test
	public void shouldReturnAllUsersForProgramIfProgamProvided() {
		EasyMock.reset(userServiceMock);
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUserMock);
		Program program = new ProgramBuilder().id(5).toProgram();
		RegisteredUser userOne = new RegisteredUserBuilder().id(3).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(4).toUser();
		EasyMock.expect(encryptionHelperMock.decryptToInteger("enc")).andReturn(5);
		EasyMock.expect(programsServiceMock.getProgramByCode("ABC")).andReturn(program);
		EasyMock.expect(userServiceMock.getAllUsersForProgram(program)).andReturn(Arrays.asList(userOne, userTwo));
		EasyMock.replay(programsServiceMock, userServiceMock);
		List<RegisteredUser> users = controller.getUsersInRoles("ABC");		
		assertEquals(2, users.size());
		assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));
		EasyMock.verify(programsServiceMock, userServiceMock);
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowExceptionForNonAdministrators() {
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUserMock.getId()).andReturn(1);
		EasyMock.replay(currentUserMock);
		controller.getUsersPage();
	}

	@Test
	public void shouldReturnCorrectView() {
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUserMock);
		assertEquals("private/staff/superAdmin/assign_roles_page", controller.getUsersPage());
	}
	
	
	@Test	
	public void shouldBindPropertyEditors() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(updateUserRolesDTOValidatorMock);
		binderMock.registerCustomEditor(Program.class, programPropertyEditorMock);
		binderMock.registerCustomEditor(RegisteredUser.class, userPropertyEditorMock);
		EasyMock.replay(binderMock);
		controller.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}
	
	@Test
	public void shouldUpdateRolesForUserIfNoErrors() {
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();

		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		EasyMock.replay(errorsMock);

		UpdateUserRolesDTO userDTO = new UpdateUserRolesDTO();
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(7).toUser();
		userDTO.setSelectedUser(selectedUser);
		Program program = new ProgramBuilder().id(5).toProgram();
		userDTO.setSelectedProgram(program);
		userDTO.setSelectedAuthorities(Authority.REVIEWER, Authority.ADMINISTRATOR);
		
		userServiceMock.updateUserWithNewRoles(selectedUser, program, Authority.REVIEWER, Authority.ADMINISTRATOR);
		EasyMock.replay(currentUserMock, userServiceMock);

		assertEquals("redirect:/manageUsers/showPage?programId=5", controller.updateUserRoles(userDTO, errorsMock));

		EasyMock.verify(userServiceMock);
	}
	@Test
	public void shouldReturnAllProgramsViewIfProgramNotSetOnUserDTO() {
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();

		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		EasyMock.replay(errorsMock);

		UpdateUserRolesDTO userDTO = new UpdateUserRolesDTO();
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(7).toUser();
		userDTO.setSelectedUser(selectedUser);	
		userDTO.setSelectedAuthorities(Authority.REVIEWER, Authority.ADMINISTRATOR);
		
		userServiceMock.updateUserWithNewRoles(selectedUser, null, Authority.REVIEWER, Authority.ADMINISTRATOR);
		EasyMock.replay(currentUserMock, userServiceMock);

		assertEquals("redirect:/manageUsers/showPage", controller.updateUserRoles(userDTO, errorsMock));

		EasyMock.verify(userServiceMock);
	}
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUserNeitherSuperAdminOrAdminOnSubmission() {
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.replay(currentUserMock);
		controller.updateUserRoles(null, null);
	}

	@Test
	public void shouldReturnToViewIfErrors() {
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();

		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUserMock, userServiceMock);

		UpdateUserRolesDTO userDTO = new UpdateUserRolesDTO();
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
		EasyMock.replay(errorsMock);

		assertEquals("private/staff/superAdmin/assign_roles_page", controller.updateUserRoles(userDTO, errorsMock));

		EasyMock.verify(userServiceMock);
	}

	@Before
	public void setUp(){
		userServiceMock = EasyMock.createMock(UserService.class);
		programsServiceMock = EasyMock.createMock(ProgramsService.class);
		userPropertyEditorMock = EasyMock.createMock(PlainTextUserPropertyEditor.class);
		programPropertyEditorMock = EasyMock.createMock(ProgramPropertyEditor.class);
		updateUserRolesDTOValidatorMock = EasyMock.createMock(UpdateUserRolesDTOValidator.class);
		currentUserMock = EasyMock.createMock(RegisteredUser.class);		
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);
		controller = new ManageUserRolesController(userServiceMock, programsServiceMock, userPropertyEditorMock,// 
				programPropertyEditorMock, updateUserRolesDTOValidatorMock, encryptionHelperMock);
	}
}
