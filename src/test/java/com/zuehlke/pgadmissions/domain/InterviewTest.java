package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;

public class InterviewTest {

	@Test
	public void shouldGetCorrectValuesForTimeParts() {
		String time = "16:45";
		Interview interview = new InterviewBuilder().interviewTime(time).build();
		assertEquals("16", interview.getTimeHours());
		assertEquals("45", interview.getTimeMinutes());

	}

	@Test
	public void shouldReturnNullsIfTimeIsNull() {
		Interview interview = new InterviewBuilder().build();
		assertNull(interview.getTimeHours());
		assertNull(interview.getTimeMinutes());
	}
}
