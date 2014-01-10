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
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.google.common.io.CharStreams;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.NotificationsDuration;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.PendingRoleNotificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.ReminderType;

public class UserDAOTest extends AutomaticRollbackTestCase {

    private UserDAO userDAO;

    private ReminderIntervalDAO reminderIntervalDAOMock;

    private NotificationsDurationDAO notificationsDurationDAOMock;

    private ApplicationContext applicationContextMock;

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
        // clear out whatever test data is in there -remember, it will all be
        // rolled back!
        sessionFactory.getCurrentSession().createSQLQuery("delete from PENDING_ROLE_NOTIFICATION").executeUpdate();
        sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_FORM_ACTION_OPTIONAL").executeUpdate();
        sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_FORM_ACTION_REQUIRED").executeUpdate();
        sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_FORM_USER_ROLE").executeUpdate();
        sessionFactory.getCurrentSession().createSQLQuery("delete from USER_ROLE_LINK").executeUpdate();
        sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_ROLE").executeUpdate();

        Role roleOne = new RoleBuilder().id(Authority.APPLICANT).build();
        Role roleTwo = new RoleBuilder().id(Authority.ADMINISTRATOR).build();
        save(roleOne, roleTwo);
        flushAndClearSession();

        RegisteredUser userOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).role(roleOne).build();
        RegisteredUser userTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("otherusername")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).roles(roleOne, roleTwo)
                .build();

        save(userOne, userTwo);

        flushAndClearSession();

        List<RegisteredUser> usersInRole = userDAO.getUsersInRole(roleTwo);
        assertEquals(1, usersInRole.size());
        assertEquals(userTwo.getId(), usersInRole.get(0).getId());

        usersInRole = userDAO.getUsersInRole(roleOne);
        assertEquals(2, usersInRole.size());
        assertTrue(listContainsId(userOne, usersInRole));
        assertTrue(listContainsId(userTwo, usersInRole));
    }

    @Test
    public void shouldGetUsersByProgramme() {

        Program programOne = new ProgramBuilder().code("111111").title("hello").build();
        Program programTwo = new ProgramBuilder().code("222222").title("hello").build();

        save(programOne, programTwo);

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

        Program program = new ProgramBuilder().code("doesntexist").title("another title").build();
        save(program);

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

        Program program = new ProgramBuilder().code("doesntexist").title("another title").build();
        save(program);

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

        Program program = new ProgramBuilder().code("doesntexist").title("another title").build();
        save(program);

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

        Program program = new ProgramBuilder().code("doesntexist").title("another title").build();
        save(program);

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

        URL resource = Resources.getResource("sql/get_users_due_to_update_notification.sql");
        userDAO.setGetUsersDueToUpdateNotificationSql(CharStreams.toString(new InputStreamReader(resource.openStream())));

        userDAO.getUsersForUpdateNotification();
    }

    @Test
    public void shouldGetPotentialUsersDueToTaskNotificationSql() throws IOException {
        ReminderInterval reminderInterval = new ReminderInterval();
        reminderInterval.setDuration(8);
        reminderInterval.setUnit(DurationUnitEnum.DAYS);
        EasyMock.expect(reminderIntervalDAOMock.getReminderInterval(ReminderType.TASK)).andReturn(reminderInterval);

        URL resource = Resources.getResource("sql/get_potential_users_due_to_task_notification.sql");
        userDAO.setGetPotentialUsersDueToTaskNotificationSql(CharStreams.toString(new InputStreamReader(resource.openStream())));

        EasyMock.replay(reminderIntervalDAOMock);
        userDAO.getPotentialUsersForTaskNotification();
        EasyMock.verify(reminderIntervalDAOMock);
    }

    @Test
    public void shouldGetPotentialUsersDueToTaskReminderSql() throws IOException {
        ReminderInterval reminderInterval = new ReminderInterval();
        reminderInterval.setDuration(8);
        reminderInterval.setUnit(DurationUnitEnum.DAYS);
        NotificationsDuration notificationsDuration = new NotificationsDuration();
        notificationsDuration.setDuration(16);
        notificationsDuration.setUnit(DurationUnitEnum.DAYS);
        expect(reminderIntervalDAOMock.getReminderInterval(ReminderType.TASK)).andReturn(reminderInterval);
        expect(notificationsDurationDAOMock.getNotificationsDuration()).andReturn(notificationsDuration);

        URL resource = Resources.getResource("sql/get_potential_users_due_to_task_reminder.sql");
        userDAO.setGetPotentialUsersDueToTaskReminderSql(CharStreams.toString(new InputStreamReader(resource.openStream())));

        EasyMock.replay(reminderIntervalDAOMock, notificationsDurationDAOMock);
        userDAO.getPotentialUsersForTaskReminder();
        EasyMock.verify(reminderIntervalDAOMock, notificationsDurationDAOMock);
    }
    
    @Test
    public void shouldGetAllSuperAdministrators(){
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role superadministratorRole = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);
        
        RegisteredUser superadmin = new RegisteredUserBuilder().role(superadministratorRole).firstName("Jane").lastName("Doe").email("somethingelse@test.com").username("somethingelse")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        sessionFactory.getCurrentSession().save(superadmin);
        
        List<RegisteredUser> superadministrators = userDAO.getSuperadministrators();
        assertTrue(listContainsId(superadmin, superadministrators));
    }
    
    @Test
    public void shouldGetAllAdmitters(){
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role admitterRole = roleDAO.getRoleByAuthority(Authority.ADMITTER);
        
        RegisteredUser admitter = new RegisteredUserBuilder().role(admitterRole).firstName("Jane").lastName("Doe").email("somethingelse@test.com").username("somethingelse")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        sessionFactory.getCurrentSession().save(admitter);
        
        List<RegisteredUser> admitters = userDAO.getAdmitters();
        assertTrue(listContainsId(admitter, admitters));
    }

    @Before
    public void prepare() {
        reminderIntervalDAOMock = EasyMock.createMock(ReminderIntervalDAO.class);
        notificationsDurationDAOMock = EasyMock.createMock(NotificationsDurationDAO.class);
        applicationContextMock = EasyMock.createMock(ApplicationContext.class);
        userDAO = new UserDAO(sessionFactory, reminderIntervalDAOMock, notificationsDurationDAOMock, applicationContextMock);
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
