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
	public void shouldGet1HourInMinutes() {
		StageDuration stageDuration = new StageDurationBuilder().stage(ApplicationFormStatus.VALIDATION).unit(DurationUnitEnum.HOURS).duration(1).build();
		Assert.assertEquals(60, stageDuration.getDurationInMinutes());
		
	}
	
	@Test
	public void shouldGet1WeekInMinutes() {
		StageDuration stageDuration = new StageDurationBuilder().stage(ApplicationFormStatus.VALIDATION).unit(DurationUnitEnum.WEEKS).duration(1).build();
		Assert.assertEquals(10080, stageDuration.getDurationInMinutes());
	}

	
	@Test
	public void shouldReturnMinutes() {
		StageDuration stageDuration = new StageDurationBuilder().stage(ApplicationFormStatus.VALIDATION).unit(DurationUnitEnum.MINUTES).duration(17).build();
		Assert.assertEquals(17, stageDuration.getDurationInMinutes());
		
	}
	
}
