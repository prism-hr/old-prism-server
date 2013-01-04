package com.zuehlke.pgadmissions.timers;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.PendingRoleNotificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.mail.NewUserMailSender;


public class NewUserNotificationTaskTest {
	private SessionFactory sessionFactoryMock;
	private Session sessionMock;
	private  NewUserNotificationTask newUserNotificationTask;
	private NewUserMailSender newUserMailSenderMock;	
	private UserDAO userDAOMock;
	

	@Test
	public void shouldGetPendingNotificationsAndSendNotifications() throws UnsupportedEncodingException {
		Transaction transactionOne = EasyMock.createMock(Transaction.class);
		Transaction transactionTwo = EasyMock.createMock(Transaction.class);
		Transaction transactionThree = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionThree);

		RegisteredUser userOne = new RegisteredUserBuilder().id(1).pendingRoleNotifications(new PendingRoleNotificationBuilder().id(1).build(),new PendingRoleNotificationBuilder().id(2).build()).build();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(3).pendingRoleNotifications(new PendingRoleNotificationBuilder().id(3).build()).build();
		
		
		sessionMock.refresh(userOne);
		sessionMock.refresh(userTwo);

		List<RegisteredUser> userList = Arrays.asList(userOne, userTwo);
		EasyMock.expect(userDAOMock.getUsersWithPendingRoleNotifications()).andReturn(userList);
		
		transactionOne.commit();

		newUserMailSenderMock.sendNewUserNotification(userOne);
		userDAOMock.save(userOne);
		transactionTwo.commit();

		newUserMailSenderMock.sendNewUserNotification(userTwo);
		userDAOMock.save(userTwo);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo,transactionThree, newUserMailSenderMock, userDAOMock);

		newUserNotificationTask.run();

		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, transactionThree, newUserMailSenderMock, userDAOMock);
		
		Assert.assertTrue(userOne.getPendingRoleNotifications().size() > 0);
		for (PendingRoleNotification notification : userOne.getPendingRoleNotifications()) {
		    Assert.assertNotNull(notification.getNotificationDate());
		}
		
		Assert.assertTrue(userTwo.getPendingRoleNotifications().size() > 0);
		for (PendingRoleNotification notification : userTwo.getPendingRoleNotifications()) {
            Assert.assertNotNull(notification.getNotificationDate());
        }
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

		RegisteredUser userOne = new RegisteredUserBuilder().id(1).pendingRoleNotifications(new PendingRoleNotificationBuilder().id(1).build(),new PendingRoleNotificationBuilder().id(2).build()).build();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(3).pendingRoleNotifications(new PendingRoleNotificationBuilder().id(3).build()).build();
		
		
		sessionMock.refresh(userOne);
		sessionMock.refresh(userTwo);

		List<RegisteredUser> userList = Arrays.asList(userOne, userTwo);
		EasyMock.expect(userDAOMock.getUsersWithPendingRoleNotifications()).andReturn(userList);
		
		transactionOne.commit();

		newUserMailSenderMock.sendNewUserNotification(userOne);
		EasyMock.expectLastCall().andThrow(new RuntimeException("aaaah!"));		
		transactionTwo.rollback();

		newUserMailSenderMock.sendNewUserNotification(userTwo);
		userDAOMock.save(userTwo);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo,transactionThree, newUserMailSenderMock, userDAOMock);

		newUserNotificationTask.run();

		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, transactionThree, newUserMailSenderMock, userDAOMock);
		assertEquals(2, userOne.getPendingRoleNotifications().size());
		
		Assert.assertTrue(userTwo.getPendingRoleNotifications().size() > 0);
		for (PendingRoleNotification notification : userTwo.getPendingRoleNotifications()) {
            Assert.assertNotNull(notification.getNotificationDate());
        }
	}

	@Before
	public void setup() {
		sessionFactoryMock = EasyMock.createMock(SessionFactory.class);
		sessionMock = EasyMock.createMock(Session.class);

		newUserMailSenderMock = EasyMock.createMock(NewUserMailSender.class);
		userDAOMock = EasyMock.createMock(UserDAO.class);				
		newUserNotificationTask = new  NewUserNotificationTask(sessionFactoryMock, newUserMailSenderMock,userDAOMock);

	}

}
