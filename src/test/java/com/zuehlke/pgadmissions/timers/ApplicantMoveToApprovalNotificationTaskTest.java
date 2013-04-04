package com.zuehlke.pgadmissions.timers;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.MOVED_TO_APPROVED_NOTIFICATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
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
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.ApplicantMailSender;
import com.zuehlke.pgadmissions.mail.StateChangeMailSender;
import com.zuehlke.pgadmissions.services.EmailTemplateService;

public class ApplicantMoveToApprovalNotificationTaskTest {

	private SessionFactory sessionFactoryMock;
	private Session sessionMock;
	private ApplicantMoveToApprovalNotificationTask notificationTask;
	private ApplicationFormDAO applicationFormDAOMock;
	private StateChangeMailSender applicationMailSenderMock;
	private String subjectMessage;
	private EmailTemplateName templateName;
	private EmailTemplate template;
	private EmailTemplateService templateServiceMock;


	@Test
	public void shouldGetApplicationsAndSendInReviewNotifications() throws UnsupportedEncodingException, ParseException {
		Transaction transactionOne = EasyMock.createMock(Transaction.class);
		Transaction transactionTwo = EasyMock.createMock(Transaction.class);
		Transaction transactionThree = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionThree);

		ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).applicant(new RegisteredUserBuilder().id(1).email("lllll@test.com").build())
				.build();
		ApplicationForm applicationFormTwo = new ApplicationFormBuilder()
				.id(2)
				.applicant(new RegisteredUserBuilder().id(2).email("jjjjjj@test.com").build())
				.build();
		sessionMock.refresh(applicationFormOne);
		sessionMock.refresh(applicationFormTwo);
		List<ApplicationForm> applicationFormList = Arrays.asList(applicationFormOne, applicationFormTwo);
		EasyMock.expect(applicationFormDAOMock.getApplicationsDueMovedToApprovalNotifications()).andReturn(applicationFormList);
		transactionOne.commit();

		applicationMailSenderMock.sendMailsForApplication(applicationFormOne, subjectMessage, templateName, template.getContent(), null);
		applicationFormDAOMock.save(applicationFormOne);
		transactionTwo.commit();

		applicationMailSenderMock.sendMailsForApplication(applicationFormTwo, subjectMessage, templateName, template.getContent(), null);
		applicationFormDAOMock.save(applicationFormTwo);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, applicationMailSenderMock, templateServiceMock, applicationFormDAOMock);

		notificationTask.run();

		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, applicationMailSenderMock, templateServiceMock, applicationFormDAOMock);

		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE),
				DateUtils.truncate(applicationFormOne.getNotificationForType(NotificationType.APPLICATION_MOVED_TO_APPROVAL_NOTIFICATION).getDate(), Calendar.DATE));
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE),
				DateUtils.truncate(applicationFormTwo.getNotificationForType(NotificationType.APPLICATION_MOVED_TO_APPROVAL_NOTIFICATION).getDate(), Calendar.DATE));
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
		ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).applicant(new RegisteredUserBuilder().id(1).email("lllll@test.com").build())
				.build();
		ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(2).applicant(new RegisteredUserBuilder().id(2).email("jjjjjj@test.com").build())
				.build();
		sessionMock.refresh(applicationFormOne);
		sessionMock.refresh(applicationFormTwo);
		List<ApplicationForm> applicationFormList = Arrays.asList(applicationFormOne, applicationFormTwo);
		EasyMock.expect(
				applicationFormDAOMock.getApplicationsDueMovedToApprovalNotifications())
				.andReturn(applicationFormList);

		transactionOne.commit();
		applicationMailSenderMock.sendMailsForApplication(applicationFormOne, subjectMessage, templateName, template.getContent(), null);
		EasyMock.expectLastCall().andThrow(new RuntimeException());
		transactionTwo.rollback();
		applicationMailSenderMock.sendMailsForApplication(applicationFormTwo, subjectMessage, templateName, template.getContent(), null);
		applicationFormDAOMock.save(applicationFormTwo);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, applicationMailSenderMock, templateServiceMock, applicationFormDAOMock);

		notificationTask.run();

		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, applicationMailSenderMock, templateServiceMock, applicationFormDAOMock);
		assertNull(applicationFormOne.getNotificationForType(NotificationType.APPLICATION_MOVED_TO_APPROVAL_NOTIFICATION));
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE),
				DateUtils.truncate(applicationFormTwo.getNotificationForType(NotificationType.APPLICATION_MOVED_TO_APPROVAL_NOTIFICATION).getDate(), Calendar.DATE));
	}

	@Before
	public void setup() {
		sessionFactoryMock = EasyMock.createMock(SessionFactory.class);
		sessionMock = EasyMock.createMock(Session.class);
		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		applicationMailSenderMock = EasyMock.createMock(ApplicantMailSender.class);
		templateServiceMock = EasyMock.createMock(EmailTemplateService.class);
		subjectMessage = "approved.notification.applicant";
		templateName = MOVED_TO_APPROVED_NOTIFICATION;
		template=new EmailTemplateBuilder().content("template content").active(true).name(templateName).build();
		EasyMock.expect(templateServiceMock.getActiveEmailTemplate(templateName)).andReturn(template);
	
		notificationTask = new ApplicantMoveToApprovalNotificationTask(sessionFactoryMock, applicationFormDAOMock, applicationMailSenderMock, subjectMessage, templateName, templateServiceMock);

	}

}
