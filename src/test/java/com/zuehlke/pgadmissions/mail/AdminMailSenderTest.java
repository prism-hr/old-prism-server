package com.zuehlke.pgadmissions.mail;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectReasonBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectionBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.utils.Environment;

public class AdminMailSenderTest {

	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;

	private AdminMailSender adminMailSender;
	private MessageSource msgSourceMock;
	private ApplicationsService applicationServiceMock;
	private ConfigurationService personServiceMock;

	@Test
	public void shouldReturnCorrectlyPopulatedModel() {

		RegisteredUser adminOne = new RegisteredUserBuilder().id(1).email("bob@test.com").toUser();
		RegisteredUser adminTwo = new RegisteredUserBuilder().id(8).email("alice@test.com").toUser();

		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().administrators(adminOne, adminTwo).toProgram())
				.toApplicationForm();

		Map<String, Object> model = adminMailSender.createModel(form);
		assertEquals(form, model.get("application"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));
		assertEquals(Environment.getInstance().getAdmissionsOfferServiceLevel(), model.get("admissionOfferServiceLevel"));

	}

	@Test
	public void shouldSendReminderEmailToAdmin() throws UnsupportedEncodingException {
		final Map<String, Object> model = new HashMap<String, Object>();

		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, applicationServiceMock, msgSourceMock, personServiceMock) {
			@Override
			Map<String, Object> createModel(ApplicationForm application) {
				return model;
			}
		};
		RegisteredUser administratorOne = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		RegisteredUser administratorTwo = new RegisteredUserBuilder().id(2).firstName("charlie").lastName("crock").email("cc@test.com").toUser();
		Program program = new ProgramBuilder().title("a program").administrators(administratorOne, administratorTwo).toProgram();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).toUser();
		ApplicationForm form = new ApplicationFormBuilder().applicationNumber("abc").id(2).program(program).applicant(applicant).toApplicationForm();

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress1 = new InternetAddress("bb@test.com", "benny brack");
		InternetAddress toAddress2 = new InternetAddress("cc@test.com", "charlie crock");
		String templatename = "private/staff/admin/mail/application_validation_reminder.ftl";

		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("message.code"),//
				EasyMock.aryEq(new Object[] { "abc", "a program", "Jane", "Smith", "Validation" }), EasyMock.eq((Locale) null))).andReturn("resolved subject");

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress1, null, "resolved subject", templatename, model, null)).andReturn(
				preparatorMock);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress2, null, "resolved subject", templatename, model, null)).andReturn(
				preparatorMock);

		javaMailSenderMock.send(preparatorMock);
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);

		adminMailSender.sendMailsForApplication(form, "message.code", templatename, null);
		EasyMock.verify(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
	}

	@Test
	public void shouldSendAdminNotificationForNewReviewComment() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, applicationServiceMock, msgSourceMock, personServiceMock) {

			@Override
			Map<String, Object> createModel(ApplicationForm form) {
				return model;
			}

		};
		RegisteredUser admin = new RegisteredUserBuilder().id(1).firstName("Bob").lastName("Bobson").email("bob@bobson.com").id(1).toUser();
		ApplicationForm form = new ApplicationFormBuilder().id(3234).applicant(new RegisteredUser()).applicationNumber("abc")
				.program(new ProgramBuilder().id(23).title("laal").administrators(admin).toProgram()).toApplicationForm();
		RegisteredUser reviewer = new RegisteredUserBuilder().id(11).toUser();
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("bob@bobson.com", "Bob Bobson");

		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("review.provided.admin"),//
				EasyMock.aryEq(new Object[] { "abc", "laal", null, null, "Validation" }), EasyMock.eq((Locale) null))).andReturn("resolved subject");

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, null,//
				"resolved subject",//
				"private/staff/admin/mail/review_submission_notification.ftl", model, null)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);

		adminMailSender.sendAdminReviewNotification(form, reviewer);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);

	}

	@Test
	public void shouldSendAdminNotificationForNewInterviewComment() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, applicationServiceMock, msgSourceMock, personServiceMock) {

			@Override
			Map<String, Object> createModel(ApplicationForm form) {
				return model;
			}

		};
		RegisteredUser admin = new RegisteredUserBuilder().id(1).firstName("Bob").lastName("Bobson").email("bob@bobson.com").id(1).toUser();
		ApplicationForm form = new ApplicationFormBuilder().id(2342).applicant(new RegisteredUser()).applicationNumber("abc")
				.program(new ProgramBuilder().id(234).title("prg").administrators(admin).toProgram()).toApplicationForm();
		RegisteredUser interviewer = new RegisteredUserBuilder().id(11).toUser();
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("bob@bobson.com", "Bob Bobson");

		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("interview.feedback.notification"),//
				EasyMock.aryEq(new Object[] { "abc", "prg", null, null, "Validation" }), EasyMock.eq((Locale) null))).andReturn("subject");

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, null,//
				"subject",//
				"private/staff/admin/mail/interview_submission_notification.ftl", model, null)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);

		adminMailSender.sendAdminInterviewNotification(form, interviewer);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);

	}

	@Test
	public void shouldSendReminderEmailToEachAdmin() throws Exception {

		final RegisteredUser administratorOne = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		InternetAddress adminOneAdr = new InternetAddress("bb@test.com");

		final RegisteredUser administratorTwo = new RegisteredUserBuilder().id(2).firstName("charlie").lastName("crock").email("cc@test.com").toUser();
		InternetAddress adminTwoAdr = new InternetAddress("cc@test.com");
		Program program = new ProgramBuilder().title("prg").administrators(administratorOne, administratorTwo).toProgram();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).toUser();
		final ApplicationForm form = new ApplicationFormBuilder().applicationNumber("abc").id(22).program(program).applicant(applicant).toApplicationForm();

		final String templatename = "private/staff/admin/mail/application_validation_reminder.ftl";

		final HashMap<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, applicationServiceMock, msgSourceMock, personServiceMock) {

			@Override
			Map<String, Object> createModel(ApplicationForm blabla) {
				return model;
			}

		};
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("msgCode"),//
				EasyMock.aryEq(new Object[] { "abc", "prg", "Jane", "Smith", "Validation" }), EasyMock.eq((Locale) null))).andReturn("subject");

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(adminOneAdr, null, "subject", templatename, model, null)).andReturn(
				preparatorMock);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(adminTwoAdr, null, "subject", templatename, model, null)).andReturn(
				preparatorMock);

		javaMailSenderMock.send(preparatorMock);
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);

		String subjectMessageCode = "msgCode";
		adminMailSender.sendMailsForApplication(form, subjectMessageCode, templatename, null);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);
	}

	@Test
	public void shouldSendApproveReminderEmailToEachApprover() throws Exception {

		final RegisteredUser approverOne = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		InternetAddress adminOneAdr = new InternetAddress("bb@test.com");

		final RegisteredUser approverTwo = new RegisteredUserBuilder().id(2).firstName("charlie").lastName("crock").email("cc@test.com").toUser();
		InternetAddress adminTwoAdr = new InternetAddress("cc@test.com");
		Program program = new ProgramBuilder().title("prg").approver(approverOne, approverTwo).toProgram();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).toUser();
		final ApplicationForm form = new ApplicationFormBuilder().applicationNumber("abc").id(22).program(program).applicant(applicant).toApplicationForm();

		final String templatename = "private/approvers/mail/application_approval_reminder.ftl";

		final HashMap<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, applicationServiceMock, msgSourceMock, personServiceMock) {

			@Override
			Map<String, Object> createModel(ApplicationForm blabla) {
				return model;
			}

		};

		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("msgCode"),//
				EasyMock.aryEq(new Object[] { "abc", "prg", "Jane", "Smith", "Validation" }), EasyMock.eq((Locale) null))).andReturn("subject");

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(adminOneAdr, null, "subject", templatename, model, null)).andReturn(
				preparatorMock);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(adminTwoAdr, null, "subject", templatename, model, null)).andReturn(
				preparatorMock);

		javaMailSenderMock.send(preparatorMock);
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);

		String subjectMessageCode = "msgCode";
		adminMailSender.sendMailsForApplication(form, subjectMessageCode, templatename, NotificationType.APPROVAL_REMINDER);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);
	}

	@Test
	public void shouldStopIfOneReminderEmailFails() throws Exception {
		final RegisteredUser administratorOne = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		InternetAddress adminOneAdr = new InternetAddress("bb@test.com");
		final RegisteredUser administratorTwo = new RegisteredUserBuilder().id(2).firstName("charlie").lastName("crock").email("cc@test.com").toUser();

		Program program = new ProgramBuilder().title("prg").administrators(administratorOne, administratorTwo).toProgram();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).toUser();
		final ApplicationForm form = new ApplicationFormBuilder().id(2).applicationNumber("abc").program(program).applicant(applicant).toApplicationForm();

		final String templatename = "private/staff/admin/mail/application_validation_reminder.ftl";

		final HashMap<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, applicationServiceMock, msgSourceMock, personServiceMock) {

			@Override
			Map<String, Object> createModel(ApplicationForm blabla) {
				return model;
			}

		};

		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("msgCode"),//
				EasyMock.aryEq(new Object[] { "abc", "prg", "Jane", "Smith", "Validation" }), EasyMock.eq((Locale) null))).andReturn("subject");

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(adminOneAdr, null, "subject", templatename, model, null)).andThrow(//
				new RuntimeException("Aarrrggghhhhh.....it's all gone wrong!!"));

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);

		String subjectMessageCode = "msgCode";
		try {
			adminMailSender.sendMailsForApplication(form, subjectMessageCode, templatename, null);
			Assert.fail("exception isn't falling through!");
		} catch (Throwable thr) {
			if (!thr.getMessage().startsWith("Aarrrggghhhhh")) {
				Assert.fail("unexpected error: " + thr);
			}
		}
		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);
	}

	@Test
	public void sendingRejectNotificationsToAdminsApproversAndSupervisors() throws Exception {
		RegisteredUser admin = new RegisteredUserBuilder().id(1).email("bob@test.com").firstName("bob").lastName("the builder").toUser();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).toUser();
		RegisteredUser approver = new RegisteredUserBuilder().firstName("George").lastName("Smith").email("george.smith@test.com").id(90).toUser();

		RegisteredUser supervisorUser1 = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		Supervisor supervisor1 = new SupervisorBuilder().id(1).user(supervisorUser1).toSupervisor();
		ApprovalRound previousApprovalRound = new ApprovalRoundBuilder().id(1).supervisors(supervisor1).toApprovalRound();

		RegisteredUser interviewerUser2 = new RegisteredUserBuilder().id(3).firstName("Fred").lastName("Forse").email("Forse@test.com").toUser();
		Supervisor supervisor3 = new SupervisorBuilder().id(3).user(interviewerUser2).toSupervisor();

		ApprovalRound latestApprovalRound = new ApprovalRoundBuilder().id(1).supervisors(supervisor3).toApprovalRound();

		Program program = new ProgramBuilder().title("prg").administrators(admin).approver(approver).toProgram();
		ApplicationForm application = new ApplicationFormBuilder().id(4).approvalRounds(previousApprovalRound, latestApprovalRound)
				.latestApprovalRound(latestApprovalRound).applicationNumber("bob").applicant(applicant)
				.program(program).toApplicationForm();

		RejectReason reason = new RejectReasonBuilder().id(2134).text("blas").toRejectReason();
		Rejection rejection = new RejectionBuilder().id(3).rejectionReason(reason).toRejection();
		application.setRejection(rejection);

		
		InternetAddress expAddr = new InternetAddress("bob@test.com", "bob the builder");
		InternetAddress supAdd = new InternetAddress("Forse@test.com", "Fred Forse");
		InternetAddress apprAdd = new InternetAddress("george.smith@test.com", "George Smith");

		String expTemplate = "private/staff/admin/mail/rejected_notification.ftl";

		final Map<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, applicationServiceMock, msgSourceMock, personServiceMock) {
			@Override
			Map<String, Object> createModel(ApplicationForm form) {
				Assert.assertNotNull(form);
				return model;
			}

		};

		//
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("rejection.notification.admin"),//
				EasyMock.aryEq(new Object[] { "bob", "prg", "Jane", "Smith", "Validation" }), EasyMock.eq((Locale) null))).andReturn("subject").anyTimes();

		MimeMessagePreparator mimePrepMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(expAddr, null, "subject", expTemplate, model, null)).andReturn(mimePrepMock1);
		javaMailSenderMock.send(mimePrepMock1);

		MimeMessagePreparator mimePrepMock2 = EasyMock.createMock(MimeMessagePreparator.class);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(supAdd, null, "subject", expTemplate, model, null)).andReturn(mimePrepMock2);
		javaMailSenderMock.send(mimePrepMock2);

		MimeMessagePreparator mimePrepMock3 = EasyMock.createMock(MimeMessagePreparator.class);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(apprAdd, null, "subject", expTemplate, model, null)).andReturn(mimePrepMock3);
		javaMailSenderMock.send(mimePrepMock3);
		
		EasyMock.replay(mimePrepMock1, mimePrepMock2, mimePrepMock3, javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock, applicationServiceMock);

		adminMailSender.sendAdminRejectNotification(application, approver);

		EasyMock.verify(mimePrepMock1, mimePrepMock2, mimePrepMock3, javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock, applicationServiceMock);
		Assert.assertEquals(approver, model.get("approver"));
		Assert.assertEquals(reason, model.get("reason"));
		Assert.assertEquals(ApplicationFormStatus.VALIDATION, model.get("previousStage"));
	}



	@Test
	public void shouldSendReviewerAssignedEmailToEachAdmin() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, applicationServiceMock, msgSourceMock, personServiceMock) {

			@Override
			Map<String, Object> createModel(ApplicationForm form) {
				return model;
			}

		};
		RegisteredUser admin = new RegisteredUserBuilder().id(1).firstName("Bob").lastName("Bobson").email("bob@bobson.com").id(1).toUser();
		ApplicationForm form = new ApplicationFormBuilder().id(213).applicationNumber("abc")
				.program(new ProgramBuilder().title("prg").administrators(admin).toProgram()).applicant(new RegisteredUser()).toApplicationForm();
		RegisteredUser reviewerUser = new RegisteredUserBuilder().id(11).toUser();
		Reviewer reviewer = new ReviewerBuilder().id(1).user(reviewerUser).toReviewer();
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("bob@bobson.com", "Bob Bobson");

		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("reviewer.assigned.admin"),//
				EasyMock.aryEq(new Object[] { "abc", "prg", null, null, "Validation" }), EasyMock.eq((Locale) null))).andReturn("subject");

		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, null, "subject",
						"private/staff/admin/mail/reviewer_assigned_notification.ftl", model, null)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);

		adminMailSender.sendReviewerAssignedNotification(form, reviewer);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);

	}

	@Test
	public void shouldSendRejectionNotificationToApplicationAdminAndCCProgramAdmin() throws Exception {
		RegisteredUser programAdminOne = new RegisteredUserBuilder().id(1).email("bob@test.com").firstName("bob").lastName("the builder").toUser();
		RegisteredUser programAdminTwo = new RegisteredUserBuilder().id(2).email("cc@test.com").firstName("charlie").lastName("crock").toUser();

		Program program = new ProgramBuilder().title("prg").administrators(programAdminOne, programAdminTwo).toProgram();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).toUser();

		ApplicationForm application = new ApplicationFormBuilder().id(4).applicationNumber("bob").applicant(applicant).program(program)
				.interviews(new InterviewBuilder().id(4).toInterview()).status(ApplicationFormStatus.REJECTED).toApplicationForm();

		RegisteredUser applicationAdmin = new RegisteredUserBuilder().id(32).email("dd@test.com").firstName("doris").lastName("day").toUser();
		application.setApplicationAdministrator(applicationAdmin);

		RegisteredUser approver = new RegisteredUserBuilder().id(11).toUser();
		RejectReason reason = new RejectReasonBuilder().id(2134).text("blas").toRejectReason();
		Rejection rejection = new RejectionBuilder().id(3).rejectionReason(reason).toRejection();
		application.setRejection(rejection);

		InternetAddress expAddr = new InternetAddress("dd@test.com", "doris day");
		String expTemplate = "private/staff/admin/mail/rejected_notification.ftl";

		final Map<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, applicationServiceMock, msgSourceMock, personServiceMock) {
			@Override
			Map<String, Object> createModel(ApplicationForm form) {
				Assert.assertNotNull(form);
				return model;
			}

		};
		InternetAddress prgAdminOne = new InternetAddress("bob@test.com", "bob the builder");
		InternetAddress prgAdminTwo = new InternetAddress("cc@test.com", "charlie crock");

		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("rejection.notification.admin"),//
				EasyMock.aryEq(new Object[] { "bob", "prg", "Jane", "Smith", "Interview" }), EasyMock.eq((Locale) null))).andReturn("subject");

		MimeMessagePreparator mimePrepMock = EasyMock.createMock(MimeMessagePreparator.class);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(//
				EasyMock.eq(expAddr),//
				EasyMock.aryEq(new InternetAddress[] { prgAdminOne, prgAdminTwo }),//
				EasyMock.eq("subject"), //
				EasyMock.eq(expTemplate), //
				EasyMock.eq(model), (InternetAddress) EasyMock.isNull())).andReturn(mimePrepMock);
		javaMailSenderMock.send(mimePrepMock);
		EasyMock.expectLastCall();
		EasyMock.replay(mimePrepMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock, applicationServiceMock);

		adminMailSender.sendAdminRejectNotification(application, approver);

		EasyMock.verify(mimePrepMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock, applicationServiceMock);

	}

	@Test
	public void sendingApprovedNotificationsOnlyToNotApproverAdmins() throws Exception {
		RegisteredUser adminOne = new RegisteredUserBuilder().id(1).email("bob@test.com").firstName("bob").lastName("the builder").toUser();
		RegisteredUser adminTwo = new RegisteredUserBuilder().id(2).email("cc@test.com").firstName("charlie").lastName("crock").toUser();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).toUser();

		ApplicationForm application = new ApplicationFormBuilder().id(4).interviews(new InterviewBuilder().id(4).toInterview())
				.status(ApplicationFormStatus.APPROVED).applicationNumber("bob").applicant(applicant)
				.program(new ProgramBuilder().title("prg").administrators(adminOne, adminTwo).toProgram()).toApplicationForm();

		InternetAddress expAddr = new InternetAddress("bob@test.com", "bob the builder");
		String expTemplate = "private/staff/admin/mail/approved_notification.ftl";

		final Map<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, applicationServiceMock, msgSourceMock, personServiceMock) {
			@Override
			Map<String, Object> createModel(ApplicationForm form) {
				Assert.assertNotNull(form);
				return model;
			}

		};
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("approved.notification"),//
				EasyMock.aryEq(new Object[] { "bob", "prg", "Jane", "Smith", "Interview" }), EasyMock.eq((Locale) null))).andReturn("subject");

		MimeMessagePreparator mimePrepMock = EasyMock.createMock(MimeMessagePreparator.class);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(expAddr, null, "subject", expTemplate, model, null)).andReturn(mimePrepMock);
		javaMailSenderMock.send(mimePrepMock);
		EasyMock.expectLastCall();

		List<Person> registryContacts = new ArrayList<Person>();
		registryContacts.add(new PersonBuilder().id(123).toPerson());
		EasyMock.expect(personServiceMock.getAllRegistryUsers()).andReturn(registryContacts);

		EasyMock.replay(mimePrepMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock, applicationServiceMock, personServiceMock);

		adminMailSender.sendAdminAndSupervisorApprovedNotification(application, adminTwo);

		EasyMock.verify(mimePrepMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock, applicationServiceMock, personServiceMock);
		Assert.assertEquals(adminTwo, model.get("approver"));
		Assert.assertEquals(registryContacts, model.get("registryContacts"));
		Assert.assertEquals(ApplicationFormStatus.INTERVIEW, model.get("previousStage"));
	}

	@Test
	public void sendingApprovedNotificationsToAdminsAndSupervisorrs() throws Exception {
		RegisteredUser admin = new RegisteredUserBuilder().id(1).email("bob@test.com").firstName("bob").lastName("the builder").toUser();
		RegisteredUser supervisor1 = new RegisteredUserBuilder().email("bob1@test.com").firstName("bob1").lastName("the builder").id(12).toUser();
		Supervisor sup1 = new SupervisorBuilder().id(1).user(supervisor1).toSupervisor();
		ApprovalRound previousApprovalRound = new ApprovalRoundBuilder().id(1).supervisors(sup1).toApprovalRound();

		RegisteredUser supervisor2 = new RegisteredUserBuilder().email("bob2@test.com").firstName("bob2").lastName("the builder").id(13).toUser();
		Supervisor sup2 = new SupervisorBuilder().id(2).user(supervisor2).toSupervisor();
		RegisteredUser supervisor3 = new RegisteredUserBuilder().email("bob3@test.com").firstName("bob3").lastName("the builder").id(14).toUser();
		Supervisor sup3 = new SupervisorBuilder().id(3).user(supervisor3).toSupervisor();
		ApprovalRound latestApprovalRound = new ApprovalRoundBuilder().id(1).supervisors(sup2, sup3).toApprovalRound();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).toUser();

		ApplicationForm application = new ApplicationFormBuilder().approvalRounds(previousApprovalRound, latestApprovalRound)
				.latestApprovalRound(latestApprovalRound).id(4).applicationNumber("bob").applicant(applicant)
				.program(new ProgramBuilder().title("prg").administrators(admin).toProgram()).toApplicationForm();

		RegisteredUser approver = new RegisteredUserBuilder().id(11).toUser();

		InternetAddress expAddr = new InternetAddress("bob@test.com", "bob the builder");
		InternetAddress supAddr1 = new InternetAddress("bob2@test.com", "bob2 the builder");
		InternetAddress supAddr2 = new InternetAddress("bob3@test.com", "bob3 the builder");

		String expTemplate = "private/staff/admin/mail/approved_notification.ftl";

		final Map<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, applicationServiceMock, msgSourceMock, personServiceMock) {
			@Override
			Map<String, Object> createModel(ApplicationForm form) {
				Assert.assertNotNull(form);
				return model;
			}

		};

		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("approved.notification"),//
				EasyMock.aryEq(new Object[] { "bob", "prg", "Jane", "Smith", "Validation" }), EasyMock.eq((Locale) null))).andReturn("subject").anyTimes();

		MimeMessagePreparator mimePrepMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator mimePrepMock2 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator mimePrepMock3 = EasyMock.createMock(MimeMessagePreparator.class);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(expAddr, null, "subject", expTemplate, model, null)).andReturn(mimePrepMock1);
		javaMailSenderMock.send(mimePrepMock1);

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(supAddr1, null, "subject", expTemplate, model, null))
				.andReturn(mimePrepMock2);
		javaMailSenderMock.send(mimePrepMock2);

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(supAddr2, null, "subject", expTemplate, model, null))
				.andReturn(mimePrepMock3);
		javaMailSenderMock.send(mimePrepMock3);

		List<Person> registryContacts = new ArrayList<Person>();
		registryContacts.add(new PersonBuilder().id(123).toPerson());
		EasyMock.expect(personServiceMock.getAllRegistryUsers()).andReturn(registryContacts);

		EasyMock.replay(mimePrepMock1, javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock, applicationServiceMock, personServiceMock);

		adminMailSender.sendAdminAndSupervisorApprovedNotification(application, approver);

		EasyMock.verify(mimePrepMock1, javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock, applicationServiceMock, personServiceMock);
		Assert.assertEquals(approver, model.get("approver"));
		Assert.assertEquals(registryContacts, model.get("registryContacts"));
		Assert.assertEquals(ApplicationFormStatus.VALIDATION, model.get("previousStage"));
	}

	@Before
	public void setUp() {
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		msgSourceMock = EasyMock.createMock(MessageSource.class);
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		personServiceMock = EasyMock.createMock(ConfigurationService.class);

		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock,//
				applicationServiceMock, msgSourceMock, personServiceMock);
	}
}
