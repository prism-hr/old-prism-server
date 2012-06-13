package com.zuehlke.pgadmissions.dto;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class TimelinePhaseTest {

	@Test
	public void shouldReturnStatusInLowerKeyAsType() {
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
	public void shouldReturnMessageCodeFromType() {
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

	@Test
	public void shouldSetMostRecentActivityDateAsMostRecentCommentDate() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
		Date commetnDateOne = format.parse("03 04 2012 09:14:12");
		Date eventDate = format.parse("03 04 2012 11:00:45");
		Date commetnDateTwo = format.parse("03 04 2012 15:14:12");

		TimelinePhase phase = new TimelinePhase();
		phase.setEventDate(eventDate);
		phase.getComments().addAll(Arrays.asList(new CommentBuilder().date(commetnDateTwo).toComment(), new CommentBuilder().date(commetnDateOne).toComment()));
		assertEquals(commetnDateTwo, phase.getMostRecentActivityDate());
	}
	
	@Test
	public void shouldSetMostRecentActivityDateAsEventDateIfNoComments() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
		
		Date eventDate = format.parse("03 04 2012 11:00:45");
		

		TimelinePhase phase = new TimelinePhase();
		phase.setEventDate(eventDate);		
		assertEquals(eventDate, phase.getMostRecentActivityDate());
	}
}
