package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
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
	public void shoudlGetRoleByInterviewerAuthority() {
		flushAndClearSession();
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		Role relaodedRole = roleDAO.getRoleByAuthority(Authority.INTERVIEWER);
		assertEquals(Authority.INTERVIEWER, relaodedRole.getId());
		assertNotNull(relaodedRole);
		
	}
	
	@Test
	public void shoudlGetRoleBySupervisorAuthority() {
		flushAndClearSession();
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		Role relaodedRole = roleDAO.getRoleByAuthority(Authority.SUPERVISOR);
		assertEquals(Authority.SUPERVISOR, relaodedRole.getId());
		assertNotNull(relaodedRole);
		
	}
	
}
