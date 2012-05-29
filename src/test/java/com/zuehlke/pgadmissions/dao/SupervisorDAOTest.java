package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;

public class SupervisorDAOTest extends AutomaticRollbackTestCase {

	@Test(expected = NullPointerException.class)
	public void shouldThrowNullPointerException() {
		SupervisorDAO supervisorDAO = new SupervisorDAO();
		supervisorDAO.getSupervisorWithId(1);
	}
	
	@Test
	public void shouldGetSupervisorWithId(){
		Supervisor supervisor = new SupervisorBuilder().id(1).email("email").toSupervisor();
		sessionFactory.getCurrentSession().save(supervisor);
		flushAndClearSession();
		
		SupervisorDAO supervisorDAO = new SupervisorDAO(sessionFactory);
		Supervisor returnedSupervisor = supervisorDAO.getSupervisorWithId(supervisor.getId());
		assertEquals(supervisor, returnedSupervisor);
	}
}
