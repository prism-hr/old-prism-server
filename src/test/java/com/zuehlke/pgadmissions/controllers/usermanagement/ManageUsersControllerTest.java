package com.zuehlke.pgadmissions.controllers.usermanagement;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.PersonBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.RegistryUserDTO;
import com.zuehlke.pgadmissions.dto.UserDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.PersonPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.SuperadminUserDTOValidator;
import com.zuehlke.pgadmissions.validators.UserDTOValidator;

public class ManageUsersControllerTest {

    private ProgramService programServiceMock;
    private ManageUsersController controller;
    private UserService userServiceMock;
    private RegisteredUser currentUserMock;
    private ProgramPropertyEditor programPropertyEditorMock;
    private UserDTOValidator newUserDTOValidatorMock;
    private EncryptionHelper encryptionHelperMock;
    private SuperadminUserDTOValidator userDTOValidatorMock;
    private PersonPropertyEditor registryPropertyEditorMock;
    private ConfigurationService configurationServiceMock;

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
        EasyMock.replay(userServiceMock);
        assertEquals(currentUserMock, controller.getUser());
        EasyMock.verify(userServiceMock);
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
        EasyMock.expect(programServiceMock.getAllEnabledPrograms()).andReturn(Arrays.asList(program1, program2));
        EasyMock.replay(programServiceMock, currentUserMock, userServiceMock);
        List<Program> programs = controller.getPrograms();
        EasyMock.verify(programServiceMock, currentUserMock, userServiceMock);
        assertEquals(2, programs.size());
        assertTrue(programs.containsAll(Arrays.asList(program1, program2)));
    }

    @Test
    public void shouldReturnProgramsOfWhichAdministratorForAdmins() {
        EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
        Program program1 = new ProgramBuilder().id(1).build();
        Program program2 = new ProgramBuilder().id(2).build();
        EasyMock.expect(currentUserMock.getProgramsOfWhichAdministrator()).andReturn(Arrays.asList(program1, program2));
        EasyMock.replay(programServiceMock, currentUserMock, userServiceMock);
        List<Program> programs = controller.getPrograms();
        EasyMock.verify(programServiceMock, currentUserMock, userServiceMock);
        assertEquals(2, programs.size());
        assertTrue(programs.containsAll(Arrays.asList(program1, program2)));
    }

    @Test
    public void shouldReturnCorrectPossibleRoles() {
        EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
        EasyMock.replay(currentUserMock);

        List<Authority> authorities = controller.getAuthorities();
        assertThat(authorities, contains( //
                Authority.ADMINISTRATOR, //
                Authority.APPROVER, //
                Authority.VIEWER));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfUserNeitherSuperAdminOrAdmin() {
        EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
        EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false).anyTimes();
        EasyMock.expect(currentUserMock.isNotInRole(Authority.ADMITTER)).andReturn(true).anyTimes();
        EasyMock.replay(currentUserMock, userServiceMock);
        controller.getAddUsersView();
        EasyMock.verify(currentUserMock, userServiceMock);
    }

    @Test
    public void shoulReturnNewUsersViewForSuperadmin() {
        EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
        EasyMock.replay(currentUserMock, userServiceMock);
        assertEquals("private/staff/superAdmin/create_new_user_in_role_page", controller.getAddUsersView());
        EasyMock.verify(currentUserMock, userServiceMock);
    }

    @Test
    public void shoulReturnNewUsersViewForAdmin() {
        EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
        EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();
        EasyMock.replay(currentUserMock, userServiceMock);
        assertEquals("private/staff/superAdmin/create_new_user_in_role_page", controller.getAddUsersView());
        EasyMock.verify(currentUserMock, userServiceMock);
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
        EasyMock.replay(currentUserMock, userServiceMock);
        controller.handleEditUserRoles(null, null);
        EasyMock.verify(currentUserMock, userServiceMock);
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
        EasyMock.expect(currentUserMock.getId()).andReturn(1);

        EasyMock.replay(currentUserMock, userServiceMock);

        assertEquals("redirect:/manageUsers/edit?programCode=ABC", controller.handleRemoveUserFromProgram(newUserDTO));

        EasyMock.verify(userServiceMock);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfUserNeitherSuperAdminOrAdminOnRemove() {
        EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
        EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false).anyTimes();
        EasyMock.replay(currentUserMock, userServiceMock);
        controller.handleRemoveUserFromProgram(null);
        EasyMock.verify(currentUserMock, userServiceMock);
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
    
    @Test
    public void shouldRegistorPropertyEditorForRegistryUsers() {
        WebDataBinder dataBinderMock = EasyMock.createMock(WebDataBinder.class);
        dataBinderMock.registerCustomEditor(Person.class, registryPropertyEditorMock);
        EasyMock.replay(dataBinderMock);
        controller.registerValidatorsAndPropertyEditorsForRegistryUsers(dataBinderMock);
        EasyMock.verify(dataBinderMock);
    }
    
    @Test
    public void shouldGetAllRegistryUsers() {
        Person personOne = new PersonBuilder().id(1).build();
        Person personTwo = new PersonBuilder().id(4).build();

        EasyMock.expect(configurationServiceMock.getAllRegistryUsers()).andReturn(Arrays.asList(personOne, personTwo));
        EasyMock.replay(configurationServiceMock);
        List<Person> allRegistryUsers = controller.getAllRegistryContacts();
        assertEquals(2, allRegistryUsers.size());
        assertTrue(allRegistryUsers.containsAll(Arrays.asList(personOne, personTwo)));
    }
    
    @Test
    public void shouldReturnAllSuperadministratorsOrderbylastnameFirstname() {
        RegisteredUser userOne = new RegisteredUserBuilder().id(1).lastName("ZZZZ").firstName("BBBB").build();
        RegisteredUser userTwo = new RegisteredUserBuilder().id(4).lastName("ZZZZ").firstName("AAAA").build();
        RegisteredUser userThree = new RegisteredUserBuilder().id(5).lastName("AA").firstName("GGG").build();
        EasyMock.expect(userServiceMock.getUsersInRole(Authority.SUPERADMINISTRATOR)).andReturn(Arrays.asList(userOne, userTwo, userThree));
        EasyMock.replay(userServiceMock);
        List<RegisteredUser> superadmins = controller.getSuperadmins();
        EasyMock.verify(userServiceMock);
        assertEquals(3, superadmins.size());
        assertEquals(userThree, superadmins.get(0));
        assertEquals(userTwo, superadmins.get(1));
        assertEquals(userOne, superadmins.get(2));
    }
    
    @Test
    public void shouldReturnAllRegistryContacts() {
        Person userOne = new PersonBuilder().id(1).lastname("ZZZZ").firstname("BBBB").build();
        Person userTwo = new PersonBuilder().id(2).lastname("AAAA").firstname("AAAA").build();
        Person userThree = new PersonBuilder().id(3).lastname("GGG").firstname("GGG").build();
        EasyMock.expect(configurationServiceMock.getAllRegistryUsers()).andReturn(Arrays.asList(userOne, userTwo, userThree));
        EasyMock.replay(configurationServiceMock);
        List<Person> superadmins = controller.getAllRegistryContacts();
        EasyMock.verify(configurationServiceMock);
        assertEquals(3, superadmins.size());
        assertEquals(userOne.getId(), superadmins.get(0).getId());
        assertEquals(userTwo.getId(), superadmins.get(1).getId());
        assertEquals(userThree.getId(), superadmins.get(2).getId());
    }
    
    @Test
    public void shouldCreateNewUserInRolesSuperAdmin() {
        EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();

        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
        

        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Jane");
        userDTO.setLastName("Doe");
        userDTO.setEmail("jane.doe@test.com");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("jane.doe@test.com")).andReturn(null);
        EasyMock.expect(userServiceMock.createNewUserForProgramme("Jane", "Doe", "jane.doe@test.com", null, Authority.SUPERADMINISTRATOR)).andReturn(
                new RegisteredUserBuilder().id(4).build());

        EasyMock.replay(errorsMock, currentUserMock, userServiceMock);

        assertEquals("redirect:/manageUsers/edit", controller.handleAddSuperadmin(userDTO, errorsMock));


        EasyMock.verify(errorsMock, currentUserMock, userServiceMock);
    }
    
    @Test
    public void shouldCreateNewUserInRolesAdmitter() {
        EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
        
        
        Person userOne = new PersonBuilder().id(1).lastname("ZZZZ").firstname("BBBB").build();
        Person userTwo = new PersonBuilder().id(2).lastname("AAAA").firstname("AAAA").build();
        Person userThree = new PersonBuilder().id(3).lastname("GGG").firstname("GGG").build();
        RegistryUserDTO userDTO = new RegistryUserDTO();
        userDTO.setRegistryUsers(Arrays.asList(userOne, userTwo, userThree));
        
        configurationServiceMock.saveRegistryUsers(userDTO.getRegistryUsers(), currentUserMock);
        
        EasyMock.replay(configurationServiceMock, currentUserMock, userServiceMock);
        
        assertEquals("redirect:/manageUsers/edit", controller.handleEditRegistryUsers(userDTO));
        
        
        EasyMock.verify(configurationServiceMock, currentUserMock, userServiceMock);
    }
    
    @Test
    public void shouldUpdateRolesForUserIfUserAlreadyExists() {
        EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(false);

        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Jane");
        userDTO.setLastName("Doe");
        userDTO.setEmail("jane.doe@test.com");

        RegisteredUser existingUser = new RegisteredUserBuilder().id(7).build();
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("jane.doe@test.com")).andReturn(existingUser);
        userServiceMock.updateUserWithNewRoles(existingUser, null, Authority.SUPERADMINISTRATOR);

        EasyMock.replay(currentUserMock, userServiceMock, errorsMock);

        assertEquals("redirect:/manageUsers/edit", controller.handleAddSuperadmin(userDTO, errorsMock));

        EasyMock.verify(userServiceMock, errorsMock, currentUserMock);
    }
    

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfUserNotSuperAdminOnSubmission() {
       
        EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
        EasyMock.replay(userServiceMock, currentUserMock);
        controller.handleAddSuperadmin(null, null);
        EasyMock.verify(userServiceMock, currentUserMock);
    }
    
    @Test
    public void shouldReturnToViewIfErrorsOnSaveSuperadmin() {
        EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
        EasyMock.replay(userServiceMock, currentUserMock);

        UserDTO userDTO = new UserDTO();
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
        EasyMock.replay(errorsMock);

        assertEquals("private/staff/superAdmin/create_new_user_in_role_page", controller.handleAddSuperadmin(userDTO, errorsMock));

        EasyMock.verify(userServiceMock);
    }
    
    @Test
    public void shouldBindSuperAdminPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(this.userDTOValidatorMock);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
        
        EasyMock.replay(binderMock);
        controller.registerValidator(binderMock);
        EasyMock.verify(binderMock);
    }
    
    @Test
    public void shouldReturnNewDTO() {

        UserDTO userDTO = controller.getUserDTO();
        assertNotNull(userDTO);
        assertNull(userDTO.getEmail());
        assertNull(userDTO.getFirstName());
        assertNull(userDTO.getLastName());
        assertEquals(0, userDTO.getSelectedAuthorities().length);
        assertNull( userDTO.getSelectedProgram());

    }

    @Before
    public void setUp() {

        userServiceMock = EasyMock.createMock(UserService.class);
        programServiceMock = EasyMock.createMock(ProgramService.class);
        currentUserMock = EasyMock.createMock(RegisteredUser.class);
        programPropertyEditorMock = EasyMock.createMock(ProgramPropertyEditor.class);
        newUserDTOValidatorMock = EasyMock.createMock(UserDTOValidator.class);
        encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
        userDTOValidatorMock = EasyMock.createMock(SuperadminUserDTOValidator.class);
        registryPropertyEditorMock = EasyMock.createMock(PersonPropertyEditor.class);
        configurationServiceMock = EasyMock.createMock(ConfigurationService.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();

        controller = new ManageUsersController(programServiceMock, userServiceMock, programPropertyEditorMock,
                newUserDTOValidatorMock, encryptionHelperMock, userDTOValidatorMock, registryPropertyEditorMock, configurationServiceMock);
    }
}
