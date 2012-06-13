package com.zuehlke.pgadmissions.dto;

import static org.junit.Assert.*;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.Reference;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceBuilder;

public class TimelineReferenceTest {

	@Test
	public void shouldReturnTypeReferece() {
		assertEquals("reference", new TimelineReference().getType());
	}

	@Test
	public void shouldReurnCorrectMessageCode() {
		Reference reference = new ReferenceBuilder().id(4).toReference();
		Referee referee = new RefereeBuilder().reference(reference).toReferee();

		TimelineReference timelineReference = new TimelineReference();
		timelineReference.setReferee(referee);
		assertEquals("timeline.reference.uploaded", timelineReference.getMessageCode());

		referee.setReference(null);
		referee.setDeclined(true);
		assertEquals("timeline.reference.declined", timelineReference.getMessageCode());

	}
	
	@Test
	public void shouldReturnEventDateAsMostRecentAticivyDate(){
		Date eventDate = DateUtils.addDays(new Date(), -2);
		TimelineReference timelineReference = new TimelineReference();
		timelineReference.setEventDate(eventDate);
		assertEquals(eventDate, timelineReference.getMostRecentActivityDate());
		
	}
}
