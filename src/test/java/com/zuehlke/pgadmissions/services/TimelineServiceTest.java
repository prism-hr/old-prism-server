package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewStateChangeEvent;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalStateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewStateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewStateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.dto.TimelinePhase;

public class TimelineServiceTest {

	private SimpleDateFormat format;
	private UserService userServiceMock;
	private TimelineService timelineService;
	private RegisteredUser currentUser;

	@Test
	public void shouldGetPhasesOrderedByEnteredDateIfNoComments() throws Exception {

		Date submissionDate = format.parse("01 04 2012 14:02:03");
		Date validatedDate = format.parse("03 04 2012 09:14:12");
		Date rejectedDate = format.parse("03 04 2012 15:57:45");
		RegisteredUser userOne = new RegisteredUserBuilder().id(1).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(2).toUser();
		RegisteredUser userThree = new RegisteredUserBuilder().id(3).toUser();
		StateChangeEvent validationPhaseEnteredEvent = new StateChangeEventBuilder().date(submissionDate).newStatus(ApplicationFormStatus.VALIDATION).user(userOne).id(1).toEvent();
		ReviewRound reviewRound = new ReviewRoundBuilder().id(1).toReviewRound();
		StateChangeEvent reviewPhaseEnteredEvent = new ReviewStateChangeEventBuilder().date(validatedDate).newStatus(ApplicationFormStatus.REVIEW).id(2).user(userTwo).reviewRound(reviewRound).toReviewStateChangeEvent();
		StateChangeEvent rejectedPhaseEnteredEvent = new StateChangeEventBuilder().date(rejectedDate).newStatus(ApplicationFormStatus.REJECTED).id(3).user(userThree).toEvent();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		applicationForm.getEvents().clear();
		applicationForm.getEvents().addAll(Arrays.asList(rejectedPhaseEnteredEvent, validationPhaseEnteredEvent, reviewPhaseEnteredEvent));

		List<TimelinePhase> phases = timelineService.getPhases(applicationForm);
		assertEquals(3, phases.size());

		TimelinePhase timelinePhaseOne = phases.get(0);
		assertEquals(rejectedDate, timelinePhaseOne.getDate());
		assertNull(timelinePhaseOne.getExitedPhaseDate());
		assertEquals(ApplicationFormStatus.REJECTED, timelinePhaseOne.getStatus());
		assertEquals(userThree, timelinePhaseOne.getAuthor());
		assertEquals("timeline.phase.rejected", timelinePhaseOne.getMessageCode());

		TimelinePhase timelinePhaseTwo = phases.get(1);
		assertEquals(validatedDate, timelinePhaseTwo.getDate());
		assertEquals(rejectedDate, timelinePhaseTwo.getExitedPhaseDate());
		assertEquals(ApplicationFormStatus.REVIEW, timelinePhaseTwo.getStatus());
		assertEquals(userTwo, timelinePhaseTwo.getAuthor());
		assertEquals(reviewRound, timelinePhaseTwo.getReviewRound());
		assertEquals("timeline.phase.review", timelinePhaseTwo.getMessageCode());

		TimelinePhase timelinePhaseThree = phases.get(2);
		assertEquals(submissionDate, timelinePhaseThree.getDate());
		assertEquals(validatedDate, timelinePhaseThree.getExitedPhaseDate());
		assertEquals(ApplicationFormStatus.VALIDATION, timelinePhaseThree.getStatus());
		assertEquals(userOne, timelinePhaseThree.getAuthor());
		assertEquals("timeline.phase.validation", timelinePhaseThree.getMessageCode());
	}
	
	@Test
	public void shouldAddReviewRoundIfReviewStateChange() throws ParseException{		
		ReviewRound reviewRound = new ReviewRoundBuilder().id(1).toReviewRound();
		StateChangeEvent reviewPhaseEnteredEvent = new ReviewStateChangeEventBuilder().newStatus(ApplicationFormStatus.REVIEW).id(2).reviewRound(reviewRound).toReviewStateChangeEvent();
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		applicationForm.getEvents().clear();
		applicationForm.getEvents().addAll(Arrays.asList(reviewPhaseEnteredEvent));

		TimelinePhase phase = timelineService.getPhases(applicationForm).get(0);

		assertEquals(reviewRound, phase.getReviewRound());
	
	
	}

	@Test
	public void shouldAddInterviewIfInterviewStateChange() throws ParseException{
		
		Interview interview = new InterviewBuilder().id(1).toInterview();
		InterviewStateChangeEvent interviewStateChangeEvent = new InterviewStateChangeEventBuilder().newStatus(ApplicationFormStatus.INTERVIEW).id(1).interview(interview).toInterviewStateChangeEvent();
		
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		applicationForm.getEvents().clear();
		applicationForm.getEvents().addAll(Arrays.asList(interviewStateChangeEvent));

		TimelinePhase phase = timelineService.getPhases(applicationForm).get(0);

		assertEquals(interview, phase.getInterview());
	
	
	}
	
	@Test
	public void shouldAddApprovalRoundIfApprovalStateChange() throws ParseException{
		
		ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).toApprovalRound();
		StateChangeEvent reviewPhaseEnteredEvent = new ApprovalStateChangeEventBuilder().newStatus(ApplicationFormStatus.REVIEW).id(2).approvalRound(approvalRound).toApprovalStateChangeEvent();
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		applicationForm.getEvents().clear();
		applicationForm.getEvents().addAll(Arrays.asList(reviewPhaseEnteredEvent));

		TimelinePhase phase = timelineService.getPhases(applicationForm).get(0);

		assertEquals(approvalRound, phase.getApprovalRound());
	
	
	
	}
	@Test
	public void shouldAddCommentsToCorrectPhase() throws Exception {

		Date submissionDate = format.parse("01 04 2012 14:02:03");
		Date validatedDate = format.parse("03 04 2012 09:14:12");

		Date commentDateOne = format.parse("01 04 2012 16:02:03");
		Comment commentOne = new CommentBuilder().date(commentDateOne).id(1).toComment();

		Date commentDateTwo = format.parse("02 04 2012 11:52:46");
		Comment commentTwo = new CommentBuilder().date(commentDateTwo).id(4).toComment();

		Date commentDateThree = format.parse("03 04 2012 17:01:41");
		Comment commentThree = new CommentBuilder().date(commentDateThree).id(5).toComment();

		Event validationPhaseEnteredEvent = new StateChangeEventBuilder().date(submissionDate).newStatus(ApplicationFormStatus.VALIDATION).id(1).toEvent();
		Event reviewPhaseEnteredEvent = new StateChangeEventBuilder().date(validatedDate).newStatus(ApplicationFormStatus.REVIEW).id(2).toEvent();

		ApplicationForm applicationForm = EasyMock.createMock(ApplicationForm.class);
		EasyMock.expect(applicationForm.getEvents()).andReturn(Arrays.asList(validationPhaseEnteredEvent, reviewPhaseEnteredEvent));
		EasyMock.expect(applicationForm.getVisibleComments(currentUser)).andReturn(Arrays.asList(commentThree, commentOne, commentTwo));
		EasyMock.replay(applicationForm);

		List<TimelinePhase> phases = timelineService.getPhases(applicationForm);
		assertEquals(2, phases.size());

		TimelinePhase timelinePhaseOne = phases.get(0);
		assertEquals(ApplicationFormStatus.REVIEW, timelinePhaseOne.getStatus());
		assertEquals(1, timelinePhaseOne.getComments().size());
		assertEquals(commentThree, timelinePhaseOne.getComments().iterator().next());

		TimelinePhase timelinePhaseTwo = phases.get(1);
		assertEquals(ApplicationFormStatus.VALIDATION, timelinePhaseTwo.getStatus());
		assertEquals(2, timelinePhaseTwo.getComments().size());
		Iterator<Comment> iterator = timelinePhaseTwo.getComments().iterator();
		assertEquals(commentTwo, iterator.next());
		assertEquals(commentOne, iterator.next());

	}

	@Before
	public void setup() {
		format = new SimpleDateFormat("dd MM yyyy hh:mm:ss");
		userServiceMock = EasyMock.createMock(UserService.class);
		timelineService = new TimelineService(userServiceMock);

		currentUser = new RegisteredUserBuilder().id(2).toUser();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);

	}

}
