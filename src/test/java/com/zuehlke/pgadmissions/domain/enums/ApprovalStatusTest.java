package com.zuehlke.pgadmissions.domain.enums;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ApprovalStatusTest {

	@Test
	public void shouldOutputCorrectDisplayValues(){
		assertEquals("Approved", ApprovalStatus.APPROVED.displayValue());
		assertEquals("Rejected", ApprovalStatus.REJECTED.displayValue());
	}

	
}
