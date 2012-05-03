package com.zuehlke.pgadmissions.mail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.utils.Environment;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.utils.Environment;


public class AdminMailSenderTest {

	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;

	private AdminMailSender adminMailSender;

	@Test
	public void shouldReturnCorrectlyPopulatedModel() {
		
		RegisteredUser admin = new RegisteredUserBuilder().id(1).firstName("Bob").lastName("Bobson").email("bob@bobson.com").id(1).toUser();
		RegisteredUser applicant = new RegisteredUserBuilder().id(10).toUser();
		RegisteredUser reviewer = new RegisteredUserBuilder().id(11).toUser();
		RegisteredUser adminOne = new RegisteredUserBuilder().id(1).email("bob@test.com").toUser();
		RegisteredUser adminTwo = new RegisteredUserBuilder().id(8).email("alice@test.com").toUser();

		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().administrators(adminOne, adminTwo).toProgram())
				.toApplicationForm();

		Map<String, Object> model = adminMailSender.createModel(form, adminOne, null);
		assertEquals(form, model.get("application"));
		assertEquals(adminOne, model.get("admin"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));

	}

	@Test
	public void shouldSendReminderEmailToAdmin() throws UnsupportedEncodingException {
		final Map<String, Object> model = new HashMap<String, Object>();

		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {
			@Override
			Map<String, Object> createModel(ApplicationForm application, RegisteredUser administrator, RegisteredUser reviewer) {
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
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "Application 2 by Jane Smith " + subjectMessage, templatename, model))
				.andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		adminMailSender.sendReminderToAdmin(form, administratorOne, subjectMessage, templatename);
		EasyMock.verify(mimeMessagePreparatorFactoryMock, javaMailSenderMock);
	}
	
	@Test
	public void shouldSendAdminNotificationForNewReviewComment() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {

			@Override
			Map<String, Object> createModel(ApplicationForm form, RegisteredUser admin, RegisteredUser reviewer) {
				return model;
			}

		};
		RegisteredUser admin = new RegisteredUserBuilder().id(1).firstName("Bob").lastName("Bobson").email("bob@bobson.com").id(1).toUser();
		ApplicationForm form = new ApplicationFormBuilder().program(new Program()).toApplicationForm();
		RegisteredUser reviewer = new RegisteredUserBuilder().id(11).toUser();
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("bob@bobson.com", "Bob Bobson");

		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "Notification - review added",
						"private/staff/admin/mail/review_submission_notification.ftl", model)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		adminMailSender.sendAdminReviewNotification(admin, form , reviewer);

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
			public void sendReminderToAdmin(ApplicationForm passedFord, RegisteredUser passedAdmin, String passedSubjectMessage, String passedTemplatename)
					throws UnsupportedEncodingException {
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

		adminMailSender.sendReminderToAdmins(form, subjectMessage, templatename);
		assertTrue(passedAdmins.containsAll(Arrays.asList(administratorOne, administratorTwo)));
	}
	
	
	@Test
	public void shouldNotStopIfOneEmailFails() throws UnsupportedEncodingException {

		final List<RegisteredUser> passedAdmins = new ArrayList<RegisteredUser>();

		final RegisteredUser administratorOne = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		final RegisteredUser administratorTwo = new RegisteredUserBuilder().id(2).firstName("charlie").lastName("crock").email("cc@test.com").toUser();
		Program program = new ProgramBuilder().administrators(administratorOne, administratorTwo).toProgram();

		final ApplicationForm form = new ApplicationFormBuilder().id(2).program(program).toApplicationForm();

		final String subjectMessage = "is overdue validation";
		final String templatename = "private/staff/admin/mail/application_validation_reminder.ftl";

		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {
			@Override
			public void sendReminderToAdmin(ApplicationForm passedFord, RegisteredUser passedAdmin, String passedSubjectMessage, String passedTemplatename)
					throws UnsupportedEncodingException {
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

		adminMailSender.sendReminderToAdmins(form, subjectMessage, templatename);
		assertTrue(passedAdmins.contains(administratorTwo));
	}
	
	
	@Before
	public void setUp() {
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock);
	}
}
