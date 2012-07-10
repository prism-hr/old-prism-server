package com.zuehlke.pgadmissions.domain.enums;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class PhoneTypeEnumTest {
	@Test
	public void shouldOutputCorrectDisplayValues(){
		assertEquals("Home",  PhoneType.HOME.getDisplayValue());
		assertEquals("Work",  PhoneType.WORK.getDisplayValue());
		assertEquals("Mobile",  PhoneType.MOBILE.getDisplayValue());
		
	}

}
