package com.zuehlke.pgadmissions.timers;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.services.RefereeService;

public class RefereeReminderTaskTest {

	private SessionFactory sessionFactoryMock;
	private Session sessionMock;	
	private RefereeReminderTask refereeReminderTask;
	private RefereeService refereeServiceMock;

	@Test
	public void shouldGetRefereesAndSendReminders(){
		Transaction transactionOne = EasyMock.createMock(Transaction.class);
		Transaction transactionTwo = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
		Referee refereeOne = new RefereeBuilder().id(1).toReferee();
		Referee refereeTwo = new RefereeBuilder().id(2).toReferee();
		EasyMock.expect(refereeServiceMock.getRefereesDueAReminder()).andReturn(Arrays.asList(refereeOne, refereeTwo));
		refereeServiceMock.sendReminderAndUpdateLastNotified(refereeOne);
		transactionOne.commit();
		refereeServiceMock.sendReminderAndUpdateLastNotified(refereeTwo);		transactionTwo.commit();
		
		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, refereeServiceMock);
		
		refereeReminderTask.run();
		
		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, refereeServiceMock);
	}
	
	@Test
	public void shouldRollBackTransactionIfExceptionOccurs(){
		Transaction transactionOne = EasyMock.createMock(Transaction.class);
		Transaction transactionTwo = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
		Referee refereeOne = new RefereeBuilder().id(1).toReferee();
		Referee refereeTwo = new RefereeBuilder().id(2).toReferee();
		EasyMock.expect(refereeServiceMock.getRefereesDueAReminder()).andReturn(Arrays.asList(refereeOne, refereeTwo));
		refereeServiceMock.sendReminderAndUpdateLastNotified(refereeOne);
		EasyMock.expectLastCall().andThrow(new RuntimeException());
		transactionOne.rollback();
		refereeServiceMock.sendReminderAndUpdateLastNotified(refereeTwo);	
		transactionTwo.commit();
		
		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, refereeServiceMock);
		
		refereeReminderTask.run();
		
		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, refereeServiceMock);
	}
	@Before
	public void setup(){
		sessionFactoryMock = EasyMock.createMock(SessionFactory.class);
		sessionMock = EasyMock.createMock(Session.class);
		
		refereeServiceMock = EasyMock.createMock(RefereeService.class);
		refereeReminderTask = new RefereeReminderTask(sessionFactoryMock, refereeServiceMock);
		
	}
	
}
