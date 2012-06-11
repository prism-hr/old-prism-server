package com.zuehlke.pgadmissions.domain.enums;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SearchCategoriesTest {
	
	@Test
	public void shouldOutputCorrectDisplayValues(){
		assertEquals("Application number",  SearchCategory.APPLICATION_NUMBER.displayValue());
		assertEquals("Applicant",  SearchCategory.APPLICANT_NAME.displayValue());
		assertEquals("Programme",  SearchCategory.PROGRAMME_NAME.displayValue());
		assertEquals("Status",  SearchCategory.APPLICATION_STATUS.displayValue());
	}
}
