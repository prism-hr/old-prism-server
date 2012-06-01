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
import com.zuehlke.pgadmissions.mail.ApproverMailSender;

public class ApproverNotificationTaskTest {

	private SessionFactory sessionFactoryMock;
	private Session sessionMock;
	private ApplicationFormDAO applicationDAOMock;
	private ApproverNotificationTask approverNotificationTask;
	private ApproverMailSender approverMailSenderMock;
	

	@Test
	public void shouldGetAdminsAndSendReminders() {
		Transaction transactionOne = EasyMock.createMock(Transaction.class);
		Transaction transactionTwo = EasyMock.createMock(Transaction.class);
		Transaction transactionThree = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionThree);
		RegisteredUser approver1 = new RegisteredUserBuilder().id(8).toUser();
		
		Program program = new ProgramBuilder().approver(approver1).id(1).toProgram();
		ApplicationForm form1 = new ApplicationFormBuilder().program(program).id(1).toApplicationForm();
		ApplicationForm form2 = new ApplicationFormBuilder().program(program).id(1).toApplicationForm();
		
		EasyMock.expect(applicationDAOMock.getApplicationsDueApprovalNotifications()).andReturn(Arrays.asList(form1, form2));
		transactionOne.commit();
		sessionMock.refresh(form1);
		
		approverMailSenderMock.sendApprovalNotificationToApprovers(form1);
		applicationDAOMock.save(form1);
		transactionTwo.commit();
		sessionMock.refresh(form2);

		approverMailSenderMock.sendApprovalNotificationToApprovers(form2);
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
		approverMailSenderMock = EasyMock.createMock(ApproverMailSender.class);
		approverNotificationTask = new ApproverNotificationTask(sessionFactoryMock, approverMailSenderMock, applicationDAOMock);

	}

	
	
}
