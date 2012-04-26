package com.zuehlke.pgadmissions.domain.enums;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SubmissionStatusTest {

	@Test
	public void shouldOutputCorrectDisplayValues(){
		assertEquals("Submitted", SubmissionStatus.SUBMITTED.displayValue());
		assertEquals("Not Submitted", SubmissionStatus.UNSUBMITTED.displayValue());
	}

	
}
