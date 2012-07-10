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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.PendingRoleNotificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class PendingRoleNotificationDAOTest extends AutomaticRollbackTestCase {

	
	private PendingRoleNotificationDAO pendingRoleNotificationDAO;
	private RoleDAO roleDAO;
	private Program program;
	private RegisteredUser user;
	
	@Test
	public void shouldReturAllPendingRoleNotifications(){
		PendingRoleNotification pendingNotificationOne = new PendingRoleNotificationBuilder().role(roleDAO.getRoleByAuthority(Authority.ADMINISTRATOR)).program(program).toPendingRoleNotification();
		PendingRoleNotification pendingNotificationTwo = new PendingRoleNotificationBuilder().role(roleDAO.getRoleByAuthority(Authority.REFEREE)).program(program).toPendingRoleNotification();
		user.getPendingRoleNotifications().addAll(Arrays.asList(pendingNotificationOne, pendingNotificationTwo));
		sessionFactory.getCurrentSession().saveOrUpdate(user);
		
		flushAndClearSession();
		
		BigInteger numberOfPendingRoleNotifications = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("select count(*) from  PENDING_ROLE_NOTIFICATION").uniqueResult();
		List<PendingRoleNotification> pendingNotifications = pendingRoleNotificationDAO.getAllPendingRoleNotifications();
		assertEquals(numberOfPendingRoleNotifications.intValue(), pendingNotifications.size());
		assertTrue(pendingNotifications.containsAll(Arrays.asList(pendingNotificationOne, pendingNotificationTwo)));
	}

	@Test
	public void shouldDeletePendingNotification(){
		PendingRoleNotification pendingNotification = new PendingRoleNotificationBuilder().role(roleDAO.getRoleByAuthority(Authority.ADMINISTRATOR)).program(program).toPendingRoleNotification();
		
		user.getPendingRoleNotifications().addAll(Arrays.asList(pendingNotification));
		sessionFactory.getCurrentSession().saveOrUpdate(user);
		assertNotNull(pendingNotification.getId());
		flushAndClearSession();
		
		pendingRoleNotificationDAO.deletePendingRoleNotifcation(pendingNotification);
		flushAndClearSession();
		assertNull(sessionFactory.getCurrentSession().get(PendingRoleNotification.class, pendingNotification.getId()));
	}

	@Before
	public void setUp(){
		super.setUp();
		pendingRoleNotificationDAO = new PendingRoleNotificationDAO(sessionFactory);
		roleDAO = new RoleDAO(sessionFactory);
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		save(user);

		program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();
		save(program);
		flushAndClearSession();
	}
}
