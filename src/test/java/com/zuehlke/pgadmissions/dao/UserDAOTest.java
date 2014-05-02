package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.builders.PendingRoleNotificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.TestData;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.utils.HibernateUtils;

public class UserDAOTest extends AutomaticRollbackTestCase {

    private UserDAO userDAO;

    @Test
    public void shouldSaveAndLoadUser() throws Exception {

        User user = new UserBuilder().firstName("Jane").lastName("Doe").email("email2@test.com").activationCode("kod_aktywacyjny")
                .userAccount(new UserAccount().withEnabled(false).withPassword("dupa")).build();

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
    public void shouldFindUsersByActivationCode() throws Exception {

        User userOne = new UserBuilder().firstName("Jane").lastName("Doe").email("email@test2.com")
                .userAccount(new UserAccount().withPassword("password").withEnabled(false)).activationCode("xyz").build();
        User userTwo = new UserBuilder().firstName("Jane").lastName("Doe").email("email@test3.com")
                .userAccount(new UserAccount().withPassword("password").withEnabled(false)).activationCode("def").build();

        save(userOne, userTwo);

        flushAndClearSession();

        User foundUser = userDAO.getUserByActivationCode("xyz");
        assertEquals(userOne.getId(), foundUser.getId());

    }

    @Test
    public void shouldFindDisabledUserByEmail() throws Exception {
        User user = TestData.aUser(TestData.aUserAccount().withEnabled(false)).withEmail("email1@test.com").withActivationCode("different_code");

        save(user);

        flushAndClearSession();

        User foundUser = userDAO.getDisabledUserByEmail("email1@test.com");
        assertEquals(user.getId(), foundUser.getId());
    }
    
    @Test
    public void shouldFindNotEnabledUserByEmail() throws Exception {
        User user = TestData.aUser(null).withEmail("email1@test.com").withActivationCode("different_code");
        
        save(user);
        
        flushAndClearSession();
        
        User foundUser = userDAO.getDisabledUserByEmail("email1@test.com");
        assertEquals(user.getId(), foundUser.getId());
    }

    @Test
    public void shouldReturnUserWithPendingNotifications() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role reviewerRole = roleDAO.getById(Authority.APPLICATION_REVIEWER);
        Role interviewerRole = roleDAO.getById(Authority.APPLICATION_INTERVIEWER);

        Institution institution = new Institution().withCode("code").withName("a10").withDomicileCode("AE")
                .withState(new State().withId(PrismState.INSTITUTION_APPROVED));
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

        Institution institution = new Institution().withCode("code").withName("a66").withDomicileCode("AE")
                .withState(new State().withId(PrismState.INSTITUTION_APPROVED));
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
    public void shouldNotReturnEnabledUserWithPendingNotifications() {
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

        User user = TestData.aUser(TestData.aUserAccount().withEnabled(false));
        save(user);
        flushAndClearSession();

        List<Integer> users = userDAO.getUsersIdsWithPendingRoleNotifications();
        assertFalse(users.contains(user.getId()));
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
        assertTrue(HibernateUtils.containsEntity(superadministrators, superadmin));
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
        assertTrue(HibernateUtils.containsEntity(admitters, admitter));
    }

    @Before
    public void prepare() {
        userDAO = new UserDAO(sessionFactory);
    }

}
