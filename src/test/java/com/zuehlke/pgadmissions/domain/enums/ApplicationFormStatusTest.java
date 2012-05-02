package com.zuehlke.pgadmissions.domain.enums;

import static org.junit.Assert.*;

import org.junit.Test;

public class ApplicationFormStatusTest {

	@Test
	public void shouldOutputCorrectDisplayValues(){
		assertEquals("Approval", ApplicationFormStatus.APPROVAL.displayValue());
		assertEquals("Approved", ApplicationFormStatus.APPROVED.displayValue());
		assertEquals("Rejected", ApplicationFormStatus.REJECTED.displayValue());
		assertEquals("Validation", ApplicationFormStatus.VALIDATION.displayValue());
		assertEquals("Not Submitted", ApplicationFormStatus.UNSUBMITTED.displayValue());
		assertEquals("Withdrawn", ApplicationFormStatus.WITHDRAWN.displayValue());
		assertEquals("Review", ApplicationFormStatus.REVIEW.displayValue());
	}

	@Test
	public void shouldReturnRejectedReviewAndApprovedForValidationState(){
		ApplicationFormStatus[] avaialbleStati = ApplicationFormStatus.getAvailableNextStati(ApplicationFormStatus.VALIDATION);
		assertArrayEquals(new ApplicationFormStatus[]{ApplicationFormStatus.REJECTED, ApplicationFormStatus.REVIEW, ApplicationFormStatus.APPROVAL},avaialbleStati);
	}
	
	@Test
	public void shouldReturnRejectedReviewAndApprovedForReviewState(){
		ApplicationFormStatus[] avaialbleStati = ApplicationFormStatus.getAvailableNextStati(ApplicationFormStatus.REVIEW);
		assertArrayEquals(new ApplicationFormStatus[]{ApplicationFormStatus.REJECTED, ApplicationFormStatus.REVIEW, ApplicationFormStatus.APPROVAL},avaialbleStati);
	}
	
	@Test
	public void shouldReturnEmptyArrayForOtherStates(){
		assertArrayEquals(new ApplicationFormStatus[]{},ApplicationFormStatus.getAvailableNextStati(ApplicationFormStatus.UNSUBMITTED));
		assertArrayEquals(new ApplicationFormStatus[]{},ApplicationFormStatus.getAvailableNextStati(ApplicationFormStatus.REJECTED));
		assertArrayEquals(new ApplicationFormStatus[]{},ApplicationFormStatus.getAvailableNextStati(ApplicationFormStatus.APPROVED));
		assertArrayEquals(new ApplicationFormStatus[]{},ApplicationFormStatus.getAvailableNextStati(ApplicationFormStatus.WITHDRAWN));
	}
}
