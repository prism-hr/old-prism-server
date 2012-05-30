package com.zuehlke.pgadmissions.domain.enums;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DurationUnitEnumTest {
	
	@Test
	public void shouldOutputCorrectDisplayValues(){
		assertEquals("Minutes",  DurationUnitEnum.MINUTES.displayValue());
		assertEquals("Hours",  DurationUnitEnum.HOURS.displayValue());
		assertEquals("Days",  DurationUnitEnum.DAYS.displayValue());
		assertEquals("Weeks",  DurationUnitEnum.WEEKS.displayValue());
	}
	

}
