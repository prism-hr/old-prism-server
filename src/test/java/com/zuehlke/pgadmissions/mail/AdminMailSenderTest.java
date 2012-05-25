package com.zuehlke.pgadmissions.mail;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectReasonBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectionBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.utils.Environment;

public class AdminMailSenderTest {

	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;

	private AdminMailSender adminMailSender;

	@Test
	public void shouldReturnCorrectlyPopulatedModel() {

		RegisteredUser adminOne = new RegisteredUserBuilder().id(1).email("bob@test.com").toUser();
		RegisteredUser adminTwo = new RegisteredUserBuilder().id(8).email("alice@test.com").toUser();

		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().administrators(adminOne, adminTwo).toProgram()).toApplicationForm();

		Map<String, Object> model = adminMailSender.createModel(form);
		assertEquals(form, model.get("application"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));

	}

	@Test
	public void shouldSendReminderEmailToAdmin() throws UnsupportedEncodingException {
		final Map<String, Object> model = new HashMap<String, Object>();

		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {
			@Override
			Map<String, Object> createModel(ApplicationForm application) {
				return model;
			}
		};
		RegisteredUser administratorOne = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		RegisteredUser administratorTwo = new RegisteredUserBuilder().id(2).firstName("charlie").lastName("crock").email("cc@test.com").toUser();
		Program program = new ProgramBuilder().administrators(administratorOne, administratorTwo).toProgram();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).toUser();
		ApplicationForm form = new ApplicationFormBuilder().id(2).program(program).applicant(applicant).toApplicationForm();

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress1 = new InternetAddress("bb@test.com", "benny brack");
		InternetAddress toAddress2 = new InternetAddress("cc@test.com", "charlie crock");
		String subjectMessage = "is overdue validation";
		String templatename = "private/staff/admin/mail/application_validation_reminder.ftl";
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress1, null, "Application 2 by Jane Smith " + subjectMessage, templatename, model)).andReturn(preparatorMock);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress2, null, "Application 2 by Jane Smith " + subjectMessage, templatename, model)).andReturn(preparatorMock);

		javaMailSenderMock.send(preparatorMock);
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		adminMailSender.sendMailsForApplication(form, subjectMessage, templatename);
		EasyMock.verify(mimeMessagePreparatorFactoryMock, javaMailSenderMock);
	}

	@Test
	public void shouldSendAdminNotificationForNewReviewComment() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {

			@Override
			Map<String, Object> createModel(ApplicationForm form) {
				return model;
			}

		};
		RegisteredUser admin = new RegisteredUserBuilder().id(1).firstName("Bob").lastName("Bobson").email("bob@bobson.com").id(1).toUser();
		ApplicationForm form = new ApplicationFormBuilder().program(new ProgramBuilder().id(23).administrators(admin).toProgram()).toApplicationForm();
		RegisteredUser reviewer = new RegisteredUserBuilder().id(11).toUser();
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("bob@bobson.com", "Bob Bobson");

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, null, "Notification - review added", "private/staff/admin/mail/review_submission_notification.ftl", model)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		adminMailSender.sendAdminReviewNotification(form, reviewer);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock);

	}

	@Test
	public void shouldSendAdminNotificationForNewInterviewComment() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {

			@Override
			Map<String, Object> createModel(ApplicationForm form) {
				return model;
			}

		};
		RegisteredUser admin = new RegisteredUserBuilder().id(1).firstName("Bob").lastName("Bobson").email("bob@bobson.com").id(1).toUser();
		ApplicationForm form = new ApplicationFormBuilder().program(new ProgramBuilder().id(234).administrators(admin).toProgram()).toApplicationForm();
		RegisteredUser interviewer = new RegisteredUserBuilder().id(11).toUser();
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("bob@bobson.com", "Bob Bobson");

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, null, "Notification - Interview added", "private/staff/admin/mail/interview_submission_notification.ftl", model)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		adminMailSender.sendAdminInterviewNotification(form, interviewer);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock);

	}

	@Test
	public void shouldSendReminderEmailToEachAdmin() throws Exception {

		final RegisteredUser administratorOne = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		InternetAddress adminOneAdr = new InternetAddress("bb@test.com");

		final RegisteredUser administratorTwo = new RegisteredUserBuilder().id(2).firstName("charlie").lastName("crock").email("cc@test.com").toUser();
		InternetAddress adminTwoAdr = new InternetAddress("cc@test.com");
		Program program = new ProgramBuilder().administrators(administratorOne, administratorTwo).toProgram();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).toUser();
		final ApplicationForm form = new ApplicationFormBuilder().id(22).program(program).applicant(applicant).toApplicationForm();

		final String templatename = "private/staff/admin/mail/application_validation_reminder.ftl";

		final HashMap<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {

			@Override
			Map<String, Object> createModel(ApplicationForm blabla) {
				return model;
			}

		};
		String expectedMessage = "Application 22 by Jane Smith is overdue validation";

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(adminOneAdr, null, expectedMessage, templatename, model)).andReturn(preparatorMock);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(adminTwoAdr, null, expectedMessage, templatename, model)).andReturn(preparatorMock);

		javaMailSenderMock.send(preparatorMock);
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		String subjectMessage = "is overdue validation";
		adminMailSender.sendMailsForApplication(form, subjectMessage, templatename);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock);
	}

	@Test
	public void shouldStopIfOneReminderEmailFails() throws Exception {
		final RegisteredUser administratorOne = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		InternetAddress adminOneAdr = new InternetAddress("bb@test.com");
		final RegisteredUser administratorTwo = new RegisteredUserBuilder().id(2).firstName("charlie").lastName("crock").email("cc@test.com").toUser();

		Program program = new ProgramBuilder().administrators(administratorOne, administratorTwo).toProgram();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).toUser();
		final ApplicationForm form = new ApplicationFormBuilder().id(2).program(program).applicant(applicant).toApplicationForm();

		final String templatename = "private/staff/admin/mail/application_validation_reminder.ftl";

		final HashMap<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {

			@Override
			Map<String, Object> createModel(ApplicationForm blabla) {
				return model;
			}

		};

		String expectedMessage = "Application 2 by Jane Smith is overdue validation";
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(adminOneAdr, null, expectedMessage, templatename, model)).andThrow(new RuntimeException("Aarrrggghhhhh.....it's all gone wrong!!"));
		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		String subjectMessage = "is overdue validation";
		try {
			adminMailSender.sendMailsForApplication(form, subjectMessage, templatename);
			Assert.fail("exception isn't falling through!");
		} catch (Throwable thr) {
			if (!thr.getMessage().startsWith("Aarrrggghhhhh")) {
				Assert.fail("unexpected error: " + thr);
			}
		}
		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock);
	}

	@Test
	public void sendingRejectNotifications() throws Exception {
		RegisteredUser admin = new RegisteredUserBuilder().id(1).email("bob@test.com").firstName("bob").lastName("the builder").toUser();
		ApplicationForm application = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().administrators(admin).toProgram()).toApplicationForm();

		RejectReason reason = new RejectReasonBuilder().id(2134).text("blas").toRejectReason();
		Rejection rejection = new RejectionBuilder().id(3).rejectionReason(reason).toRejection();
		application.setRejection(rejection);

		RegisteredUser approver = new RegisteredUserBuilder().id(11).toUser();
		InternetAddress expAddr = new InternetAddress("bob@test.com", "bob the builder");
		String expSubjet = "Notification - rejected application";
		String expTemplate = "private/staff/admin/mail/rejected_notification.ftl";

		final Map<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {
			@Override
			Map<String, Object> createModel(ApplicationForm form) {
				Assert.assertNotNull(form);
				return model;
			}

		};

		MimeMessagePreparator mimePrepMock = EasyMock.createMock(MimeMessagePreparator.class);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(expAddr, null, expSubjet, expTemplate, model)).andReturn(mimePrepMock);
		javaMailSenderMock.send(mimePrepMock);
		EasyMock.expectLastCall();
		EasyMock.replay(mimePrepMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock);

		adminMailSender.sendAdminRejectNotification(application, approver);

		EasyMock.verify(mimePrepMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock);
		Assert.assertEquals(approver, model.get("approver"));
		Assert.assertEquals(reason, model.get("reason"));
	}

	@Test
	public void sendingRejectNotificationsOnlyToNotApproverAdmins() throws Exception {
		RegisteredUser adminOne = new RegisteredUserBuilder().id(1).email("bob@test.com").firstName("bob").lastName("the builder").toUser();
		RegisteredUser adminTwo = new RegisteredUserBuilder().id(2).email("cc@test.com").firstName("charlie").lastName("crock").toUser();

		ApplicationForm application = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().administrators(adminOne, adminTwo).toProgram()).toApplicationForm();

		RejectReason reason = new RejectReasonBuilder().id(2134).text("blas").toRejectReason();
		Rejection rejection = new RejectionBuilder().id(3).rejectionReason(reason).toRejection();
		application.setRejection(rejection);

		InternetAddress expAddr = new InternetAddress("bob@test.com", "bob the builder");
		String expSubjet = "Notification - rejected application";
		String expTemplate = "private/staff/admin/mail/rejected_notification.ftl";

		final Map<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {
			@Override
			Map<String, Object> createModel(ApplicationForm form) {
				Assert.assertNotNull(form);
				return model;
			}

		};

		MimeMessagePreparator mimePrepMock = EasyMock.createMock(MimeMessagePreparator.class);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(expAddr, null, expSubjet, expTemplate, model)).andReturn(mimePrepMock);
		javaMailSenderMock.send(mimePrepMock);
		EasyMock.expectLastCall();
		EasyMock.replay(mimePrepMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock);

		adminMailSender.sendAdminRejectNotification(application, adminTwo);

		EasyMock.verify(mimePrepMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock);
		Assert.assertEquals(adminTwo, model.get("approver"));
	}

	@Test
	public void sendingNoRejectNotificationsOnlyOneProgramAdminWhoApproved() throws Exception {
		RegisteredUser adminOne = new RegisteredUserBuilder().id(1).email("bob@test.com").firstName("bob").lastName("the builder").toUser();
		ApplicationForm application = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().administrators(adminOne).toProgram()).toApplicationForm();

		RejectReason reason = new RejectReasonBuilder().id(2134).text("blas").toRejectReason();
		Rejection rejection = new RejectionBuilder().id(3).rejectionReason(reason).toRejection();
		application.setRejection(rejection);

		final Map<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {
			@Override
			Map<String, Object> createModel(ApplicationForm form) {
				Assert.assertNotNull(form);
				return model;
			}

		};

		EasyMock.replay(javaMailSenderMock, mimeMessagePreparatorFactoryMock);

		adminMailSender.sendAdminRejectNotification(application, adminOne);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock);
	}

	@Test
	public void shouldSendReviewerAssignedEmailToEachAdmin() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {

			@Override
			Map<String, Object> createModel(ApplicationForm form) {
				return model;
			}

		};
		RegisteredUser admin = new RegisteredUserBuilder().id(1).firstName("Bob").lastName("Bobson").email("bob@bobson.com").id(1).toUser();
		ApplicationForm form = new ApplicationFormBuilder().program(new ProgramBuilder().id(2311).administrators(admin).toProgram()).toApplicationForm();
		RegisteredUser reviewerUser = new RegisteredUserBuilder().id(11).toUser();
		Reviewer reviewer = new ReviewerBuilder().id(1).user(reviewerUser).toReviewer();
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress =  new InternetAddress("bob@bobson.com", "Bob Bobson") ;

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, null, "Notification - Reviewer assigned", "private/staff/admin/mail/reviewer_assigned_notification.ftl", model)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		adminMailSender.sendReviewerAssignedNotification(form, reviewer);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock);

	}

	@Test
	public void shouldSendRejectionNotificationToApplicationAdminAndCCProgramAdmin() throws Exception {
		RegisteredUser programAdminOne = new RegisteredUserBuilder().id(1).email("bob@test.com").firstName("bob").lastName("the builder").toUser();
		RegisteredUser programAdminTwo = new RegisteredUserBuilder().id(2).email("cc@test.com").firstName("charlie").lastName("crock").toUser();

		Program program = new ProgramBuilder().administrators(programAdminOne, programAdminTwo).toProgram();
		ApplicationForm application = new ApplicationFormBuilder().id(4).program(program).toApplicationForm();

		RegisteredUser applicationAdmin = new RegisteredUserBuilder().id(32).email("dd@test.com").firstName("doris").lastName("day").toUser();
		application.setApplicationAdministrator(applicationAdmin);

		RegisteredUser approver = new RegisteredUserBuilder().id(11).toUser();
		RejectReason reason = new RejectReasonBuilder().id(2134).text("blas").toRejectReason();
		Rejection rejection = new RejectionBuilder().id(3).rejectionReason(reason).toRejection();
		application.setRejection(rejection);

		InternetAddress expAddr = new InternetAddress("dd@test.com", "doris day");
		String expSubjet = "Notification - rejected application";
		String expTemplate = "private/staff/admin/mail/rejected_notification.ftl";

		final Map<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {
			@Override
			Map<String, Object> createModel(ApplicationForm form) {
				Assert.assertNotNull(form);
				return model;
			}

		};
		InternetAddress prgAdminOne = new InternetAddress("bob@test.com", "bob the builder");
		InternetAddress prgAdminTwo = new InternetAddress("cc@test.com", "charlie crock");

		MimeMessagePreparator mimePrepMock = EasyMock.createMock(MimeMessagePreparator.class);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(//
				EasyMock.eq(expAddr),// 
				EasyMock.aryEq(new InternetAddress[] { prgAdminOne, prgAdminTwo }),// 
				EasyMock.eq(expSubjet), //
				EasyMock.eq(expTemplate), //
				EasyMock.eq(model))).andReturn(mimePrepMock);
		javaMailSenderMock.send(mimePrepMock);
		EasyMock.expectLastCall();
		EasyMock.replay(mimePrepMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock);

		adminMailSender.sendAdminRejectNotification(application, approver);

		EasyMock.verify(mimePrepMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock);

	}

	@Before
	public void setUp() {
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock);
	}
}
