package com.zuehlke.pgadmissions.timers;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.services.MailService;

public class RefereeReminderTaskTest {

	private SessionFactory sessionFactoryMock;
	private Session sessionMock;	
	private RefereeReminderTask refereeReminderTask;
	private MailService mailServiceMock;

	@Test
	public void shouldGetRefereesAndSendReminders(){
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
		EasyMock.expect(mailServiceMock.getRefereesDueAReminder()).andReturn(Arrays.asList(refereeOne, refereeTwo));
		transactionOne.commit();
		
		mailServiceMock.sendRefereeReminderAndUpdateLastNotified(refereeOne);
		transactionTwo.commit();
		
		mailServiceMock.sendRefereeReminderAndUpdateLastNotified(refereeTwo);		
		transactionThree.commit();
		
		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock);
		
		refereeReminderTask.run();
		
		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock);
	}
	
	@Test
	public void shouldRollBackTransactionIfExceptionOccurs(){
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
		EasyMock.expect(mailServiceMock.getRefereesDueAReminder()).andReturn(Arrays.asList(refereeOne, refereeTwo));
		transactionOne.commit();
		mailServiceMock.sendRefereeReminderAndUpdateLastNotified(refereeOne);
		
		EasyMock.expectLastCall().andThrow(new RuntimeException());
		transactionTwo.rollback();
		mailServiceMock.sendRefereeReminderAndUpdateLastNotified(refereeTwo);	
		transactionThree.commit();
		
		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock);
		
		refereeReminderTask.run();
		
		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock);
	}
	@Before
	public void setup(){
		sessionFactoryMock = EasyMock.createMock(SessionFactory.class);
		sessionMock = EasyMock.createMock(Session.class);
		
		mailServiceMock = EasyMock.createMock(MailService.class);
		refereeReminderTask = new RefereeReminderTask(sessionFactoryMock, mailServiceMock);
		
	}
	
}
