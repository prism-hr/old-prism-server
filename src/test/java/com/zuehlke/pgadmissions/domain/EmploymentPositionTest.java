package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.EmploymentPositionBuilder;

public class EmploymentPositionTest {

	
	@Test
	public void shouldCreateEmploymentPosition(){
		EmploymentPosition employmentPosition = new EmploymentPositionBuilder().application(new ApplicationForm()).
				employer("fr").endDate(new Date()).id(1).language(new Language()).remit("dfsfsd").startDate(new Date()).title("rerew").
				toEmploymentPosition();	
		Assert.assertNotNull(employmentPosition.getPosition_employer());
		Assert.assertNotNull(employmentPosition.getPosition_remit());
		Assert.assertNotNull(employmentPosition.getPosition_title());
		Assert.assertNotNull(employmentPosition.getApplication());
		Assert.assertNotNull(employmentPosition.getId());
		Assert.assertNotNull(employmentPosition.getPosition_endDate());
		Assert.assertNotNull(employmentPosition.getPosition_language());
		Assert.assertNotNull(employmentPosition.getPosition_startDate());
	}
}
