package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.builders.SuggestedSupervisorBuilder;

public class SuggestedSupervisorMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadSuggestedSupervisor(){
		SuggestedSupervisor person = new SuggestedSupervisorBuilder().firstname("bob").lastname("smith").email("email@Test.com").aware(true).build();
		sessionFactory.getCurrentSession().save(person);
		assertNotNull(person.getId());
		
		SuggestedSupervisor reloadedSuggestedSupervisor = (SuggestedSupervisor) sessionFactory.getCurrentSession().get(SuggestedSupervisor.class, person.getId());
		assertSame(person, reloadedSuggestedSupervisor);
		
		flushAndClearSession();
		reloadedSuggestedSupervisor = (SuggestedSupervisor) sessionFactory.getCurrentSession().get(SuggestedSupervisor.class, person.getId());
		assertNotSame(person, reloadedSuggestedSupervisor);
		assertEquals("bob", reloadedSuggestedSupervisor.getFirstname());
		assertEquals("smith", reloadedSuggestedSupervisor.getLastname());
		assertEquals("email@Test.com", reloadedSuggestedSupervisor.getEmail());
		assertTrue(reloadedSuggestedSupervisor.isAware());
	}
}
