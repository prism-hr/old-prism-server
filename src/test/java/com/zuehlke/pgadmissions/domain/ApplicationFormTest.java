package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

public class ApplicationFormTest {

	@Test
	public void shouldReturnReviewableFalseIfAppliactioNFormUnsubmittfe() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().toApplicationForm();
		assertFalse(applicationForm.isReviewable());
	}

	@Test
	public void shouldReturnReviewableFalseIfApplicationFormRejected() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).approvedSatus(ApprovalStatus.REJECTED)
				.toApplicationForm();
		assertFalse(applicationForm.isReviewable());
	}

	@Test
	public void shouldReturnReviewableFalseIfApplicationFormAccepted() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).approvedSatus(ApprovalStatus.APPROVED)
				.toApplicationForm();
		assertFalse(applicationForm.isReviewable());
	}

	@Test
	public void shouldReturnReviewableTrueIfApplicationFormSubmittedButNeitherRejectedOrApproved() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		assertTrue(applicationForm.isReviewable());
	}

	@Test
	public void shouldReturnDecidedTrueIfRejectedOrApproved() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().approvedSatus(ApprovalStatus.APPROVED).toApplicationForm();
		assertTrue(applicationForm.isDecided());
		applicationForm = new ApplicationFormBuilder().approvedSatus(ApprovalStatus.REJECTED).toApplicationForm();
		assertTrue(applicationForm.isDecided());
	}

	@Test
	public void shouldReturnDecidedFalseIfNeitherRejectedOrApproved() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		assertFalse(applicationForm.isDecided());
	}

	@Test
	public void shouldReturnTrueOnlyIfCVInSupportingDocuments() {
		Document document = new DocumentBuilder().type(DocumentType.CV).toDocument();
		ApplicationForm applicationForm = new ApplicationFormBuilder().toApplicationForm();
		assertFalse(applicationForm.isCVUploaded());
		applicationForm.getSupportingDocuments().add(document);
		assertTrue(applicationForm.isCVUploaded());
	}
	
	@Test
	public void shouldReturnTrueOnlyIfCVIfPersonalStatementInDocuments() {
		Document document = new DocumentBuilder().type(DocumentType.PERSONAL_STATEMENT).toDocument();
		ApplicationForm applicationForm = new ApplicationFormBuilder().toApplicationForm();
		assertFalse(applicationForm.isPersonalStatementUploaded());
		applicationForm.getSupportingDocuments().add(document);
		assertTrue(applicationForm.isPersonalStatementUploaded());
	}
}
