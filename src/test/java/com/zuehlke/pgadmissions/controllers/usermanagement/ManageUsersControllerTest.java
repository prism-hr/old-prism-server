package com.zuehlke.pgadmissions.controllers.usermanagement;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.unitils.easymock.EasyMockUnitils.replay;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.UserDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.services.ManageUsersService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.SuperadminUserDTOValidator;
import com.zuehlke.pgadmissions.validators.UserDTOValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ManageUsersControllerTest {

    @Mock
    @InjectIntoByType
    private ProgramService programService;

    @Mock
    @InjectIntoByType
    private UserService userService;

    @Mock
    @InjectIntoByType
    private ProgramPropertyEditor programPropertyEditor;

    @Mock
    @InjectIntoByType
    private UserDTOValidator newUserDTOValidator;

    @Mock
    @InjectIntoByType
    private EncryptionHelper encryptionHelper;

    @Mock
    @InjectIntoByType
    private SuperadminUserDTOValidator userDTOValidator;

    @Mock
    @InjectIntoByType
    private UserPropertyEditor registryPropertyEditor;

    @Mock
    @InjectIntoByType
    private ConfigurationService configurationService;

    @Mock
    @InjectIntoByType
    private RoleService roleService;

    @Mock
    @InjectIntoByType
    private ManageUsersService manageUsersService;

    @TestedObject
    private ManageUsersController controller;

    private User currentUser = new User();

    @Test
    public void shouldReturnCurrentUser() {
        replay();
        assertEquals(currentUser, controller.getUser());

    }

    @Test
    public void shouldReturnAllProgramsForSuperAdminUser() {
        Program program1 = new Program().withId(1);
        Program program2 = new Program().withId(2);
        EasyMock.expect(programService.getAllEnabledPrograms()).andReturn(Arrays.asList(program1, program2));
        replay();
        List<Program> programs = controller.getPrograms();

        assertEquals(2, programs.size());
        assertTrue(programs.containsAll(Arrays.asList(program1, program2)));
    }

    @Test
    public void shouldReturnProgramsOfWhichAdministratorForAdmins() {
        Program program1 = new Program().withId(1);
        Program program2 = new Program().withId(2);
        replay();
        List<Program> programs = controller.getPrograms();

        assertEquals(2, programs.size());
        assertTrue(programs.containsAll(Arrays.asList(program1, program2)));
    }

    @Test
    public void shouldReturnCorrectPossibleRoles() {
        replay();

        List<Authority> authorities = controller.getAuthorities();
        assertThat(authorities, contains( //
                Authority.PROGRAM_ADMINISTRATOR, //
                Authority.PROGRAM_APPROVER, //
                Authority.PROGRAM_VIEWER));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfUserNeitherSuperAdminOrAdmin() {
        replay();
        controller.getAddUsersView();

    }

    @Test
    public void shoulReturnNewUsersViewForSuperadmin() {
        replay();
        assertEquals("private/staff/superAdmin/create_new_user_in_role_page", controller.getAddUsersView());

    }

    @Test
    public void shoulReturnNewUsersViewForAdmin() {
        replay();
        assertEquals("private/staff/superAdmin/create_new_user_in_role_page", controller.getAddUsersView());

    }

    @Test
    public void shouldCreateNewUserInRoles() {
        EasyMock.reset(userService);
        EasyMock.expect(userService.getCurrentUser()).andReturn(currentUser).anyTimes();

        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
        replay();

        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setFirstName("Jane");
        newUserDTO.setLastName("Doe");
        newUserDTO.setEmail("jane.doe@test.com");
        Program program = new Program().withId(5).withCode("ABC");
        newUserDTO.setSelectedProgram(program);
        newUserDTO.setSelectedAuthorities(Authority.APPLICATION_REVIEWER, Authority.PROGRAM_ADMINISTRATOR);
        EasyMock.expect(userService.getUserByEmailIncludingDisabledAccounts("jane.doe@test.com")).andReturn(null);
        EasyMock.expect(manageUsersService.setUserRoles("Jane", "Doe", "jane.doe@test.com", true, program, Authority.APPLICATION_REVIEWER, Authority.PROGRAM_ADMINISTRATOR))
                .andReturn(new User().withId(4));

        replay();

        assertEquals("redirect:/manageUsers/edit?programCode=ABC", controller.handleEditUserRoles(newUserDTO, errorsMock));

    }

    @Test
    public void shouldUpdateRolesForUserIfUserAlreadyExistsButNotInRole() {
        EasyMock.reset(userService);
        EasyMock.expect(userService.getCurrentUser()).andReturn(currentUser).anyTimes();

        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
        replay();

        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setFirstName("Jane");
        newUserDTO.setLastName("Doe");
        newUserDTO.setEmail("jane.doe@test.com");
        Program program = new Program().withId(5).withCode("ABC");
        newUserDTO.setSelectedProgram(program);
        newUserDTO.setSelectedAuthorities(Authority.APPLICATION_REVIEWER, Authority.PROGRAM_ADMINISTRATOR);

        replay();

        assertEquals("redirect:/manageUsers/edit?programCode=ABC", controller.handleEditUserRoles(newUserDTO, errorsMock));

    }

    @Test
    public void shouldReturnToAllProgramsViewIfNoProgramOnUserDTO() {
        EasyMock.reset(userService);
        EasyMock.expect(userService.getCurrentUser()).andReturn(currentUser).anyTimes();

        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
        replay();

        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setFirstName("Jane");
        newUserDTO.setLastName("Doe");
        newUserDTO.setEmail("jane.doe@test.com");

        newUserDTO.setSelectedAuthorities(Authority.APPLICATION_REVIEWER, Authority.PROGRAM_ADMINISTRATOR);

        User existingUser = new User().withId(7);

        replay();

        assertEquals("redirect:/manageUsers/edit", controller.handleEditUserRoles(newUserDTO, errorsMock));

    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfUserNeitherSuperAdminOrAdminOnSubmission() {
        replay();
        controller.handleEditUserRoles(null, null);

    }

    @Test
    public void shouldReturnToViewIfErrors() {
        EasyMock.reset(userService);
        EasyMock.expect(userService.getCurrentUser()).andReturn(currentUser).anyTimes();

        replay();

        UserDTO newUserDTO = new UserDTO();
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
        replay();

        assertEquals("private/staff/superAdmin/create_new_user_in_role_page", controller.handleEditUserRoles(newUserDTO, errorsMock));

    }

    @Test
    public void shouldBindPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(newUserDTOValidator);
        binderMock.registerCustomEditor(Program.class, programPropertyEditor);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));

        replay();
        controller.registerPropertyEditors(binderMock);

    }

    @Test
    public void shouldGetAdmitters() {
        List<User> admitters = Lists.newArrayList();
        System prismSystem = new System();

        EasyMock.expect(roleService.getUsersInRole(prismSystem, Authority.INSTITUTION_ADMITTER)).andReturn(admitters);
        replay();
        List<User> returned = controller.getAdmitters();

        assertSame(admitters, returned);
    }

    @Test
    public void shouldReturnAllSuperadministratorsOrderbylastnameFirstname() {
        User userOne = new User().withId(1).withLastName("ZZZZ").withFirstName("BBBB");
        User userTwo = new User().withId(4).withLastName("ZZZZ").withFirstName("AAAA");
        User userThree = new User().withId(5).withLastName("AA").withFirstName("GGG");
        System prismSystem = new System();
        
        EasyMock.expect(roleService.getUsersInRole(prismSystem, Authority.SYSTEM_ADMINISTRATOR)).andReturn(Arrays.asList(userOne, userTwo, userThree));
        replay();
        List<User> superadmins = controller.getSuperadministrators();

        assertEquals(3, superadmins.size());
        assertEquals(userThree, superadmins.get(0));
        assertEquals(userTwo, superadmins.get(1));
        assertEquals(userOne, superadmins.get(2));
    }

    @Test
    public void shouldCreateNewUserInRolesSuperAdmin() {

        System prismSystem = new System();

        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(false);

        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Jane");
        userDTO.setLastName("Doe");
        userDTO.setEmail("jane.doe@test.com");

        EasyMock.expect(manageUsersService.setUserRoles("Jane", "Doe", "jane.doe@test.com", true, prismSystem, Authority.SYSTEM_ADMINISTRATOR)).andReturn(
                new User().withId(4));

        replay();

        assertEquals("redirect:/manageUsers/edit", controller.handleAddSuperadmin(userDTO, errorsMock));

    }

    @Test
    public void shouldUpdateRolesForUserIfUserAlreadyExists() {
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(false);

        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Jane");
        userDTO.setLastName("Doe");
        userDTO.setEmail("jane.doe@test.com");

        User existingUser = new User().withId(7);

        replay();

        assertEquals("redirect:/manageUsers/edit", controller.handleAddSuperadmin(userDTO, errorsMock));

    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfUserNotSuperAdminOnSubmission() {

        replay();
        controller.handleAddSuperadmin(null, null);

    }

    @Test
    public void shouldReturnToViewIfErrorsOnSaveSuperadmin() {
        replay();

        UserDTO userDTO = new UserDTO();
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
        replay();

        assertEquals("private/staff/superAdmin/create_new_user_in_role_page", controller.handleAddSuperadmin(userDTO, errorsMock));

    }

    @Test
    public void shouldBindSuperAdminPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(userDTOValidator);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));

        replay();
        controller.registerValidator(binderMock);

    }

}
