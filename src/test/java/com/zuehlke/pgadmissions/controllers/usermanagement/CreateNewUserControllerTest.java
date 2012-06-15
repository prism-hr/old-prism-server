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
import com.zuehlke.pgadmissions.dto.NewUserDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserDTOValidator;

public class CreateNewUserControllerTest {

	private ProgramsService programServiceMock;
	private CreateNewUserController controller;
	private UserService userServiceMock;
	private RegisteredUser currentUserMock;
	private ProgramPropertyEditor programPropertyEditorMock;
	private NewUserDTOValidator newUserDTOValidatorMock;

	
	@Test
	public void shouldGetSelectedProgramfIdProvided() {
		Program program = new ProgramBuilder().id(5).code("ABC").toProgram();
		EasyMock.expect(programServiceMock.getProgramByCode("ABC")).andReturn(program);
		EasyMock.replay(programServiceMock);

		assertEquals(program, controller.getSelectedProgram("ABC"));
	}

	@Test
	public void shoudlReturnNullIfProgramIdNotProvided() {
		assertNull(controller.getSelectedProgram(null));
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoucnExceptionIfNoSuchProgram() {

		EasyMock.expect(programServiceMock.getProgramByCode("ABC")).andReturn(null);
		EasyMock.replay(programServiceMock);
		controller.getSelectedProgram("ABC");
	}
	@Test
	public void shouldReturnCurrentUser() {
		assertEquals(currentUserMock, controller.getUser());
	}
	
	@Test
	public void shouldReturnNewNewUserDTO(){
		assertNotNull(controller.getNewUserDTO());
	}
	
	@Test
	public void shouldReturnAllProgramsForSuperAdminUser() {
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
		Program program1 = new ProgramBuilder().id(1).toProgram();
		Program program2 = new ProgramBuilder().id(2).toProgram();
		EasyMock.expect(programServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
		EasyMock.replay(programServiceMock, currentUserMock);
		List<Program> programs = controller.getPrograms();
		assertEquals(2, programs.size());
		assertTrue(programs.containsAll(Arrays.asList(program1, program2)));
	}

	@Test
	public void shouldReturnProgramsOfWhichAdministratorForAdmins() {
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
		Program program1 = new ProgramBuilder().id(1).toProgram();
		Program program2 = new ProgramBuilder().id(2).toProgram();
		EasyMock.expect(currentUserMock.getProgramsOfWhichAdministrator()).andReturn(Arrays.asList(program1, program2));
		EasyMock.replay(programServiceMock, currentUserMock);
		List<Program> programs = controller.getPrograms();
		assertEquals(2, programs.size());
		assertTrue(programs.containsAll(Arrays.asList(program1, program2)));
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

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUserNeitherSuperAdminOrAdmin() {
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.replay(currentUserMock);
		controller.getAddUsersView();
	}

	@Test
	public void shoulReturnNewUsersViewForSuperadmin() {
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUserMock);
		assertEquals("private/staff/superAdmin/create_new_user_in_role_page", controller.getAddUsersView());
	}

	@Test
	public void shoulReturnNewUsersViewForAdmin() {
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUserMock);
		assertEquals("private/staff/superAdmin/create_new_user_in_role_page", controller.getAddUsersView());
	}

	@Test
	public void shouldCreateNewUserInRoles() {
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();

		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		EasyMock.replay(errorsMock);

		NewUserDTO newUserDTO = new NewUserDTO();
		newUserDTO.setFirstName("Jane");
		newUserDTO.setLastName("Doe");
		newUserDTO.setEmail("jane.doe@test.com");
		Program program = new ProgramBuilder().id(5).toProgram();
		newUserDTO.setSelectedProgram(program);
		newUserDTO.setSelectedAuthorities(Authority.REVIEWER, Authority.ADMINISTRATOR);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("jane.doe@test.com")).andReturn(null);
		EasyMock.expect(userServiceMock.createNewUserForProgramme("Jane", "Doe", "jane.doe@test.com", program, Authority.REVIEWER, Authority.ADMINISTRATOR))
				.andReturn(new RegisteredUserBuilder().id(4).toUser());
		EasyMock.replay(currentUserMock, userServiceMock);

		assertEquals("redirect:/manageUsers/showPage?programId=5", controller.handleNewUserToProgramSubmission(newUserDTO, errorsMock));

		EasyMock.verify(userServiceMock);
	}

	@Test
	public void shouldUpdateRolesForUserIfUserAlreadyExists() {
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();

		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		EasyMock.replay(errorsMock);

		NewUserDTO newUserDTO = new NewUserDTO();
		newUserDTO.setFirstName("Jane");
		newUserDTO.setLastName("Doe");
		newUserDTO.setEmail("jane.doe@test.com");
		Program program = new ProgramBuilder().id(5).toProgram();
		newUserDTO.setSelectedProgram(program);
		newUserDTO.setSelectedAuthorities(Authority.REVIEWER, Authority.ADMINISTRATOR);

		RegisteredUser existingUser = new RegisteredUserBuilder().id(7).toUser();
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("jane.doe@test.com")).andReturn(existingUser);
		userServiceMock.updateUserWithNewRoles(existingUser, program, Authority.REVIEWER, Authority.ADMINISTRATOR);
		EasyMock.replay(currentUserMock, userServiceMock);

		assertEquals("redirect:/manageUsers/showPage?programId=5", controller.handleNewUserToProgramSubmission(newUserDTO, errorsMock));

		EasyMock.verify(userServiceMock);
	}
	@Test
	public void shouldReturnToAllProgramsViewIfNoProgramOnUserDTO() {
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();

		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		EasyMock.replay(errorsMock);

		NewUserDTO newUserDTO = new NewUserDTO();
		newUserDTO.setFirstName("Jane");
		newUserDTO.setLastName("Doe");
		newUserDTO.setEmail("jane.doe@test.com");

		newUserDTO.setSelectedAuthorities(Authority.REVIEWER, Authority.ADMINISTRATOR);

		RegisteredUser existingUser = new RegisteredUserBuilder().id(7).toUser();
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("jane.doe@test.com")).andReturn(existingUser);
		userServiceMock.updateUserWithNewRoles(existingUser, null, Authority.REVIEWER, Authority.ADMINISTRATOR);
		EasyMock.replay(currentUserMock, userServiceMock);

		assertEquals("redirect:/manageUsers/showPage", controller.handleNewUserToProgramSubmission(newUserDTO, errorsMock));

		EasyMock.verify(userServiceMock);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUserNeitherSuperAdminOrAdminOnSubmission() {
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.replay(currentUserMock);
		controller.handleNewUserToProgramSubmission(null, null);
	}

	@Test
	public void shouldReturnToViewIfErrors() {
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();

		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUserMock, userServiceMock);

		NewUserDTO newUserDTO = new NewUserDTO();
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
		EasyMock.replay(errorsMock);

		assertEquals("private/staff/superAdmin/create_new_user_in_role_page", controller.handleNewUserToProgramSubmission(newUserDTO, errorsMock));

		EasyMock.verify(userServiceMock);
	}

	@Test
	public void shouldBindPropertyEditors() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(newUserDTOValidatorMock);
		binderMock.registerCustomEditor(Program.class, programPropertyEditorMock);

		EasyMock.replay(binderMock);
		controller.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}

	@Before
	public void setUp() {

		userServiceMock = EasyMock.createMock(UserService.class);
		programServiceMock = EasyMock.createMock(ProgramsService.class);
		currentUserMock = EasyMock.createMock(RegisteredUser.class);
		programPropertyEditorMock = EasyMock.createMock(ProgramPropertyEditor.class);
		newUserDTOValidatorMock = EasyMock.createMock(NewUserDTOValidator.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);
		controller = new CreateNewUserController(programServiceMock, userServiceMock, programPropertyEditorMock, newUserDTOValidatorMock);

	}
}
