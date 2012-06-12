package com.zuehlke.pgadmissions.timers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.AdminMailSender;

public class AdminReminderTimerTaskTest {
	private SessionFactory sessionFactoryMock;
	private Session sessionMock;
	private AdminReminderTimerTask reminderTask;
	private ApplicationFormDAO applicationFormDAOMock;
	private AdminMailSender adminMailSenderMock;
	private String subjectMessage;
	private String emailTemplate;
	private NotificationType notificationType;
	private ApplicationFormStatus status;
	private String firstSubjectMessage;
	private String firstEmailTemplate;

	@Test
	public void shouldGetApplicationsAndSendInReviewNotifications() throws ParseException {
		Transaction transactionOne = EasyMock.createMock(Transaction.class);
		Transaction transactionTwo = EasyMock.createMock(Transaction.class);
		Transaction transactionThree = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionThree);

		ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).toApplicationForm();
		ApplicationForm applicationFormTwo = new ApplicationFormBuilder()//
				.id(2)//
				.notificationRecords(//
						new NotificationRecordBuilder().id(1).notificationType(NotificationType.VALIDATION_REMINDER)//
								.notificationDate(new SimpleDateFormat("dd MM yyyy").parse("01 02 2011")).toNotificationRecord()).toApplicationForm();
		sessionMock.refresh(applicationFormOne);
		sessionMock.refresh(applicationFormTwo);
		List<ApplicationForm> applicationFormList = Arrays.asList(applicationFormOne, applicationFormTwo);
		EasyMock.expect(applicationFormDAOMock.getApplicationsDueAdminReminder(notificationType, status)).andReturn(applicationFormList);
		transactionOne.commit();

		adminMailSenderMock.sendMailsForApplication(applicationFormOne, firstSubjectMessage, firstEmailTemplate);
		applicationFormDAOMock.save(applicationFormOne);
		transactionTwo.commit();

		adminMailSenderMock.sendMailsForApplication(applicationFormTwo, subjectMessage, emailTemplate);
		applicationFormDAOMock.save(applicationFormTwo);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, adminMailSenderMock, applicationFormDAOMock);

		reminderTask.run();

		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, adminMailSenderMock, applicationFormDAOMock);

		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(applicationFormOne.getNotificationForType(NotificationType.VALIDATION_REMINDER).getDate(), Calendar.DATE));
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(applicationFormTwo.getNotificationForType(NotificationType.VALIDATION_REMINDER).getDate(), Calendar.DATE));
	}

	@Test
	public void shouldRollBackTransactionIfExceptionOccurs() {
		Transaction transactionOne = EasyMock.createMock(Transaction.class);
		Transaction transactionTwo = EasyMock.createMock(Transaction.class);
		Transaction transactionThree = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionThree);
		ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).toApplicationForm();
		ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(2).toApplicationForm();
		sessionMock.refresh(applicationFormOne);
		sessionMock.refresh(applicationFormTwo);
		List<ApplicationForm> applicationFormList = Arrays.asList(applicationFormOne, applicationFormTwo);
		EasyMock.expect(//
				applicationFormDAOMock.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER,//
						ApplicationFormStatus.VALIDATION)).andReturn(applicationFormList);

		transactionOne.commit();
		adminMailSenderMock.sendMailsForApplication(applicationFormOne, firstSubjectMessage, firstEmailTemplate);
		EasyMock.expectLastCall().andThrow(new RuntimeException());
		transactionTwo.rollback();
		adminMailSenderMock.sendMailsForApplication(applicationFormTwo, firstSubjectMessage, firstEmailTemplate);
		applicationFormDAOMock.save(applicationFormTwo);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, adminMailSenderMock, applicationFormDAOMock);

		reminderTask.run();

		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, adminMailSenderMock, applicationFormDAOMock);
		assertNull(applicationFormOne.getNotificationForType(NotificationType.VALIDATION_REMINDER).getDate());
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(//
				applicationFormTwo.getNotificationForType(NotificationType.VALIDATION_REMINDER).getDate(), Calendar.DATE));
	}

	@Before
	public void setup() {
		sessionFactoryMock = EasyMock.createMock(SessionFactory.class);
		sessionMock = EasyMock.createMock(Session.class);
		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		adminMailSenderMock = EasyMock.createMock(AdminMailSender.class);
		firstSubjectMessage = "first is overdue validation";
		subjectMessage = "is overdue validation";
		firstEmailTemplate = "private/staff/admin/mail/first_application_validation_reminder.ftl";
		emailTemplate = "private/staff/admin/mail/application_validation_reminder.ftl";
		notificationType = NotificationType.VALIDATION_REMINDER;
		status = ApplicationFormStatus.VALIDATION;

		reminderTask = new AdminReminderTimerTask(sessionFactoryMock, applicationFormDAOMock, adminMailSenderMock,// 
				notificationType, status, firstSubjectMessage, firstEmailTemplate, subjectMessage, emailTemplate);
	}
}
