package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.builders.PendingRoleNotificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class PendingRoleNotificationDAOTest extends AutomaticRollbackTestCase {

    private PendingRoleNotificationDAO pendingRoleNotificationDAO;
    private RoleDAO roleDAO;
    private Program program;
    private User user;

    @Test
    public void shouldReturAllPendingRoleNotifications() {
        PendingRoleNotification pendingNotificationOne = new PendingRoleNotificationBuilder().role(roleDAO.getById(Authority.PROGRAM_ADMINISTRATOR)).program(program)
                .build();
        PendingRoleNotification pendingNotificationTwo = new PendingRoleNotificationBuilder().role(roleDAO.getById(Authority.APPLICATION_REFEREE)).program(program).build();
        user.getPendingRoleNotifications().addAll(Arrays.asList(pendingNotificationOne, pendingNotificationTwo));
        sessionFactory.getCurrentSession().saveOrUpdate(user);

        flushAndClearSession();

        BigInteger numberOfPendingRoleNotifications = (BigInteger) sessionFactory.getCurrentSession()
                .createSQLQuery("select count(*) from  PENDING_ROLE_NOTIFICATION").uniqueResult();
        List<PendingRoleNotification> pendingNotifications = pendingRoleNotificationDAO.getAllPendingRoleNotifications();

        assertEquals(numberOfPendingRoleNotifications.intValue(), pendingNotifications.size());
        assertTrue(listContainsId(pendingNotificationOne, pendingNotifications));
        assertTrue(listContainsId(pendingNotificationTwo, pendingNotifications));
    }

    @Test
    public void shouldDeletePendingNotification() {
        PendingRoleNotification pendingNotification = new PendingRoleNotificationBuilder().role(roleDAO.getById(Authority.PROGRAM_ADMINISTRATOR)).program(program)
                .build();

        user.getPendingRoleNotifications().addAll(Arrays.asList(pendingNotification));
        sessionFactory.getCurrentSession().saveOrUpdate(user);
        assertNotNull(pendingNotification.getId());
        flushAndClearSession();

        pendingRoleNotificationDAO.deletePendingRoleNotifcation(pendingNotification);
        flushAndClearSession();
        assertNull(sessionFactory.getCurrentSession().get(PendingRoleNotification.class, pendingNotification.getId()));
    }

    @Before
    public void initialise() {
        pendingRoleNotificationDAO = new PendingRoleNotificationDAO(sessionFactory);
        roleDAO = new RoleDAO(sessionFactory);
        user = new UserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").userAccount(new UserAccount().withEnabled(false))
                .build();
        save(user);
        flushAndClearSession();
        program = testObjectProvider.getEnabledProgram();
    }

    private boolean listContainsId(PendingRoleNotification notification, List<PendingRoleNotification> notifications) {
        for (PendingRoleNotification entry : notifications) {
            if (entry.getId().equals(notification.getId())) {
                return true;
            }
        }
        return false;
    }
}
