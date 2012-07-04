package com.zuehlke.pgadmissions.timers;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.mail.RegistryMailSender;
import com.zuehlke.pgadmissions.services.ApplicationsService;

public class RegistryNotificationTimerTaskTest {
		private RegistryNotificationTimerTask registryTask;

		private ApplicationsService applicationServiceMock;
		private SessionFactory sessionFactoryMock;
		private RegistryMailSender mailSenderMock;
		private Session sessionMock;

		@Before
		public void setup() {
			applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
			sessionFactoryMock = EasyMock.createMock(SessionFactory.class);
			mailSenderMock = EasyMock.createMock(RegistryMailSender.class);
			sessionMock = EasyMock.createMock(Session.class);

			registryTask = new RegistryNotificationTimerTask(sessionFactoryMock, mailSenderMock, applicationServiceMock);
		}

		@Test
		public void shouldSendRegistryNotifications() throws Exception {
			EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();

			Transaction tx1 = EasyMock.createMock(Transaction.class);
			EasyMock.expect(sessionMock.beginTransaction()).andReturn(tx1);

			RegisteredUser admin = new RegisteredUserBuilder().id(18).toUser();
			Program program = new ProgramBuilder().id(1023).administrators(admin).toProgram();
			RegisteredUser approver = new RegisteredUserBuilder().id(123).toUser();
			ApplicationForm application = new ApplicationFormBuilder().id(10)//
					.program(program).approver(approver)//
					.toApplicationForm();
			List<ApplicationForm> applList = new ArrayList<ApplicationForm>();
			applList.add(application);
			EasyMock.expect(applicationServiceMock.getApplicationsDueRegistryNotification()).andReturn(applList);
			tx1.commit();

			Transaction tx2 = EasyMock.createMock(Transaction.class);
			EasyMock.expect(sessionMock.beginTransaction()).andReturn(tx2);
			
			sessionMock.refresh(application);
			mailSenderMock.sendApplicationToRegistryContacts(application);
			applicationServiceMock.save(application);

			tx2.commit();
			EasyMock.replay(applicationServiceMock, sessionFactoryMock, sessionMock, mailSenderMock, tx1, tx2);

			registryTask.run();

			EasyMock.verify(applicationServiceMock, sessionFactoryMock, sessionMock, mailSenderMock, tx1, tx2);
			Assert.assertFalse(application.getRegistryUsersDueNotification());
		}

		@Test
		public void shouldRollBackTransactionIfExceptionOccurs() throws Exception {
			EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();

			Transaction tx1 = EasyMock.createMock(Transaction.class);
			EasyMock.expect(sessionMock.beginTransaction()).andReturn(tx1);

			RegisteredUser admin = new RegisteredUserBuilder().id(18).toUser();
			Program program = new ProgramBuilder().id(1023).administrators(admin).toProgram();
			RegisteredUser approver = new RegisteredUserBuilder().id(123).toUser();
			ApplicationForm application = new ApplicationFormBuilder().id(10)//
					.program(program).approver(approver)//
					.toApplicationForm();
			List<ApplicationForm> applList = new ArrayList<ApplicationForm>();
			applList.add(application);
			EasyMock.expect(applicationServiceMock.getApplicationsDueRegistryNotification()).andReturn(applList);
			tx1.commit();

			Transaction tx2 = EasyMock.createMock(Transaction.class);
			EasyMock.expect(sessionMock.beginTransaction()).andReturn(tx2);
			
			sessionMock.refresh(application);
			mailSenderMock.sendApplicationToRegistryContacts(application);
			EasyMock.expectLastCall().andThrow(new RuntimeException());

			tx2.rollback();
			EasyMock.replay(applicationServiceMock, sessionFactoryMock, sessionMock, mailSenderMock, tx1, tx2);

			registryTask.run();

			EasyMock.verify(applicationServiceMock, sessionFactoryMock, sessionMock, mailSenderMock, tx1, tx2);
		}
	}
