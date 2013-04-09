package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.NEW_USER_SUGGESTION;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.EmailTemplateBuilder;
import com.zuehlke.pgadmissions.domain.builders.PendingRoleNotificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.services.EmailTemplateService;
import com.zuehlke.pgadmissions.utils.Environment;

public class NewUserMailSenderTest {

	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private MessageSource msgSourceMock;
	private EmailTemplateService templateServiceMock;
	private NewUserMailSender mailSender;

	@Test
	public void shouldReturnCorrectlyPopulatedModel() {

		RegisteredUser admin = new RegisteredUserBuilder().id(2).build();
		Program program = new ProgramBuilder().id(1).title("bob").build();
		Role role_1 = new RoleBuilder().id(4).authorityEnum(Authority.SUPERADMINISTRATOR).build();
		PendingRoleNotification roleNotification_1 = new PendingRoleNotificationBuilder().program(program).role(role_1).addedByUser(admin).build();

		Role role_2 = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).id(3).build();
		PendingRoleNotification roleNotification_2 = new PendingRoleNotificationBuilder().program(program).role(role_2).addedByUser(admin).build();

		Role role_3 = new RoleBuilder().authorityEnum(Authority.REVIEWER).id(4).build();
		PendingRoleNotification roleNotification_3 = new PendingRoleNotificationBuilder().program(program).role(role_3).addedByUser(admin).build();
		
		Role role_4 = new RoleBuilder().authorityEnum(Authority.INTERVIEWER).id(4).build();
		PendingRoleNotification roleNotification_4 = new PendingRoleNotificationBuilder().program(program).role(role_4).addedByUser(admin).build();
		
		Role role_5 = new RoleBuilder().authorityEnum(Authority.SUPERVISOR).id(5).build();
		PendingRoleNotification roleNotification_5 = new PendingRoleNotificationBuilder().program(program).role(role_5).addedByUser(admin).build();
		
		Role role_6 = new RoleBuilder().authorityEnum(Authority.APPROVER).id(5).build();
		PendingRoleNotification roleNotification_6 = new PendingRoleNotificationBuilder().program(program).role(role_6).addedByUser(admin).build();
		
		RegisteredUser user = new RegisteredUserBuilder().id(1).firstName("Bob").lastName("Smith").email("email@test.com").pendingRoleNotifications(roleNotification_1, roleNotification_2, roleNotification_3, roleNotification_4, roleNotification_5, roleNotification_6).build();

		Map<String, Object> model = mailSender.createModel(user);

		assertEquals(user, model.get("newUser"));
		assertEquals(admin, model.get("admin"));
		assertEquals(program, model.get("program"));
		assertEquals("Superadministrator, Administrator, Default Reviewer, Default Interviewer, Default Supervisor and Approver for bob", model.get("newRoles"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));
	}

	@Test
	public void shouldSendNotificationEmailToUser() throws UnsupportedEncodingException {
		final Map<String, Object> model = new HashMap<String, Object>();
		final Program program = new ProgramBuilder().id(1).title("program title").build();
		mailSender = new NewUserMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock) {

			@Override
			public Map<String, Object> createModel(RegisteredUser user) {
				model.put("program", program);
				model.put("newRoles", "user role");

				return model;
			}
		};

		RegisteredUser admin = new RegisteredUserBuilder().id(2).build();
		
		Role role_1 = new RoleBuilder().id(4).authorityEnum(Authority.ADMINISTRATOR).build();
		PendingRoleNotification roleNotification_1 = new PendingRoleNotificationBuilder().program(program).role(role_1).addedByUser(admin).build();

		Role role_2 = new RoleBuilder().authorityEnum(Authority.INTERVIEWER).id(3).build();
		PendingRoleNotification roleNotification_2 = new PendingRoleNotificationBuilder().program(program).role(role_2).addedByUser(admin).build();

		RegisteredUser user = new RegisteredUserBuilder().id(1).firstName("Bob").lastName("Smith").email("email@test.com").pendingRoleNotifications(roleNotification_1, roleNotification_2).build();

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("email@test.com", "Bob Smith");
		
		EmailTemplate template = new EmailTemplateBuilder().active(true)
				.content("New user suggestion template").name(NEW_USER_SUGGESTION).build();
		expect(templateServiceMock.getActiveEmailTemplate(NEW_USER_SUGGESTION)).andReturn(template);

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "resolved subject", NEW_USER_SUGGESTION, template.getContent(), model, null))
		.andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.expect(msgSourceMock.getMessage(//
				EasyMock.eq("registration.invitation"),// 
				EasyMock.aryEq(new Object[] { }),// 
				EasyMock.eq((Locale) null))).andReturn("resolved subject");

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock);

		mailSender.sendNewUserNotification(user);
		EasyMock.verify(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock);
	}
	
	@Test
	public void shouldSendNotificationEmailWithCorrectSubjectToUserWhenProgramIsNull() throws UnsupportedEncodingException {
		final Map<String, Object> model = new HashMap<String, Object>();

		mailSender = new NewUserMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock) {

			@Override
			public Map<String, Object> createModel(RegisteredUser user) {
				model.put("program", null);
				model.put("newRoles", "user role");

				return model;
			}
		};

		RegisteredUser admin = new RegisteredUserBuilder().id(2).build();
		Program program = new ProgramBuilder().id(1).build();
		Role role_1 = new RoleBuilder().id(4).authorityEnum(Authority.ADMINISTRATOR).build();
		PendingRoleNotification roleNotification_1 = new PendingRoleNotificationBuilder().program(program).role(role_1).addedByUser(admin).build();

		Role role_2 = new RoleBuilder().authorityEnum(Authority.INTERVIEWER).id(3).build();
		PendingRoleNotification roleNotification_2 = new PendingRoleNotificationBuilder().program(program).role(role_2).addedByUser(admin).build();

		RegisteredUser user = new RegisteredUserBuilder().id(1).firstName("Bob").lastName("Smith").email("email@test.com").pendingRoleNotifications(roleNotification_1, roleNotification_2).build();

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("email@test.com", "Bob Smith");
		
		EmailTemplate template = new EmailTemplateBuilder().active(true)
				.content("New user suggestion template").name(NEW_USER_SUGGESTION).build();
		expect(templateServiceMock.getActiveEmailTemplate(NEW_USER_SUGGESTION)).andReturn(template);

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "resolved subject", NEW_USER_SUGGESTION, template.getContent(), model, null))
		.andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.expect(msgSourceMock.getMessage(//
				EasyMock.eq("registration.invitation.superadmin"),// 
				EasyMock.aryEq(new Object[] { "user role"}),// 
				EasyMock.eq((Locale) null))).andReturn("resolved subject");

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock);

		mailSender.sendNewUserNotification(user);
		EasyMock.verify(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock);
	}
	@Before
	public void setUp() {
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		msgSourceMock = createMock(MessageSource.class);
		templateServiceMock = createMock(EmailTemplateService.class);
		mailSender = new NewUserMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock);
	}
}
