package com.zuehlke.pgadmissions.timers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.InterviewerDAO;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.mail.InterviewerMailSender;


public class InterviewerNotificationTaskTest {
	private SessionFactory sessionFactoryMock;
	private Session sessionMock;
	private InterviewerNotificationTask interviewerNotificationTask;
	private InterviewerMailSender mailServiceMock;
	private InterviewerDAO interviewerDAOMock;
	

	@Test
	public void shouldGetInterviewersAndSendReminders() throws UnsupportedEncodingException {
		Transaction transactionOne = EasyMock.createMock(Transaction.class);
		Transaction transactionTwo = EasyMock.createMock(Transaction.class);
		Transaction transactionThree = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionThree);

		Interviewer interviewerOne = new InterviewerBuilder().id(1).user(new RegisteredUserBuilder().email("hello@test.com").build()).build();
		Interviewer interviewerTwo = new InterviewerBuilder().id(2).user(new RegisteredUserBuilder().email("hello@test.com").build()).build();
		sessionMock.refresh(interviewerOne);
		sessionMock.refresh(interviewerTwo);
		List<Interviewer> interviewerList = Arrays.asList(interviewerOne, interviewerTwo);
		EasyMock.expect(interviewerDAOMock.getInterviewersDueNotification()).andReturn(interviewerList);
		
		transactionOne.commit();

		mailServiceMock.sendInterviewerNotification(interviewerOne);
		interviewerDAOMock.save(interviewerOne);
		transactionTwo.commit();

		mailServiceMock.sendInterviewerNotification(interviewerTwo);
		interviewerDAOMock.save(interviewerTwo);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, interviewerDAOMock);

		interviewerNotificationTask.run();

		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, interviewerDAOMock);
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(interviewerOne.getLastNotified(), Calendar.DATE));
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(interviewerTwo.getLastNotified(), Calendar.DATE));
	}

	@Test
	public void shouldRollBackTransactionIfExceptionOccurs() throws UnsupportedEncodingException {
		Transaction transactionOne = EasyMock.createMock(Transaction.class);
		Transaction transactionTwo = EasyMock.createMock(Transaction.class);
		Transaction transactionThree = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionThree);
		Interviewer interviewerOne = new InterviewerBuilder().user(new RegisteredUserBuilder().email("hello@test.com").build()).id(1).build();
		Interviewer interviewerTwo = new InterviewerBuilder().user(new RegisteredUserBuilder().email("hello@test.com").build()).id(2).build();
		sessionMock.refresh(interviewerOne);
		sessionMock.refresh(interviewerTwo);
		EasyMock.expect(interviewerDAOMock.getInterviewersDueNotification()).andReturn(Arrays.asList(interviewerOne, interviewerTwo));
		
		transactionOne.commit();
		mailServiceMock.sendInterviewerNotification(interviewerOne);

		EasyMock.expectLastCall().andThrow(new RuntimeException());
		transactionTwo.rollback();
		mailServiceMock.sendInterviewerNotification(interviewerTwo);
		interviewerDAOMock.save(interviewerTwo);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, interviewerDAOMock);

		interviewerNotificationTask.run();

		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, interviewerDAOMock);
		assertNull(interviewerOne.getLastNotified());
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(interviewerTwo.getLastNotified(), Calendar.DATE));
	}

	@Before
	public void setup() {
		sessionFactoryMock = EasyMock.createMock(SessionFactory.class);
		sessionMock = EasyMock.createMock(Session.class);

		mailServiceMock = EasyMock.createMock(InterviewerMailSender.class);
		interviewerDAOMock = EasyMock.createMock(InterviewerDAO.class);		
		interviewerNotificationTask = new InterviewerNotificationTask(sessionFactoryMock, mailServiceMock, interviewerDAOMock);

	}

}
