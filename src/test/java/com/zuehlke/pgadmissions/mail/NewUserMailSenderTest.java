package com.zuehlke.pgadmissions.mail;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.PendingRoleNotificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.utils.Environment;


public class NewUserMailSenderTest {

	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;

	private NewUserMailSender mailSender;

	@Test
	public void shouldReturnCorrectlyPopulatedModel() {
		
		RegisteredUser admin = new RegisteredUserBuilder().id(2).toUser();
		Program program = new ProgramBuilder().id(1).toProgram();
		Role role_1 = new RoleBuilder().id(4).authorityEnum(Authority.ADMINISTRATOR).toRole();
		PendingRoleNotification roleNotification_1 = new PendingRoleNotificationBuilder().program(program).role(role_1).addedByUser(admin).toPendingRoleNotification();
				
		Role role_2 = new RoleBuilder().authorityEnum(Authority.INTERVIEWER).id(3).toRole();
		PendingRoleNotification roleNotification_2 = new PendingRoleNotificationBuilder().program(program).role(role_2).addedByUser(admin).toPendingRoleNotification();
		
		RegisteredUser user = new RegisteredUserBuilder().id(1).firstName("Bob").lastName("Smith").email("email@test.com").pendingRoleNotifications(roleNotification_1, roleNotification_2).toUser();
		
		Map<String, Object> model = mailSender.createModel(user);
		
		assertEquals(user, model.get("newUser"));
		assertEquals(admin, model.get("admin"));
		assertEquals(program, model.get("program"));
		assertEquals("ADMINISTRATOR, INTERVIEWER", model.get("newRoles"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));
	}

	@Test
	public void shouldSendNotificationEmailToUse() throws UnsupportedEncodingException {
		final Map<String, Object> model = new HashMap<String, Object>();

		mailSender = new NewUserMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {

			@Override
			public Map<String, Object> createModel(RegisteredUser user) {
				return model;
			}				
		};
		

		RegisteredUser admin = new RegisteredUserBuilder().id(2).toUser();
		Program program = new ProgramBuilder().id(1).toProgram();
		Role role_1 = new RoleBuilder().id(4).authorityEnum(Authority.ADMINISTRATOR).toRole();
		PendingRoleNotification roleNotification_1 = new PendingRoleNotificationBuilder().program(program).role(role_1).addedByUser(admin).toPendingRoleNotification();
				
		Role role_2 = new RoleBuilder().authorityEnum(Authority.INTERVIEWER).id(3).toRole();
		PendingRoleNotification roleNotification_2 = new PendingRoleNotificationBuilder().program(program).role(role_2).addedByUser(admin).toPendingRoleNotification();
		
		RegisteredUser user = new RegisteredUserBuilder().id(1).firstName("Bob").lastName("Smith").email("email@test.com").pendingRoleNotifications(roleNotification_1, roleNotification_2).toUser();
		
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);		
		InternetAddress toAddress = new InternetAddress("email@test.com", "Bob Smith");
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "UCL Portal Registration", "private/staff/mail/new_user_suggestion.ftl", model)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		mailSender.sendNewUserNotification(user);
		EasyMock.verify(mimeMessagePreparatorFactoryMock, javaMailSenderMock);
	}

	
	
	
	@Before
	public void setUp() {
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		mailSender = new NewUserMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock);
	}
}
