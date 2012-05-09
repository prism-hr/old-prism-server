package com.zuehlke.pgadmissions.services;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.InterviewerDAO;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class InterviewerServiceTest {

	private InterviewerDAO interviewerDAOMock;
	private InterviewerService interviewerService;

	@Test
	public void shouldGetInterviewerById() {
		Interviewer interviewer = EasyMock.createMock(Interviewer.class);
		interviewer.setId(2);
		EasyMock.expect(interviewerDAOMock.getInterviewerById(2)).andReturn(interviewer);
		EasyMock.replay(interviewer, interviewerDAOMock);
		Assert.assertEquals(interviewer, interviewerService.getInterviewerById(2));
	}
	
	@Test
	public void shouldGetInterviewerByUser() {
		Interviewer interviewer = EasyMock.createMock(Interviewer.class);
		RegisteredUser user = new RegisteredUserBuilder().id(1).toUser();
		interviewer.setUser(user);
		EasyMock.expect(interviewerDAOMock.getInterviewerByUser(user)).andReturn(interviewer);
		EasyMock.replay(interviewer, interviewerDAOMock);
		Assert.assertEquals(interviewer, interviewerService.getInterviewerByUser(user));
	}
	
	@Test
	public void shouldDelegateSaveToDAO() {
		Interviewer interviewer = EasyMock.createMock(Interviewer.class);
		interviewerDAOMock.save(interviewer);
		EasyMock.replay(interviewerDAOMock);
		interviewerService.save(interviewer);
		EasyMock.verify(interviewerDAOMock);
	}
	
	@Before
	public void setUp() {
		interviewerDAOMock = EasyMock.createMock(InterviewerDAO.class);
		interviewerService = new InterviewerService(interviewerDAOMock);
	}
	
}
