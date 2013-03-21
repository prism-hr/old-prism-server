package com.zuehlke.pgadmissions.timers;

import static org.junit.Assert.assertEquals;

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

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.services.MailService;

public class ApplicationUpdatedNotificationTaskTest {
	private SessionFactory sessionFactoryMock;
	private Session sessionMock;	
	private ApplicationUpdatedNotificationTask updateNotificationTask;
	private MailService mailServiceMock;
	private ApplicationFormDAO applicationFormDAOMock;

	@Test
	public void shouldGetApplicationsAndSendNotifications() {
		Transaction transactionOne = EasyMock.createMock(Transaction.class);
		Transaction transactionTwo = EasyMock.createMock(Transaction.class);
		Transaction transactionThree = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionThree);
		
		NotificationRecord record = new NotificationRecord(NotificationType.UPDATED_NOTIFICATION);
		record.setDate(DateUtils.addDays(new Date(), -12));
		
		ApplicationForm appOne = new ApplicationFormBuilder().id(1).notificationRecords(record).build();
		ApplicationForm appTwo = new ApplicationFormBuilder().id(2).build();
		sessionMock.refresh(appOne);
		sessionMock.refresh(appTwo);
		EasyMock.expect(applicationFormDAOMock.getApplicationsDueUpdateNotification()).andReturn(Arrays.asList(appOne, appTwo));
		applicationFormDAOMock.save(appOne);
		applicationFormDAOMock.save(appTwo);
		transactionOne.commit();
		
		mailServiceMock.sendApplicationUpdatedMailToAdmins(appOne);
		transactionTwo.commit();
		
		mailServiceMock.sendApplicationUpdatedMailToAdmins(appTwo);		
		transactionThree.commit();
		
		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, applicationFormDAOMock, mailServiceMock);
		
		updateNotificationTask.run();
		
		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, applicationFormDAOMock, mailServiceMock);
        assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(appOne.getNotificationForType(NotificationType.UPDATED_NOTIFICATION).getDate(), Calendar.DATE));
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
		ApplicationForm appOne = new ApplicationFormBuilder().id(1).build();
		ApplicationForm appTwo = new ApplicationFormBuilder().id(2).build();
		sessionMock.refresh(appOne);
		sessionMock.refresh(appTwo);
		EasyMock.expect(applicationFormDAOMock.getApplicationsDueUpdateNotification()).andReturn(Arrays.asList(appOne, appTwo));
		transactionOne.commit();
		mailServiceMock.sendApplicationUpdatedMailToAdmins(appOne);
		
		EasyMock.expectLastCall().andThrow(new RuntimeException());
		transactionTwo.rollback();
		
		mailServiceMock.sendApplicationUpdatedMailToAdmins(appTwo);		
		transactionThree.commit();
		applicationFormDAOMock.save(appTwo);
		
		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, applicationFormDAOMock, mailServiceMock);
		
		updateNotificationTask.run();
		
		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo,applicationFormDAOMock, mailServiceMock);
	}
	@Before
	public void setup(){
		sessionFactoryMock = EasyMock.createMock(SessionFactory.class);
		sessionMock = EasyMock.createMock(Session.class);
		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		mailServiceMock = EasyMock.createMock(MailService.class);
		updateNotificationTask= new ApplicationUpdatedNotificationTask(sessionFactoryMock, mailServiceMock, applicationFormDAOMock);
		
	}
	
}
