package com.zuehlke.pgadmissions.domain.enums;

import junit.framework.Assert;

import org.junit.Test;


public class AwareStatusTest {

	@Test
	public void shouldOutputCorrectDisplayValues(){
		Assert.assertEquals("Yes", AwareStatus.YES.displayValue());
		Assert.assertEquals("No", AwareStatus.NO.displayValue());
	}
	
	@Test
	public void shouldReturnNullWhenNullStringGiven() {
		Assert.assertNull(AwareStatus.fromString(null));
	}
	
	@Test
	public void shouldReturnYesAwareStatusWhenYesStringGiven() {
		Assert.assertEquals(AwareStatus.YES, AwareStatus.fromString("YES"));
	}
	
	@Test
	public void shouldReturnNoAwareStatusWhenNoStringGiven() {
		Assert.assertEquals(AwareStatus.NO, AwareStatus.fromString("NO"));
	}
}
