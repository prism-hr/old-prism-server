package com.zuehlke.pgadmissions.controllers.usermanagement;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.UserDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.UserDTOValidator;

public class UserRoleControllerTest {

    private ProgramsService programServiceMock;
    private UserRoleController controller;
    private UserService userServiceMock;
    private RegisteredUser currentUserMock;
    private ProgramPropertyEditor programPropertyEditorMock;
    private UserDTOValidator newUserDTOValidatorMock;
    private EncryptionHelper encryptionHelperMock;

    @Test
    public void shouldGetSelectedProgramfIdProvided() {
        Program program = new ProgramBuilder().id(5).code("ABC").build();

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
    public void shouldReturnNewUserDTO() {
        String programCode = "DEF";
        Program program = new ProgramBuilder().id(1).build();
        EasyMock.expect(programServiceMock.getProgramByCode(programCode)).andReturn(program);
        EasyMock.replay(programServiceMock);
        UserDTO userDTO = controller.getNewUserDTO(null, programCode);
        assertNotNull(userDTO);
        assertNull(userDTO.getEmail());
        assertNull(userDTO.getFirstName());
        assertNull(userDTO.getLastName());
        assertEquals(0, userDTO.getSelectedAuthorities().length);
        assertEquals(program, userDTO.getSelectedProgram());
    }

    @Test
    public void shouldPopulateUserDTOWithUserDetails() {
        String programCode = "DEF";
        Program program = new ProgramBuilder().id(1).build();
        EasyMock.reset(userServiceMock);
        String encryptedUserId = "abc";
        RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userMock.getEmail()).andReturn("bsmith@test.com");
        EasyMock.expect(userMock.getFirstName()).andReturn("bob");
        EasyMock.expect(userMock.getLastName()).andReturn("Smith");
        EasyMock.expect(userMock.getAuthoritiesForProgram(program)).andReturn(Arrays.asList(Authority.ADMINISTRATOR, Authority.INTERVIEWER));
        EasyMock.expect(encryptionHelperMock.decryptToInteger(encryptedUserId)).andReturn(1);
        EasyMock.expect(userServiceMock.getUser(1)).andReturn(userMock);

        EasyMock.expect(programServiceMock.getProgramByCode(programCode)).andReturn(program);
        EasyMock.replay(encryptionHelperMock, userServiceMock, userMock, programServiceMock);

        UserDTO userDTO = controller.getNewUserDTO(encryptedUserId, programCode);

        assertNotNull(userDTO);
        assertEquals("bsmith@test.com", userDTO.getEmail());
        assertEquals("bob", userDTO.getFirstName());
        assertEquals("Smith", userDTO.getLastName());
        assertArrayEquals(new Authority[] { Authority.ADMINISTRATOR, Authority.INTERVIEWER }, userDTO.getSelectedAuthorities());
        assertEquals(program, userDTO.getSelectedProgram());
        assertFalse(userDTO.isNewUser());
    }

    @Test
    public void shouldReturnAllProgramsForSuperAdminUser() {
        EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
        Program program1 = new ProgramBuilder().id(1).build();
        Program program2 = new ProgramBuilder().id(2).build();
        EasyMock.expect(programServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
        EasyMock.replay(programServiceMock, currentUserMock);
        List<Program> programs = controller.getPrograms();
        assertEquals(2, programs.size());
        assertTrue(programs.containsAll(Arrays.asList(program1, program2)));
    }

    @Test
    public void shouldReturnProgramsOfWhichAdministratorForAdmins() {
        EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
        Program program1 = new ProgramBuilder().id(1).build();
        Program program2 = new ProgramBuilder().id(2).build();
        EasyMock.expect(currentUserMock.getProgramsOfWhichAdministrator()).andReturn(Arrays.asList(program1, program2));
        EasyMock.replay(programServiceMock, currentUserMock);
        List<Program> programs = controller.getPrograms();
        assertEquals(2, programs.size());
        assertTrue(programs.containsAll(Arrays.asList(program1, program2)));
    }

    @Test
    public void shouldReturnCorrectPossibleRoles() {
        EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
        EasyMock.replay(currentUserMock);

        List<Authority> authorities = controller.getAuthorities();
        assertEquals(5, authorities.size());
        assertThat(authorities, contains( //
                Authority.ADMINISTRATOR, //
                Authority.APPROVER, //
                Authority.INTERVIEWER, //
                Authority.REVIEWER, //
                Authority.SUPERVISOR));
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

        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setFirstName("Jane");
        newUserDTO.setLastName("Doe");
        newUserDTO.setEmail("jane.doe@test.com");
        Program program = new ProgramBuilder().id(5).code("ABC").build();
        newUserDTO.setSelectedProgram(program);
        newUserDTO.setSelectedAuthorities(Authority.REVIEWER, Authority.ADMINISTRATOR);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("jane.doe@test.com")).andReturn(null);
        EasyMock.expect(userServiceMock.createNewUserForProgramme("Jane", "Doe", "jane.doe@test.com", program, Authority.REVIEWER, Authority.ADMINISTRATOR))
                .andReturn(new RegisteredUserBuilder().id(4).build());

        EasyMock.replay(currentUserMock, userServiceMock);

        assertEquals("redirect:/manageUsers/edit?programCode=ABC", controller.handleEditUserRoles(newUserDTO, errorsMock));

        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldUpdateRolesForUserIfUserAlreadyExistsButNotInRole() {
        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();

        EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
        EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
        EasyMock.replay(errorsMock);

        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setFirstName("Jane");
        newUserDTO.setLastName("Doe");
        newUserDTO.setEmail("jane.doe@test.com");
        Program program = new ProgramBuilder().id(5).code("ABC").build();
        newUserDTO.setSelectedProgram(program);
        newUserDTO.setSelectedAuthorities(Authority.REVIEWER, Authority.ADMINISTRATOR);

        RegisteredUser existingUser = new RegisteredUserBuilder().id(7).build();
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("jane.doe@test.com")).andReturn(existingUser);
        userServiceMock.updateUserWithNewRoles(existingUser, program, Authority.REVIEWER, Authority.ADMINISTRATOR);

        EasyMock.replay(currentUserMock, userServiceMock);

        assertEquals("redirect:/manageUsers/edit?programCode=ABC", controller.handleEditUserRoles(newUserDTO, errorsMock));

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

        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setFirstName("Jane");
        newUserDTO.setLastName("Doe");
        newUserDTO.setEmail("jane.doe@test.com");

        newUserDTO.setSelectedAuthorities(Authority.REVIEWER, Authority.ADMINISTRATOR);

        RegisteredUser existingUser = new RegisteredUserBuilder().id(7).build();
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("jane.doe@test.com")).andReturn(existingUser);
        userServiceMock.updateUserWithNewRoles(existingUser, null, Authority.REVIEWER, Authority.ADMINISTRATOR);

        EasyMock.replay(currentUserMock, userServiceMock);

        assertEquals("redirect:/manageUsers/edit", controller.handleEditUserRoles(newUserDTO, errorsMock));

        EasyMock.verify(userServiceMock);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfUserNeitherSuperAdminOrAdminOnSubmission() {
        EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
        EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false).anyTimes();
        EasyMock.replay(currentUserMock);
        controller.handleEditUserRoles(null, null);
    }

    @Test
    public void shouldReturnToViewIfErrors() {
        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();

        EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
        EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();
        EasyMock.replay(currentUserMock, userServiceMock);

        UserDTO newUserDTO = new UserDTO();
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
        EasyMock.replay(errorsMock);

        assertEquals("private/staff/superAdmin/create_new_user_in_role_page", controller.handleEditUserRoles(newUserDTO, errorsMock));

        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldUpdateExistingUserWithEmptyRolesOnRemove() {
        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();

        EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
        EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();

        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setFirstName("Jane");
        newUserDTO.setLastName("Doe");
        newUserDTO.setEmail("jane.doe@test.com");
        Program program = new ProgramBuilder().id(5).code("ABC").build();
        newUserDTO.setSelectedProgram(program);

        RegisteredUser existingUser = new RegisteredUserBuilder().id(7).build();
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("jane.doe@test.com")).andReturn(existingUser);
        userServiceMock.deleteUserFromProgramme(existingUser, program);

        EasyMock.replay(currentUserMock, userServiceMock);

        assertEquals("redirect:/manageUsers/edit?programCode=ABC", controller.handleRemoveUserFromProgram(newUserDTO));

        EasyMock.verify(userServiceMock);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfUserNeitherSuperAdminOrAdminOnRemove() {
        EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
        EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false).anyTimes();
        EasyMock.replay(currentUserMock);
        controller.handleRemoveUserFromProgram(null);
    }

    @Test
    public void shouldBindPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(newUserDTOValidatorMock);
        binderMock.registerCustomEditor(Program.class, programPropertyEditorMock);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));

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
        newUserDTOValidatorMock = EasyMock.createMock(UserDTOValidator.class);
        encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
        EasyMock.replay(userServiceMock);

        controller = new UserRoleController(programServiceMock, userServiceMock, programPropertyEditorMock, newUserDTOValidatorMock, encryptionHelperMock);
    }
}
