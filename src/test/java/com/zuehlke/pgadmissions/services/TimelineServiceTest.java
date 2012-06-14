package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalStateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewStateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewStateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.TimelineObject;
import com.zuehlke.pgadmissions.dto.TimelinePhase;
import com.zuehlke.pgadmissions.dto.TimelineReference;

public class TimelineServiceTest {

	private SimpleDateFormat format;
	private UserService userServiceMock;
	private TimelineService timelineService;
	private RegisteredUser currentUser;

	@Test
	public void shouldGetObjectOrderedByEnteredDateIfNoComments() throws Exception {

		Date submissionDate = format.parse("01 04 2012 14:02:03");
		Date validatedDate = format.parse("03 04 2012 09:14:12");
		Date rejectedDate = format.parse("03 04 2012 15:57:45");
		Date referenceDate = format.parse("03 04 2012 11:00:45");
		RegisteredUser userOne = new RegisteredUserBuilder().id(1).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(2).toUser();
		RegisteredUser userThree = new RegisteredUserBuilder().id(3).toUser();
		RegisteredUser userFour = new RegisteredUserBuilder().id(4).toUser();

		Event validationPhaseEnteredEvent = new StateChangeEventBuilder().date(submissionDate).newStatus(ApplicationFormStatus.VALIDATION).user(userOne).id(1)
				.toEvent();

		Event reviewPhaseEnteredEvent = new ReviewStateChangeEventBuilder().date(validatedDate).newStatus(ApplicationFormStatus.REVIEW).id(2).user(userTwo)
				.toReviewStateChangeEvent();
		Event rejectedPhaseEnteredEvent = new StateChangeEventBuilder().date(rejectedDate).newStatus(ApplicationFormStatus.REJECTED).id(3).user(userThree)
				.toEvent();
		Event referenceEvent = new ReferenceEventBuilder().date(referenceDate).referee(new RefereeBuilder().id(4).toReferee()).id(4).user(userFour).toEvent();

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		applicationForm.getEvents().clear();
		applicationForm.getEvents().addAll(Arrays.asList(rejectedPhaseEnteredEvent, validationPhaseEnteredEvent, reviewPhaseEnteredEvent, referenceEvent));

		List<TimelineObject> objects = timelineService.getTimelineObjects(applicationForm);
		assertEquals(4, objects.size());

		TimelinePhase timelinePhaseOne = (TimelinePhase) objects.get(0);

		assertEquals(rejectedDate, timelinePhaseOne.getEventDate());
		assertNull(timelinePhaseOne.getExitedPhaseDate());
		assertEquals(ApplicationFormStatus.REJECTED, timelinePhaseOne.getStatus());
		assertEquals(userThree, timelinePhaseOne.getAuthor());

		TimelineReference timelineReference = (TimelineReference) objects.get(1);
		assertEquals(referenceDate, timelineReference.getEventDate());
		assertEquals(userFour, timelineReference.getAuthor());

		TimelinePhase timelinePhaseThree = (TimelinePhase) objects.get(2);
		assertEquals(validatedDate, timelinePhaseThree.getEventDate());
		assertEquals(rejectedDate, timelinePhaseThree.getExitedPhaseDate());
		assertEquals(ApplicationFormStatus.REVIEW, timelinePhaseThree.getStatus());
		assertEquals(userTwo, timelinePhaseThree.getAuthor());

		TimelinePhase timelinePhaseFour = (TimelinePhase) objects.get(3);
		assertEquals(submissionDate, timelinePhaseFour.getEventDate());
		assertEquals(validatedDate, timelinePhaseFour.getExitedPhaseDate());
		assertEquals(ApplicationFormStatus.VALIDATION, timelinePhaseFour.getStatus());
		assertEquals(userOne, timelinePhaseFour.getAuthor());

	}

	@Test
	public void shouldOderObejctsByMostRecentActivity() throws ParseException {

		Date validatedDate = format.parse("03 04 2012 09:14:12");
		Date referenceDate = format.parse("03 04 2012 11:00:45");
		Date commentDate = format.parse("03 04 2012 15:14:12");
		
		Event reviewPhaseEnteredEvent = new ReviewStateChangeEventBuilder().date(validatedDate).newStatus(ApplicationFormStatus.REVIEW).id(2)
				.toReviewStateChangeEvent();

		Event referenceEvent = new ReferenceEventBuilder().date(referenceDate).referee(new RefereeBuilder().id(4).toReferee()).id(4).toEvent();

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).events(reviewPhaseEnteredEvent, referenceEvent).comments(new CommentBuilder().date(commentDate).toComment()).toApplicationForm();

		List<TimelineObject> objects = timelineService.getTimelineObjects(applicationForm);
		assertEquals(2, objects.size());

		assertTrue(objects.get(0) instanceof TimelinePhase);
		assertTrue(objects.get(1) instanceof TimelineReference);
		

	}

	@Test
	public void shouldAddReviewRoundIfReviewStateChange() throws ParseException {
		ReviewRound reviewRound = new ReviewRoundBuilder().id(1).toReviewRound();
		StateChangeEvent reviewPhaseEnteredEvent = new ReviewStateChangeEventBuilder().newStatus(ApplicationFormStatus.REVIEW).id(2).reviewRound(reviewRound)
				.toReviewStateChangeEvent();

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		applicationForm.getEvents().clear();
		applicationForm.getEvents().addAll(Arrays.asList(reviewPhaseEnteredEvent));

		TimelinePhase phase = (TimelinePhase) timelineService.getTimelineObjects(applicationForm).get(0);

		assertEquals(reviewRound, phase.getReviewRound());

	}

	@Test
	public void shouldAddInterviewIfInterviewStateChange() throws ParseException {

		Interview interview = new InterviewBuilder().id(1).toInterview();
		InterviewStateChangeEvent interviewStateChangeEvent = new InterviewStateChangeEventBuilder().newStatus(ApplicationFormStatus.INTERVIEW).id(1)
				.interview(interview).toInterviewStateChangeEvent();

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		applicationForm.getEvents().clear();
		applicationForm.getEvents().addAll(Arrays.asList(interviewStateChangeEvent));

		TimelinePhase phase = (TimelinePhase) timelineService.getTimelineObjects(applicationForm).get(0);

		assertEquals(interview, phase.getInterview());

	}

	@Test
	public void shouldAddApprovalRoundIfApprovalStateChange() throws ParseException {

		ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).toApprovalRound();
		StateChangeEvent reviewPhaseEnteredEvent = new ApprovalStateChangeEventBuilder().newStatus(ApplicationFormStatus.REVIEW).id(2)
				.approvalRound(approvalRound).toApprovalStateChangeEvent();

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		applicationForm.getEvents().clear();
		applicationForm.getEvents().addAll(Arrays.asList(reviewPhaseEnteredEvent));

		TimelinePhase phase = (TimelinePhase) timelineService.getTimelineObjects(applicationForm).get(0);

		assertEquals(approvalRound, phase.getApprovalRound());

	}

	@Test
	public void shouldAddRefereeToTimelineReference() throws ParseException {

		Referee referee = new RefereeBuilder().id(4).toReferee();
		Event event = new ReferenceEventBuilder().referee(referee).toEvent();

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		applicationForm.getEvents().clear();
		applicationForm.getEvents().addAll(Arrays.asList(event));

		TimelineReference timelineReference = (TimelineReference) timelineService.getTimelineObjects(applicationForm).get(0);

		assertEquals(referee, timelineReference.getReferee());

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

		List<TimelineObject> phases = timelineService.getTimelineObjects(applicationForm);
		assertEquals(2, phases.size());

		TimelinePhase timelinePhaseOne = (TimelinePhase) phases.get(0);
		assertEquals(ApplicationFormStatus.REVIEW, timelinePhaseOne.getStatus());
		assertEquals(1, timelinePhaseOne.getComments().size());
		assertEquals(commentThree, timelinePhaseOne.getComments().iterator().next());

		TimelinePhase timelinePhaseTwo = (TimelinePhase) phases.get(1);
		assertEquals(ApplicationFormStatus.VALIDATION, timelinePhaseTwo.getStatus());
		assertEquals(2, timelinePhaseTwo.getComments().size());
		Iterator<Comment> iterator = timelinePhaseTwo.getComments().iterator();
		assertEquals(commentTwo, iterator.next());
		assertEquals(commentOne, iterator.next());

	}
	@Test
	public void shouldSetRejectedByApproverTrueIfRejectUserIsApproverInProgram(){
		RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).program(program).toApplicationForm();
		EasyMock.expect(userMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(true);
		EasyMock.replay(userMock);
		
		Event event = new StateChangeEventBuilder().application(applicationForm).user(userMock).newStatus(ApplicationFormStatus.REJECTED).toEvent();		
		applicationForm.getEvents().add(event);

		List<TimelineObject> phases = timelineService.getTimelineObjects(applicationForm);
		TimelinePhase phase = (TimelinePhase) phases.get(0);
		assertTrue(phase.isRejectedByApprover());		
	}
	
	@Test
	public void shouldSetRejectedByApproverFalseIfRejectUserIsNOTApproverInProgram(){
		RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).program(program).toApplicationForm();
		EasyMock.expect(userMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(false);
		EasyMock.replay(userMock);
		
		Event event = new StateChangeEventBuilder().application(applicationForm).user(userMock).newStatus(ApplicationFormStatus.REJECTED).toEvent();		
		applicationForm.getEvents().add(event);

		List<TimelineObject> phases = timelineService.getTimelineObjects(applicationForm);
		TimelinePhase phase = (TimelinePhase) phases.get(0);
		assertFalse(phase.isRejectedByApprover());		
	}
	@Before
	public void setup() {
		format = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
		userServiceMock = EasyMock.createMock(UserService.class);
		timelineService = new TimelineService(userServiceMock);

		currentUser = new RegisteredUserBuilder().id(2).toUser();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);

	}

}
