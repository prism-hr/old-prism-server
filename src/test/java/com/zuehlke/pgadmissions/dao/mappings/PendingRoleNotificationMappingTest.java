package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.PendingRoleNotificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class PendingRoleNotificationMappingTest extends AutomaticRollbackTestCase {

	private Program program;
	private Role reviewerRole;
	private RegisteredUser creatingUser;

	@Test
	public void shouldSaveAndLoadPendingRoleNotification() throws ParseException {

		PendingRoleNotification notificationRecord = new PendingRoleNotificationBuilder().role(reviewerRole).program(program).addedByUser(creatingUser).toPendingRoleNotification();
		sessionFactory.getCurrentSession().saveOrUpdate(notificationRecord);
		assertNotNull(notificationRecord.getId());
		PendingRoleNotification reloadedPendingRoleNotification = (PendingRoleNotification) sessionFactory.getCurrentSession().get(
				PendingRoleNotification.class, notificationRecord.getId());
		assertSame(notificationRecord, reloadedPendingRoleNotification);

		flushAndClearSession();
		reloadedPendingRoleNotification = (PendingRoleNotification) sessionFactory.getCurrentSession().get(PendingRoleNotification.class,
				notificationRecord.getId());
		assertNotSame(notificationRecord, reloadedPendingRoleNotification);
		assertEquals(notificationRecord, reloadedPendingRoleNotification);

		assertEquals(reviewerRole, reloadedPendingRoleNotification.getRole());
		assertEquals(program, reloadedPendingRoleNotification.getProgram());
		assertEquals(creatingUser, reloadedPendingRoleNotification.getAddedByUser());

	}

	@Test
	public void shouldLoadUserForPendingRoleNotification() throws ParseException {

		PendingRoleNotification pendingNotification = new PendingRoleNotificationBuilder().role(reviewerRole).program(program).toPendingRoleNotification();
		sessionFactory.getCurrentSession().saveOrUpdate(pendingNotification);

		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).pendingRoleNotifications(pendingNotification).toUser();
		save(user);
		flushAndClearSession();
		PendingRoleNotification reloadedRecord = (PendingRoleNotification) sessionFactory.getCurrentSession().get(PendingRoleNotification.class,
				pendingNotification.getId());

		assertEquals(user, reloadedRecord.getUser());

	}

	@Before
	public void setUp() {
		super.setUp();
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		reviewerRole = roleDAO.getRoleByAuthority(Authority.REVIEWER);

		program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();
		save(program);
		
		creatingUser = new RegisteredUserBuilder().firstName("Hanna").lastName("Doe").email("email@test.com").username("username2").password("password2")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		save(creatingUser);
		flushAndClearSession();
	}
}
