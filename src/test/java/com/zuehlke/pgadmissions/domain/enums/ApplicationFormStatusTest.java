package com.zuehlke.pgadmissions.domain.enums;

import static org.junit.Assert.*;

import org.junit.Test;

public class ApplicationFormStatusTest {

	@Test
	public void shouldOutputCorrectDisplayValues(){
		assertEquals("Approved", ApplicationFormStatus.APPROVED.displayValue());
		assertEquals("Rejected", ApplicationFormStatus.REJECTED.displayValue());
		assertEquals("Validation", ApplicationFormStatus.VALIDATION.displayValue());
		assertEquals("Not Submitted", ApplicationFormStatus.UNSUBMITTED.displayValue());
		assertEquals("Withdrawn", ApplicationFormStatus.WITHDRAWN.displayValue());
	}

	@Test
	public void shouldReturnRejectedAndApprovedForValidationState(){
		ApplicationFormStatus[] avaialbleStati = ApplicationFormStatus.getAvailableNextStati(ApplicationFormStatus.VALIDATION);
		assertArrayEquals(new ApplicationFormStatus[]{ApplicationFormStatus.REJECTED, ApplicationFormStatus.APPROVED},avaialbleStati);
	}
	
	@Test
	public void shouldReturnEmptyArrayForOtherStates(){
		assertArrayEquals(new ApplicationFormStatus[]{},ApplicationFormStatus.getAvailableNextStati(ApplicationFormStatus.UNSUBMITTED));
		assertArrayEquals(new ApplicationFormStatus[]{},ApplicationFormStatus.getAvailableNextStati(ApplicationFormStatus.REJECTED));
		assertArrayEquals(new ApplicationFormStatus[]{},ApplicationFormStatus.getAvailableNextStati(ApplicationFormStatus.APPROVED));
		assertArrayEquals(new ApplicationFormStatus[]{},ApplicationFormStatus.getAvailableNextStati(ApplicationFormStatus.WITHDRAWN));
	}
}
