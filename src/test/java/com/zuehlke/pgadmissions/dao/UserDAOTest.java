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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.NotificationsDuration;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.builders.PendingRoleNotificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.domain.builders.UserNotificationListBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.InstitutionState;
import com.zuehlke.pgadmissions.domain.enums.ReminderType;
import com.zuehlke.pgadmissions.domain.helpers.NotificationListTestScenario;
import com.zuehlke.pgadmissions.domain.helpers.UserTestHarness;

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

        User user = new UserBuilder().firstName("Jane").lastName("Doe").email("email2@test.com").activationCode("kod_aktywacyjny").userAccount(new UserAccount().withEnabled(false).withPassword("dupa")).build();

        assertNull(user.getId());

        userDAO.save(user);

        assertNotNull(user.getId());
        Integer id = user.getId();
        User reloadedUser = userDAO.getById(id);
        assertSame(user, reloadedUser);

        flushAndClearSession();

        reloadedUser = userDAO.getById(id);
        assertNotSame(user, reloadedUser);
        assertEquals(user.getId(), reloadedUser.getId());

    }

    @Test
    public void shouldFindUsersByUsername() throws Exception {

        User userOne = new UserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .userAccount(new UserAccount().withPassword("password").withEnabled(true)).build();
        User userTwo = new UserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .userAccount(new UserAccount().withPassword("password").withEnabled(true)).build();

        save(userOne, userTwo);

        flushAndClearSession();

        User foundUser = userDAO.getUserByUsername("username");
        assertEquals(userOne.getId(), foundUser.getId());

    }

    @Test
    public void shouldFindUsersByActivationCode() throws Exception {

        User userOne = new UserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .userAccount(new UserAccount().withPassword("password").withEnabled(false)).activationCode("xyz").build();
        User userTwo = new UserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .userAccount(new UserAccount().withPassword("password").withEnabled(false)).activationCode("def").build();

        save(userOne, userTwo);

        flushAndClearSession();

        User foundUser = userDAO.getUserByActivationCode("xyz");
        assertEquals(userOne.getId(), foundUser.getId());

    }

    @Test
    public void shouldFindDisabledUsersByEmail() throws Exception {
        User userOne = new UserBuilder().firstName("Jane").lastName("Doe").email("email1@test.com")
                .userAccount(new UserAccount().withPassword("password").withEnabled(false)).activationCode("xyz").build();

        User userTwo = new UserBuilder().firstName("Jane").lastName("Doe").email("email1@test.com")
                .userAccount(new UserAccount().withPassword("password").withEnabled(true)).activationCode("def").build();

        save(userOne, userTwo);

        flushAndClearSession();

        User foundUser = userDAO.getDisabledUserByEmail("email1@test.com");
        assertEquals(userOne.getId(), foundUser.getId());
    }

    @Test
    public void shouldReturnUserWithPendingNotifications() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role reviewerRole = roleDAO.getById(Authority.APPLICATION_REVIEWER);
        Role interviewerRole = roleDAO.getById(Authority.APPLICATION_INTERVIEWER);

        Institution institution = new QualificationInstitutionBuilder().code("code").name("a10").domicileCode("AE")
                .state(InstitutionState.INSTITUTION_APPROVED).build();
        Program program = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SYSTEM_ADMINISTRATOR)).code("doesntexist")
                .title("another title").institution(institution).build();
        save(institution, program);

        PendingRoleNotification pendingOne = new PendingRoleNotificationBuilder().role(reviewerRole).program(program).build();
        PendingRoleNotification pendingTwo = new PendingRoleNotificationBuilder().role(interviewerRole).program(program).build();

        User user = new UserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").userAccount(new UserAccount().withEnabled(false))
                .pendingRoleNotifications(pendingOne, pendingTwo).build();
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
        Role reviewerRole = roleDAO.getById(Authority.APPLICATION_REVIEWER);
        Role interviewerRole = roleDAO.getById(Authority.APPLICATION_INTERVIEWER);

        Institution institution = new QualificationInstitutionBuilder().code("code").name("a66").domicileCode("AE")
                .state(InstitutionState.INSTITUTION_APPROVED).build();
        Program program = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SYSTEM_ADMINISTRATOR)).code("doesntexist")
                .title("another title").institution(institution).build();
        save(institution, program);

        PendingRoleNotification pendingOne = new PendingRoleNotificationBuilder().role(reviewerRole).program(program).build();
        PendingRoleNotification pendingTwo = new PendingRoleNotificationBuilder().role(interviewerRole).program(program).build();

        User user = new UserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").userAccount(new UserAccount().withEnabled(false))
                .pendingRoleNotifications(pendingOne, pendingTwo).build();
        save(user);
        flushAndClearSession();

        users = userDAO.getUsersIdsWithPendingRoleNotifications();
        assertEquals(previousNumberOfUsers + 1, users.size());
    }

    @Test
    public void shouldNotReturnUserWithPendingNotificationsIfDateIsNull() {
        List<Integer> users = userDAO.getUsersIdsWithPendingRoleNotifications();
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role reviewerRole = roleDAO.getById(Authority.APPLICATION_REVIEWER);
        Role interviewerRole = roleDAO.getById(Authority.APPLICATION_INTERVIEWER);

        Program program = (Program) sessionFactory.getCurrentSession().get(Program.class, 63);

        Date now = new Date();

        PendingRoleNotification pendingOne = new PendingRoleNotificationBuilder().role(reviewerRole).program(program).notificationDate(now).build();
        PendingRoleNotification pendingTwo = new PendingRoleNotificationBuilder().role(interviewerRole).program(program).notificationDate(now).build();

        User user = new UserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").userAccount(new UserAccount().withEnabled(false))
                .pendingRoleNotifications(pendingOne, pendingTwo).build();
        save(user);
        flushAndClearSession();

        users = userDAO.getUsersIdsWithPendingRoleNotifications();
        assertFalse(users.contains(user.getId()));
    }

    @Test
    public void shouldNotReturnEnalbedUserWithPendingNotifications() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role reviewerRole = roleDAO.getById(Authority.APPLICATION_REVIEWER);
        Role interviewerRole = roleDAO.getById(Authority.APPLICATION_INTERVIEWER);

        Program program = (Program) sessionFactory.getCurrentSession().get(Program.class, 63);

        PendingRoleNotification pendingOne = new PendingRoleNotificationBuilder().role(reviewerRole).program(program).build();
        PendingRoleNotification pendingTwo = new PendingRoleNotificationBuilder().role(interviewerRole).program(program).build();

        User user = new UserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").userAccount(new UserAccount().withEnabled(true))
                .pendingRoleNotifications(pendingOne, pendingTwo).build();
        save(user);
        flushAndClearSession();

        List<Integer> users = userDAO.getUsersIdsWithPendingRoleNotifications();
        assertFalse(users.contains(user.getId()));
    }

    @Test
    public void shouldNotReturnUserWithNoPendingNotifications() {

        User user = new UserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").userAccount(new UserAccount().withEnabled(false)).build();
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
        Role superadministratorRole = roleDAO.getById(Authority.SYSTEM_ADMINISTRATOR);

        User superadmin = new UserBuilder()
        // .role(superadministratorRole)
                .firstName("Jane").lastName("Doe").email("somethingelse@test.com").userAccount(new UserAccount().withEnabled(false)).build();
        sessionFactory.getCurrentSession().save(superadmin);

        List<User> superadministrators = userDAO.getSuperadministrators();
        assertTrue(listContainsId(superadmin, superadministrators));
    }

    @Test
    public void shouldGetAllAdmitters() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role admitterRole = roleDAO.getById(Authority.INSTITUTION_ADMITTER);

        User admitter = new UserBuilder()
        // .role(admitterRole)
                .firstName("Jane").lastName("Doe").email("somethingelse@test.com").userAccount(new UserAccount().withEnabled(false)).build();
        sessionFactory.getCurrentSession().save(admitter);

        List<User> admitters = userDAO.getAdmitters();
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

        HashMap<Integer, UserTestHarness> testInstances = userNotificationListBuilder.builtTestInstances();
        List<Integer> usersDueTaskReminder = userDAO.getUsersDueTaskReminder(notificationBaselineDate);
        List<Integer> usersDueTaskNotification = userDAO.getUsersDueTaskNotification(notificationBaselineDate);
        List<Integer> usersDueUpdateNotification = userDAO.getUsersDueUpdateNotification(notificationBaselineDate);
        List<Integer> usersDueOpportunityRequestNotification = userDAO.getUsersDueOpportunityRequestNotification(notificationBaselineDate);

        EasyMock.verify(reminderIntervalDAOMock, notificationsDurationDAOMock);

        int actualTaskReminderCount = 0;
        int actualTaskNotificationCount = 0;
        int actualUpdateNotificationCount = 0;
        int actualOpportunityRequestNotificationCount = 0;

        for (Integer user : usersDueTaskReminder) {
            if (testInstances.containsKey(user)) {
                assertEquals(NotificationListTestScenario.TASK_REMINDER_SUCCESS, testInstances.get(user).getNotificationListTestScenario());
                actualTaskReminderCount++;
            }
        }

        for (Integer user : usersDueTaskNotification) {
            if (testInstances.containsKey(user)) {
                assertEquals(NotificationListTestScenario.TASK_NOTIFICATION_SUCCESS, testInstances.get(user).getNotificationListTestScenario());
                actualTaskNotificationCount++;
            }
        }

        for (Integer user : usersDueUpdateNotification) {
            if (testInstances.containsKey(user)) {
                assertEquals(NotificationListTestScenario.UPDATE_NOTIFICATION_SUCCESS, testInstances.get(user).getNotificationListTestScenario());
                actualUpdateNotificationCount++;
            }
        }

        for (Integer user : usersDueOpportunityRequestNotification) {
            if (testInstances.containsKey(user)) {
                assertEquals(NotificationListTestScenario.OPPORTUNITY_REQUEST_NOTIFICATION_SUCCESS, testInstances.get(user).getNotificationListTestScenario());
                actualOpportunityRequestNotificationCount++;
            }
        }

        assertEquals(userNotificationListBuilder.getTaskReminderSuccessCount() / NOTIFICATION_TEST_ITERATIONS * 5, actualTaskReminderCount);
        assertEquals(userNotificationListBuilder.getTaskNotificationSuccessCount() / NOTIFICATION_TEST_ITERATIONS * 5, actualTaskNotificationCount);
        assertEquals(userNotificationListBuilder.getUpdateNotificationSuccessCount() / NOTIFICATION_TEST_ITERATIONS * 5, actualUpdateNotificationCount);
        assertEquals(userNotificationListBuilder.getOpportunityRequestNotificationSuccessCount() / NOTIFICATION_TEST_ITERATIONS * 5,
                actualOpportunityRequestNotificationCount);
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

        userNotificationListBuilder = new UserNotificationListBuilder(sessionFactory, notificationBaselineDate, NOTIFICATION_TEST_ITERATIONS,
                NOTIFICATION_REMINDER_INTERVAL, NOTIFICATION_EXPIRY_INTERVAL, testObjectProvider.getEnabledProgram());
    }

    private boolean listContainsId(User user, List<User> users) {
        for (User entry : users) {
            if (user.getId().equals(entry.getId())) {
                return true;
            }
        }
        return false;
    }
}
