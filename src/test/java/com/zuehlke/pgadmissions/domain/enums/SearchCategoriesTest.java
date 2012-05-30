package com.zuehlke.pgadmissions.domain.enums;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SearchCategoriesTest {
	
	@Test
	public void shouldOutputCorrectDisplayValues(){
		assertEquals("Application Code",  SearchCategories.APPLICATION_CODE.displayValue());
		assertEquals("Applicant Name",  SearchCategories.APPLICANT_NAME.displayValue());
		assertEquals("Programme Name",  SearchCategories.PROGRAMME_NAME.displayValue());
		assertEquals("Application Status",  SearchCategories.APPLICATION_STATUS.displayValue());
	}
}
