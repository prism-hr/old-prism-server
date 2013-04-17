package com.zuehlke.pgadmissions.timers;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.mail.SupervisorMailSender;

public class SupervisorNotificationTaskTest {
    
	private SessionFactory sessionFactoryMock;
	
	private Session sessionMock;
	
	private SupervisorNotificationTask supervisorNotificationTask;
	
	private SupervisorMailSender mailServiceMock;
	
	private SupervisorDAO supervisorDAOMock;
	
	@Before
	public void setup() {
	    sessionFactoryMock = EasyMock.createMock(SessionFactory.class);
	    sessionMock = EasyMock.createMock(Session.class);
	    mailServiceMock = EasyMock.createMock(SupervisorMailSender.class);
	    supervisorDAOMock = EasyMock.createMock(SupervisorDAO.class);       
	    supervisorNotificationTask = new SupervisorNotificationTask(sessionFactoryMock, mailServiceMock, supervisorDAOMock);
	}

	@Test
	public void shouldGetPrimarySupervisorsAndSendNotifications() throws UnsupportedEncodingException {
        Transaction transactionOne = EasyMock.createMock(Transaction.class);
        Transaction transactionTwo = EasyMock.createMock(Transaction.class);
        Transaction transactionThree = EasyMock.createMock(Transaction.class);
        EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
        EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
        EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
        EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionThree);

        RegisteredUser admin1=new RegisteredUserBuilder().id(1).build();
        RegisteredUser admin2=new RegisteredUserBuilder().id(2).build();
        Program program = new ProgramBuilder().id(20).administrators(admin1, admin2).build();
        ApplicationForm appFormOne = new ApplicationFormBuilder().id(987).program(program).build();
        ApplicationForm appFormTwo = new ApplicationFormBuilder().id(988).program(program).build();
        ApprovalRound appRoundOne = new ApprovalRoundBuilder().id(54).application(appFormOne).build();
        ApprovalRound appRoundTwo = new ApprovalRoundBuilder().id(55).application(appFormTwo).build();
        Supervisor supervisorOne = new SupervisorBuilder().user(new RegisteredUserBuilder().email("hello@test.com").build()).id(1).approvalRound(appRoundOne).build();
        Supervisor supervisorTwo = new SupervisorBuilder().user(new RegisteredUserBuilder().email("hello@test.com").build()).id(2).approvalRound(appRoundTwo).build();
        sessionMock.refresh(supervisorOne);
        sessionMock.refresh(supervisorTwo);
        List<Supervisor> supervisorList = Arrays.asList(supervisorOne, supervisorTwo);
        EasyMock.expect(supervisorDAOMock.getPrimarySupervisorsDueNotification()).andReturn(supervisorList);
        
        transactionOne.commit();

        mailServiceMock.sendPrimarySupervisorConfirmationNotificationAndCopyAdmins(supervisorOne, asList(admin1, admin2));

        supervisorDAOMock.save(supervisorOne);
        transactionTwo.commit();

        mailServiceMock.sendPrimarySupervisorConfirmationNotificationAndCopyAdmins(supervisorTwo, asList(admin1, admin2));
        supervisorDAOMock.save(supervisorTwo);
        transactionThree.commit();

        EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, supervisorDAOMock);

        supervisorNotificationTask.run();

        EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, supervisorDAOMock);
        assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(supervisorOne.getLastNotified(), Calendar.DATE));
        assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(supervisorTwo.getLastNotified(), Calendar.DATE));
    }
	
	@Test
	public void shouldRollBackTransactionIfExceptionOccurs() throws UnsupportedEncodingException {
        Transaction transactionOne = EasyMock.createMock(Transaction.class);
        Transaction transactionTwo = EasyMock.createMock(Transaction.class);
        Transaction transactionThree = EasyMock.createMock(Transaction.class);
        EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
        EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
        EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
        EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionThree);
        
        RegisteredUser admin1=new RegisteredUserBuilder().id(1).build();
        RegisteredUser admin2=new RegisteredUserBuilder().id(2).build();
        Program program = new ProgramBuilder().id(20).administrators(admin1, admin2).build();
        ApplicationForm appFormOne = new ApplicationFormBuilder().id(987).program(program).build();
        ApplicationForm appFormTwo = new ApplicationFormBuilder().id(988).program(program).build();
        ApprovalRound appRoundOne = new ApprovalRoundBuilder().id(54).application(appFormOne).build();
        ApprovalRound appRoundTwo = new ApprovalRoundBuilder().id(55).application(appFormTwo).build();
        Supervisor supervisorOne = new SupervisorBuilder().user(new RegisteredUserBuilder().email("hello@test.com").build()).id(1).approvalRound(appRoundOne).build();
        Supervisor supervisorTwo = new SupervisorBuilder().user(new RegisteredUserBuilder().email("hello@test.com").build()).id(2).approvalRound(appRoundTwo).build();
        sessionMock.refresh(supervisorOne);
        sessionMock.refresh(supervisorTwo);
        EasyMock.expect(supervisorDAOMock.getPrimarySupervisorsDueNotification()).andReturn(asList(supervisorOne, supervisorTwo));
        
        transactionOne.commit();
        mailServiceMock.sendPrimarySupervisorConfirmationNotificationAndCopyAdmins(supervisorOne, asList(admin1, admin2));

        EasyMock.expectLastCall().andThrow(new RuntimeException());
        transactionTwo.rollback();
        mailServiceMock.sendPrimarySupervisorConfirmationNotificationAndCopyAdmins(supervisorTwo, asList(admin1, admin2));
        supervisorDAOMock.save(supervisorTwo);
        transactionThree.commit();

        EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, supervisorDAOMock);

        supervisorNotificationTask.run();

        EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, mailServiceMock, supervisorDAOMock);
        assertNull(supervisorOne.getLastNotified());
        assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(supervisorTwo.getLastNotified(), Calendar.DATE));
    }
}
