package com.zuehlke.pgadmissions.controllers.usermanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.UserDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.SuperadminUserDTOValidator;

public class SuperadminControllerTest {

	private UserService userServiceMock;
	private SuperadminController controller;
	private SuperadminUserDTOValidator userDTOValidatorMcok;

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResouceNotFoundExceptionIfNotSuperadmin() {
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.replay(userServiceMock, currentUserMock);
		controller.getSuperadminPage();

	}

	@Test
	public void shouldReturnSuperadminManagementPage() {
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(userServiceMock, currentUserMock);
		assertEquals("private/staff/superAdmin/superadmin_management", controller.getSuperadminPage());

	}

	@Test
	public void shouldReturnAllSuperadministratorsOrderbylastnameFirstname() {
		RegisteredUser userOne = new RegisteredUserBuilder().id(1).lastName("ZZZZ").firstName("BBBB").build();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(4).lastName("ZZZZ").firstName("AAAA").build();
		RegisteredUser userThree = new RegisteredUserBuilder().id(5).lastName("AA").firstName("GGG").build();
		EasyMock.expect(userServiceMock.getUsersInRole(Authority.SUPERADMINISTRATOR)).andReturn(Arrays.asList(userOne, userTwo, userThree));
		EasyMock.replay(userServiceMock);
		List<RegisteredUser> superadmins = controller.getSuperadmins();
		assertEquals(3, superadmins.size());
		assertEquals(userThree, superadmins.get(0));
		assertEquals(userTwo, superadmins.get(1));
		assertEquals(userOne, superadmins.get(2));
	}

	public void shouldCreateNewUserInRoles() {
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();

		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();

		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		EasyMock.replay(errorsMock);

		UserDTO userDTO = new UserDTO();
		userDTO.setFirstName("Jane");
		userDTO.setLastName("Doe");
		userDTO.setEmail("jane.doe@test.com");

		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("jane.doe@test.com")).andReturn(null);
		EasyMock.expect(userServiceMock.createNewUserForProgramme("Jane", "Doe", "jane.doe@test.com", null, Authority.SUPERADMINISTRATOR)).andReturn(
				new RegisteredUserBuilder().id(4).build());

		EasyMock.replay(currentUserMock, userServiceMock);

		assertEquals("redirect:/manageUsers/superadmins", controller.handleAddSuperAdmin(userDTO, errorsMock));


		EasyMock.verify(userServiceMock);
	}

	@Test
	public void shouldUpdateRolesForUserIfUserAlreadyExists() {
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();

		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		EasyMock.replay(errorsMock);

		UserDTO userDTO = new UserDTO();
		userDTO.setFirstName("Jane");
		userDTO.setLastName("Doe");
		userDTO.setEmail("jane.doe@test.com");

		RegisteredUser existingUser = new RegisteredUserBuilder().id(7).build();
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("jane.doe@test.com")).andReturn(existingUser);
		userServiceMock.updateUserWithNewRoles(existingUser, null, Authority.SUPERADMINISTRATOR);

		EasyMock.replay(currentUserMock, userServiceMock);

		assertEquals("redirect:/manageUsers/superadmins", controller.handleAddSuperAdmin(userDTO, errorsMock));

		EasyMock.verify(userServiceMock);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUserNotSuperAdminOnSubmission() {
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.replay(userServiceMock, currentUserMock);
		controller.handleAddSuperAdmin(null, null);
	}

	@Test
	public void shouldReturnToViewIfErrors() {
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(userServiceMock, currentUserMock);

		UserDTO userDTO = new UserDTO();
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
		EasyMock.replay(errorsMock);

		assertEquals("private/staff/superAdmin/superadmin_management", controller.handleAddSuperAdmin(userDTO, errorsMock));

		EasyMock.verify(userServiceMock);
	}

	@Test
	public void shouldBindPropertyEditors() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(userDTOValidatorMcok);
		binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
		
		EasyMock.replay(binderMock);
		controller.registerValidator(binderMock);
		EasyMock.verify(binderMock);
	}
	
	@Test
	public void shouldReturnNewUserDTO() {

		UserDTO userDTO = controller.getUserDTO();
		assertNotNull(userDTO);
		assertNull(userDTO.getEmail());
		assertNull(userDTO.getFirstName());
		assertNull(userDTO.getLastName());
		assertEquals(0, userDTO.getSelectedAuthorities().length);
		assertNull( userDTO.getSelectedProgram());

	}
	@Test
	public void shouldReturnCurrentUser() {
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);
		assertEquals(currentUserMock, controller.getUser());
	}
	@Before
	public void setup() {
		userServiceMock = EasyMock.createMock(UserService.class);
		userDTOValidatorMcok = EasyMock.createMock(SuperadminUserDTOValidator.class);
		controller = new SuperadminController(userServiceMock, userDTOValidatorMcok);
	}
}
