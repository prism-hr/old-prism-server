package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.builders.PersonBuilder;


public class PersonDAOTest extends AutomaticRollbackTestCase {

	
	@Test
	public void shouldSaveAndGetRegistryUserWithId(){
		Person registryUser = new PersonBuilder().id(1).firstname("john").lastname("mark").email("email").build();
		sessionFactory.getCurrentSession().save(registryUser);
		flushAndClearSession();
		
		PersonDAO registryUserDAO = new PersonDAO(sessionFactory);
		Person returnedRegistryUser = registryUserDAO.getPersonWithId(registryUser.getId());
		assertEquals(registryUser.getId(), returnedRegistryUser.getId());
	}
	
	@Test
	public void shouldDeletePerson(){
		Person registryUser = new PersonBuilder().id(1).firstname("john").lastname("mark").email("email").build();
		sessionFactory.getCurrentSession().save(registryUser);
		flushAndClearSession();
		
		PersonDAO registryUserDAO = new PersonDAO(sessionFactory);
		Integer id = registryUser.getId();
		Person returnedRegistryUser = registryUserDAO.getPersonWithId(id);
		registryUserDAO.delete(returnedRegistryUser);
		
		flushAndClearSession();
		assertNull( registryUserDAO.getPersonWithId(id));
	}
}
