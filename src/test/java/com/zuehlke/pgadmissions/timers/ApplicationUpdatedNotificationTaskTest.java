package com.zuehlke.pgadmissions.timers;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.MailService;

public class ApplicationUpdatedNotificationTaskTest {
	private SessionFactory sessionFactoryMock;
	private Session sessionMock;	
	private ApplicationUpdatedNotificationTask updateNotificationTask;
	private MailService mailServiceMock;
	private ApplicationsService applicationServiceMock;

	@Test
	public void shouldGetApplicationsAndSendNotifications(){
		Transaction transactionOne = EasyMock.createMock(Transaction.class);
		Transaction transactionTwo = EasyMock.createMock(Transaction.class);
		Transaction transactionThree = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionThree);
		
		ApplicationForm appOne = new ApplicationFormBuilder().id(1).toApplicationForm();
		ApplicationForm appTwo = new ApplicationFormBuilder().id(2).toApplicationForm();
		sessionMock.refresh(appOne);
		sessionMock.refresh(appTwo);
		EasyMock.expect(applicationServiceMock.getApplicationsDueUpdateNotification()).andReturn(Arrays.asList(appOne, appTwo));
		transactionOne.commit();
		
		mailServiceMock.sendApplicationUpdatedMailToAdmins(appOne);
		transactionTwo.commit();
		
		mailServiceMock.sendApplicationUpdatedMailToAdmins(appTwo);		
		transactionThree.commit();
		
		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, applicationServiceMock, mailServiceMock);
		
		updateNotificationTask.run();
		
		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, applicationServiceMock, mailServiceMock);
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
		ApplicationForm appOne = new ApplicationFormBuilder().id(1).toApplicationForm();
		ApplicationForm appTwo = new ApplicationFormBuilder().id(2).toApplicationForm();
		sessionMock.refresh(appOne);
		sessionMock.refresh(appTwo);
		EasyMock.expect(applicationServiceMock.getApplicationsDueUpdateNotification()).andReturn(Arrays.asList(appOne, appTwo));
		transactionOne.commit();
		mailServiceMock.sendApplicationUpdatedMailToAdmins(appOne);
		
		EasyMock.expectLastCall().andThrow(new RuntimeException());
		transactionTwo.rollback();
		mailServiceMock.sendApplicationUpdatedMailToAdmins(appTwo);		
		transactionThree.commit();
		
		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, applicationServiceMock, mailServiceMock);
		
		updateNotificationTask.run();
		
		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo,applicationServiceMock, mailServiceMock);
	}
	@Before
	public void setup(){
		sessionFactoryMock = EasyMock.createMock(SessionFactory.class);
		sessionMock = EasyMock.createMock(Session.class);
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		mailServiceMock = EasyMock.createMock(MailService.class);
		updateNotificationTask= new ApplicationUpdatedNotificationTask(sessionFactoryMock, mailServiceMock, applicationServiceMock);
		
	}
	
}
