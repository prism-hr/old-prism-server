package com.zuehlke.pgadmissions.domain.enums;

import junit.framework.Assert;

import org.junit.Test;


public class PrimaryStatusTest {

	@Test
	public void shouldOutputCorrectDisplayValues(){
		Assert.assertEquals("Yes", CheckedStatus.YES.displayValue());
		Assert.assertEquals("No", CheckedStatus.NO.displayValue());
	}
	
	@Test
	public void shouldReturnNullWhenNullStringGiven() {
		Assert.assertNull(CheckedStatus.fromString(null));
	}
	
	@Test
	public void shouldReturnYesPrimaryStatusWhenYesStringGiven() {
		Assert.assertEquals(CheckedStatus.YES, CheckedStatus.fromString("YES"));
	}
	
	@Test
	public void shouldReturnNoPrimaryStatusWhenNoStringGiven() {
		Assert.assertEquals(CheckedStatus.NO, CheckedStatus.fromString("NO"));
	}
}
