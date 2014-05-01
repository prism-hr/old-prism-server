package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.ApplicationsFilteringDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilteringBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.exceptions.LinkAccountsException;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class UserServiceTest {

    private User currentUser;

    @Mock
    @InjectIntoByType
    private UserDAO userDAOMock;

    @Mock
    @InjectIntoByType
    private RoleDAO roleDAOMock;

    @Mock
    @InjectIntoByType
    private ApplicationsFilteringDAO filteringDAOMock;

    private User currentUserMock;

    @Mock
    @InjectIntoByType
    private EncryptionUtils encryptionUtilsMock;

    @Mock
    @InjectIntoByType
    private MailSendingService mailServiceMock;

    @Mock
    @InjectIntoByType
    private WorkflowService applicationFormUserRoleServiceMock;

    @TestedObject
    private UserService userService;

//    @Test
//    public void shouldGetUserFromDAO() {
//        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
//        EasyMock.expect(userDAOMock.get(1)).andReturn(user);
//
//        replay();
//        assertEquals(user, userService.getUser(1));
//    }
//
//    @Test
//    public void shouldGetUserFromDAOByActivationCode() {
//        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
//        EasyMock.expect(userDAOMock.getUserByActivationCode("Abc")).andReturn(user);
//
//        replay();
//        assertEquals(user, userService.getUserByActivationCode("Abc"));
//    }
//
//    @Test
//    public void shouldGetAllUsersWithAuthority() {
//        RegisteredUser userOne = new RegisteredUserBuilder().id(1).build();
//        RegisteredUser userTwo = new RegisteredUserBuilder().id(2).build();
//        Authority auth = Authority.ADMINISTRATOR;
//        Role role = new RoleBuilder().build();
//        EasyMock.expect(roleDAOMock.getById(auth)).andReturn(role);
//        EasyMock.expect(userDAOMock.getUsersInRole(role.getId())).andReturn(Arrays.asList(userOne, userTwo));
//
//        EasyMockUnitils.replay();
//        List<RegisteredUser> users = userService.getUsersInRole(auth);
//        assertEquals(2, users.size());
//        assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));
//    }
//
//    @Test
//    public void shouldGetAllUsersForProgram() {
//        RegisteredUser userOne = new RegisteredUserBuilder().id(2).build();
//        RegisteredUser userTow = new RegisteredUserBuilder().id(3).build();
//        Program program = new ProgramBuilder().id(7).build();
//        EasyMock.expect(userDAOMock.getUsersForProgram(program)).andReturn(Arrays.asList(userOne, userTow));
//
//        EasyMockUnitils.replay();
//        List<RegisteredUser> users = userService.getAllUsersForProgram(program);
//
//        assertEquals(2, users.size());
//        assertTrue(users.containsAll(Arrays.asList(userOne, userTow)));
//    }
//
//    @Test
//    public void shouldGetAllInternalUsers() {
//        RegisteredUser userOne = new RegisteredUserBuilder().id(1).build();
//        RegisteredUser userTwo = new RegisteredUserBuilder().id(2).build();
//        EasyMock.expect(userDAOMock.getInternalUsers()).andReturn(Arrays.asList(userOne, userTwo));
//        EasyMockUnitils.replay();
//        List<RegisteredUser> internalUsers = userService.getAllInternalUsers();
//        assertEquals(2, internalUsers.size());
//        assertTrue(internalUsers.containsAll(Arrays.asList(userOne, userTwo)));
//    }
//
//    @Test
//    public void shouldDelegateSaveToDAO() {
//        RegisteredUser user = EasyMock.createMock(RegisteredUser.class);
//        userDAOMock.save(user);
//        EasyMockUnitils.replay();
//        userService.save(user);
//        EasyMockUnitils.verify();
//    }
//
//    @Test
//    public void shouldGetAllUsers() {
//        RegisteredUser userOne = EasyMock.createMock(RegisteredUser.class);
//        RegisteredUser userTwo = EasyMock.createMock(RegisteredUser.class);
//        EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(userOne, userTwo));
//
//        EasyMockUnitils.replay();
//        List<RegisteredUser> allUsers = userService.getAllUsers();
//
//        Assert.assertEquals(2, allUsers.size());
//        Assert.assertTrue(allUsers.contains(userOne));
//        Assert.assertTrue(allUsers.contains(userTwo));
//    }
//
//    @Test
//    public void shouldGetUserByUsername() {
//        RegisteredUser user = EasyMock.createMock(RegisteredUser.class);
//        EasyMock.expect(userDAOMock.getUserByUsername("mike")).andReturn(user);
//        
//        EasyMockUnitils.replay();
//        Assert.assertEquals(user, userService.getUserByUsername("mike"));
//    }
//
//    @Test
//    public void shouldAddRoleToUser() {
//        RegisteredUser user = new RegisteredUserBuilder().build();
//        EasyMock.expect(roleDAOMock.getById(Authority.ADMINISTRATOR)).andReturn(new RoleBuilder().id(Authority.ADMINISTRATOR).build());
//        userDAOMock.save(user);
//        EasyMockUnitils.replay();
//        userService.addRoleToUser(user, Authority.ADMINISTRATOR);
//        assertEquals(1, user.getRoles().size());
//        assertEquals(Authority.ADMINISTRATOR, user.getRoles().get(0).getId());
//    }
//
//    @Test
//    public void shouldSaveSelectedUser() {
//        RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
//        userDAOMock.save(selectedUser);
//        Program selectedProgram = new ProgramBuilder().id(4).build();
//
//        EasyMockUnitils.replay();
//        userService.updateUserWithNewRoles(selectedUser, selectedProgram);
//        EasyMockUnitils.verify();
//    }
//
//    @Test
//    public void shouldSetApplicationsFiltering() {
//        ApplicationsFiltering filtering = new ApplicationsFilteringBuilder().build();
//        RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).filtering(filtering).build();
//
//        userDAOMock.save(selectedUser);
//        EasyMock.expect(filteringDAOMock.merge(filtering)).andReturn(filtering);
//
//        replay();
//        userService.setFiltering(selectedUser, filtering);
//        verify();
//
//        assertSame(filtering, selectedUser.getFiltering());
//    }
//
//    @Test
//    public void shouldAddUserRoleAdminIfNotAlreadyAdminAndAdminInNewRoles() {
//        RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
//        Program selectedProgram = new ProgramBuilder().id(4).build();
//
//        EasyMock.expect(roleDAOMock.getById(Authority.ADMINISTRATOR)).andReturn(new RoleBuilder().id(Authority.ADMINISTRATOR).build()).anyTimes();
//        applicationFormUserRoleServiceMock.insertProgramRole(selectedUser, selectedProgram, Authority.ADMINISTRATOR);
//        userDAOMock.save(selectedUser);
//
//        replay();
//        userService.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.ADMINISTRATOR);
//        verify();
//
//        assertTrue(selectedUser.isInRole(Authority.ADMINISTRATOR));
//    }
//
//    @Test
//    public void shouldAddUserRoleApproverIfNotAlreadyApproverAndAproverInNewRoles() {
//        RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
//        Program selectedProgram = new ProgramBuilder().id(4).build();
//
//        EasyMock.expect(roleDAOMock.getById(Authority.APPROVER)).andReturn(new RoleBuilder().id(Authority.APPROVER).build()).anyTimes();
//        applicationFormUserRoleServiceMock.insertProgramRole(selectedUser, selectedProgram, Authority.APPROVER);
//        userDAOMock.save(selectedUser);
//
//        replay();
//        userService.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.APPROVER);
//        verify();
//
//        assertTrue(selectedUser.isInRole(Authority.APPROVER));
//    }
//
//    @Test
//    public void shouldAddProgramToAdminListIfNew() {
//        Program selectedProgram = new ProgramBuilder().id(1).build();
//        RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
//        Role role = new RoleBuilder().id(Authority.ADMINISTRATOR).build();
//
//        EasyMock.expect(roleDAOMock.getById(Authority.ADMINISTRATOR)).andReturn(role).anyTimes();
//        applicationFormUserRoleServiceMock.insertProgramRole(selectedUser, selectedProgram, Authority.ADMINISTRATOR);
//        userDAOMock.save(selectedUser);
//
//        replay();
//        userService.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.ADMINISTRATOR);
//        verify();
//
//        assertTrue(selectedUser.getProgramsOfWhichAdministrator().contains(selectedProgram));
//    }
//
//    @Test
//    public void shouldAddProgramToApproverlistIfNew() {
//        Program selectedProgram = new ProgramBuilder().id(1).build();
//        RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
//        Role role = new RoleBuilder().id(Authority.APPROVER).build();
//        EasyMock.expect(roleDAOMock.getById(Authority.APPROVER)).andReturn(role).anyTimes();
//        applicationFormUserRoleServiceMock.insertProgramRole(selectedUser, selectedProgram, Authority.APPROVER);
//        userDAOMock.save(selectedUser);
//
//        replay();
//        userService.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.APPROVER);
//        verify();
//
//        assertTrue(selectedUser.getProgramsOfWhichApprover().contains(selectedProgram));
//    }
//
//    @Test
//    public void shouldRemoveFromProgramsOfWhichAdministratorIfNoLongerInList() {
//        Program selectedProgram = new ProgramBuilder().id(1).build();
//        RegisteredUser selectedUser = new RegisteredUserBuilder().programsOfWhichAdministrator(selectedProgram).id(1).build();
//
//        applicationFormUserRoleServiceMock.deleteProgramRole(selectedUser, selectedProgram, Authority.ADMINISTRATOR);
//        userDAOMock.save(selectedUser);
//
//        replay();
//        userService.updateUserWithNewRoles(selectedUser, selectedProgram);
//        verify();
//
//        assertFalse(selectedUser.getProgramsOfWhichAdministrator().contains(selectedProgram));
//    }
//
//    @Test
//    public void shouldRemoveFromProgramsOfWhichApproverIfNoLongerInList() {
//        Program selectedProgram = new ProgramBuilder().id(1).build();
//        RegisteredUser selectedUser = new RegisteredUserBuilder().programsOfWhichApprover(selectedProgram).id(1).build();
//
//        applicationFormUserRoleServiceMock.deleteProgramRole(selectedUser, selectedProgram, Authority.APPROVER);
//        userDAOMock.save(selectedUser);
//
//        replay();
//        userService.updateUserWithNewRoles(selectedUser, selectedProgram);
//        verify();
//
//        assertFalse(selectedUser.getProgramsOfWhichApprover().contains(selectedProgram));
//    }
//
//    @Test(expected = IllegalStateException.class)
//    public void shouldThrowISEwhenUserAlreadyExistsForNewUserInProgramme() {
//        RegisteredUser existingUser = new RegisteredUserBuilder().id(1).build();
//
//        EasyMock.expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(existingUser);
//        EasyMockUnitils.replay();
//        userService.createNewUserForProgramme("la", "le", "some@email.com", new ProgramBuilder().id(4).build());
//    }
//
//    @Test
//    public void shouldCreateUserAndWithRolesInProgramme() {
//        Program program = new ProgramBuilder().id(4).build();
//        RegisteredUser newUser = new RegisteredUserBuilder().id(5).build();
//
//        Role role_1 = new RoleBuilder().build();
//        Role role_2 = new RoleBuilder().build();
//        Role role_3 = new RoleBuilder().build();
//        Role role_6 = new RoleBuilder().build();
//
//        expect(authenticationService.getCurrentUser()).andReturn(currentUserMock).anyTimes();
//        expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(null);
//        expect(
//                userFactoryMock.createNewUserInRoles("la", "le", "some@email.com",
//                        Arrays.asList(Authority.SUPERADMINISTRATOR, Authority.ADMINISTRATOR, Authority.APPROVER, Authority.VIEWER))).andReturn(newUser);
//        userDAOMock.save(newUser);
//
//        expect(roleDAOMock.getById(Authority.SUPERADMINISTRATOR)).andReturn(role_1);
//        expect(roleDAOMock.getById(Authority.ADMINISTRATOR)).andReturn(role_2);
//        expect(roleDAOMock.getById(Authority.APPROVER)).andReturn(role_3);
//        expect(roleDAOMock.getById(Authority.VIEWER)).andReturn(role_6);
//
//        applicationFormUserRoleServiceMock.insertUserRole(newUser, Authority.SUPERADMINISTRATOR);
//        applicationFormUserRoleServiceMock.insertProgramRole(newUser, program, Authority.ADMINISTRATOR);
//        applicationFormUserRoleServiceMock.insertProgramRole(newUser, program, Authority.APPROVER);
//        applicationFormUserRoleServiceMock.insertProgramRole(newUser, program, Authority.VIEWER);
//
//        replay();
//        RegisteredUser createdUser = userService.createNewUserForProgramme("la", "le", "some@email.com", program, Authority.SUPERADMINISTRATOR,
//                Authority.ADMINISTRATOR, Authority.APPROVER, Authority.VIEWER);
//        verify();
//
//        assertEquals(newUser, createdUser);
//
//        assertTrue(createdUser.getProgramsOfWhichAdministrator().contains(program));
//        assertTrue(createdUser.getProgramsOfWhichApprover().contains(program));
//
//        assertEquals(4, createdUser.getPendingRoleNotifications().size());
//
//        PendingRoleNotification pendingRoleNotification = createdUser.getPendingRoleNotifications().get(0);
//        assertEquals(role_1, pendingRoleNotification.getRole());
//        assertNull(pendingRoleNotification.getProgram());
//        assertEquals(currentUserMock, pendingRoleNotification.getAddedByUser());
//
//        pendingRoleNotification = createdUser.getPendingRoleNotifications().get(1);
//        assertEquals(role_2, pendingRoleNotification.getRole());
//        assertEquals(program, pendingRoleNotification.getProgram());
//        assertEquals(currentUserMock, pendingRoleNotification.getAddedByUser());
//
//        pendingRoleNotification = createdUser.getPendingRoleNotifications().get(2);
//        assertEquals(role_3, pendingRoleNotification.getRole());
//        assertEquals(program, pendingRoleNotification.getProgram());
//        assertEquals(currentUserMock, pendingRoleNotification.getAddedByUser());
//
//        pendingRoleNotification = createdUser.getPendingRoleNotifications().get(3);
//        assertEquals(role_6, pendingRoleNotification.getRole());
//        assertEquals(program, pendingRoleNotification.getProgram());
//        assertEquals(currentUserMock, pendingRoleNotification.getAddedByUser());
//    }
//
//    @Test(expected = IllegalStateException.class)
//    public void shouldThrowISEwhenUserAlreadyExistsForNewUserNotInProgramme() {
//        RegisteredUser existingUser = new RegisteredUserBuilder().id(1).build();
//
//        EasyMock.expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(existingUser);
//        EasyMockUnitils.replay();
//        userService.createNewUserInRole("la", "le", "some@email.com", Authority.APPROVER);
//    }
//
//    @Test
//    public void shouldCreateUserAndWithRolesNotInAnyProgramme() {
//
//        EasyMock.expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(null);
//        RegisteredUser newUser = new RegisteredUserBuilder().id(5).build();
//        EasyMock.expect(userFactoryMock.createNewUserInRoles("la", "le", "some@email.com", Authority.REVIEWER)).andReturn(newUser);
//        userDAOMock.save(newUser);
//
//        EasyMockUnitils.replay();
//
//        RegisteredUser createdUser = userService.createNewUserInRole("la", "le", "some@email.com", Authority.REVIEWER);
//
//        EasyMockUnitils.verify();
//        assertEquals(newUser, createdUser);
//
//        assertTrue(createdUser.getProgramsOfWhichAdministrator().isEmpty());
//        assertTrue(createdUser.getProgramsOfWhichApprover().isEmpty());
//
//        assertTrue(createdUser.getPendingRoleNotifications().isEmpty());
//    }
//
//    @Test
//    public void shouldCreateUserWithDirectToLinkToAddReview() {
//
//        EasyMock.expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(null);
//        RegisteredUser newUser = new RegisteredUserBuilder().id(5).build();
//        EasyMock.expect(userFactoryMock.createNewUserInRoles("la", "le", "some@email.com", Authority.REVIEWER)).andReturn(newUser);
//        userDAOMock.save(newUser);
//        EasyMock.expectLastCall().times(2);
//
//        EasyMockUnitils.replay();
//
//        RegisteredUser createdUser = userService.createNewUserInRole("la", "le", "some@email.com", DirectURLsEnum.ADD_REVIEW, new ApplicationFormBuilder()
//                .id(1).applicationNumber("bob").build(), Authority.REVIEWER);
//
//        EasyMockUnitils.verify();
//        assertEquals(newUser, createdUser);
//        assertEquals("/reviewFeedback?applicationId=bob", createdUser.getDirectToUrl());
//
//        assertTrue(createdUser.getProgramsOfWhichAdministrator().isEmpty());
//        assertTrue(createdUser.getProgramsOfWhichApprover().isEmpty());
//
//        assertTrue(createdUser.getPendingRoleNotifications().isEmpty());
//    }
//
//    @Test
//    public void shouldCreateUserWithDirectToLinkToViewapplication() {
//
//        EasyMock.expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(null);
//        RegisteredUser newUser = new RegisteredUserBuilder().id(5).build();
//        EasyMock.expect(userFactoryMock.createNewUserInRoles("la", "le", "some@email.com", Authority.INTERVIEWER)).andReturn(newUser);
//        userDAOMock.save(newUser);
//        EasyMock.expectLastCall().times(2);
//
//        EasyMockUnitils.replay();
//
//        RegisteredUser createdUser = userService.createNewUserInRole("la", "le", "some@email.com", DirectURLsEnum.VIEW_APPLIATION_PRIOR_TO_INTERVIEW,
//                new ApplicationFormBuilder().id(1).applicationNumber("bob").build(), Authority.INTERVIEWER);
//
//        EasyMockUnitils.verify();
//        assertEquals(newUser, createdUser);
//        assertEquals("/application?view=view&applicationId=bob", createdUser.getDirectToUrl());
//
//        assertTrue(createdUser.getProgramsOfWhichAdministrator().isEmpty());
//        assertTrue(createdUser.getProgramsOfWhichApprover().isEmpty());
//
//        assertTrue(createdUser.getPendingRoleNotifications().isEmpty());
//    }
//
//    @Test
//    public void shouldUpdateCurrentUserAndSendEmailConfirmation() throws UnsupportedEncodingException {
//        final RegisteredUser currentUser = new RegisteredUserBuilder().firstName("f").lastName("l").id(7).withPassword("12").email("em").username("em").build();
//        RegisteredUser userOne = new RegisteredUserBuilder().firstName("a").firstName2("a2").firstName3("a3").lastName("o").email("two").withPassword("12")
//                .withNewPassword("newpass").build();
//
//        EasyMock.expect(encryptionUtilsMock.getMD5Hash("newpass")).andReturn("encryptednewpass");
//        expect(authenticationService.getCurrentUser()).andReturn(currentUser);
//        userDAOMock.save(currentUser);
//
//        replay();
//        userService.updateCurrentUser(userOne);
//        verify();
//
//        assertEquals("two", currentUser.getUsername());
//        assertEquals("two", currentUser.getEmail());
//        assertEquals("a", currentUser.getFirstName());
//        assertEquals("a2", currentUser.getFirstName2());
//        assertEquals("a3", currentUser.getFirstName3());
//        assertEquals("o", currentUser.getLastName());
//        assertEquals("encryptednewpass", currentUser.getPassword());
//    }
//
//    @Test
//    public void shouldNotChangePassIfPasswordIsBlank() {
//        final RegisteredUser currentUser = new RegisteredUserBuilder().withPassword("12").email("em").username("em").build();
//        RegisteredUser userOne = new RegisteredUserBuilder().username("one").email("two").withPassword("").id(5).build();
//
//        expect(authenticationService.getCurrentUser()).andReturn(currentUser);
//        userDAOMock.save(currentUser);
//
//        replay();
//        userService.updateCurrentUser(userOne);
//        verify();
//
//        assertEquals("two", currentUser.getUsername());
//        assertEquals("two", currentUser.getEmail());
//        assertEquals("12", currentUser.getPassword());
//    }
//
//    @Test
//    public void shouldReturnIfAccountsHaveAlreadyBeenLinked() {
//        final RegisteredUser currentAccount = new RegisteredUserBuilder().id(1).accountNonExpired(true).accountNonLocked(true).enabled(true)
//                .activationCode("abc").email("B@A.com").withPassword("password").build();
//
//        final RegisteredUser secondAccount = new RegisteredUserBuilder().id(2).accountNonExpired(true).accountNonLocked(true).enabled(true)
//                .activationCode("abcd").email("A@B.com").withPassword("password").build();
//
//        secondAccount.setPrimaryAccount(currentAccount);
//
//    }
//
//    @Test(expected = LinkAccountsException.class)
//    public void shouldReturnFalseIfAccountsDisabled() throws LinkAccountsException {
//        final RegisteredUser currentAccount = new RegisteredUserBuilder().id(1).accountNonExpired(true).accountNonLocked(true).enabled(true)
//                .activationCode("abc").email("B@A.com").withPassword("password").build();
//
//        final RegisteredUser secondAccount = new RegisteredUserBuilder().id(2).accountNonExpired(true).accountNonLocked(true).enabled(false)
//                .activationCode("abcd").email("A@B.com").withPassword("password").build();
//
//        expect(authenticationService.getCurrentUser()).andReturn(currentAccount);
//        expect(userDAOMock.getUserByEmail(secondAccount.getEmail())).andReturn(secondAccount);
//
//        replay();
//        userService.linkAccounts(secondAccount.getEmail());
//    }
//
//    @Test(expected = LinkAccountsException.class)
//    public void shouldReturnFalseIfAccountsIsExpired() throws LinkAccountsException {
//        final RegisteredUser currentAccount = new RegisteredUserBuilder().id(1).accountNonExpired(true).accountNonLocked(true).enabled(true)
//                .activationCode("abc").email("B@A.com").withPassword("password").build();
//
//        final RegisteredUser secondAccount = new RegisteredUserBuilder().id(2).accountNonExpired(false).accountNonLocked(true).enabled(true)
//                .activationCode("abcd").email("A@B.com").withPassword("password").build();
//
//        expect(authenticationService.getCurrentUser()).andReturn(currentAccount);
//        expect(userDAOMock.getUserByEmail(secondAccount.getEmail())).andReturn(secondAccount);
//
//        replay();
//        userService.linkAccounts(secondAccount.getEmail());
//    }
//
//    @Before
//    public void setUp() {
//        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
//        currentUser = new RegisteredUserBuilder().id(8).username("bob").role(new RoleBuilder().id(Authority.APPLICANT).build()).build();
//        currentUserMock = EasyMockUnitils.createMock(RegisteredUser.class);
//        authenticationToken.setDetails(currentUser);
//        SecurityContextImpl secContext = new SecurityContextImpl();
//        secContext.setAuthentication(authenticationToken);
//        SecurityContextHolder.setContext(secContext);
//    }
//
//    @After
//    public void tearDown() {
//        SecurityContextHolder.clearContext();
//    }

}