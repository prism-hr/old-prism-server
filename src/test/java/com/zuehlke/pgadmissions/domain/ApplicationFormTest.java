package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

public class ApplicationFormTest {

	
	@Test
	public void shouldReturnReviewableFalseIfAppliactioNFormUnsubmittfe(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().toApplicationForm();
		assertFalse(applicationForm.isReviewable());
	}
	
	@Test
	public void shouldReturnReviewableFalseIfApplicationFormRejected(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).approvedSatus(ApprovalStatus.REJECTED).toApplicationForm();
		assertFalse(applicationForm.isReviewable());
	}
	
	@Test
	public void shouldReturnReviewableFalseIfApplicationFormAccepted(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).approvedSatus(ApprovalStatus.APPROVED).toApplicationForm();
		assertFalse(applicationForm.isReviewable());
	}
	
	@Test
	public void shouldReturnReviewableTrueIfApplicationFormSubmittedButNeitherRejectedOrApproved(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		assertTrue(applicationForm.isReviewable());
	}
	
	@Test
	public void shouldReturnDecidedTrueIfRejectedOrApproved(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().approvedSatus(ApprovalStatus.APPROVED).toApplicationForm();
		assertTrue(applicationForm.isDecided());
		applicationForm = new ApplicationFormBuilder().approvedSatus(ApprovalStatus.REJECTED).toApplicationForm();
		assertTrue(applicationForm.isDecided());
	}
	@Test
	public void shouldReturnDecidedFalseIfNeitherRejectedOrApproved(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		assertFalse(applicationForm.isDecided());
	}
	
	@Test
	public void shouldSaveQualificationInApplicationForm() throws ParseException{
		ApplicationForm applicationForm = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		Qualification qual = new QualificationBuilder().q_award_date(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/02")).q_country("").application(applicationForm).q_grade("").q_institution("").q_language_of_study("").q_level("").q_name_of_programme("").q_score("").q_start_date(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).q_termination_date(new SimpleDateFormat("yyyy/MM/dd").parse("2006/10/10")).q_termination_reason("").q_type("").toQualification();
		
		
	}
}

