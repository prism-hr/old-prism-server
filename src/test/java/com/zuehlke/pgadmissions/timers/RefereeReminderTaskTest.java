package com.zuehlke.pgadmissions.timers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.mail.RefereeMailSender;

public class RefereeReminderTaskTest {

	private SessionFactory sessionFactoryMock;
	private Session sessionMock;
	private RefereeReminderTask refereeReminderTask;
	private RefereeMailSender mailServiceMock;
	private RefereeDAO refereeDAOMock;

	@Test
	public void shouldGetRefereesAndSendReminders() throws UnsupportedEncodingException {
		Transaction transactionOne = EasyMock.createMock(Transaction.class);
		Transaction transactionTwo = EasyMock.createMock(Transaction.class);
		Transaction transactionThree = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionThree);

		Referee refereeOne = new RefereeBuilder().id(1).build();
		Referee refereeTwo = new RefereeBuilder().id(2).build();
		sessionMock.refresh(refereeOne);
		sessionMock.refresh(refereeTwo);
		EasyMock.expect(refereeDAOMock.getRefereesDueAReminder()).andReturn(Arrays.asList(refereeOne, refereeTwo));
		transactionOne.commit();

		mailServiceMock.sendRefereeReminder(refereeOne);
		refereeDAOMock.save(refereeOne);
		transactionTwo.commit();

		mailServiceMock.sendRefereeReminder(refereeTwo);
		refereeDAOMock.save(refereeTwo);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, refereeDAOMock);

		refereeReminderTask.run();

		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, refereeDAOMock);
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(refereeOne.getLastNotified(), Calendar.DATE));
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(refereeTwo.getLastNotified(), Calendar.DATE));
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
		Referee refereeOne = new RefereeBuilder().id(1).build();
		Referee refereeTwo = new RefereeBuilder().id(2).build();
		sessionMock.refresh(refereeOne);
		sessionMock.refresh(refereeTwo);
		EasyMock.expect(refereeDAOMock.getRefereesDueAReminder()).andReturn(Arrays.asList(refereeOne, refereeTwo));
		transactionOne.commit();
		mailServiceMock.sendRefereeReminder(refereeOne);

		EasyMock.expectLastCall().andThrow(new RuntimeException());
		transactionTwo.rollback();
		mailServiceMock.sendRefereeReminder(refereeTwo);
		refereeDAOMock.save(refereeTwo);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, refereeDAOMock);

		refereeReminderTask.run();

		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, refereeDAOMock);
		assertNull(refereeOne.getLastNotified());
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(refereeTwo.getLastNotified(), Calendar.DATE));
	}

	@Before
	public void setup() {
		sessionFactoryMock = EasyMock.createMock(SessionFactory.class);
		sessionMock = EasyMock.createMock(Session.class);

		mailServiceMock = EasyMock.createMock(RefereeMailSender.class);
		refereeDAOMock = EasyMock.createMock(RefereeDAO.class);
		refereeReminderTask = new RefereeReminderTask(sessionFactoryMock, mailServiceMock, refereeDAOMock);

	}

}
