package com.zuehlke.pgadmissions.services;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.InterviewDAO;
import com.zuehlke.pgadmissions.domain.Interview;

public class InterviewServiceTest {

	private InterviewDAO interviewDAOMock;
	private InterviewService interviewService;
	
	@Test
	public void shouldGetInterviewById() {
		Interview interview = EasyMock.createMock(Interview.class);
		interview.setId(2);
		EasyMock.expect(interviewDAOMock.getInterviewById(2)).andReturn(interview);
		EasyMock.replay(interview, interviewDAOMock);
		Assert.assertEquals(interview, interviewService.getInterviewById(2));
	}
	
	@Test
	public void shouldDelegateSaveToDAO() {
		Interview interview = EasyMock.createMock(Interview.class);
		interviewDAOMock.save(interview);
		EasyMock.replay(interviewDAOMock);
		interviewService.save(interview);
		EasyMock.verify(interviewDAOMock);
	}
	
	
	@Before
	public void setUp() {
		interviewDAOMock = EasyMock.createMock(InterviewDAO.class);
		interviewService = new InterviewService(interviewDAOMock);
	}
	
}
