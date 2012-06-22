package com.zuehlke.pgadmissions.domain.enums;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ValidationQuestionOptionsTest {
	@Test
	public void shouldHaveCorrectDisplayValues(){
		assertEquals("Yes", ValidationQuestionOptions.YES.getDisplayValue());
		assertEquals("No", ValidationQuestionOptions.NO.getDisplayValue());
		assertEquals("Unsure", ValidationQuestionOptions.UNSURE.getDisplayValue());
	}

}
