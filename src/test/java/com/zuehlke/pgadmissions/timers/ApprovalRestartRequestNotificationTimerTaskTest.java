package com.zuehlke.pgadmissions.timers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.ApprovalRestartRequestMailSender;
import com.zuehlke.pgadmissions.services.ApplicationsService;

public class ApprovalRestartRequestNotificationTimerTaskTest {
	private ApprovalRestartRequestNotificationTimerTask notificationTask;

	private ApplicationsService applicationServiceMock;
	private SessionFactory sessionFactoryMock;
	private ApprovalRestartRequestMailSender mailSenderMock;
	private Session sessionMock;

	@Before
	public void setup() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		sessionFactoryMock = EasyMock.createMock(SessionFactory.class);
		mailSenderMock = EasyMock.createMock(ApprovalRestartRequestMailSender.class);
		sessionMock = EasyMock.createMock(Session.class);

		notificationTask = new ApprovalRestartRequestNotificationTimerTask(sessionFactoryMock, mailSenderMock, applicationServiceMock);
	}

	@Test
	public void shouldSendApprovalRestartRequestNotifications() throws Exception {
		Transaction transactionOne = EasyMock.createMock(Transaction.class);
		Transaction transactionTwo = EasyMock.createMock(Transaction.class);
		Transaction transactionThree = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionThree);

		ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).build();
		ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(2).build();
		sessionMock.refresh(applicationFormOne);
		sessionMock.refresh(applicationFormTwo);
		List<ApplicationForm> applicationFormList = Arrays.asList(applicationFormOne, applicationFormTwo);
		EasyMock.expect(applicationServiceMock.getApplicationsDueApprovalRestartRequestNotification()).andReturn(applicationFormList);
		transactionOne.commit();

		mailSenderMock.sendRequestRestartApproval(applicationFormOne);
		applicationServiceMock.save(applicationFormOne);
		transactionTwo.commit();

		mailSenderMock.sendRequestRestartApproval(applicationFormTwo);
		applicationServiceMock.save(applicationFormTwo);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailSenderMock, applicationServiceMock);

		notificationTask.run();

		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailSenderMock, applicationServiceMock);

		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE),
				DateUtils.truncate(applicationFormOne.getNotificationForType(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION).getDate(), Calendar.DATE));
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE),
				DateUtils.truncate(applicationFormTwo.getNotificationForType(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION).getDate(), Calendar.DATE));

	}

	@Test
	public void shouldRollBackTransactionIfExceptionOccurs() throws Exception {
		Transaction transactionOne = EasyMock.createMock(Transaction.class);
		Transaction transactionTwo = EasyMock.createMock(Transaction.class);
		Transaction transactionThree = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionThree);
		ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).build();
		ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(2).build();
		sessionMock.refresh(applicationFormOne);
		sessionMock.refresh(applicationFormTwo);
		List<ApplicationForm> applicationFormList = Arrays.asList(applicationFormOne, applicationFormTwo);
		EasyMock.expect(applicationServiceMock.getApplicationsDueApprovalRestartRequestNotification()).andReturn(applicationFormList);

		transactionOne.commit();
		mailSenderMock.sendRequestRestartApproval(applicationFormOne);
		EasyMock.expectLastCall().andThrow(new RuntimeException());
		transactionTwo.rollback();
		mailSenderMock.sendRequestRestartApproval(applicationFormTwo);
		applicationServiceMock.save(applicationFormTwo);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailSenderMock, applicationServiceMock);

		notificationTask.run();

		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailSenderMock, applicationServiceMock);

		assertNull(applicationFormOne.getNotificationForType(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION));
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE),
				DateUtils.truncate(applicationFormTwo.getNotificationForType(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION).getDate(), Calendar.DATE));
	}
}
