package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
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
	public void shouldSeeNoCommentsIfReferee(){		
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).comments(new CommentBuilder().id(4).toComment()).toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(6).referees(new RefereeBuilder().application(applicationForm).toReferee()).role(new RoleBuilder().authorityEnum(Authority.REFEREE).toRole()).toUser();
		assertTrue(applicationForm.getVisibleComments(user).isEmpty());
	}


	
	@Test
	public void shouldNotSeeOtherReviewersCommentsIfReviewer() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
		RegisteredUser reviewerOne = new RegisteredUserBuilder().id(6).toUser();
		RegisteredUser reviewerTwo = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).id(7).toUser();
		
		Comment commentOne = new CommentBuilder().date(format.parse("01 01 2011")).id(4).user(reviewerTwo).toComment();
		Comment commentTwo = new CommentBuilder().date(format.parse("01 10 2011")).id(6).user(reviewerOne).toComment();
		Comment commentThree = new CommentBuilder().date(format.parse("01 05 2011")).id(9).user(reviewerTwo).toComment();
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().reviewers(reviewerOne, reviewerTwo).id(5).comments(commentOne, commentTwo, commentThree).toApplicationForm();
		
		List<Comment> visibleComments = applicationForm.getVisibleComments(reviewerTwo);
		assertEquals(2, visibleComments.size());		
		assertEquals(commentThree, visibleComments.get(0));
		assertEquals(commentOne, visibleComments.get(1));
	}
	
	@Test
	public void shouldNotSeeAllCommentsIfNotReviewer() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
		RegisteredUser reviewerOne = new RegisteredUserBuilder().id(6).toUser();
		RegisteredUser reviewerTwo = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).id(7).toUser();
		
		Comment commentOne = new CommentBuilder().date(format.parse("01 01 2011")).id(4).user(reviewerTwo).toComment();
		Comment commentTwo = new CommentBuilder().date(format.parse("01 10 2011")).id(6).user(reviewerOne).toComment();
		Comment commentThree = new CommentBuilder().date(format.parse("01 05 2011")).id(9).user(reviewerTwo).toComment();
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().reviewers(reviewerOne, reviewerTwo).id(5).comments(commentOne, commentTwo, commentThree).toApplicationForm();
		
		List<Comment> visibleComments = applicationForm.getVisibleComments(reviewerTwo);
		assertEquals(3, visibleComments.size());		
	}
	
	
	@Test
	public void shouldAddEventIfStatusIsChanged(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().toApplicationForm();
		assertEquals(1, applicationForm.getEvents().size());
		applicationForm.setStatus(ApplicationFormStatus.REVIEW);
		assertEquals(2, applicationForm.getEvents().size());
		assertEquals(ApplicationFormStatus.UNSUBMITTED, applicationForm.getEvents().get(0).getNewStatus());
		assertEquals(ApplicationFormStatus.REVIEW, applicationForm.getEvents().get(1).getNewStatus());
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(applicationForm.getEvents().get(0).getDate(), Calendar.DATE));		
		
	}
	


}
