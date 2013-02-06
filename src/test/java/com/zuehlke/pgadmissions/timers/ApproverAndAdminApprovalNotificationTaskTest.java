package com.zuehlke.pgadmissions.timers;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

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
import com.zuehlke.pgadmissions.mail.ApproverAdminMailSender;

public class ApproverAndAdminApprovalNotificationTaskTest {

	private SessionFactory sessionFactoryMock;
	private Session sessionMock;
	private ApplicationFormDAO applicationDAOMock;
	private ApproverAndAdminApprovalNotificationTask approverNotificationTask;
	private ApproverAdminMailSender approverMailSenderMock;
	

	@Test
	public void shouldGetAdminsAndSendReminders() {
		Transaction transactionOne = EasyMock.createMock(Transaction.class);
		Transaction transactionTwo = EasyMock.createMock(Transaction.class);
		Transaction transactionThree = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionThree);
		RegisteredUser approver1 = new RegisteredUserBuilder().id(8).build();
		
		Program program = new ProgramBuilder().approver(approver1).id(1).build();
		ApplicationForm form1 = new ApplicationFormBuilder().program(program).id(1).build();
		ApplicationForm form2 = new ApplicationFormBuilder().program(program).id(1).build();
		
		EasyMock.expect(applicationDAOMock.getApplicationsDueApprovalNotifications()).andReturn(Arrays.asList(form1, form2));
		transactionOne.commit();
		sessionMock.refresh(form1);
		
		approverMailSenderMock.sendApprovalNotificationToApproversAndAdmins(form1);
		applicationDAOMock.save(form1);
		transactionTwo.commit();
		sessionMock.refresh(form2);

		approverMailSenderMock.sendApprovalNotificationToApproversAndAdmins(form2);
		applicationDAOMock.save(form2);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, applicationDAOMock);

		approverNotificationTask.run();

		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, applicationDAOMock);
	}
	
	
	
	
	
	@Before
	public void setup() {
		sessionFactoryMock = EasyMock.createMock(SessionFactory.class);
		sessionMock = EasyMock.createMock(Session.class);
		applicationDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		approverMailSenderMock = EasyMock.createMock(ApproverAdminMailSender.class);
		approverNotificationTask = new ApproverAndAdminApprovalNotificationTask(sessionFactoryMock, approverMailSenderMock, applicationDAOMock);

	}

	
	
}
