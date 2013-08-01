package com.zuehlke.pgadmissions.domain;

import junit.framework.Assert;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.StageDurationBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

public class StageDurationTest {
	
	@Test
	public void shouldGet1DayInMinutes() {
		StageDuration stageDuration = new StageDurationBuilder().stage(ApplicationFormStatus.VALIDATION).unit(DurationUnitEnum.DAYS).duration(1).build();
		Assert.assertEquals(1440, stageDuration.getDurationInMinutes());
	}
	
	@Test
	public void shouldGet1WeekInMinutes() {
		StageDuration stageDuration = new StageDurationBuilder().stage(ApplicationFormStatus.VALIDATION).unit(DurationUnitEnum.WEEKS).duration(1).build();
		Assert.assertEquals(7200, stageDuration.getDurationInMinutes());
	}

}
