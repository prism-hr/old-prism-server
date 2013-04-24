package com.zuehlke.pgadmissions.dao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Throttle;
import com.zuehlke.pgadmissions.domain.builders.ThrottleBuilder;

public class ThrottleDAOTest extends AutomaticRollbackTestCase {
	
	private ThrottleDAO dao;
	
	@Before
	public void prepare() {
		dao = new ThrottleDAO(sessionFactory);
	}
	
	@Test
	public void shouldSaveNewThrottle() {
		Throttle throttle = new ThrottleBuilder().enabled(true).batchSize(10).build();
		
		dao.save(throttle);
		
		assertNotNull(throttle.getId());
	}
	
	@Test
	public void shouldUpdateExistingThrottle() {
		Throttle existingThrottle = new ThrottleBuilder().enabled(true).batchSize(10).build();
		Throttle newThrottle = new ThrottleBuilder().enabled(false).batchSize(12).build();
		save(existingThrottle);
		flushAndClearSession();
		Integer existingId=existingThrottle.getId();
		newThrottle.setId(existingId);
		
		dao.update(newThrottle);
		
		assertEquals(existingId, newThrottle.getId());
		Throttle result = dao.getById(existingId);
		assertEquals(newThrottle.getBatchSize(), result.getBatchSize());
		assertEquals(newThrottle.getEnabled(), result.getEnabled());
	}
	
	@Test
	public void shouldReturnThrottle() {
		Throttle throttle = new ThrottleBuilder().enabled(true).batchSize(10).build();
		save(throttle);
		flushAndClearSession();

		Throttle result = dao.getById(throttle.getId());
		
		assertNotNull(result);
		assertEquals(throttle.getBatchSize(), result.getBatchSize());
		assertEquals(throttle.getEnabled(), result.getEnabled());
	}
	
	@Test
	public void shouldReturnTheFirstThrottle() {
		Throttle throttle = new ThrottleBuilder().enabled(true).batchSize(10).build();
		save(throttle);
		flushAndClearSession();
		
		Throttle result = dao.get();
		
		assertNotNull(result);
	}
	
	@Test
	public void shouldReturnOnlyOneThrottle() {
		Throttle throttle1 = new ThrottleBuilder().enabled(true).batchSize(10).build();
		Throttle throttle2 = new ThrottleBuilder().enabled(true).batchSize(12).build();
		save(throttle1, throttle2);
		flushAndClearSession();
		
		Throttle result = dao.get();
		
		assertNotNull(result);
	}

}
