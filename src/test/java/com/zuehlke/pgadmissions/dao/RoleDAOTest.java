package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class RoleDAOTest extends AutomaticRollbackTestCase {

	
	@Test(expected = NullPointerException.class)
	public void shouldThrowNullPointerException() {
		RoleDAO roleDAO = new RoleDAO();
		roleDAO.getById(Authority.PROGRAM_ADMINISTRATOR);
	}
	
	@Test
	public void shoudlGetRoleByApplicantAuthority() {
		flushAndClearSession();
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		Role relaodedRole = roleDAO.getById(Authority.APPLICATION_CREATOR);
		assertNotNull(relaodedRole);

	}

	@Test
	public void shoudlGetRoleByAdminAuthority() {
		flushAndClearSession();
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		Role relaodedRole = roleDAO.getById(Authority.PROGRAM_ADMINISTRATOR);
		assertNotNull(relaodedRole);
		
	}
	
	@Test
	public void shoudlGetRoleByReviewerAuthority() {
		flushAndClearSession();
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		Role relaodedRole = roleDAO.getById(Authority.APPLICATION_REVIEWER);
		assertNotNull(relaodedRole);
		
	}
	
	@Test
	public void shoudlGetRoleBySuperadminAuthority() {
		flushAndClearSession();
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		Role relaodedRole = roleDAO.getById(Authority.SYSTEM_ADMINISTRATOR);
		assertNotNull(relaodedRole);
		
	}
	
	@Test
	public void shoudlGetRoleByApproverAuthority() {
		flushAndClearSession();
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		Role relaodedRole = roleDAO.getById(Authority.PROGRAM_APPROVER);
		assertNotNull(relaodedRole);
		
	}
	@Test
	public void shoudlGetRoleByInterviewerAuthority() {
		flushAndClearSession();
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		Role relaodedRole = roleDAO.getById(Authority.APPLICATION_INTERVIEWER);
		assertEquals(Authority.APPLICATION_INTERVIEWER, relaodedRole.getId());
		assertNotNull(relaodedRole);
		
	}
	
	@Test
	public void shoudlGetRoleBySupervisorAuthority() {
		flushAndClearSession();
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		Role relaodedRole = roleDAO.getById(Authority.APPLICATION_PRIMARY_SUPERVISOR);
		assertEquals(Authority.APPLICATION_PRIMARY_SUPERVISOR, relaodedRole.getId());
		assertNotNull(relaodedRole);
		
	}
	
}
