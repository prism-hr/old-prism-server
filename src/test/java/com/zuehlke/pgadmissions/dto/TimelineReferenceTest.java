package com.zuehlke.pgadmissions.dto;

import static org.junit.Assert.*;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;

public class TimelineReferenceTest {

	@Test
	public void shouldReturnTypeReferece() {
		assertEquals("reference", new TimelineReference().getType());
	}

	@Test
	public void shouldReurnCorrectMessageCode() {
		ReferenceComment reference = new ReferenceCommentBuilder().id(4).build();
		Referee referee = new RefereeBuilder().reference(reference).build();

		TimelineReference timelineReference = new TimelineReference();
		timelineReference.setReferee(referee);
		assertEquals("timeline.reference.responded", timelineReference.getMessageCode());

		referee.setReference(null);
		referee.setDeclined(true);
		assertEquals("timeline.reference.responded", timelineReference.getMessageCode());

	}
	
	@Test
	public void shouldReturnEventDateAsMostRecentAticivyDate(){
		Date eventDate = DateUtils.addDays(new Date(), -2);
		TimelineReference timelineReference = new TimelineReference();
		timelineReference.setEventDate(eventDate);
		assertEquals(eventDate, timelineReference.getMostRecentActivityDate());
		
	}
	
	@Test
	public void shouldReturnRefereeAsCapacity(){
		assertEquals("referee", new TimelineReference().getUserCapacity());
	}
}
