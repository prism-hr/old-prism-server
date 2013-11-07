package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ApplicationsFilteringDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilteringBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.exceptions.LinkAccountsException;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

public class UserServiceTest {

    private RegisteredUser currentUser;

    private UserDAO userDAOMock;

    private UserService userService;

    private RoleDAO roleDAOMock;

    private ApplicationsFilteringDAO filteringDAOMock;

    private UserService userServiceWithCurrentUserOverride;

    private RegisteredUser currentUserMock;

    private UserFactory userFactoryMock;

    private EncryptionUtils encryptionUtilsMock;

    private MailSendingService mailServiceMock;

    private ProgramsService programsServiceMock;

    private ApplicationFormUserRoleService applicationFormUserRoleServiceMock;

    @Test
    public void shouldGetUserFromDAO() {
        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
        EasyMock.expect(userDAOMock.get(1)).andReturn(user);
        EasyMock.replay(userDAOMock);
        assertEquals(user, userService.getUser(1));
    }

    @Test
    public void shouldGetUserFromDAOByActivationCode() {
        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
        EasyMock.expect(userDAOMock.getUserByActivationCode("Abc")).andReturn(user);
        EasyMock.replay(userDAOMock);
        assertEquals(user, userService.getUserByActivationCode("Abc"));
    }

    @Test
    public void shouldGetAllUsersWithAuthority() {
        RegisteredUser userOne = new RegisteredUserBuilder().id(1).build();
        RegisteredUser userTwo = new RegisteredUserBuilder().id(2).build();
        Authority auth = Authority.ADMINISTRATOR;
        Role role = new RoleBuilder().build();
        EasyMock.expect(roleDAOMock.getRoleByAuthority(auth)).andReturn(role);
        EasyMock.expect(userDAOMock.getUsersInRole(role)).andReturn(Arrays.asList(userOne, userTwo));
        EasyMock.replay(roleDAOMock, userDAOMock);

        List<RegisteredUser> users = userService.getUsersInRole(auth);
        assertEquals(2, users.size());
        assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));
    }

    @Test
    public void shouldGetAllUsersForProgram() {
        RegisteredUser userOne = new RegisteredUserBuilder().id(2).build();
        RegisteredUser userTow = new RegisteredUserBuilder().id(3).build();
        Program program = new ProgramBuilder().id(7).build();
        EasyMock.expect(userDAOMock.getUsersForProgram(program)).andReturn(Arrays.asList(userOne, userTow));
        EasyMock.replay(userDAOMock);
        List<RegisteredUser> users = userService.getAllUsersForProgram(program);
        assertEquals(2, users.size());
        assertTrue(users.containsAll(Arrays.asList(userOne, userTow)));
    }

    @Test
    public void shouldGetAllInternalUsers() {
        RegisteredUser userOne = new RegisteredUserBuilder().id(1).build();
        RegisteredUser userTwo = new RegisteredUserBuilder().id(2).build();
        EasyMock.expect(userDAOMock.getInternalUsers()).andReturn(Arrays.asList(userOne, userTwo));
        EasyMock.replay(userDAOMock);
        List<RegisteredUser> internalUsers = userService.getAllInternalUsers();
        assertEquals(2, internalUsers.size());
        assertTrue(internalUsers.containsAll(Arrays.asList(userOne, userTwo)));
    }

    @Test
    public void shouldDelegateSaveToDAO() {
        RegisteredUser user = EasyMock.createMock(RegisteredUser.class);
        userDAOMock.save(user);
        EasyMock.replay(userDAOMock);
        userService.save(user);
        EasyMock.verify(userDAOMock);
    }

    @Test
    public void shouldGetAllUsers() {
        RegisteredUser userOne = EasyMock.createMock(RegisteredUser.class);
        RegisteredUser userTwo = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(userOne, userTwo));

        EasyMock.replay(userOne, userTwo, userDAOMock);
        List<RegisteredUser> allUsers = userService.getAllUsers();

        Assert.assertEquals(2, allUsers.size());
        Assert.assertTrue(allUsers.contains(userOne));
        Assert.assertTrue(allUsers.contains(userTwo));
    }

    @Test
    public void shouldGetUserByUsername() {
        RegisteredUser user = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userDAOMock.getUserByUsername("mike")).andReturn(user);

        EasyMock.replay(user, userDAOMock);
        Assert.assertEquals(user, userService.getUserByUsername("mike"));
    }

    @Test
    public void shouldGetUserFromSecurityContextAndRefresh() {
        RegisteredUser refreshedUser = new RegisteredUser();
        EasyMock.expect(userDAOMock.get(8)).andReturn(refreshedUser);
        EasyMock.expect(programsServiceMock.getProgramsForWhichCanManageProjects(refreshedUser)).andReturn(Lists.newArrayList(new Program()));

        EasyMock.replay(userDAOMock, programsServiceMock);
        RegisteredUser user = userService.getCurrentUser();
        EasyMock.verify(userDAOMock, programsServiceMock);

        assertSame(refreshedUser, user);
        assertTrue(refreshedUser.isCanManageProjects());
    }

    @Test
    public void shouldAddRoleToUser() {
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.ADMINISTRATOR)).andReturn(new RoleBuilder().id(Authority.ADMINISTRATOR).build());
        EasyMock.replay(roleDAOMock);
        RegisteredUser user = new RegisteredUserBuilder().build();
        userService.addRoleToUser(user, Authority.ADMINISTRATOR);
        assertEquals(1, user.getRoles().size());
        assertEquals(Authority.ADMINISTRATOR, user.getRoles().get(0).getId());
    }

    @Test
    public void shouldSaveSelectedUser() {
        RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
        userDAOMock.save(selectedUser);
        Program selectedProgram = new ProgramBuilder().id(4).build();

        EasyMock.replay(userDAOMock);
        userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram);
        EasyMock.verify(userDAOMock);
    }

    @Test
    public void shouldSetApplicationsFiltering() {
        ApplicationsFiltering filtering = new ApplicationsFilteringBuilder().build();
        RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).filtering(filtering).build();

        userDAOMock.save(selectedUser);
        EasyMock.expect(filteringDAOMock.merge(filtering)).andReturn(filtering);

        replay(userDAOMock, filteringDAOMock);
        userService.setFiltering(selectedUser, filtering);
        verify(userDAOMock, filteringDAOMock);

        assertSame(filtering, selectedUser.getFiltering());
    }

    @Test
    public void shouldAddUserRoleAdminIfNotAlreadyAdminAndAdminInNewRoles() {
        RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
        Program selectedProgram = new ProgramBuilder().id(4).build();
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.ADMINISTRATOR)).andReturn(new RoleBuilder().id(Authority.ADMINISTRATOR).build()).anyTimes();

        replay(roleDAOMock);
        userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.ADMINISTRATOR);
        verify(roleDAOMock);

        assertTrue(selectedUser.isInRole(Authority.ADMINISTRATOR));
    }

    @Test
    public void shouldAddUserRoleApproverIfNotAlreadyApproverAndAproverInNewRoles() {
        RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
        Program selectedProgram = new ProgramBuilder().id(4).build();
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.APPROVER)).andReturn(new RoleBuilder().id(Authority.APPROVER).build()).anyTimes();

        EasyMock.replay(roleDAOMock);
        userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.APPROVER);
        assertTrue(selectedUser.isInRole(Authority.APPROVER));
    }

    @Test
    public void shouldAddUserRoleReviewerIfNotAlreadyRevieweAndRevieweInNewRoles() {
        RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
        Program selectedProgram = new ProgramBuilder().id(4).build();
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.REVIEWER)).andReturn(new RoleBuilder().id(Authority.REVIEWER).build()).anyTimes();

        replay(roleDAOMock);
        userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.REVIEWER);
        verify(roleDAOMock);

        assertTrue(selectedUser.isInRole(Authority.REVIEWER));
    }

    @Test
    public void shouldAddUserRoleInterviewerIfNotAlreadyInterviewerAndRevieweInNewRoles() {
        RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
        Program selectedProgram = new ProgramBuilder().id(4).build();
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.INTERVIEWER)).andReturn(new RoleBuilder().id(Authority.INTERVIEWER).build()).anyTimes();

        replay(roleDAOMock);
        userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.INTERVIEWER);
        verify(roleDAOMock);

        assertTrue(selectedUser.isInRole(Authority.INTERVIEWER));
    }

    @Test
    public void shouldAddUserRoleSupervisorIfNotAlreadySupervisorAndRevieweInNewRoles() {
        RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
        Program selectedProgram = new ProgramBuilder().id(4).build();
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.SUPERVISOR)).andReturn(new RoleBuilder().id(Authority.SUPERVISOR).build()).anyTimes();

        replay(roleDAOMock);
        userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.SUPERVISOR);
        verify(roleDAOMock);

        assertTrue(selectedUser.isInRole(Authority.SUPERVISOR));
    }

    @Test
    public void shouldAddUserRoleSuperAdmimnIfNotAlreadySuperadminAndSuperadminInNewRoles() {
        RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
        Program selectedProgram = new ProgramBuilder().id(4).build();
        Role role = new RoleBuilder().id(Authority.SUPERADMINISTRATOR).build();
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.SUPERADMINISTRATOR)).andReturn(role).anyTimes();

        replay(roleDAOMock);
        userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.SUPERADMINISTRATOR);
        verify(roleDAOMock);

        assertTrue(selectedUser.isInRole(Authority.SUPERADMINISTRATOR));
        EasyMock.verify(roleDAOMock);
    }

    @Test
    public void shouldNotRemoveSuperadminRoleIfNotInNewListAndUserIsNotSuperadmin() {
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER)).andReturn(new RoleBuilder().id(Authority.VIEWER).build());
        EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
        EasyMock.replay(currentUserMock, roleDAOMock);
        Role role = new RoleBuilder().id(Authority.SUPERADMINISTRATOR).build();
        RegisteredUser selectedUser = new RegisteredUserBuilder().role(role).id(1).build();

        Program selectedProgram = new ProgramBuilder().id(3).build();
        userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram);
        assertTrue(selectedUser.isInRole(Authority.SUPERADMINISTRATOR));
    }

    @Test
    public void shouldAddProgramToAdminListIfNew() {
        Program selectedProgram = new ProgramBuilder().id(1).build();
        RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
        Role role = new RoleBuilder().id(Authority.ADMINISTRATOR).build();
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.ADMINISTRATOR)).andReturn(role).anyTimes();
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER)).andReturn(new RoleBuilder().id(Authority.VIEWER).build()).anyTimes();

        EasyMock.replay(roleDAOMock);
        userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.ADMINISTRATOR);
        assertTrue(selectedUser.getProgramsOfWhichAdministrator().contains(selectedProgram));
        EasyMock.verify(roleDAOMock);
    }

    @Test
    public void shouldAddProgramToApproverlistIfNew() {
        Program selectedProgram = new ProgramBuilder().id(1).build();
        RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
        Role role = new RoleBuilder().id(Authority.APPROVER).build();
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.APPROVER)).andReturn(role).anyTimes();
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER)).andReturn(new RoleBuilder().id(Authority.VIEWER).build()).anyTimes();
        EasyMock.replay(roleDAOMock);
        userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.APPROVER);
        assertTrue(selectedUser.getProgramsOfWhichApprover().contains(selectedProgram));
        EasyMock.verify(roleDAOMock);
    }

    @Test
    public void shouldRemoveFromProgramsOfWhichAdministratorIfNoLongerInList() {
        Program selectedProgram = new ProgramBuilder().id(1).build();
        RegisteredUser selectedUser = new RegisteredUserBuilder().programsOfWhichAdministrator(selectedProgram).id(1).build();

        userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram);

        assertFalse(selectedUser.getProgramsOfWhichAdministrator().contains(selectedProgram));
    }

    @Test
    public void shouldRemoveFromProgramsOfWhichApproverIfNoLongerInList() {
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER)).andReturn(new RoleBuilder().id(Authority.VIEWER).build());
        EasyMock.replay(roleDAOMock);

        Program selectedProgram = new ProgramBuilder().id(1).build();
        RegisteredUser selectedUser = new RegisteredUserBuilder().programsOfWhichApprover(selectedProgram).id(1).build();
        userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram);
        assertFalse(selectedUser.getProgramsOfWhichApprover().contains(selectedProgram));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowISEwhenUserAlreadyExistsForNewUserInProgramme() {
        RegisteredUser existingUser = new RegisteredUserBuilder().id(1).build();

        EasyMock.expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(existingUser);
        EasyMock.replay(userDAOMock);
        userService.createNewUserForProgramme("la", "le", "some@email.com", new ProgramBuilder().id(4).build());
    }

    @Test
    public void shouldCreateUserAndWithRolesInProgramme() {
        Program program = new ProgramBuilder().id(4).build();
        RegisteredUser newUser = new RegisteredUserBuilder().id(5).build();

        EasyMock.expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(null);

        EasyMock.expect(
                userFactoryMock.createNewUserInRoles("la", "le", "some@email.com",
                        Arrays.asList(Authority.SUPERADMINISTRATOR, Authority.ADMINISTRATOR, Authority.APPROVER, Authority.VIEWER))).andReturn(newUser);

        userDAOMock.save(newUser);
        EasyMock.expectLastCall().andDelegateTo(new CheckProgrammeAndSimulateSaveDAO(program));

        Role role_1 = new RoleBuilder().build();
        Role role_2 = new RoleBuilder().build();
        Role role_3 = new RoleBuilder().build();
        Role role_6 = new RoleBuilder().build();
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.SUPERADMINISTRATOR)).andReturn(role_1);
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.ADMINISTRATOR)).andReturn(role_2);
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.APPROVER)).andReturn(role_3);
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER)).andReturn(role_6);

        EasyMock.replay(userDAOMock, roleDAOMock, userFactoryMock);

        RegisteredUser createdUser = userServiceWithCurrentUserOverride.createNewUserForProgramme("la", "le", "some@email.com", program,
                Authority.SUPERADMINISTRATOR, Authority.ADMINISTRATOR, Authority.APPROVER);

        EasyMock.verify(userDAOMock, roleDAOMock, userFactoryMock);
        assertEquals(newUser, createdUser);

        assertTrue(createdUser.getProgramsOfWhichAdministrator().contains(program));
        assertTrue(createdUser.getProgramsOfWhichApprover().contains(program));

        assertEquals(4, createdUser.getPendingRoleNotifications().size());

        PendingRoleNotification pendingRoleNotification = createdUser.getPendingRoleNotifications().get(0);
        assertEquals(role_1, pendingRoleNotification.getRole());
        assertNull(pendingRoleNotification.getProgram());
        assertEquals(currentUserMock, pendingRoleNotification.getAddedByUser());

        pendingRoleNotification = createdUser.getPendingRoleNotifications().get(1);
        assertEquals(role_2, pendingRoleNotification.getRole());
        assertEquals(program, pendingRoleNotification.getProgram());
        assertEquals(currentUserMock, pendingRoleNotification.getAddedByUser());

        pendingRoleNotification = createdUser.getPendingRoleNotifications().get(2);
        assertEquals(role_3, pendingRoleNotification.getRole());
        assertEquals(program, pendingRoleNotification.getProgram());
        assertEquals(currentUserMock, pendingRoleNotification.getAddedByUser());

        pendingRoleNotification = createdUser.getPendingRoleNotifications().get(3);
        assertEquals(role_6, pendingRoleNotification.getRole());
        assertEquals(program, pendingRoleNotification.getProgram());
        assertEquals(currentUserMock, pendingRoleNotification.getAddedByUser());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowISEwhenUserAlreadyExistsForNewUserNotInProgramme() {
        RegisteredUser existingUser = new RegisteredUserBuilder().id(1).build();

        EasyMock.expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(existingUser);
        EasyMock.replay(userDAOMock);
        userService.createNewUserInRole("la", "le", "some@email.com", null, null, Authority.APPROVER);
    }

    @Test
    public void shouldCreateUserAndWithRolesNotInAnyProgramme() {

        EasyMock.expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(null);
        RegisteredUser newUser = new RegisteredUserBuilder().id(5).build();
        EasyMock.expect(userFactoryMock.createNewUserInRoles("la", "le", "some@email.com", Authority.REVIEWER)).andReturn(newUser);
        userDAOMock.save(newUser);

        EasyMock.replay(userDAOMock, userFactoryMock);

        RegisteredUser createdUser = userServiceWithCurrentUserOverride.createNewUserInRole("la", "le", "some@email.com", null, null, Authority.REVIEWER);

        EasyMock.verify(userDAOMock, userFactoryMock);
        assertEquals(newUser, createdUser);

        assertTrue(createdUser.getProgramsOfWhichAdministrator().isEmpty());
        assertTrue(createdUser.getProgramsOfWhichApprover().isEmpty());

        assertTrue(createdUser.getPendingRoleNotifications().isEmpty());
    }

    @Test
    public void shouldCreateUserWithDirectToLinkToAddReview() {

        EasyMock.expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(null);
        RegisteredUser newUser = new RegisteredUserBuilder().id(5).build();
        EasyMock.expect(userFactoryMock.createNewUserInRoles("la", "le", "some@email.com", Authority.REVIEWER)).andReturn(newUser);
        userDAOMock.save(newUser);

        EasyMock.replay(userDAOMock, userFactoryMock);

        RegisteredUser createdUser = userServiceWithCurrentUserOverride.createNewUserInRole("la", "le", "some@email.com", DirectURLsEnum.ADD_REVIEW,
                new ApplicationFormBuilder().id(1).applicationNumber("bob").build(), Authority.REVIEWER);

        EasyMock.verify(userDAOMock, userFactoryMock);
        assertEquals(newUser, createdUser);
        assertEquals("/reviewFeedback?applicationId=bob", createdUser.getDirectToUrl());

        assertTrue(createdUser.getProgramsOfWhichAdministrator().isEmpty());
        assertTrue(createdUser.getProgramsOfWhichApprover().isEmpty());

        assertTrue(createdUser.getPendingRoleNotifications().isEmpty());
    }

    @Test
    public void shouldCreateUserWithDirectToLinkToViewapplication() {

        EasyMock.expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(null);
        RegisteredUser newUser = new RegisteredUserBuilder().id(5).build();
        EasyMock.expect(userFactoryMock.createNewUserInRoles("la", "le", "some@email.com", Authority.INTERVIEWER)).andReturn(newUser);
        userDAOMock.save(newUser);

        EasyMock.replay(userDAOMock, userFactoryMock);

        RegisteredUser createdUser = userServiceWithCurrentUserOverride.createNewUserInRole("la", "le", "some@email.com",
                DirectURLsEnum.VIEW_APPLIATION_PRIOR_TO_INTERVIEW, new ApplicationFormBuilder().id(1).applicationNumber("bob").build(), Authority.INTERVIEWER);

        EasyMock.verify(userDAOMock, userFactoryMock);
        assertEquals(newUser, createdUser);
        assertEquals("/application?view=view&applicationId=bob", createdUser.getDirectToUrl());

        assertTrue(createdUser.getProgramsOfWhichAdministrator().isEmpty());
        assertTrue(createdUser.getProgramsOfWhichApprover().isEmpty());

        assertTrue(createdUser.getPendingRoleNotifications().isEmpty());
    }

    @Test
    public void shouldGetAllPreviousInterviewersOfProgam() {
        RegisteredUser userOne = new RegisteredUserBuilder().id(5).build();
        RegisteredUser userTwo = new RegisteredUserBuilder().id(6).build();
        Program program = new ProgramBuilder().id(5).build();
        EasyMock.expect(userDAOMock.getAllPreviousInterviewersOfProgram(program)).andReturn(Arrays.asList(userOne, userTwo));
        EasyMock.replay(userDAOMock);

        List<RegisteredUser> users = userService.getAllPreviousInterviewersOfProgram(program);
        assertEquals(2, users.size());
        assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));
    }

    @Test
    public void shouldGetAllPreviousReviewersOfProgam() {
        RegisteredUser userOne = new RegisteredUserBuilder().id(5).build();
        RegisteredUser userTwo = new RegisteredUserBuilder().id(6).build();
        Program program = new ProgramBuilder().id(5).build();
        EasyMock.expect(userDAOMock.getAllPreviousReviewersOfProgram(program)).andReturn(Arrays.asList(userOne, userTwo));
        EasyMock.replay(userDAOMock);

        List<RegisteredUser> users = userService.getAllPreviousReviewersOfProgram(program);
        assertEquals(2, users.size());
        assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));
    }

    @Test
    public void shouldGetAllReviewersWillingToItnerview() {
        RegisteredUser userOne = new RegisteredUserBuilder().id(5).build();
        RegisteredUser userTwo = new RegisteredUserBuilder().id(6).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).build();
        EasyMock.expect(userDAOMock.getReviewersWillingToInterview(applicationForm)).andReturn(Arrays.asList(userOne, userTwo));
        EasyMock.replay(userDAOMock);

        List<RegisteredUser> users = userService.getReviewersWillingToInterview(applicationForm);
        assertEquals(2, users.size());
        assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));
    }

    @Test
    public void shouldGetAllPreviousSupervisorsOfProgam() {
        RegisteredUser userOne = new RegisteredUserBuilder().id(5).build();
        RegisteredUser userTwo = new RegisteredUserBuilder().id(6).build();
        Program program = new ProgramBuilder().id(5).build();
        EasyMock.expect(userDAOMock.getAllPreviousSupervisorsOfProgram(program)).andReturn(Arrays.asList(userOne, userTwo));
        EasyMock.replay(userDAOMock);

        List<RegisteredUser> users = userService.getAllPreviousSupervisorsOfProgram(program);
        assertEquals(2, users.size());
        assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));
    }

    @Test
    public void shouldUpdateCurrentUserAndSendEmailConfirmation() throws UnsupportedEncodingException {
        final RegisteredUser currentUser = new RegisteredUserBuilder().firstName("f").lastName("l").id(7).password("12").email("em").username("em").build();
        userServiceWithCurrentUserOverride = new UserService(userDAOMock, roleDAOMock, filteringDAOMock, userFactoryMock, encryptionUtilsMock, mailServiceMock,
                programsServiceMock, applicationFormUserRoleServiceMock) {

            @Override
            public RegisteredUser getCurrentUser() {
                return currentUser;
            }

        };
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("newpass")).andReturn("encryptednewpass");
        RegisteredUser userOne = new RegisteredUserBuilder().firstName("a").firstName2("a2").firstName3("a3").lastName("o").email("two").password("12")
                .newPassword("newpass").build();

        EasyMock.replay(encryptionUtilsMock);

        userServiceWithCurrentUserOverride.updateCurrentUser(userOne);
        EasyMock.verify(encryptionUtilsMock);
        assertEquals("two", currentUser.getUsername());
        assertEquals("two", currentUser.getEmail());
        assertEquals("a", currentUser.getFirstName());
        assertEquals("a2", currentUser.getFirstName2());
        assertEquals("a3", currentUser.getFirstName3());
        assertEquals("o", currentUser.getLastName());
        assertEquals("encryptednewpass", currentUser.getPassword());
    }

    @Test
    public void shouldNotChangePassIfPasswordIsBlank() {
        final RegisteredUser currentUser = new RegisteredUserBuilder().password("12").email("em").username("em").build();
        userServiceWithCurrentUserOverride = new UserService(userDAOMock, roleDAOMock, filteringDAOMock, userFactoryMock, encryptionUtilsMock, mailServiceMock,
                programsServiceMock, applicationFormUserRoleServiceMock) {

            @Override
            public RegisteredUser getCurrentUser() {
                return currentUser;
            }

        };
        RegisteredUser userOne = new RegisteredUserBuilder().username("one").email("two").password("").id(5).build();
        userServiceWithCurrentUserOverride.save(currentUser);
        userServiceWithCurrentUserOverride.updateCurrentUser(userOne);
        assertEquals("two", currentUser.getUsername());
        assertEquals("two", currentUser.getEmail());
        assertEquals("12", currentUser.getPassword());
    }

    @Test
    public void shouldReturnIfAccountsHaveAlreadyBeenLinked() {
        final RegisteredUser currentAccount = new RegisteredUserBuilder().id(1).accountNonExpired(true).accountNonLocked(true).enabled(true)
                .activationCode("abc").email("B@A.com").password("password").build();

        final RegisteredUser secondAccount = new RegisteredUserBuilder().id(2).accountNonExpired(true).accountNonLocked(true).enabled(true)
                .activationCode("abcd").email("A@B.com").password("password").build();

        secondAccount.setPrimaryAccount(currentAccount);

        userServiceWithCurrentUserOverride = new UserService(userDAOMock, roleDAOMock, filteringDAOMock, userFactoryMock, encryptionUtilsMock, mailServiceMock,
                programsServiceMock, applicationFormUserRoleServiceMock) {

            @Override
            public RegisteredUser getCurrentUser() {
                return currentAccount;
            }

            @Override
            public RegisteredUser getUserByEmail(String email) {
                return secondAccount;
            }
        };

        try {
            userServiceWithCurrentUserOverride.linkAccounts(secondAccount.getEmail());
        } catch (LinkAccountsException e) {
            fail();
        }
    }

    @Test(expected = LinkAccountsException.class)
    public void shouldReturnFalseIfAccountsDisabled() throws LinkAccountsException {
        final RegisteredUser currentAccount = new RegisteredUserBuilder().id(1).accountNonExpired(true).accountNonLocked(true).enabled(true)
                .activationCode("abc").email("B@A.com").password("password").build();

        final RegisteredUser secondAccount = new RegisteredUserBuilder().id(2).accountNonExpired(true).accountNonLocked(true).enabled(false)
                .activationCode("abcd").email("A@B.com").password("password").build();

        userServiceWithCurrentUserOverride = new UserService(userDAOMock, roleDAOMock, filteringDAOMock, userFactoryMock, encryptionUtilsMock, mailServiceMock,
                programsServiceMock, applicationFormUserRoleServiceMock) {

            @Override
            public RegisteredUser getCurrentUser() {
                return currentAccount;
            }

            @Override
            public RegisteredUser getUserByEmail(String email) {
                return secondAccount;
            }
        };

        userServiceWithCurrentUserOverride.linkAccounts(secondAccount.getEmail());
    }

    @Test(expected = LinkAccountsException.class)
    public void shouldReturnFalseIfAccountsIsExpired() throws LinkAccountsException {
        final RegisteredUser currentAccount = new RegisteredUserBuilder().id(1).accountNonExpired(true).accountNonLocked(true).enabled(true)
                .activationCode("abc").email("B@A.com").password("password").build();

        final RegisteredUser secondAccount = new RegisteredUserBuilder().id(2).accountNonExpired(false).accountNonLocked(true).enabled(true)
                .activationCode("abcd").email("A@B.com").password("password").build();

        userServiceWithCurrentUserOverride = new UserService(userDAOMock, roleDAOMock, filteringDAOMock, userFactoryMock, encryptionUtilsMock, mailServiceMock,
                programsServiceMock, applicationFormUserRoleServiceMock) {

            @Override
            public RegisteredUser getCurrentUser() {
                return currentAccount;
            }

            @Override
            public RegisteredUser getUserByEmail(String email) {
                return secondAccount;
            }
        };

        userServiceWithCurrentUserOverride.linkAccounts(secondAccount.getEmail());
    }

    @Before
    public void setUp() {
        encryptionUtilsMock = EasyMock.createMock(EncryptionUtils.class);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
        currentUser = new RegisteredUserBuilder().id(8).username("bob").role(new RoleBuilder().id(Authority.APPLICANT).build()).build();
        currentUserMock = EasyMock.createMock(RegisteredUser.class);
        authenticationToken.setDetails(currentUser);
        SecurityContextImpl secContext = new SecurityContextImpl();
        secContext.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(secContext);

        userDAOMock = EasyMock.createMock(UserDAO.class);
        roleDAOMock = EasyMock.createMock(RoleDAO.class);
        filteringDAOMock = EasyMock.createMock(ApplicationsFilteringDAO.class);
        userFactoryMock = EasyMock.createMock(UserFactory.class);
        mailServiceMock = createMock(MailSendingService.class);
        programsServiceMock = createMock(ProgramsService.class);
        applicationFormUserRoleServiceMock = createMock(ApplicationFormUserRoleService.class);

        userService = new UserService(userDAOMock, roleDAOMock, filteringDAOMock, userFactoryMock, encryptionUtilsMock, mailServiceMock, programsServiceMock,
                applicationFormUserRoleServiceMock);
        userServiceWithCurrentUserOverride = new UserService(userDAOMock, roleDAOMock, filteringDAOMock, userFactoryMock, encryptionUtilsMock, mailServiceMock,
                programsServiceMock, applicationFormUserRoleServiceMock) {

            @Override
            public RegisteredUser getCurrentUser() {
                return currentUserMock;
            }
        };
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    class CheckProgrammeAndSimulateSaveDAO extends UserDAO {
        private final Program expectedProgramme;

        public CheckProgrammeAndSimulateSaveDAO(Program programme) {
            super(null, null, null, null);
            this.expectedProgramme = programme;
        }

        @Override
        public void save(RegisteredUser user) {
            Assert.assertTrue(user.getProgramsOfWhichApprover().contains(expectedProgramme));
        }
    }
}
