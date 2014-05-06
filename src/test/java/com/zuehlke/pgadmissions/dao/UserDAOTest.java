package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.builders.TestData;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
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

    @Before
    public void prepare() {
        userDAO = new UserDAO(sessionFactory);
    }

}
