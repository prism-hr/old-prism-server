package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;

public class InterviewTest {

	@Test
	public void shouldGetCorrectValuesForTimeParts() {
		String time = "04:45 AM";
		Interview interview = new InterviewBuilder().interviewTime(time).toInterview();
		assertEquals("04", interview.getTimeParts()[0]);
		assertEquals("45", interview.getTimeParts()[1]);
		assertEquals("AM", interview.getTimeParts()[2]);
	}

	@Test
	public void shouldReturnArrayOfNullStringsIfTimeIsNull() {
		Interview interview = new InterviewBuilder().toInterview();
		assertNull(interview.getTimeParts()[0]);
		assertNull(interview.getTimeParts()[1]);
		assertNull(interview.getTimeParts()[2]);
	}
}
