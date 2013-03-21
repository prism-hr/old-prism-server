package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ThrottleDAO;
import com.zuehlke.pgadmissions.domain.Throttle;
import com.zuehlke.pgadmissions.domain.builders.ThrottleBuilder;

public class ThrottleServiceTest {
	
	private ThrottleService service;
	private ThrottleDAO mockRepo;
	
	@Before
	public void setup() {
		mockRepo = createMock(ThrottleDAO.class);
		service = new ThrottleService(mockRepo);
	}
	
	@Test
	public void shouldSaveNewThrottle() {
		Throttle throttle = new ThrottleBuilder().enabled(true).batchSize(12).build();
		mockRepo.save(throttle);
		replay(mockRepo);
	
		service.updateThrottle(throttle);
		
		verify(mockRepo);
	}
	
	@Test
	public void shouldUpdateExistingThrottle() {
		Throttle throttle = new ThrottleBuilder().id(2).enabled(true).batchSize(12).build();
		mockRepo.update(throttle);
		replay(mockRepo);
		
		service.updateThrottle(throttle);
		
		verify(mockRepo);
	}
	
	@Test
	public void shouldGetExistingThrottle() {
		Throttle throttle = new ThrottleBuilder().id(2).enabled(true).batchSize(12).build();
		expect(mockRepo.getById(2)).andReturn(throttle);
		replay(mockRepo);
		
		service.getThrottle(2);
		
		verify(mockRepo);
	}
	

}
