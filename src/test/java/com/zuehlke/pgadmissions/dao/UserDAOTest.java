package com.zuehlke.pgadmissions.dao;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.NotificationsDuration;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.PendingRoleNotificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.UserNotificationListBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.ReminderType;
import com.zuehlke.pgadmissions.domain.helpers.NotificationListTestScenario;
import com.zuehlke.pgadmissions.domain.helpers.RegisteredUserTestHarness;

public class UserDAOTest extends AutomaticRollbackTestCase {
    
    private static final int NOTIFICATION_TEST_ITERATIONS = 10;

    private static final int NOTIFICATION_REMINDER_INTERVAL = 8;

    private static final int NOTIFICATION_EXPIRY_INTERVAL = 16;

    private Date notificationBaselineDate;

    private UserDAO userDAO;

    private RoleDAO roleDAO;

    private ReminderIntervalDAO reminderIntervalDAOMock;

    private NotificationsDurationDAO notificationsDurationDAOMock;

    private UserNotificationListBuilder userNotificationListBuilder;

    @Test
    public void shouldSaveAndLoadUser() throws Exception {

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        assertNull(user.getId());

        userDAO.save(user);

        assertNotNull(user.getId());
        Integer id = user.getId();
        RegisteredUser reloadedUser = userDAO.get(id);
        assertSame(user, reloadedUser);

        flushAndClearSession();

        reloadedUser = userDAO.get(id);
        assertNotSame(user, reloadedUser);
        assertEquals(user.getId(), reloadedUser.getId());

    }

    @Test
    public void shouldFindUsersByUsername() throws Exception {

        RegisteredUser userOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();
        RegisteredUser userTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("otherusername")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();

        save(userOne, userTwo);

        flushAndClearSession();

        RegisteredUser foundUser = userDAO.getUserByUsername("username");
        assertEquals(userOne.getId(), foundUser.getId());

    }

    @Test
    public void shouldFindUsersByActivationCode() throws Exception {

        RegisteredUser userOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).activationCode("xyz")
                .build();
        RegisteredUser userTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("otherusername")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).activationCode("def")
                .build();

        save(userOne, userTwo);

        flushAndClearSession();

        RegisteredUser foundUser = userDAO.getUserByActivationCode("xyz");
        assertEquals(userOne.getId(), foundUser.getId());

    }

    @Test
    public void shouldFindDisabledUsersByEmail() throws Exception {
        RegisteredUser userOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email1@test.com").username("username")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).activationCode("xyz")
                .build();

        RegisteredUser userTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email1@test.com").username("otherusername")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).activationCode("def").build();

        save(userOne, userTwo);

        flushAndClearSession();

        RegisteredUser foundUser = userDAO.getDisabledUserByEmail("email1@test.com");
        assertEquals(userOne.getId(), foundUser.getId());
    }

    @Test
    public void shouldGetUsersByRole() {
        Role roleOne = roleDAO.getRoleByAuthority(Authority.APPLICANT);
        if (roleOne == null) {
            roleOne = new RoleBuilder().id(Authority.APPLICANT).doSendUpdateNotification(false).build();
            save(roleOne);
        }
        Role roleTwo = roleDAO.getRoleByAuthority(Authority.ADMINISTRATOR);
        if (roleTwo == null) {
            roleTwo = new RoleBuilder().id(Authority.ADMINISTRATOR).doSendUpdateNotification(false).build();
            save(roleTwo);
        }

        RegisteredUser userOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).role(roleOne).build();
        RegisteredUser userTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("otherusername")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).roles(roleOne, roleTwo)
                .build();
        save(userOne, userTwo);
        List<Integer> testUserIds = Arrays.asList(userOne.getId(), userTwo.getId());

        int roleOneHitCounter = 0;
        for (RegisteredUser user : userDAO.getUsersInRole(roleOne)) {
            if (testUserIds.contains(user.getId())) {
                roleOneHitCounter++;
            }
        } 
        assertEquals(2, roleOneHitCounter);
   
        int roleTwoHitCounter = 0;
        for (RegisteredUser user : userDAO.getUsersInRole(roleTwo)) {
            if (testUserIds.contains(user.getId())) {
                roleTwoHitCounter++;
            }
        }
        assertEquals(1, roleTwoHitCounter);
        
    }

    @Test
    public void shouldGetUsersByProgramme() {
        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").countryCode("AE").enabled(true).build();
        Program programOne = new ProgramBuilder().code("111111").title("hello").institution(institution).build();
        Program programTwo = new ProgramBuilder().code("222222").title("hello").institution(institution).build();

        save(institution, programOne, programTwo);

        flushAndClearSession();

        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role superAdminRole = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);

        int numberOfExistingSuperAdminUsers = userDAO.getUsersInRole(superAdminRole).size();

        RegisteredUser superAdminOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username1")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).role(superAdminRole).build();

        RegisteredUser superAdminTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).roles(superAdminRole).build();

        RegisteredUser superAdminThree = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username3")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).roles(superAdminRole).build();

        RegisteredUser approverOne = new RegisteredUserBuilder().programsOfWhichApprover(programOne).firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username4").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();

        RegisteredUser approverTwo = new RegisteredUserBuilder().programsOfWhichApprover(programTwo).firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username5").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();

        RegisteredUser approverThree = new RegisteredUserBuilder().programsOfWhichApprover(programOne).firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username6").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(true).build();

        RegisteredUser administratorOne = new RegisteredUserBuilder().programsOfWhichAdministrator(programOne).firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username10").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(true).build();

        RegisteredUser administratorTwo = new RegisteredUserBuilder().programsOfWhichAdministrator(programTwo).firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username11").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(true).build();

        RegisteredUser administratorThree = new RegisteredUserBuilder().programsOfWhichAdministrator(programOne).firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username12").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(true).build();

        RegisteredUser viewer = new RegisteredUserBuilder().programsOfWhichViewer(programOne).programsOfWhichApprover(programOne).firstName("Jane")
                .lastName("Doe").email("email@test.com").username("username20").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(true).build();

        save(superAdminOne, superAdminTwo, superAdminThree, administratorOne, administratorThree, administratorTwo, approverOne, approverThree, approverTwo,
                viewer);

        flushAndClearSession();

        List<RegisteredUser> usersInProgram = userDAO.getUsersForProgram(programOne);
        assertEquals(numberOfExistingSuperAdminUsers + 8, usersInProgram.size());
        assertTrue(listContainsId(superAdminOne, usersInProgram));
        assertTrue(listContainsId(superAdminThree, usersInProgram));
        assertTrue(listContainsId(superAdminTwo, usersInProgram));
        assertTrue(listContainsId(approverOne, usersInProgram));
        assertTrue(listContainsId(approverThree, usersInProgram));
        assertTrue(listContainsId(administratorOne, usersInProgram));
        assertTrue(listContainsId(administratorThree, usersInProgram));
        assertTrue(listContainsId(viewer, usersInProgram));
    }

    @Test
    public void shouldReturnSuperAdmininistrator() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role superAdminRole = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);

        RegisteredUser superAdmin = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username1")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).role(superAdminRole).build();
        save(superAdmin);
        flushAndClearSession();
        List<RegisteredUser> users = userDAO.getInternalUsers();
        assertTrue(listContainsId(superAdmin, users));
    }

    @Test
    public void shouldReturnAdmininistrator() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role superAdminRole = roleDAO.getRoleByAuthority(Authority.ADMINISTRATOR);

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username1").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).role(superAdminRole).build();
        save(user);
        flushAndClearSession();
        List<RegisteredUser> users = userDAO.getInternalUsers();
        assertTrue(listContainsId(user, users));
    }

    @Test
    public void shouldReturnReviewer() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role superAdminRole = roleDAO.getRoleByAuthority(Authority.REVIEWER);

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username1").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).role(superAdminRole).build();
        save(user);
        flushAndClearSession();
        List<RegisteredUser> users = userDAO.getInternalUsers();
        assertTrue(listContainsId(user, users));
    }

    @Test
    public void shouldReturnInteverviweer() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role superAdminRole = roleDAO.getRoleByAuthority(Authority.INTERVIEWER);

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username1").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).role(superAdminRole).build();
        save(user);
        flushAndClearSession();
        List<RegisteredUser> users = userDAO.getInternalUsers();
        assertTrue(listContainsId(user, users));
    }

    @Test
    public void shouldReturnApprover() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role superAdminRole = roleDAO.getRoleByAuthority(Authority.APPROVER);

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username1").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).role(superAdminRole).build();
        save(user);
        flushAndClearSession();
        List<RegisteredUser> users = userDAO.getInternalUsers();
        assertTrue(listContainsId(user, users));
    }

    @Test
    public void shouldNotReturnApplicant() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role superAdminRole = roleDAO.getRoleByAuthority(Authority.APPLICANT);

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username1").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).role(superAdminRole).build();
        save(user);
        flushAndClearSession();
        List<RegisteredUser> users = userDAO.getInternalUsers();
        assertFalse(users.contains(user));
    }

    @Test
    public void shouldNotReturnReferee() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username1").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true)
                .role(roleDAO.getRoleByAuthority(Authority.REFEREE)).build();
        save(user);
        flushAndClearSession();
        List<RegisteredUser> users = userDAO.getInternalUsers();
        assertFalse(users.contains(user));
    }

    @Test
    public void shouldReturnEachUserOnlyOnce() {
        List<RegisteredUser> usersBefore = userDAO.getInternalUsers();
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username1").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true)
                .roles(roleDAO.getRoleByAuthority(Authority.APPROVER), roleDAO.getRoleByAuthority(Authority.REVIEWER)).build();
        save(user);
        flushAndClearSession();
        List<RegisteredUser> users = userDAO.getInternalUsers();
        assertEquals(usersBefore.size() + 1, users.size());
    }

    @Test
    public void shouldReturnUserWithPendingNotifications() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role reviewerRole = roleDAO.getRoleByAuthority(Authority.REVIEWER);
        Role interviewerRole = roleDAO.getRoleByAuthority(Authority.INTERVIEWER);

        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").countryCode("AE").enabled(true).build();
        Program program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();
        save(institution, program);

        PendingRoleNotification pendingOne = new PendingRoleNotificationBuilder().role(reviewerRole).program(program).build();
        PendingRoleNotification pendingTwo = new PendingRoleNotificationBuilder().role(interviewerRole).program(program).build();

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).pendingRoleNotifications(pendingOne, pendingTwo)
                .build();
        save(user);
        flushAndClearSession();

        List<Integer> users = userDAO.getUsersIdsWithPendingRoleNotifications();
        assertTrue(users.contains(user.getId()));
    }

    @Test
    public void shouldReturnUserWithPendingNotificationsOnlyOnce() {
        List<Integer> users = userDAO.getUsersIdsWithPendingRoleNotifications();
        int previousNumberOfUsers = users.size();
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role reviewerRole = roleDAO.getRoleByAuthority(Authority.REVIEWER);
        Role interviewerRole = roleDAO.getRoleByAuthority(Authority.INTERVIEWER);

        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").countryCode("AE").enabled(true).build();
        Program program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();
        save(institution, program);

        PendingRoleNotification pendingOne = new PendingRoleNotificationBuilder().role(reviewerRole).program(program).build();
        PendingRoleNotification pendingTwo = new PendingRoleNotificationBuilder().role(interviewerRole).program(program).build();

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).pendingRoleNotifications(pendingOne, pendingTwo)
                .build();
        save(user);
        flushAndClearSession();

        users = userDAO.getUsersIdsWithPendingRoleNotifications();
        assertEquals(previousNumberOfUsers + 1, users.size());
    }

    @Test
    public void shouldNotReturnUserWithPendingNotificationsIfDateIsNull() {
        List<Integer> users = userDAO.getUsersIdsWithPendingRoleNotifications();
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role reviewerRole = roleDAO.getRoleByAuthority(Authority.REVIEWER);
        Role interviewerRole = roleDAO.getRoleByAuthority(Authority.INTERVIEWER);

        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").countryCode("AE").enabled(true).build();
        Program program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();
        save(institution, program);

        Date now = new Date();

        PendingRoleNotification pendingOne = new PendingRoleNotificationBuilder().role(reviewerRole).program(program).notificationDate(now).build();
        PendingRoleNotification pendingTwo = new PendingRoleNotificationBuilder().role(interviewerRole).program(program).notificationDate(now).build();

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).pendingRoleNotifications(pendingOne, pendingTwo)
                .build();
        save(user);
        flushAndClearSession();

        users = userDAO.getUsersIdsWithPendingRoleNotifications();
        assertFalse(users.contains(user.getId()));
    }

    @Test
    public void shouldNotReturnEnalbedUserWithPendingNotifications() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role reviewerRole = roleDAO.getRoleByAuthority(Authority.REVIEWER);
        Role interviewerRole = roleDAO.getRoleByAuthority(Authority.INTERVIEWER);

        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").countryCode("AE").enabled(true).build();
        Program program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();
        save(institution, program);

        PendingRoleNotification pendingOne = new PendingRoleNotificationBuilder().role(reviewerRole).program(program).build();
        PendingRoleNotification pendingTwo = new PendingRoleNotificationBuilder().role(interviewerRole).program(program).build();

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).pendingRoleNotifications(pendingOne, pendingTwo)
                .build();
        save(user);
        flushAndClearSession();

        List<Integer> users = userDAO.getUsersIdsWithPendingRoleNotifications();
        assertFalse(users.contains(user.getId()));
    }

    @Test
    public void shouldNotReturnUserWithNoPendingNotifications() {

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        save(user);
        flushAndClearSession();

        List<Integer> users = userDAO.getUsersIdsWithPendingRoleNotifications();
        assertFalse(users.contains(user.getId()));
    }

    @Test
    public void shouldGetUsersForUpdateNotification() throws IOException {
        userDAO.getUsersDueUpdateNotification(notificationBaselineDate);
    }

    @Test
    public void shouldGetPotentialUsersDueToTaskNotification() throws IOException {
        ReminderInterval reminderInterval = new ReminderInterval();
        reminderInterval.setDuration(NOTIFICATION_REMINDER_INTERVAL);
        reminderInterval.setUnit(DurationUnitEnum.DAYS);
        NotificationsDuration notificationsDuration = new NotificationsDuration();
        notificationsDuration.setDuration(NOTIFICATION_EXPIRY_INTERVAL);
        notificationsDuration.setUnit(DurationUnitEnum.DAYS);
        expect(reminderIntervalDAOMock.getReminderInterval(ReminderType.TASK)).andReturn(reminderInterval);
        expect(notificationsDurationDAOMock.getNotificationsDuration()).andReturn(notificationsDuration);

        EasyMock.replay(reminderIntervalDAOMock, notificationsDurationDAOMock);
        userDAO.getUsersDueTaskNotification(notificationBaselineDate);
        EasyMock.verify(reminderIntervalDAOMock, notificationsDurationDAOMock);
    }

    @Test
    public void shouldGetPotentialUsersDueToTaskReminder() throws IOException {
        ReminderInterval reminderInterval = new ReminderInterval();
        reminderInterval.setDuration(NOTIFICATION_REMINDER_INTERVAL);
        reminderInterval.setUnit(DurationUnitEnum.DAYS);
        NotificationsDuration notificationsDuration = new NotificationsDuration();
        notificationsDuration.setDuration(NOTIFICATION_EXPIRY_INTERVAL);
        notificationsDuration.setUnit(DurationUnitEnum.DAYS);
        expect(reminderIntervalDAOMock.getReminderInterval(ReminderType.TASK)).andReturn(reminderInterval);
        expect(notificationsDurationDAOMock.getNotificationsDuration()).andReturn(notificationsDuration);

        EasyMock.replay(reminderIntervalDAOMock, notificationsDurationDAOMock);
        userDAO.getUsersDueTaskReminder(notificationBaselineDate);
        EasyMock.verify(reminderIntervalDAOMock, notificationsDurationDAOMock);
    }

    @Test
    public void shouldGetAllSuperAdministrators() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role superadministratorRole = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);

        RegisteredUser superadmin = new RegisteredUserBuilder().role(superadministratorRole).firstName("Jane").lastName("Doe").email("somethingelse@test.com")
                .username("somethingelse").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .build();
        sessionFactory.getCurrentSession().save(superadmin);

        List<RegisteredUser> superadministrators = userDAO.getSuperadministrators();
        assertTrue(listContainsId(superadmin, superadministrators));
    }

    @Test
    public void shouldGetAllAdmitters() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role admitterRole = roleDAO.getRoleByAuthority(Authority.ADMITTER);

        RegisteredUser admitter = new RegisteredUserBuilder().role(admitterRole).firstName("Jane").lastName("Doe").email("somethingelse@test.com")
                .username("somethingelse").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .build();
        sessionFactory.getCurrentSession().save(admitter);

        List<RegisteredUser> admitters = userDAO.getAdmitters();
        assertTrue(listContainsId(admitter, admitters));
    }

    @Test
    public void shouldGetCorrectUsersForNotifications() {
        ReminderInterval reminderInterval = new ReminderInterval();
        reminderInterval.setDuration(NOTIFICATION_REMINDER_INTERVAL);
        reminderInterval.setUnit(DurationUnitEnum.DAYS);
        NotificationsDuration notificationsDuration = new NotificationsDuration();
        notificationsDuration.setDuration(NOTIFICATION_EXPIRY_INTERVAL);
        notificationsDuration.setUnit(DurationUnitEnum.DAYS);
        expect(reminderIntervalDAOMock.getReminderInterval(ReminderType.TASK)).andReturn(reminderInterval).anyTimes();
        expect(notificationsDurationDAOMock.getNotificationsDuration()).andReturn(notificationsDuration).anyTimes();

        EasyMock.replay(reminderIntervalDAOMock, notificationsDurationDAOMock);

        HashMap<Integer, RegisteredUserTestHarness> testInstances = userNotificationListBuilder.builtTestInstances();
        List<Integer> usersDueTaskReminder = userDAO.getUsersDueTaskReminder(notificationBaselineDate);
        List<Integer> usersDueTaskNotification = userDAO.getUsersDueTaskNotification(notificationBaselineDate);
        List<Integer> usersDueUpdateNotification = userDAO.getUsersDueUpdateNotification(notificationBaselineDate);

        EasyMock.verify(reminderIntervalDAOMock, notificationsDurationDAOMock);
        
        int actualTaskReminderCount = 0;
        int actualTaskNotificationCount = 0;
        int actualUpdateNotificationCount = 0;

        for (Integer user : usersDueTaskReminder) {
            if (testInstances.containsKey(user)) {         
                assertEquals(NotificationListTestScenario.TASKREMINDERSUCCESS, testInstances.get(user).getNotificationListTestScenario());
                actualTaskReminderCount++;
            }
        }

        for (Integer user : usersDueTaskNotification) {
            if (testInstances.containsKey(user)) {
                assertEquals(NotificationListTestScenario.TASKNOTIFICATIONSUCCESS, testInstances.get(user).getNotificationListTestScenario());
                actualTaskNotificationCount++;
            }
        }

        for (Integer user : usersDueUpdateNotification) {
            if (testInstances.containsKey(user)) {
                assertEquals(NotificationListTestScenario.UPDATENOTIFICATIONSUCCESS, testInstances.get(user).getNotificationListTestScenario());
                actualUpdateNotificationCount++;
            }
        }
        
        assertEquals(userNotificationListBuilder.getTaskReminderSuccessCount() / NOTIFICATION_TEST_ITERATIONS, actualTaskReminderCount);
        assertEquals(userNotificationListBuilder.getTaskNotificationSuccessCount() / NOTIFICATION_TEST_ITERATIONS, actualTaskNotificationCount);
        assertEquals(userNotificationListBuilder.getUpdateNotificationSuccessCount() / NOTIFICATION_TEST_ITERATIONS, actualUpdateNotificationCount);
    }

    @Before
    public void prepare() {
        reminderIntervalDAOMock = EasyMock.createMock(ReminderIntervalDAO.class);
        notificationsDurationDAOMock = EasyMock.createMock(NotificationsDurationDAO.class);
        userDAO = new UserDAO(sessionFactory, reminderIntervalDAOMock, notificationsDurationDAOMock);
        roleDAO = new RoleDAO(sessionFactory);

        DateTime baseline = new DateTime(new Date());
        DateTime cleanBaseline = new DateTime(baseline.getYear(), baseline.getMonthOfYear(), baseline.getDayOfMonth(), 0, 0, 0);
        notificationBaselineDate = cleanBaseline.toDate();

        userNotificationListBuilder = new UserNotificationListBuilder(sessionFactory, notificationBaselineDate, NOTIFICATION_TEST_ITERATIONS, NOTIFICATION_REMINDER_INTERVAL, NOTIFICATION_EXPIRY_INTERVAL);
    }

    private boolean listContainsId(RegisteredUser user, List<RegisteredUser> users) {
        for (RegisteredUser entry : users) {
            if (user.getId().equals(entry.getId())) {
                return true;
            }
        }
        return false;
    }
}
