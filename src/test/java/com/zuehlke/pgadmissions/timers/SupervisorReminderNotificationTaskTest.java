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

import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.mail.SupervisorMailSender;

public class SupervisorReminderNotificationTaskTest {
    
	private SessionFactory sessionFactoryMock;
	
	private Session sessionMock;
	
	private SupervisorReminderTask supervisorNotificationTask;
	
	private SupervisorMailSender mailServiceMock;
	
	private SupervisorDAO supervisorDAOMock;
	
	@Before
	public void setup() {
	    sessionFactoryMock = EasyMock.createMock(SessionFactory.class);
	    sessionMock = EasyMock.createMock(Session.class);
	    mailServiceMock = EasyMock.createMock(SupervisorMailSender.class);
	    supervisorDAOMock = EasyMock.createMock(SupervisorDAO.class);       
	    supervisorNotificationTask = new SupervisorReminderTask(sessionFactoryMock, mailServiceMock, supervisorDAOMock);
	}

	@Test
	public void shouldGetPrimarySupervisorsAndSendNotifications() throws UnsupportedEncodingException {
        Transaction transactionOne = EasyMock.createMock(Transaction.class);
        Transaction transactionTwo = EasyMock.createMock(Transaction.class);
        Transaction transactionThree = EasyMock.createMock(Transaction.class);
        EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
        EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
        EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
        EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionThree);

        Supervisor supervisorOne = new SupervisorBuilder().id(1).user(new RegisteredUserBuilder().email("hello@test.com").build()).build();
        Supervisor supervisorTwo = new SupervisorBuilder().id(2).user(new RegisteredUserBuilder().email("hello@test.com").build()).build();
        sessionMock.refresh(supervisorOne);
        sessionMock.refresh(supervisorTwo);
        List<Supervisor> supervisorList = Arrays.asList(supervisorOne, supervisorTwo);
        EasyMock.expect(supervisorDAOMock.getPrimarySupervisorsDueReminder()).andReturn(supervisorList);
        
        transactionOne.commit();

        mailServiceMock.sendPrimarySupervisorConfirmationNotificationReminder(supervisorOne);
        supervisorDAOMock.save(supervisorOne);
        transactionTwo.commit();

        mailServiceMock.sendPrimarySupervisorConfirmationNotificationReminder(supervisorTwo);
        supervisorDAOMock.save(supervisorTwo);
        transactionThree.commit();

        EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, supervisorDAOMock);

        supervisorNotificationTask.run();

        EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, supervisorDAOMock);
        assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(supervisorOne.getLastNotified(), Calendar.DATE));
        assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(supervisorTwo.getLastNotified(), Calendar.DATE));
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
        Supervisor supervisorOne = new SupervisorBuilder().user(new RegisteredUserBuilder().email("hello@test.com").build()).id(1).build();
        Supervisor supervisorTwo = new SupervisorBuilder().user(new RegisteredUserBuilder().email("hello@test.com").build()).id(2).build();
        sessionMock.refresh(supervisorOne);
        sessionMock.refresh(supervisorTwo);
        EasyMock.expect(supervisorDAOMock.getPrimarySupervisorsDueReminder()).andReturn(Arrays.asList(supervisorOne, supervisorTwo));
        
        transactionOne.commit();
        mailServiceMock.sendPrimarySupervisorConfirmationNotificationReminder(supervisorOne);

        EasyMock.expectLastCall().andThrow(new RuntimeException());
        transactionTwo.rollback();
        mailServiceMock.sendPrimarySupervisorConfirmationNotificationReminder(supervisorTwo);
        supervisorDAOMock.save(supervisorTwo);
        transactionThree.commit();

        EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, supervisorDAOMock);

        supervisorNotificationTask.run();

        EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, supervisorDAOMock);
        assertNull(supervisorOne.getLastNotified());
        assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(supervisorTwo.getLastNotified(), Calendar.DATE));
    }
}
