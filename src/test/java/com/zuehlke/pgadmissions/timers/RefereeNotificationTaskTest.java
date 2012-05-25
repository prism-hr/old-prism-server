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

import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.mail.RefereeMailSender;
import com.zuehlke.pgadmissions.services.RefereeService;

public class RefereeNotificationTaskTest {

	private SessionFactory sessionFactoryMock;
	private Session sessionMock;
	private RefereeNotificationTask refereeNotificationTask;
	private RefereeMailSender mailServiceMock;
	private RefereeDAO refereeDAOMock;
	private RefereeService refereeServiceMock;

	@Test
	public void shouldGetRefereesAndSendReminders() throws UnsupportedEncodingException {
		Transaction transactionOne = EasyMock.createMock(Transaction.class);
		Transaction transactionTwo = EasyMock.createMock(Transaction.class);
		Transaction transactionThree = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionThree);

		Referee refereeOne = new RefereeBuilder().id(1).toReferee();
		Referee refereeTwo = new RefereeBuilder().id(2).toReferee();
		sessionMock.refresh(refereeOne);
		sessionMock.refresh(refereeTwo);
		List<Referee> refereeList = Arrays.asList(refereeOne, refereeTwo);
		EasyMock.expect(refereeDAOMock.getRefereesDueNotification()).andReturn(refereeList);
		refereeServiceMock.processRefereesRoles(refereeList);
		transactionOne.commit();

		mailServiceMock.sendRefereeNotification(refereeOne);
		refereeDAOMock.save(refereeOne);
		transactionTwo.commit();

		mailServiceMock.sendRefereeNotification(refereeTwo);
		refereeDAOMock.save(refereeTwo);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, refereeDAOMock, refereeServiceMock);

		refereeNotificationTask.run();

		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, refereeDAOMock, refereeServiceMock);
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
		Referee refereeOne = new RefereeBuilder().id(1).toReferee();
		Referee refereeTwo = new RefereeBuilder().id(2).toReferee();
		sessionMock.refresh(refereeOne);
		sessionMock.refresh(refereeTwo);
		EasyMock.expect(refereeDAOMock.getRefereesDueNotification()).andReturn(Arrays.asList(refereeOne, refereeTwo));
		
		transactionOne.commit();
		mailServiceMock.sendRefereeNotification(refereeOne);

		EasyMock.expectLastCall().andThrow(new RuntimeException());
		transactionTwo.rollback();
		mailServiceMock.sendRefereeNotification(refereeTwo);
		refereeDAOMock.save(refereeTwo);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, refereeDAOMock);

		refereeNotificationTask.run();

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
		refereeServiceMock = EasyMock.createMock(RefereeService.class);
		refereeNotificationTask = new RefereeNotificationTask(sessionFactoryMock, mailServiceMock, refereeDAOMock, refereeServiceMock);

	}

}
