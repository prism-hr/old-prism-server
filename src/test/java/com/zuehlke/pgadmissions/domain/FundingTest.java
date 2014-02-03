package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.FundingBuilder;
import com.zuehlke.pgadmissions.domain.enums.FundingType;

public class FundingTest {
	
	@Test
	public void shouldCreateFunding(){
		Funding funding = new FundingBuilder().id(1).application(new ApplicationForm()).awardDate(new Date())
				.description("description").type(FundingType.EMPLOYER).value("1000").build();
		Assert.assertNotNull(funding.getDescription());
		Assert.assertNotNull(funding.getValue());
		Assert.assertNotNull(funding.getApplication());
		Assert.assertNotNull(funding.getAwardDate());
		Assert.assertNotNull(funding.getId());
		Assert.assertNotNull(funding.getType());
	}

}
