package com.zuehlke.pgadmissions.timers;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.mail.AdminMailSender;
import com.zuehlke.pgadmissions.services.CommentService;

public class AdminInterviewFeedbackNotificationTaskTest {

	private SessionFactory sessionFactoryMock;
	private Session sessionMock;
	private AdminInterviewFeedbackNotificationTask adminInterviewTask;
	private AdminMailSender adminMailSenderMock;
	private CommentService commentServiceMock;
	
	@Test
	public void shouldGetAdminsAndSendReminders() {
		Transaction transactionOne = EasyMock.createMock(Transaction.class);
		Transaction transactionTwo = EasyMock.createMock(Transaction.class);
		Transaction transactionThree = EasyMock.createMock(Transaction.class);
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock).anyTimes();
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionOne);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionTwo);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionThree);
		RegisteredUser admin1 = new RegisteredUserBuilder().id(8).build();
		
		Program program = new ProgramBuilder().administrators(admin1).id(1).build();
		ApplicationForm form = new ApplicationFormBuilder().program(program).id(1).build();
		
		RegisteredUser interviewer = new RegisteredUserBuilder().id(9).build();
		InterviewComment interviewComment1 = new InterviewCommentBuilder().user(interviewer).application(form).id(1).build();
		InterviewComment interviewComment2 = new InterviewCommentBuilder().user(interviewer).application(form).id(2).build();
		sessionMock.refresh(interviewComment1);
		sessionMock.refresh(interviewComment2);
		EasyMock.expect(commentServiceMock.getInterviewCommentsDueNotification()).andReturn(Arrays.asList(interviewComment1, interviewComment2));
		transactionOne.commit();

		adminMailSenderMock.sendAdminInterviewNotification(form, interviewer);
		commentServiceMock.save(interviewComment1);
		transactionTwo.commit();

		adminMailSenderMock.sendAdminInterviewNotification(form, interviewer);
		commentServiceMock.save(interviewComment2);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, adminMailSenderMock, commentServiceMock);

		adminInterviewTask.run();

		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, adminMailSenderMock, commentServiceMock);
		assertEquals(true, interviewComment1.isAdminsNotified());
		assertEquals(true, interviewComment2.isAdminsNotified());
	}
	
	
	
	@Before
	public void setup() {
		sessionFactoryMock = EasyMock.createMock(SessionFactory.class);
		sessionMock = EasyMock.createMock(Session.class);
		commentServiceMock = EasyMock.createMock(CommentService.class);
		adminMailSenderMock = EasyMock.createMock(AdminMailSender.class);
		adminInterviewTask = new AdminInterviewFeedbackNotificationTask(sessionFactoryMock, adminMailSenderMock, commentServiceMock);

	}

	
	
}
