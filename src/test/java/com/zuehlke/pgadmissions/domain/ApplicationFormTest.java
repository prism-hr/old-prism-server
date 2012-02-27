package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

public class ApplicationFormTest {

	
	@Test
	public void shouldReturnFalseIfAppliactioNFormUnsubmittfe(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().toApplicationForm();
		assertFalse(applicationForm.isReviewable());
	}
	
	@Test
	public void shouldReturnFalseIfApplicationFormRejected(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).approvedSatus(ApprovalStatus.REJECTED).toApplicationForm();
		assertFalse(applicationForm.isReviewable());
	}
	
	@Test
	public void shouldReturnFalseIfApplicationFormAccepted(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).approvedSatus(ApprovalStatus.APPROVED).toApplicationForm();
		assertFalse(applicationForm.isReviewable());
	}
	
	@Test
	public void shouldReturnTrueIfApplicationFormSubmittedButNeitherRejectedOrApproved(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		assertTrue(applicationForm.isReviewable());
	}
}
