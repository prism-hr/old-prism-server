package com.zuehlke.pgadmissions.timers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.mail.RegistryMailSender;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.utils.CommentFactory;

public class RegistryNotificationTimerTaskTest {
		private RegistryNotificationTimerTask registryTask;

		private ApplicationsService applicationServiceMock;
		private SessionFactory sessionFactoryMock;
		private RegistryMailSender mailSenderMock;
		private Session sessionMock;

		private ConfigurationService configurationServiceMock;

		private CommentFactory commentFactoryMock;

		private CommentService commentServiceMock;

		@Before
		public void setup() {
			applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
			sessionFactoryMock = EasyMock.createMock(SessionFactory.class);
			mailSenderMock = EasyMock.createMock(RegistryMailSender.class);
			sessionMock = EasyMock.createMock(Session.class);
			configurationServiceMock = EasyMock.createMock(ConfigurationService.class);
			commentFactoryMock = EasyMock.createMock(CommentFactory.class);
			commentServiceMock = EasyMock.createMock(CommentService.class);
			registryTask = new RegistryNotificationTimerTask(sessionFactoryMock, mailSenderMock, applicationServiceMock, configurationServiceMock, commentFactoryMock,commentServiceMock);
		}

		@Test
		public void shouldSendRegistryNotifications() throws Exception {
			EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();

			Transaction tx1 = EasyMock.createMock(Transaction.class);
			EasyMock.expect(sessionMock.beginTransaction()).andReturn(tx1);
		
			RegisteredUser adminRequestingNotification = new RegisteredUserBuilder().id(5).build();
			ApplicationForm application = new ApplicationFormBuilder().id(10).adminRequestedRegistry(adminRequestingNotification).build();
			List<ApplicationForm> applList = new ArrayList<ApplicationForm>();
			applList.add(application);
			EasyMock.expect(applicationServiceMock.getApplicationsDueRegistryNotification()).andReturn(applList);
			tx1.commit();

			Transaction tx2 = EasyMock.createMock(Transaction.class);
			EasyMock.expect(sessionMock.beginTransaction()).andReturn(tx2);
			
			sessionMock.refresh(application);
			Person registryUser1 = new PersonBuilder().id(2).firstname("Bob").lastname("Jones").email("jones@test.com").build();
			Person registryUser2 = new PersonBuilder().id(3).firstname("Karla").lastname("Peters").email("peters@test.com").build();
			Person registryUser3 = new PersonBuilder().id(5).firstname("Hanna").lastname("Hobnob").email("hanna@test.com").build();
			List<Person> registryContacts = Arrays.asList(registryUser1, registryUser2, registryUser3);
			EasyMock.expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryContacts);
			
			mailSenderMock.sendApplicationToRegistryContacts(application, registryContacts);
			Comment comment = new CommentBuilder().id(5).build();
			EasyMock.expect(commentFactoryMock.createComment(application, adminRequestingNotification, "Referred to UCL Admissions for advice on eligibility and fees status. Referral send to Bob Jones (jones@test.com), Karla Peters (peters@test.com) and Hanna Hobnob (hanna@test.com).", CommentType.GENERIC,null)).andReturn(comment);
			commentServiceMock.save(comment);
			applicationServiceMock.save(application);
			
			tx2.commit();
			EasyMock.replay(applicationServiceMock, sessionFactoryMock, sessionMock, mailSenderMock,configurationServiceMock,commentFactoryMock, commentServiceMock,  tx1, tx2);

			registryTask.run();

			EasyMock.verify(applicationServiceMock, sessionFactoryMock, sessionMock, mailSenderMock,configurationServiceMock, commentFactoryMock,commentServiceMock, tx1, tx2);
			Assert.assertFalse(application.getRegistryUsersDueNotification());
			
		}

		@Test
		public void shouldRollBackTransactionIfExceptionOccurs() throws Exception {
			EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();

			Transaction tx1 = EasyMock.createMock(Transaction.class);
			EasyMock.expect(sessionMock.beginTransaction()).andReturn(tx1);

			RegisteredUser admin = new RegisteredUserBuilder().id(18).build();
			Program program = new ProgramBuilder().id(1023).administrators(admin).build();
			RegisteredUser approver = new RegisteredUserBuilder().id(123).build();
			ApplicationForm application = new ApplicationFormBuilder().id(10)//
					.program(program).approver(approver)//
					.build();
			List<ApplicationForm> applList = new ArrayList<ApplicationForm>();
			applList.add(application);
			EasyMock.expect(applicationServiceMock.getApplicationsDueRegistryNotification()).andReturn(applList);
			tx1.commit();

			Transaction tx2 = EasyMock.createMock(Transaction.class);
			EasyMock.expect(sessionMock.beginTransaction()).andReturn(tx2);
			
			sessionMock.refresh(application);
			Person registryUser1 = new PersonBuilder().id(2).firstname("Bob").lastname("Jones").email("jones@test.com").build();
			Person registryUser2 = new PersonBuilder().id(3).firstname("Karla").lastname("Peters").email("peters@test.com").build();
			List<Person> registryContacts = Arrays.asList(registryUser1, registryUser2);
			EasyMock.expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryContacts);
			mailSenderMock.sendApplicationToRegistryContacts(application, registryContacts);
			EasyMock.expectLastCall().andThrow(new RuntimeException());

			tx2.rollback();
			EasyMock.replay(applicationServiceMock, sessionFactoryMock, sessionMock, mailSenderMock, configurationServiceMock, tx1, tx2);

			registryTask.run();

			EasyMock.verify(applicationServiceMock, sessionFactoryMock, sessionMock, mailSenderMock,configurationServiceMock,  tx1, tx2);
		}
	}
