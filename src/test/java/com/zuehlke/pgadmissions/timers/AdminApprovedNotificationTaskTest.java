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

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.AdminMailSender;

public class AdminApprovedNotificationTaskTest {
	private AdminApprovedNotificationTask approvedTask;

	private ApplicationFormDAO applicationDaoMock;
	private SessionFactory sessionFactoryMock;
	private AdminMailSender mailSenderMock;
	private Session sessionMock;

	@Before
	public void setup() {
		applicationDaoMock = EasyMock.createMock(ApplicationFormDAO.class);
		sessionFactoryMock = EasyMock.createMock(SessionFactory.class);
		mailSenderMock = EasyMock.createMock(AdminMailSender.class);
		sessionMock = EasyMock.createMock(Session.class);

		approvedTask = new AdminApprovedNotificationTask(sessionFactoryMock, mailSenderMock, applicationDaoMock);
	}

	@Test
	public void shouldSendApprovedNotifications() throws Exception {
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
		EasyMock.expect(applicationDaoMock.getApplicationsDueApprovedNotifications()).andReturn(applList);
		tx1.commit();

		Transaction tx2 = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(tx2);
		
		sessionMock.refresh(application);
		mailSenderMock.sendAdminAndSupervisorApprovedNotification(application, approver);
		applicationDaoMock.save(application);

		tx2.commit();
		EasyMock.replay(applicationDaoMock, sessionFactoryMock, sessionMock, mailSenderMock, tx1, tx2);

		approvedTask.run();

		EasyMock.verify(applicationDaoMock, sessionFactoryMock, sessionMock, mailSenderMock, tx1, tx2);
		Assert.assertEquals(NotificationType.APPROVED_NOTIFICATION, application.getNotificationRecords().get(application.getNotificationRecords().size()-1).getNotificationType());
	}

	@Test
	public void shouldCallMailSenderEvenIfApproverIsAdmin() throws Exception {
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();

		Transaction tx1 = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(tx1);

		RegisteredUser admin = new RegisteredUserBuilder().id(18).build();
		Program program = new ProgramBuilder().id(1023).administrators(admin).build();
		ApplicationForm application = new ApplicationFormBuilder().id(10)//
				.program(program).approver(admin)//
				.build();
		List<ApplicationForm> applList = new ArrayList<ApplicationForm>();
		applList.add(application);
		EasyMock.expect(applicationDaoMock.getApplicationsDueApprovedNotifications()).andReturn(applList);
		tx1.commit();

		Transaction tx2 = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(tx2);
		
		sessionMock.refresh(application);
		mailSenderMock.sendAdminAndSupervisorApprovedNotification(application, admin);
		applicationDaoMock.save(application);

		tx2.commit();
		EasyMock.replay(applicationDaoMock, sessionFactoryMock, sessionMock, mailSenderMock, tx1, tx2);

		approvedTask.run();

		EasyMock.verify(applicationDaoMock, sessionFactoryMock, sessionMock, mailSenderMock, tx1, tx2);
		Assert.assertEquals(NotificationType.APPROVED_NOTIFICATION, application.getNotificationRecords().get(application.getNotificationRecords().size()-1).getNotificationType());
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
		EasyMock.expect(applicationDaoMock.getApplicationsDueApprovedNotifications()).andReturn(applList);
		tx1.commit();

		Transaction tx2 = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(tx2);
		
		sessionMock.refresh(application);
		mailSenderMock.sendAdminAndSupervisorApprovedNotification(application, approver);
		EasyMock.expectLastCall().andThrow(new RuntimeException());

		tx2.rollback();
		EasyMock.replay(applicationDaoMock, sessionFactoryMock, sessionMock, mailSenderMock, tx1, tx2);

		approvedTask.run();

		EasyMock.verify(applicationDaoMock, sessionFactoryMock, sessionMock, mailSenderMock, tx1, tx2);
	}
}
