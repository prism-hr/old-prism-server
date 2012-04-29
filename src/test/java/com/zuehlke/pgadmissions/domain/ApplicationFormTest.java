package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

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

	
}
