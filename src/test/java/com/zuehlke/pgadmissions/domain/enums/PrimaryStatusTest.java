package com.zuehlke.pgadmissions.domain.enums;

import junit.framework.Assert;

import org.junit.Test;


public class PrimaryStatusTest {

	@Test
	public void shouldOutputCorrectDisplayValues(){
		Assert.assertEquals("Yes", PrimaryStatus.YES.displayValue());
		Assert.assertEquals("No", PrimaryStatus.NO.displayValue());
	}
	
	@Test
	public void shouldReturnNullWhenNullStringGiven() {
		Assert.assertNull(AddressStatus.fromString(null));
	}
	
	@Test
	public void shouldReturnYesPrimaryStatusWhenYesStringGiven() {
		Assert.assertEquals(PrimaryStatus.YES, PrimaryStatus.fromString("YES"));
	}
	
	@Test
	public void shouldReturnNoPrimaryStatusWhenNoStringGiven() {
		Assert.assertEquals(PrimaryStatus.NO, PrimaryStatus.fromString("NO"));
	}
}
