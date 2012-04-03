package com.zuehlke.pgadmissions.domain.enums;

import junit.framework.Assert;

import org.junit.Test;

public class AddressStatusTest {
	
	@Test
	public void shouldReturnNullWhenNullStringGiven() {
		Assert.assertNull(AddressStatus.fromString(null));
	}
	
	@Test
	public void shouldReturnYesAddressStatusWhenYesStringGiven() {
		Assert.assertEquals(AddressStatus.YES, AddressStatus.fromString("YES"));
	}
	
	@Test
	public void shouldReturnNoAddressStatusWhenNoStringGiven() {
		Assert.assertEquals(AddressStatus.NO, AddressStatus.fromString("NO"));
	}


}
