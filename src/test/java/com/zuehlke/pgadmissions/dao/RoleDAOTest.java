package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.*;
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
	public void shoudlGetRoleByApplicantAuthority() {
		flushAndClearSession();
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		Role relaodedRole = roleDAO.getRoleByAuthority(Authority.APPLICANT);
		assertNotNull(relaodedRole);

	}

	@Test
	public void shoudlGetRoleByAdminAuthority() {
		flushAndClearSession();
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		Role relaodedRole = roleDAO.getRoleByAuthority(Authority.ADMINISTRATOR);
		assertNotNull(relaodedRole);
		
	}
	
	@Test
	public void shoudlGetRoleByReviewerAuthority() {
		flushAndClearSession();
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		Role relaodedRole = roleDAO.getRoleByAuthority(Authority.REVIEWER);
		assertNotNull(relaodedRole);
		
	}
	
	@Test
	public void shoudlGetRoleBySuperadminAuthority() {
		flushAndClearSession();
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		Role relaodedRole = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);
		assertNotNull(relaodedRole);
		
	}
	
	@Test
	public void shoudlGetRoleByApproverAuthority() {
		flushAndClearSession();
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		Role relaodedRole = roleDAO.getRoleByAuthority(Authority.APPROVER);
		assertNotNull(relaodedRole);
		
	}
	@Test
	public void shoudlGetRoleByRefereeAuthority() {
		flushAndClearSession();
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		Role relaodedRole = roleDAO.getRoleByAuthority(Authority.REFEREE);
		assertEquals(Authority.REFEREE, relaodedRole.getAuthorityEnum());
		assertNotNull(relaodedRole);
		
	}
	
}
