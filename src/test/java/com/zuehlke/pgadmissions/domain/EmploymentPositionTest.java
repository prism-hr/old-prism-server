package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.EmploymentPositionBuilder;

public class EmploymentPositionTest {

	@Test
	public void shouldCreateEmploymentPosition(){
		EmploymentPosition employmentPosition = new EmploymentPositionBuilder().application(new ApplicationForm()).
				employerName("fr").endDate(new Date()).id(1).remit("dfsfsd").startDate(new Date()).position("rerew").
				toEmploymentPosition();	
		Assert.assertNotNull(employmentPosition.getEmployerName());
		Assert.assertNotNull(employmentPosition.getRemit());
		Assert.assertNotNull(employmentPosition.getPosition());
		Assert.assertNotNull(employmentPosition.getApplication());
		Assert.assertNotNull(employmentPosition.getId());
		Assert.assertNotNull(employmentPosition.getEndDate());
		Assert.assertNotNull(employmentPosition.getStartDate());
	}
}
