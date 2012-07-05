package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;

public class ApplicationFormTest {



	@Test
	public void shouldReturnReviewableFalseIfApplicationFormRejected() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REJECTED)
				.toApplicationForm();
		assertFalse(applicationForm.isModifiable());
	}

	@Test
	public void shouldReturnReviewableFalseIfApplicationFormAccepted() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED)
				.toApplicationForm();
		assertFalse(applicationForm.isModifiable());
	}

	

	@Test
	public void shouldReturnReviewableFalseIfApplicationFormAcceptedRejecteOrwitdrawn() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.UNSUBMITTED).toApplicationForm();
		assertTrue(applicationForm.isModifiable());
		applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertTrue(applicationForm.isModifiable());
		applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REJECTED).toApplicationForm();
		assertFalse(applicationForm.isModifiable());
		applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).toApplicationForm();
		assertFalse(applicationForm.isModifiable());
		applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.WITHDRAWN).toApplicationForm();
		assertFalse(applicationForm.isModifiable());
	}
	
	@Test
	public void shouldReturnDecidedTrueIfRejectedOrApproved() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).toApplicationForm();
		assertTrue(applicationForm.isDecided());
		applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REJECTED).toApplicationForm();
		assertTrue(applicationForm.isDecided());
	}

	@Test
	public void shouldReturnDecidedFalseIfNeitherUnsubmitterOrValidation() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.UNSUBMITTED).toApplicationForm();
		assertFalse(applicationForm.isDecided());
		applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertFalse(applicationForm.isDecided());
	}

	@Test
	public void shouldReturnNotificationOfCorrectType(){
		NotificationRecord validationReminder = new NotificationRecordBuilder().id(1).notificationType(NotificationType.VALIDATION_REMINDER).toNotificationRecord();
		NotificationRecord submissionUpdateNotification = new NotificationRecordBuilder().id(2).notificationType(NotificationType.UPDATED_NOTIFICATION).toNotificationRecord();
		ApplicationForm applicationForm = new ApplicationFormBuilder().notificationRecords(validationReminder).toApplicationForm();
		assertEquals(validationReminder, applicationForm.getNotificationForType(NotificationType.VALIDATION_REMINDER));
		assertNull(applicationForm.getNotificationForType(NotificationType.UPDATED_NOTIFICATION));
		applicationForm.getNotificationRecords().add(submissionUpdateNotification);
		assertEquals(submissionUpdateNotification, applicationForm.getNotificationForType(NotificationType.UPDATED_NOTIFICATION));
	}

	
	@Test
	public void shouldReturnTrueIfInStateByString(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.UNSUBMITTED).toApplicationForm();
		assertTrue(applicationForm.isInState("UNSUBMITTED"));
		assertFalse(applicationForm.isInState("VALIDATION"));
		assertFalse(applicationForm.isInState("BOB"));
	}
	
	@Test
	public void shouldSeeNoCommentsApplicant(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).comments(new CommentBuilder().id(4).toComment()).toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(6).role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		assertTrue(applicationForm.getVisibleComments(user).isEmpty());
	}
	
	@Test
	public void shouldSeeOwnCommentOnlyfRefereeOnly(){		
		Comment comment = new CommentBuilder().id(4).toComment();
		RegisteredUser user = new RegisteredUserBuilder().id(6).roles(new RoleBuilder().authorityEnum(Authority.REFEREE).toRole()).toUser();
		Referee referee = new RefereeBuilder().user(user).toReferee();
		user.getReferees().add(referee);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).comments(comment).referees(referee).toApplicationForm();
		referee.setApplication(applicationForm);
		ReferenceComment referenceComment = new ReferenceCommentBuilder().id(5).referee(referee).toReferenceComment();
		applicationForm.getApplicationComments().add(referenceComment);
	
		assertEquals(1, applicationForm.getVisibleComments(user).size());
		assertTrue(applicationForm.getVisibleComments(user).contains(referenceComment));
	}
	

	@Test
	public void shouldSeeAllCommentsIfNotApplicantOrRefereeOnly() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
		RegisteredUser reviewerUserOne = new RegisteredUserBuilder().id(6).toUser();
		RegisteredUser reviewerUserTwo = new RegisteredUserBuilder().referees().roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole(), new RoleBuilder().authorityEnum(Authority.REFEREE).toRole()).id(7).toUser();
		
		Comment commentOne = new CommentBuilder().date(format.parse("01 01 2011")).id(4).user(reviewerUserTwo).toComment();
		Comment commentTwo = new CommentBuilder().date(format.parse("01 10 2011")).id(6).user(reviewerUserOne).toComment();
		Comment commentThree = new CommentBuilder().date(format.parse("01 05 2011")).id(9).user(reviewerUserTwo).toComment();
		
		ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUserOne).toReviewer(),new ReviewerBuilder().user( reviewerUserTwo).toReviewer()).toReviewRound();
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().reviewRounds(reviewRound).id(5).comments(commentOne, commentTwo, commentThree).toApplicationForm();
		Referee referee = new RefereeBuilder().application(applicationForm).toReferee();
		reviewerUserTwo.getReferees().add(referee);
		List<Comment> visibleComments = applicationForm.getVisibleComments(reviewerUserTwo);
		assertEquals(3, visibleComments.size());		
	}
	
	@Test
	public void shouldReturnStateChangeEventsEventsSortedByDate() throws ParseException{
		Event validationEvent = new StateChangeEventBuilder().id(1).date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/01/01")).newStatus(ApplicationFormStatus.VALIDATION).toEvent();
		Event reviewEvent = new StateChangeEventBuilder().id(2).date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/02/02")).newStatus(ApplicationFormStatus.REVIEW).toEvent();
		StateChangeEvent approvalEvent = new StateChangeEventBuilder().id(3).date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/04")).newStatus(ApplicationFormStatus.APPROVAL).toEvent();
		Event rejectedEvent = new StateChangeEventBuilder().id(40).date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).newStatus(ApplicationFormStatus.REJECTED).toEvent();
		ApplicationForm application = new ApplicationFormBuilder().id(1).events(approvalEvent, rejectedEvent, reviewEvent, validationEvent).toApplicationForm();
		List<StateChangeEvent> eventsSortedByDate = application.getStateChangeEventsSortedByDate();
		Assert.assertEquals(validationEvent, eventsSortedByDate.get(0));
		Assert.assertEquals(reviewEvent, eventsSortedByDate.get(1));
		Assert.assertEquals(approvalEvent, eventsSortedByDate.get(2));
		Assert.assertEquals(rejectedEvent, eventsSortedByDate.get(3));
		//fail("re-implement when other event types created");
	}
	
	

	
	@Test
	public void shouldReturnUsersWilingToSupervise()  throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
		RegisteredUser reviewerUserOne = new RegisteredUserBuilder().id(6).toUser();
		RegisteredUser reviewerUserTwo = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).id(7).toUser();
		
		Comment commentOne = new CommentBuilder().date(format.parse("01 01 2011")).id(4).user(reviewerUserTwo).toComment();
		Comment commentTwo = new CommentBuilder().date(format.parse("01 10 2011")).id(6).user(reviewerUserOne).toComment();
		ReviewComment review1 = new ReviewCommentBuilder().willingToInterview(true).id(10).user(reviewerUserTwo).toReviewComment();
		ReviewComment review2 = new ReviewCommentBuilder().willingToInterview(false).id(11).user(reviewerUserTwo).toReviewComment();
		ReviewComment review3 = new ReviewCommentBuilder().willingToInterview(true).id(12).user(reviewerUserOne).toReviewComment();
		InterviewComment interviewComment = new InterviewCommentBuilder().willingToSupervise(true).id(12).user(reviewerUserTwo).toInterviewComment();
		InterviewComment interviewComment1 = new InterviewCommentBuilder().willingToSupervise(false).id(12).user(reviewerUserOne).toInterviewComment();
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).comments(commentOne, commentTwo, review1, review2, review3, interviewComment, interviewComment1).toApplicationForm();
		
		List<RegisteredUser> users = applicationForm.getUsersWillingToSupervise();
		assertEquals(1, users.size());		

	
	}
	
	@Test
	public void shouldReturnEmptyListIfNoUsersWillingToSupervise() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
		RegisteredUser reviewerUserOne = new RegisteredUserBuilder().id(6).toUser();
		RegisteredUser reviewerUserTwo = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).id(7).toUser();
		
		Comment commentOne = new CommentBuilder().date(format.parse("01 01 2011")).id(4).user(reviewerUserTwo).toComment();
		Comment commentTwo = new CommentBuilder().date(format.parse("01 10 2011")).id(6).user(reviewerUserOne).toComment();
		ReviewComment review1 = new ReviewCommentBuilder().willingToInterview(true).id(10).user(reviewerUserTwo).toReviewComment();
		ReviewComment review2 = new ReviewCommentBuilder().willingToInterview(false).id(11).user(reviewerUserTwo).toReviewComment();
		ReviewComment review3 = new ReviewCommentBuilder().willingToInterview(true).id(12).user(reviewerUserOne).toReviewComment();
		InterviewComment interviewComment1 = new InterviewCommentBuilder().willingToSupervise(false).id(12).user(reviewerUserOne).toInterviewComment();
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).comments(commentOne, commentTwo, review1, review2, review3, interviewComment1).toApplicationForm();
		
		List<RegisteredUser> users = applicationForm.getUsersWillingToSupervise();
		assertEquals(Collections.EMPTY_LIST, users);	
		
	}
	
	
	@Test
	public void shouldReturnUsersWilingTointerview()  throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
		RegisteredUser reviewerUserOne = new RegisteredUserBuilder().id(6).toUser();
		RegisteredUser reviewerUserTwo = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).id(7).toUser();
		
		Comment commentOne = new CommentBuilder().date(format.parse("01 01 2011")).id(4).user(reviewerUserTwo).toComment();
		Comment commentTwo = new CommentBuilder().date(format.parse("01 10 2011")).id(6).user(reviewerUserOne).toComment();
		ReviewComment review1 = new ReviewCommentBuilder().willingToInterview(true).id(10).user(reviewerUserTwo).toReviewComment();
		ReviewComment review2 = new ReviewCommentBuilder().willingToInterview(false).id(11).user(reviewerUserTwo).toReviewComment();
		ReviewComment review3 = new ReviewCommentBuilder().willingToInterview(true).id(12).user(reviewerUserOne).toReviewComment();
		InterviewComment interviewComment = new InterviewCommentBuilder().willingToSupervise(true).id(12).user(reviewerUserTwo).toInterviewComment();
		InterviewComment interviewComment1 = new InterviewCommentBuilder().willingToSupervise(false).id(12).user(reviewerUserOne).toInterviewComment();
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).comments(commentOne, commentTwo, review1, review2, review3, interviewComment, interviewComment1).toApplicationForm();
		
		List<RegisteredUser> users = applicationForm.getReviewersWillingToInterview();
		assertEquals(2, users.size());		
		
		
	}
	
	@Test
	public void shouldReturnEmptyListIfNoUsersWillingToInterview() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
		RegisteredUser reviewerUserOne = new RegisteredUserBuilder().id(6).toUser();
		RegisteredUser reviewerUserTwo = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).id(7).toUser();
		
		Comment commentOne = new CommentBuilder().date(format.parse("01 01 2011")).id(4).user(reviewerUserTwo).toComment();
		Comment commentTwo = new CommentBuilder().date(format.parse("01 10 2011")).id(6).user(reviewerUserOne).toComment();
		ReviewComment review2 = new ReviewCommentBuilder().willingToInterview(false).id(11).user(reviewerUserTwo).toReviewComment();
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).comments(commentOne, commentTwo, review2).toApplicationForm();
		
		List<RegisteredUser> users = applicationForm.getReviewersWillingToInterview();
		assertEquals(Collections.EMPTY_LIST, users);	
		
	}
	
	@After
	public void tearDown(){
		SecurityContextHolder.clearContext();
	}

}
