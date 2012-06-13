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
	
	@Test
	public void shouldReturnMessageCodeFromType(){
		TimelinePhase timelinePhase = new TimelinePhase();
		timelinePhase.setStatus(ApplicationFormStatus.VALIDATION);
		assertEquals("timeline.phase.validation", timelinePhase.getMessageCode());
		
		
		timelinePhase = new TimelinePhase();
		timelinePhase.setStatus(ApplicationFormStatus.REVIEW);
		assertEquals("timeline.phase.review", timelinePhase.getMessageCode());
		
		timelinePhase = new TimelinePhase();
		timelinePhase.setStatus(ApplicationFormStatus.WITHDRAWN);
		assertEquals("timeline.phase.withdrawn", timelinePhase.getMessageCode());
		
		
		timelinePhase = new TimelinePhase();
		timelinePhase.setStatus(ApplicationFormStatus.UNSUBMITTED);
		assertEquals("timeline.phase.not_submitted", timelinePhase.getMessageCode());
	}
}
