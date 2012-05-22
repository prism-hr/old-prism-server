package com.zuehlke.pgadmissions.mail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

		Map<String, Object> model = adminMailSender.createModel(form, adminOne, null, null, null);
		assertEquals(form, model.get("application"));
		assertEquals(adminOne, model.get("admin"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));

	}

	@Test
	public void shouldSendReminderEmailToAdmin() throws UnsupportedEncodingException {
		final Map<String, Object> model = new HashMap<String, Object>();

		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {
			@Override
			Map<String, Object> createModel(ApplicationForm application, RegisteredUser administrator, RegisteredUser reviewer, RegisteredUser interviewer, List<Reviewer> reviewers) {
				return model;
			}
		};
		RegisteredUser administratorOne = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		RegisteredUser administratorTwo = new RegisteredUserBuilder().id(2).firstName("charlie").lastName("crock").email("cc@test.com").toUser();
		Program program = new ProgramBuilder().administrators(administratorOne, administratorTwo).toProgram();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).toUser();
		ApplicationForm form = new ApplicationFormBuilder().id(2).program(program).applicant(applicant).toApplicationForm();

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("bb@test.com", "benny brack");
		String subjectMessage = "is overdue validation";
		String templatename = "private/staff/admin/mail/application_validation_reminder.ftl";
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "Application 2 by Jane Smith " + subjectMessage, templatename, model)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		adminMailSender.sendMailToAdmin(form, administratorOne, subjectMessage, templatename);
		EasyMock.verify(mimeMessagePreparatorFactoryMock, javaMailSenderMock);
	}

	@Test
	public void shouldSendAdminNotificationForNewReviewComment() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {

			@Override
			Map<String, Object> createModel(ApplicationForm form, RegisteredUser admin, RegisteredUser reviewer, RegisteredUser interviewer, List<Reviewer> reviewers) {
				return model;
			}

		};
		RegisteredUser admin = new RegisteredUserBuilder().id(1).firstName("Bob").lastName("Bobson").email("bob@bobson.com").id(1).toUser();
		ApplicationForm form = new ApplicationFormBuilder().program(new Program()).toApplicationForm();
		RegisteredUser reviewer = new RegisteredUserBuilder().id(11).toUser();
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("bob@bobson.com", "Bob Bobson");

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "Notification - review added", "private/staff/admin/mail/review_submission_notification.ftl", model)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		adminMailSender.sendAdminReviewNotification(admin, form, reviewer);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock);

	}

	@Test
	public void shouldSendAdminNotificationForNewInterviewComment() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {

			@Override
			Map<String, Object> createModel(ApplicationForm form, RegisteredUser admin, RegisteredUser reviewer, RegisteredUser interviewer, List<Reviewer> reviewers) {
				return model;
			}

		};
		RegisteredUser admin = new RegisteredUserBuilder().id(1).firstName("Bob").lastName("Bobson").email("bob@bobson.com").id(1).toUser();
		ApplicationForm form = new ApplicationFormBuilder().program(new Program()).toApplicationForm();
		RegisteredUser interviewer = new RegisteredUserBuilder().id(11).toUser();
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("bob@bobson.com", "Bob Bobson");

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "Notification - Interview added", "private/staff/admin/mail/interview_submission_notification.ftl", model)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		adminMailSender.sendAdminInterviewNotification(admin, form, interviewer);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock);

	}

	@Test
	public void shouldSendReminderEmailToEachAdmin() throws UnsupportedEncodingException {

		final List<RegisteredUser> passedAdmins = new ArrayList<RegisteredUser>();

		final RegisteredUser administratorOne = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		final RegisteredUser administratorTwo = new RegisteredUserBuilder().id(2).firstName("charlie").lastName("crock").email("cc@test.com").toUser();
		Program program = new ProgramBuilder().administrators(administratorOne, administratorTwo).toProgram();

		final ApplicationForm form = new ApplicationFormBuilder().id(2).program(program).toApplicationForm();

		final String subjectMessage = "is overdue validation";
		final String templatename = "private/staff/admin/mail/application_validation_reminder.ftl";

		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {
			@Override
			public void sendMailToAdmin(ApplicationForm passedFord, RegisteredUser passedAdmin, String passedSubjectMessage, String passedTemplatename) throws UnsupportedEncodingException {
				if (form == passedFord && passedSubjectMessage == subjectMessage && passedTemplatename == templatename) {
					if (passedAdmin == administratorOne) {
						passedAdmins.add(administratorOne);
					}
					if (passedAdmin == administratorTwo) {
						passedAdmins.add(administratorTwo);
					}
				}
			}

		};

		adminMailSender.sendMailsForApplication(form, subjectMessage, templatename);
		assertTrue(passedAdmins.containsAll(Arrays.asList(administratorOne, administratorTwo)));
	}

	@Test
	public void shouldNotStopIfOneReminderEmailFails() throws UnsupportedEncodingException {
		final List<RegisteredUser> passedAdmins = new ArrayList<RegisteredUser>();

		final RegisteredUser administratorOne = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		final RegisteredUser administratorTwo = new RegisteredUserBuilder().id(2).firstName("charlie").lastName("crock").email("cc@test.com").toUser();
		Program program = new ProgramBuilder().administrators(administratorOne, administratorTwo).toProgram();

		final ApplicationForm form = new ApplicationFormBuilder().id(2).program(program).toApplicationForm();

		final String subjectMessage = "is overdue validation";
		final String templatename = "private/staff/admin/mail/application_validation_reminder.ftl";

		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {
			@Override
			public void sendMailToAdmin(ApplicationForm passedFord, RegisteredUser passedAdmin, String passedSubjectMessage, String passedTemplatename) throws UnsupportedEncodingException {
				if (form == passedFord && passedSubjectMessage == subjectMessage && passedTemplatename == templatename) {
					if (passedAdmin == administratorOne) {
						throw new RuntimeException("Aarrrggghhhhh.....it's all gone wrong!!");
					}
					if (passedAdmin == administratorTwo) {
						passedAdmins.add(administratorTwo);
					}
				}
			}

		};

		adminMailSender.sendMailsForApplication(form, subjectMessage, templatename);
		assertTrue(passedAdmins.contains(administratorTwo));
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
			Map<String, Object> createModel(ApplicationForm form, RegisteredUser administrator, RegisteredUser reviewer, RegisteredUser interviewer, List<Reviewer> reviewers) {
				Assert.assertNull(reviewer);
				Assert.assertNull(interviewer);
				Assert.assertNotNull(administrator);
				Assert.assertNotNull(form);
				return model;
			}

		};

		MimeMessagePreparator mimePrepMock = EasyMock.createMock(MimeMessagePreparator.class);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(expAddr, expSubjet, expTemplate, model)).andReturn(mimePrepMock);
		javaMailSenderMock.send(mimePrepMock);
		EasyMock.expectLastCall();
		EasyMock.replay(mimePrepMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock);

		adminMailSender.sendAdminRejectNotification(admin, application, approver);

		EasyMock.verify(mimePrepMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock);
		Assert.assertEquals(approver, model.get("approver"));
		Assert.assertEquals(reason, model.get("reason"));
	}
	
	
	@Test
	public void shouldSendReviewerAssignedEmailToEachAdmin() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {

			@Override
			Map<String, Object> createModel(ApplicationForm form, RegisteredUser admin, RegisteredUser reviewer, RegisteredUser interviewer, List<Reviewer> reviewers) {
				return model;
			}

		};
		RegisteredUser admin = new RegisteredUserBuilder().id(1).firstName("Bob").lastName("Bobson").email("bob@bobson.com").id(1).toUser();
		ApplicationForm form = new ApplicationFormBuilder().program(new Program()).toApplicationForm();
		RegisteredUser reviewerUser = new RegisteredUserBuilder().id(11).toUser();
		Reviewer reviewer = new ReviewerBuilder().id(1).user(reviewerUser).toReviewer();
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("bob@bobson.com", "Bob Bobson");

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "Notification - Reviewer assigned", "private/staff/admin/mail/reviewer_assigned_notification.ftl", model)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		adminMailSender.sendReviewerAssignedNotification(Arrays.asList(reviewer), admin, form);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock);

	}
	
	
	@Before
	public void setUp() {
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock);
	}
}
