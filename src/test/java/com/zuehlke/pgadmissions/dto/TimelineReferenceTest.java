package com.zuehlke.pgadmissions.dto;

import static org.junit.Assert.*;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

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

	@Test
	public void shouldReturnTooltipWithAuthorDetailsWhenNoReferenceComment(){
		Referee referee = new RefereeBuilder().build();
		RegisteredUser author = new RegisteredUserBuilder().firstName("Name").lastName("LastName").email("email@mail.com").build();
		
		TimelineReference timelineReference = new TimelineReference();
		timelineReference.setReferee(referee);
		timelineReference.setAuthor(author);
		
		assertEquals("Name LastName (email@mail.com) as: Referee", timelineReference.getTooltipMessage());
	}

	@Test
	public void shouldReturnTooltipWithProviderDetailsWhenReferenceComment(){
		RegisteredUser provider = new RegisteredUserBuilder().firstName("First").lastName("Last").email("firstlast@mail.com").build();
		ReferenceComment comment = new ReferenceCommentBuilder().id(4).providedBy(provider).build();
		Referee referee = new RefereeBuilder().reference(comment).build();
		RegisteredUser author = new RegisteredUserBuilder().firstName("Name").lastName("LastName").email("email@mail.com").build();

		TimelineReference timelineReference = new TimelineReference();
		timelineReference.setReferee(referee);
		timelineReference.setAuthor(author);
		
		assertEquals("First Last (firstlast@mail.com) as: Referee", timelineReference.getTooltipMessage());
	}

	@Test
	public void shouldReturnTooltipWithAuthorDetailsWhenReferenceCommentWithNoProvider(){
		ReferenceComment comment = new ReferenceCommentBuilder().id(4).build();
		Referee referee = new RefereeBuilder().reference(comment).build();
		RegisteredUser author = new RegisteredUserBuilder().firstName("Name").lastName("LastName").email("email@mail.com").build();
		
		TimelineReference timelineReference = new TimelineReference();
		timelineReference.setReferee(referee);
		timelineReference.setAuthor(author);
		
		assertEquals("Name LastName (email@mail.com) as: Referee", timelineReference.getTooltipMessage());
	}
}
