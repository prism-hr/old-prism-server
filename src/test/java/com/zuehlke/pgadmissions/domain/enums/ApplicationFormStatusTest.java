package com.zuehlke.pgadmissions.domain.enums;

import static org.junit.Assert.assertEquals;

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
		assertEquals("Interview", ApplicationFormStatus.INTERVIEW.displayValue());
		assertEquals("Review", ApplicationFormStatus.REVIEW.displayValue());
	}
	
	@Test
    public void shouldConvertCorrectly(){
        assertEquals(ApplicationFormStatus.APPROVAL, ApplicationFormStatus.convert("Approv"));
        assertEquals(ApplicationFormStatus.APPROVED, ApplicationFormStatus.convert("Approve"));
        assertEquals(ApplicationFormStatus.INTERVIEW, ApplicationFormStatus.convert("Inter"));
        assertEquals(ApplicationFormStatus.REJECTED, ApplicationFormStatus.convert("Reje"));
        assertEquals(ApplicationFormStatus.REQUEST_RESTART_APPROVAL, ApplicationFormStatus.convert("Revision"));
        assertEquals(ApplicationFormStatus.REVIEW, ApplicationFormStatus.convert("eview"));
        assertEquals(ApplicationFormStatus.UNSUBMITTED, ApplicationFormStatus.convert("mitted"));
        assertEquals(ApplicationFormStatus.VALIDATION, ApplicationFormStatus.convert("Valid"));
        assertEquals(ApplicationFormStatus.WITHDRAWN, ApplicationFormStatus.convert("Withd"));
    }
}
