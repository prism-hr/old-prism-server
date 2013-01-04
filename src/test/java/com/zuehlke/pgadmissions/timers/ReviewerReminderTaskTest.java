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

import com.zuehlke.pgadmissions.dao.ReviewerDAO;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.mail.ReviewerMailSender;


public class ReviewerReminderTaskTest {
	private SessionFactory sessionFactoryMock;
	private Session sessionMock;
	private  ReviewerReminderTask reviewerNotificationTask;
	private ReviewerMailSender mailServiceMock;
	private ReviewerDAO reviewerDAOMock;
	

	@Test
	public void shouldGetReviewersAndSendReminders() throws UnsupportedEncodingException {
		Transaction transactionOne = EasyMock.createMock(Transaction.class);
		Transaction transactionTwo = EasyMock.createMock(Transaction.class);
		Transaction transactionThree = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionThree);

		Reviewer reviewerOne = new ReviewerBuilder().id(1).user(new RegisteredUserBuilder().email("hello@test.com").build()).build();
		Reviewer reviewerTwo = new ReviewerBuilder().id(2).user(new RegisteredUserBuilder().email("hello@test.com").build()).build();
		sessionMock.refresh(reviewerOne);
		sessionMock.refresh(reviewerTwo);
		List<Reviewer> reviewerList = Arrays.asList(reviewerOne, reviewerTwo);
		EasyMock.expect(reviewerDAOMock.getReviewersDueReminder()).andReturn(reviewerList);
		
		transactionOne.commit();

		mailServiceMock.sendReviewerReminder(reviewerOne);
		reviewerDAOMock.save(reviewerOne);
		transactionTwo.commit();

		mailServiceMock.sendReviewerReminder(reviewerTwo);
		reviewerDAOMock.save(reviewerTwo);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, reviewerDAOMock);

		reviewerNotificationTask.run();

		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, reviewerDAOMock);
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(reviewerOne.getLastNotified(), Calendar.DATE));
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(reviewerTwo.getLastNotified(), Calendar.DATE));
	}

	@Test
	public void shouldRollBackTransactionIfExceptionOccurs()  {
		Transaction transactionOne = EasyMock.createMock(Transaction.class);
		Transaction transactionTwo = EasyMock.createMock(Transaction.class);
		Transaction transactionThree = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionThree);
		Reviewer reviewerOne = new ReviewerBuilder().user(new RegisteredUserBuilder().email("hello@test.com").build()).id(1).build();
		Reviewer reviewerTwo = new ReviewerBuilder().user(new RegisteredUserBuilder().email("hello@test.com").build()).id(2).build();
		sessionMock.refresh(reviewerOne);
		sessionMock.refresh(reviewerTwo);
		EasyMock.expect(reviewerDAOMock.getReviewersDueReminder()).andReturn(Arrays.asList(reviewerOne, reviewerTwo));
		
		transactionOne.commit();
		mailServiceMock.sendReviewerReminder(reviewerOne);

		EasyMock.expectLastCall().andThrow(new RuntimeException("lalal"));
		transactionTwo.rollback();
		mailServiceMock.sendReviewerReminder(reviewerTwo);
		reviewerDAOMock.save(reviewerTwo);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, reviewerDAOMock);

		reviewerNotificationTask.run();

		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, reviewerDAOMock);
		assertNull(reviewerOne.getLastNotified());
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(reviewerTwo.getLastNotified(), Calendar.DATE));
	}

	@Before
	public void setup() {
		sessionFactoryMock = EasyMock.createMock(SessionFactory.class);
		sessionMock = EasyMock.createMock(Session.class);

		mailServiceMock = EasyMock.createMock(ReviewerMailSender.class);
		reviewerDAOMock = EasyMock.createMock(ReviewerDAO.class);		
		reviewerNotificationTask = new  ReviewerReminderTask(sessionFactoryMock, mailServiceMock, reviewerDAOMock);

	}

}
