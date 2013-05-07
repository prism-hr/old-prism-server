package com.zuehlke.pgadmissions.domain.enums;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HomeOrOverseasEnumTest {

	@Test
	public void shouldHaveCorrectDisplayValues(){
		assertEquals("Home/EU", HomeOrOverseas.HOME.getDisplayValue());
		assertEquals("Overseas", HomeOrOverseas.OVERSEAS.getDisplayValue());
		assertEquals("Unsure", HomeOrOverseas.UNSURE.getDisplayValue());
	}
}
