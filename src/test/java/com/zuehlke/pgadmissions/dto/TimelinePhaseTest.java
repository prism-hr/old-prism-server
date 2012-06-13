package com.zuehlke.pgadmissions.dto;

import static org.junit.Assert.*;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class TimelinePhaseTest {

	@Test
	public void shouldReturnStatusInLowerKeyAsType(){
		TimelinePhase timelinePhase = new TimelinePhase();
		timelinePhase.setStatus(ApplicationFormStatus.VALIDATION);
		assertEquals("validation", timelinePhase.getType());
		
		
		timelinePhase = new TimelinePhase();
		timelinePhase.setStatus(ApplicationFormStatus.REVIEW);
		assertEquals("review", timelinePhase.getType());
		
		timelinePhase = new TimelinePhase();
		timelinePhase.setStatus(ApplicationFormStatus.WITHDRAWN);
		assertEquals("withdrawn", timelinePhase.getType());
		
		
		timelinePhase = new TimelinePhase();
		timelinePhase.setStatus(ApplicationFormStatus.UNSUBMITTED);
		assertEquals("not_submitted", timelinePhase.getType());
	}
}
