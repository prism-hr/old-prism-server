package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.builders.PersonBuilder;

public class PersonMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadPerson(){
		Person person = new PersonBuilder().firstname("bob").lastname("smith").email("email@Test.com").build();
		sessionFactory.getCurrentSession().save(person);
		assertNotNull(person.getId());
		
		Person reloadedPerson = (Person) sessionFactory.getCurrentSession().get(Person.class, person.getId());
		assertSame(person, reloadedPerson);
		
		flushAndClearSession();
		reloadedPerson = (Person) sessionFactory.getCurrentSession().get(Person.class, person.getId());
		assertNotSame(person, reloadedPerson);
		assertEquals("bob", reloadedPerson.getFirstname());
		assertEquals("smith", reloadedPerson.getLastname());
		assertEquals("email@Test.com", reloadedPerson.getEmail());
	}
}
