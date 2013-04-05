package com.zuehlke.pgadmissions.timers;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.APPLICATION_VALIDATION_REMINDER;
import static org.easymock.EasyMock.expect;
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
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.EmailTemplateBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.AdminMailSender;
import com.zuehlke.pgadmissions.services.EmailTemplateService;

public class RegisteredUserReminderTimerTaskTest {
	private SessionFactory sessionFactoryMock;
	private Session sessionMock;
	private RegisteredUserReminderTimerTask reminderTask;
	private ApplicationFormDAO applicationFormDAOMock;
	private AdminMailSender adminMailSenderMock;
	private String subjectMessage;
	private NotificationType notificationType;
	private ApplicationFormStatus status;
	private String firstSubjectMessage;
	
	private EmailTemplate template;
	private EmailTemplate templateFirst;
	private EmailTemplateName templateName;
	private EmailTemplateName templateNameFirst;
	private EmailTemplateService templateServiceMock;

	@Test
	public void shouldGetApplicationsAndSendInReviewNotifications() throws ParseException {
		Transaction transactionOne = EasyMock.createMock(Transaction.class);
		Transaction transactionTwo = EasyMock.createMock(Transaction.class);
		Transaction transactionThree = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionThree);

		ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).build();
		ApplicationForm applicationFormTwo = new ApplicationFormBuilder()//
				.id(2)//
				.notificationRecords(//
						new NotificationRecordBuilder().id(1).notificationType(NotificationType.VALIDATION_REMINDER)//
								.notificationDate(new SimpleDateFormat("dd MM yyyy").parse("01 02 2011")).build()).build();
		sessionMock.refresh(applicationFormOne);
		sessionMock.refresh(applicationFormTwo);
		List<ApplicationForm> applicationFormList = Arrays.asList(applicationFormOne, applicationFormTwo);
		expect(applicationFormDAOMock.getApplicationsDueUserReminder(notificationType, status)).andReturn(applicationFormList);
		transactionOne.commit();
		
		expect(templateServiceMock.getActiveEmailTemplate(templateNameFirst)).andReturn(templateFirst);

		adminMailSenderMock.sendMailsForApplication(applicationFormOne, firstSubjectMessage, templateNameFirst, templateFirst.getContent(), NotificationType.VALIDATION_REMINDER);
		applicationFormDAOMock.save(applicationFormOne);
		transactionTwo.commit();

		expect(templateServiceMock.getActiveEmailTemplate(templateName)).andReturn(template);
		
		adminMailSenderMock.sendMailsForApplication(applicationFormTwo, subjectMessage, templateName, template.getContent(), NotificationType.VALIDATION_REMINDER);
		applicationFormDAOMock.save(applicationFormTwo);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, templateServiceMock, adminMailSenderMock, applicationFormDAOMock);

		reminderTask.run();

		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, templateServiceMock, adminMailSenderMock, applicationFormDAOMock);

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
		ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).build();
		ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(2).build();
		sessionMock.refresh(applicationFormOne);
		sessionMock.refresh(applicationFormTwo);
		List<ApplicationForm> applicationFormList = Arrays.asList(applicationFormOne, applicationFormTwo);
		EasyMock.expect(//
				applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.VALIDATION_REMINDER,//
						ApplicationFormStatus.VALIDATION)).andReturn(applicationFormList);

		transactionOne.commit();
		
		expect(templateServiceMock.getActiveEmailTemplate(templateNameFirst)).andReturn(templateFirst);

		adminMailSenderMock.sendMailsForApplication(applicationFormOne, firstSubjectMessage, templateNameFirst, templateFirst.getContent(), NotificationType.VALIDATION_REMINDER);
		EasyMock.expectLastCall().andThrow(new RuntimeException());
		transactionTwo.rollback();
		
		expect(templateServiceMock.getActiveEmailTemplate(templateNameFirst)).andReturn(templateFirst);
		
		adminMailSenderMock.sendMailsForApplication(applicationFormTwo, firstSubjectMessage, templateNameFirst, templateFirst.getContent(), NotificationType.VALIDATION_REMINDER);
		applicationFormDAOMock.save(applicationFormTwo);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, adminMailSenderMock, templateServiceMock, applicationFormDAOMock);

		reminderTask.run();

		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, adminMailSenderMock, templateServiceMock, applicationFormDAOMock);
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
		templateServiceMock = EasyMock.createMock(EmailTemplateService.class);
		firstSubjectMessage = "first is overdue validation";
		subjectMessage = "is overdue validation";
		notificationType = NotificationType.VALIDATION_REMINDER;
		status = ApplicationFormStatus.VALIDATION;
		templateName =APPLICATION_VALIDATION_REMINDER;
		templateNameFirst = APPLICATION_VALIDATION_REMINDER;
		template=new EmailTemplateBuilder().content("template content").active(true).name(templateName).build();
		templateFirst=new EmailTemplateBuilder().content("template content").active(true).name(templateNameFirst).build();
		//EasyMock.expect(templateServiceMock.getActiveEmailTemplate(templateName)).andReturn(template);

		reminderTask = new RegisteredUserReminderTimerTask(sessionFactoryMock, applicationFormDAOMock, adminMailSenderMock,// 
				notificationType, status, firstSubjectMessage, templateNameFirst, subjectMessage, templateName, templateServiceMock);
	}
}
