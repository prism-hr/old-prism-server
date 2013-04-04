package com.zuehlke.pgadmissions.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.ApprovalStateChangeEvent;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewStateChangeEvent;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceEvent;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.ReviewStateChangeEvent;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.services.EventFactory;
import com.zuehlke.pgadmissions.services.UserService;

public class EventFactoryTest {

	private UserService userServiceMock;
	private EventFactory eventFactory;

	@Test
	public void shouldReturnStateChangeEvent() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
		EasyMock.replay(userServiceMock);
		ApplicationFormStatus status = ApplicationFormStatus.VALIDATION;
		Event event = eventFactory.createEvent(status);
		assertTrue(event instanceof StateChangeEvent);
		StateChangeEvent stageChangeEvent = (StateChangeEvent) event;
		assertEquals(user, stageChangeEvent.getUser());
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(stageChangeEvent.getDate(), Calendar.DATE));

		assertEquals(status, stageChangeEvent.getNewStatus());
	}
	
	@Test
	public void shouldReturnReviewStateChabgeEvent() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
		EasyMock.replay(userServiceMock);
		ReviewRound reviewRound = new ReviewRoundBuilder().id(1).build();
		Event event = eventFactory.createEvent( reviewRound);
		
		assertTrue(event instanceof ReviewStateChangeEvent);
		ReviewStateChangeEvent stageChangeEvent = (ReviewStateChangeEvent) event;
		assertEquals(user, stageChangeEvent.getUser());
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(stageChangeEvent.getDate(), Calendar.DATE));
		assertEquals(ApplicationFormStatus.REVIEW, stageChangeEvent.getNewStatus());
		assertEquals(reviewRound, stageChangeEvent.getReviewRound());
	}
	
	@Test
	public void shouldReturnInterviewStateChangeEvent() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
		EasyMock.replay(userServiceMock);
		Interview interview = new InterviewBuilder().id(1).build();
		Event event = eventFactory.createEvent( interview);
		
		assertTrue(event instanceof InterviewStateChangeEvent);
		InterviewStateChangeEvent stageChangeEvent = (InterviewStateChangeEvent) event;
		assertEquals(user, stageChangeEvent.getUser());
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(stageChangeEvent.getDate(), Calendar.DATE));
		assertEquals(ApplicationFormStatus.INTERVIEW, stageChangeEvent.getNewStatus());
		assertEquals(interview, stageChangeEvent.getInterview());
	}
	
	
	@Test
	public void shouldReturnApprovalStateChangeEvent() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
		EasyMock.replay(userServiceMock);
		ApprovalRound approval = new ApprovalRoundBuilder().id(1).build();
		Event event = eventFactory.createEvent( approval);
		
		assertTrue(event instanceof ApprovalStateChangeEvent);
		ApprovalStateChangeEvent stageChangeEvent = (ApprovalStateChangeEvent) event;
		assertEquals(user, stageChangeEvent.getUser());
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(stageChangeEvent.getDate(), Calendar.DATE));
		assertEquals(ApplicationFormStatus.APPROVAL, stageChangeEvent.getNewStatus());
		assertEquals(approval, stageChangeEvent.getApprovalRound());
	}
	
	@Test
	public void shouldReturnReferencEvent() {
		RegisteredUser currentUser = new RegisteredUserBuilder().id(1).build();
		RegisteredUser refereeUser = new RegisteredUserBuilder().id(2).build();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.replay(userServiceMock);
	
		Referee referee = new RefereeBuilder().id(1).user(refereeUser).toReferee();
		Event event = eventFactory.createEvent( referee);
		assertTrue(event instanceof ReferenceEvent);
		ReferenceEvent referenceEvent = (ReferenceEvent) event;
		assertEquals(refereeUser, referenceEvent.getUser());
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(referenceEvent.getDate(), Calendar.DATE));		
		assertEquals(referee, referenceEvent.getReferee());
	}
	
	@Before
	public void setup() {
		userServiceMock = EasyMock.createMock(UserService.class);
		eventFactory = new EventFactory(userServiceMock);
	}
}
