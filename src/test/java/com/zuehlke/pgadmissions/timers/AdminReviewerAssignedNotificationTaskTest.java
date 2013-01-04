package com.zuehlke.pgadmissions.timers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ReviewerDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.mail.AdminMailSender;


public class AdminReviewerAssignedNotificationTaskTest {
	private SessionFactory sessionFactoryMock;
	private Session sessionMock;
	private  AdminReviewerAssignedNotificationTask adminReviewerAssignedNotificationTask;
	private AdminMailSender adminMailSenderMock;	
	private ReviewerDAO reviewerDAOMock;
	
	@Test
	public void shouldGetAdminsRequireReviewerAssignedNotificationAndSendNotifications() throws UnsupportedEncodingException {
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
			
			RegisteredUser reviewerUser1 = new RegisteredUserBuilder().id(9).build();
			RegisteredUser reviewerUser2 = new RegisteredUserBuilder().id(9).build();
			ReviewRound reviewRound = new ReviewRoundBuilder().application(form).id(1).build();
			Reviewer reviewer1 = new ReviewerBuilder().reviewRound(reviewRound).id(1).user(reviewerUser1).build();
			Reviewer reviewer2 = new ReviewerBuilder().reviewRound(reviewRound).id(2).user(reviewerUser2).build();
			
			sessionMock.refresh(reviewer1);
			sessionMock.refresh(reviewer2);
			EasyMock.expect(reviewerDAOMock.getReviewersRequireAdminNotification()).andReturn(Arrays.asList(reviewer1, reviewer2));
			transactionOne.commit();

			adminMailSenderMock.sendReviewerAssignedNotification(form, reviewer1);
			reviewerDAOMock.save(reviewer1);
			transactionTwo.commit();

			adminMailSenderMock.sendReviewerAssignedNotification(form, reviewer2);
			reviewerDAOMock.save(reviewer2);
			transactionThree.commit();

			EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, adminMailSenderMock, reviewerDAOMock);

			adminReviewerAssignedNotificationTask.run();

			EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, adminMailSenderMock, reviewerDAOMock);
			assertNotNull(reviewer1.getDateAdminsNotified());
			assertNotNull(reviewer2.getDateAdminsNotified());
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

		RegisteredUser admin1 = new RegisteredUserBuilder().id(8).build();
		
		Program program = new ProgramBuilder().administrators(admin1).id(1).build();
		ApplicationForm form = new ApplicationFormBuilder().program(program).id(1).build();
		
		RegisteredUser reviewerUser1 = new RegisteredUserBuilder().id(9).build();
		RegisteredUser reviewerUser2 = new RegisteredUserBuilder().id(9).build();
		ReviewRound reviewRound = new ReviewRoundBuilder().application(form).id(1).build();
		Reviewer reviewer1 = new ReviewerBuilder().reviewRound(reviewRound).id(1).user(reviewerUser1).build();
		Reviewer reviewer2 = new ReviewerBuilder().reviewRound(reviewRound).id(2).user(reviewerUser2).build();	
		
		sessionMock.refresh(reviewer1);
		sessionMock.refresh(reviewer2);

		List<Reviewer> reviewerList = Arrays.asList(reviewer1, reviewer2);
		EasyMock.expect(reviewerDAOMock.getReviewersRequireAdminNotification()).andReturn(reviewerList);
		
		transactionOne.commit();

		adminMailSenderMock.sendReviewerAssignedNotification(form, reviewer1);
		EasyMock.expectLastCall().andThrow(new RuntimeException("aaaah!"));		
		transactionTwo.rollback();

		adminMailSenderMock.sendReviewerAssignedNotification(form, reviewer2);
		reviewerDAOMock.save(reviewer2);
		transactionThree.commit();

		EasyMock.replay(sessionFactoryMock, sessionMock, transactionOne, transactionTwo,transactionThree, adminMailSenderMock, reviewerDAOMock);

		adminReviewerAssignedNotificationTask.run();

		EasyMock.verify(sessionFactoryMock, sessionMock, transactionOne, transactionTwo, transactionThree, adminMailSenderMock, reviewerDAOMock);
		assertNull(reviewer1.getDateAdminsNotified());
		assertNotNull(reviewer2.getDateAdminsNotified());
	}

	@Before
	public void setup() {
		sessionFactoryMock = EasyMock.createMock(SessionFactory.class);
		sessionMock = EasyMock.createMock(Session.class);

		adminMailSenderMock = EasyMock.createMock(AdminMailSender.class);
		reviewerDAOMock = EasyMock.createMock(ReviewerDAO.class);				
		adminReviewerAssignedNotificationTask = new  AdminReviewerAssignedNotificationTask(sessionFactoryMock, adminMailSenderMock,reviewerDAOMock);

	}

}
