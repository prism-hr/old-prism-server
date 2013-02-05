package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class RoleMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadRole() {
		//clear out whatever test data is in there -remember, it will all be rolled back!
		sessionFactory.getCurrentSession().createSQLQuery("delete from PENDING_ROLE_NOTIFICATION").executeUpdate();
		sessionFactory.getCurrentSession().createSQLQuery("delete from USER_ROLE_LINK").executeUpdate();
		sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_ROLE").executeUpdate();
		flushAndClearSession();
		Role role = new Role();
		role.setAuthorityEnum(Authority.APPLICANT);

		sessionFactory.getCurrentSession().save(role);
		flushAndClearSession();

		assertNotNull(role.getId());
		Integer id = role.getId();

		Role reloadedFile = (Role) sessionFactory.getCurrentSession().get(Role.class, id);
		assertEquals(role.getId(), reloadedFile.getId());
		assertNotSame(role, reloadedFile);
		assertEquals(Authority.APPLICANT, role.getAuthorityEnum());

	}
}
