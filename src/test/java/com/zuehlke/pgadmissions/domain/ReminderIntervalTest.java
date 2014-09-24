package com.zuehlke.pgadmissions.domain;

import junit.framework.Assert;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

public class ReminderIntervalTest {

	@Test
	public void shouldGet1DayInMinutes() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.DAYS);
		Assert.assertEquals(1440, reminderInterval.getDurationInMinutes());
	}
	
	
	@Test
	public void shouldGet1WeekInMinutes() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);
		Assert.assertEquals(10080, reminderInterval.getDurationInMinutes());
	}

}