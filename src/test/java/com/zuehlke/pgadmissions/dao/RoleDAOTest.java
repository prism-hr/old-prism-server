package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class RoleDAOTest extends AutomaticRollbackTestCase {

	
	@Test(expected = NullPointerException.class)
	public void shouldThrowNullPointerException() {
		RoleDAO roleDAO = new RoleDAO();
		roleDAO.getRoleByAuthority(Authority.ADMINISTRATOR);
	}
	
	@Test
	public void shoudlGetRoleByAuthority() {
		flushAndClearSession();
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		Role relaodedRole = roleDAO.getRoleByAuthority(Authority.APPLICANT);
		assertNotNull(relaodedRole);

	}

}
