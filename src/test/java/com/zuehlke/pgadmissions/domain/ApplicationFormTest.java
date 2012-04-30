package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
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
	
}
