package com.zuehlke.pgadmissions.timers;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.easymock.EasyMock;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.mail.AdminMailSender;
import com.zuehlke.pgadmissions.services.CommentService;

public class AdminReviewFeedbackNotificationTaskTest {

	private SessionFactory sessionFactoryMock;
	private Session sessionMock;
	private AdminReviewFeedbackNotificationTask adminReviewTask;
	private AdminMailSender adminMailSenderMock;
	private CommentService commentServiceMock;
	
	@Test
	public void shouldGetAdminsAndSendReminders() throws UnsupportedEncodingException {
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
		
		RegisteredUser reviewer = new RegisteredUserBuilder().id(9).build();
		ReviewComment reviewComment1 = new ReviewCommentBuilder().user(reviewer).application(form).id(1).build();
		ReviewComment reviewComment2 = new ReviewCommentBuilder().user(reviewer).application(form).id(2).build();
		sessionMock.refresh(reviewComment1);
		sessionMock.refresh(reviewComment2);
		EasyMock.expect(commentServiceMock.getReviewCommentsDueNotification()).andReturn(Arrays.asList(reviewComment1, reviewComment2));
		transactionOne.commit();

		adminMailSenderMock.sendAdminReviewNotification(form, reviewer);
		commentServiceMock.save(reviewComment1);
		transactionTwo.commit();

		adminMailSenderMock.sendAdminReviewNotification(form, reviewer);
		commentServiceMock.save(reviewComment2);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, adminMailSenderMock, commentServiceMock);

		adminReviewTask.run();

		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, adminMailSenderMock, commentServiceMock);
		assertEquals(true, reviewComment1.isAdminsNotified());
		assertEquals(true, reviewComment2.isAdminsNotified());
	}
	
	
	
	@Before
	public void setup() {
		sessionFactoryMock = EasyMock.createMock(SessionFactory.class);
		sessionMock = EasyMock.createMock(Session.class);
		commentServiceMock = EasyMock.createMock(CommentService.class);
		adminMailSenderMock = EasyMock.createMock(AdminMailSender.class);
		adminReviewTask = new AdminReviewFeedbackNotificationTask(sessionFactoryMock, adminMailSenderMock, commentServiceMock);

	}

	
	
}
