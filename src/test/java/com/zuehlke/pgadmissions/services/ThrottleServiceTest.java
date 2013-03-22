package com.zuehlke.pgadmissions.services;

import org.easymock.EasyMock;
import org.junit.Before;

import com.zuehlke.pgadmissions.dao.ThrottleDAO;

public class ThrottleServiceTest {
	
	private ThrottleService service;
	
	private ThrottleDAO mockRepo;
	
	@Before
	public void setup() {
		mockRepo = EasyMock.createMock(ThrottleDAO.class);
		service = new ThrottleService(mockRepo);
	}
	
	//TODO: Claudio, do again :D
}
