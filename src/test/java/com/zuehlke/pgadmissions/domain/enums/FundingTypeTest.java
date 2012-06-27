package com.zuehlke.pgadmissions.domain.enums;

import junit.framework.Assert;

import org.junit.Test;


public class FundingTypeTest {

	@Test
	public void shouldOutputCorrectDisplayValues(){
		Assert.assertEquals("Employer", FundingType.EMPLOYER.getDisplayValue());
		Assert.assertEquals("Scholarship/Grant", FundingType.SCHOLARSHIP.getDisplayValue());
		Assert.assertEquals("Industrial sponsor", FundingType.SPONSOR.getDisplayValue());
	}
}
