package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewStateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ApplicationCreatedPhase;
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
        RegisteredUser userOne = new RegisteredUserBuilder().id(1).build();
        RegisteredUser userTwo = new RegisteredUserBuilder().id(2).build();
        RegisteredUser userThree = new RegisteredUserBuilder().id(3).build();
        RegisteredUser userFour = new RegisteredUserBuilder().id(4).build();

        Event validationPhaseEnteredEvent = new StateChangeEventBuilder().date(submissionDate).newStatus(ApplicationFormStatus.VALIDATION).user(userOne).id(1)
                .build();
        Event reviewPhaseEnteredEvent = new ReviewStateChangeEventBuilder().date(validatedDate).newStatus(ApplicationFormStatus.REVIEW).id(2).user(userTwo)
                .build();
        Event rejectedPhaseEnteredEvent = new StateChangeEventBuilder().date(rejectedDate).newStatus(ApplicationFormStatus.REJECTED).id(3).user(userThree)
                .build();
        Event referenceEvent = new ReferenceEventBuilder().date(referenceDate).referee(new RefereeBuilder().id(4).build()).id(4).user(userFour).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(new ProgramBuilder().build()).build();
        applicationForm.getEvents().clear();
        applicationForm.getEvents().addAll(Arrays.asList(rejectedPhaseEnteredEvent, validationPhaseEnteredEvent, reviewPhaseEnteredEvent, referenceEvent));

        List<TimelineObject> objects = timelineService.getTimelineObjects(applicationForm);
        assertEquals(5, objects.size());

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
    public void shouldAddCreatedPhaseIfUserIsApplicant() throws ParseException {
        Date creationDate = format.parse("01 03 2012 14:02:03");
        Date submissionDate = format.parse("01 04 2012 14:02:03");

        RegisteredUser userOne = new RegisteredUserBuilder().id(1).build();
        Event validationPhaseEnteredEvent = new StateChangeEventBuilder().date(submissionDate).newStatus(ApplicationFormStatus.VALIDATION).user(userOne).id(1)
                .build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(new ProgramBuilder().build()).appDate(creationDate).applicant(currentUser)
                .build();
        applicationForm.getEvents().clear();
        applicationForm.getEvents().addAll(Arrays.asList(validationPhaseEnteredEvent));

        List<TimelineObject> objects = timelineService.getTimelineObjects(applicationForm);
        assertEquals(2, objects.size());
        TimelinePhase timelinePhaseTwo = (TimelinePhase) objects.get(0);
        assertEquals(ApplicationFormStatus.VALIDATION, timelinePhaseTwo.getStatus());

        TimelinePhase timelinePhaseOne = (TimelinePhase) objects.get(1);
        assertEquals(creationDate, timelinePhaseOne.getEventDate());
        assertEquals(currentUser, timelinePhaseOne.getAuthor());
        assertEquals(ApplicationFormStatus.UNSUBMITTED, timelinePhaseOne.getStatus());

    }

    @Test
    public void shouldOderObejctsByMostRecentActivity() throws ParseException {

        Date validatedDate = format.parse("03 04 2012 09:14:12");
        Date referenceDate = format.parse("03 04 2012 11:00:45");
        Date commentDate = format.parse("03 04 2012 15:14:12");

        Event reviewPhaseEnteredEvent = new ReviewStateChangeEventBuilder().date(validatedDate).newStatus(ApplicationFormStatus.REVIEW).id(2).build();

        Event referenceEvent = new ReferenceEventBuilder().date(referenceDate).referee(new RefereeBuilder().id(4).build()).id(4).build();

        Comment comment = new CommentBuilder().date(commentDate).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).events(reviewPhaseEnteredEvent, referenceEvent).comments(comment)
                .program(new ProgramBuilder().build()).build();

        List<TimelineObject> objects = timelineService.getTimelineObjects(applicationForm);
        assertEquals(3, objects.size());

        assertTrue(objects.get(0) instanceof TimelineReference);
        assertTrue(objects.get(1) instanceof TimelinePhase);
        assertTrue(objects.get(2) instanceof ApplicationCreatedPhase);

    }

    @Test
    public void shouldAddReviewRoundIfReviewStateChange() throws ParseException {
        ReviewRound reviewRound = new ReviewRoundBuilder().id(1).build();
        StateChangeEvent reviewPhaseEnteredEvent = new ReviewStateChangeEventBuilder().newStatus(ApplicationFormStatus.REVIEW).id(2).reviewRound(reviewRound)
                .build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(new ProgramBuilder().build()).build();
        applicationForm.getEvents().clear();
        applicationForm.getEvents().addAll(Arrays.asList(reviewPhaseEnteredEvent));

        TimelinePhase phase = (TimelinePhase) timelineService.getTimelineObjects(applicationForm).get(1);

        assertEquals(reviewRound, phase.getReviewRound());

    }

    @Test
    public void shouldAddInterviewIfInterviewStateChange() throws ParseException {

        Interview interview = new InterviewBuilder().id(1).build();
        InterviewStateChangeEvent interviewStateChangeEvent = new InterviewStateChangeEventBuilder().newStatus(ApplicationFormStatus.INTERVIEW).id(1)
                .interview(interview).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(new ProgramBuilder().build()).build();
        applicationForm.getEvents().clear();
        applicationForm.getEvents().addAll(Arrays.asList(interviewStateChangeEvent));

        TimelinePhase phase = (TimelinePhase) timelineService.getTimelineObjects(applicationForm).get(1);

        assertEquals(interview, phase.getInterview());

    }

    @Test
    public void shouldAddApprovalRoundIfApprovalStateChange() throws ParseException {

        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).build();
        StateChangeEvent reviewPhaseEnteredEvent = new ApprovalStateChangeEventBuilder().newStatus(ApplicationFormStatus.REVIEW).id(2)
                .approvalRound(approvalRound).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(new ProgramBuilder().build()).build();
        applicationForm.getEvents().clear();
        applicationForm.getEvents().addAll(Arrays.asList(reviewPhaseEnteredEvent));

        TimelinePhase phase = (TimelinePhase) timelineService.getTimelineObjects(applicationForm).get(1);

        assertEquals(approvalRound, phase.getApprovalRound());

    }

    @Test
    public void shouldAddRefereeToTimelineReference() throws ParseException {

        Referee referee = new RefereeBuilder().id(4).build();
        Event event = new ReferenceEventBuilder().referee(referee).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(new ProgramBuilder().build()).build();
        applicationForm.getEvents().clear();
        applicationForm.getEvents().addAll(Arrays.asList(event));

        TimelineReference timelineReference = (TimelineReference) timelineService.getTimelineObjects(applicationForm).get(0);

        assertEquals(referee, timelineReference.getReferee());

    }

    @Test
    public void shouldAddCommentsToCorrectPhase() throws Exception {

        Date submissionDate = format.parse("01 04 2012 14:02:03");
        Date validatedDate = format.parse("03 04 2012 09:14:12");

        Date commentDateOne = submissionDate;
        Comment commentOne = new CommentBuilder().date(commentDateOne).id(1).build();

        Date commentDateTwo = format.parse("02 04 2012 11:52:46");
        Comment commentTwo = new CommentBuilder().date(commentDateTwo).id(4).build();

        Date commentDateThree = format.parse("03 04 2012 17:01:41");
        Comment commentThree = new CommentBuilder().date(commentDateThree).id(5).build();

        // reference comments should be ignored
        Date commentDateFour = format.parse("03 04 2012 16:00:00");
        Comment referenceComment = new ReferenceCommentBuilder().date(commentDateFour).id(5).build();

        Event validationPhaseEnteredEvent = new StateChangeEventBuilder().date(submissionDate).newStatus(ApplicationFormStatus.VALIDATION).id(1).build();
        Event reviewPhaseEnteredEvent = new StateChangeEventBuilder().date(validatedDate).newStatus(ApplicationFormStatus.REVIEW).id(2).build();

        ApplicationForm applicationForm = EasyMock.createNiceMock(ApplicationForm.class);
        EasyMock.expect(applicationForm.getApplicationTimestamp()).andReturn(new Date(0));
        EasyMock.expect(applicationForm.getEvents()).andReturn(Arrays.asList(validationPhaseEnteredEvent, reviewPhaseEnteredEvent));
        EasyMock.expect(applicationForm.getVisibleComments(currentUser)).andReturn(Arrays.asList(commentThree, commentOne, referenceComment, commentTwo));
        EasyMock.replay(applicationForm);

        List<TimelineObject> phases = timelineService.getTimelineObjects(applicationForm);
        assertEquals(3, phases.size());

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
    public void shouldSetRejectedByApproverTrueIfRejectUserIsApproverInProgram() {
        RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
        Program program = new ProgramBuilder().id(1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).program(program).build();
        EasyMock.expect(userMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(true);
        EasyMock.replay(userMock);

        Event event = new StateChangeEventBuilder().application(applicationForm).user(userMock).newStatus(ApplicationFormStatus.REJECTED).build();
        applicationForm.getEvents().add(event);

        List<TimelineObject> phases = timelineService.getTimelineObjects(applicationForm);
        TimelinePhase phase = (TimelinePhase) phases.get(1);
        assertTrue(phase.isRejectedByApprover());
    }

    @Test
    public void shouldSetRejectedByApproverFalseIfRejectUserIsNOTApproverInProgram() {
        RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
        Program program = new ProgramBuilder().id(1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).program(program).build();
        EasyMock.expect(userMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(false);
        EasyMock.replay(userMock);

        Event event = new StateChangeEventBuilder().application(applicationForm).user(userMock).newStatus(ApplicationFormStatus.REJECTED).build();
        applicationForm.getEvents().add(event);

        List<TimelineObject> phases = timelineService.getTimelineObjects(applicationForm);
        TimelinePhase phase = (TimelinePhase) phases.get(0);
        assertFalse(phase.isRejectedByApprover());
    }

    @Test
    public void shouldSetMessageCodeOntimelinePhasesForApprovalRestarts() throws ParseException {

        Date firstApprovalDate = format.parse("01 03 2012 14:02:03");
        Date secondApprovalDate = format.parse("01 04 2012 14:02:03");
        Date thirdApprovalDate = format.parse("05 04 2012 14:02:03");

        Event approvalEventOne = new StateChangeEventBuilder().date(firstApprovalDate).newStatus(ApplicationFormStatus.APPROVAL).id(1).build();
        Event approvalEventTwo = new StateChangeEventBuilder().date(secondApprovalDate).newStatus(ApplicationFormStatus.APPROVAL).id(2).build();
        Event approvalEventThree = new StateChangeEventBuilder().date(thirdApprovalDate).newStatus(ApplicationFormStatus.APPROVAL).id(3).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(new ProgramBuilder().build())
                .events(approvalEventOne, approvalEventTwo, approvalEventThree).build();

        List<TimelineObject> objects = timelineService.getTimelineObjects(applicationForm);
        assertEquals(4, objects.size());
        TimelinePhase timelinePhase2 = (TimelinePhase) objects.get(0);
        assertEquals("timeline.phase.approval", timelinePhase2.getMessageCode());
        TimelinePhase timelinePhase3 = (TimelinePhase) objects.get(1);
        assertEquals("timeline.phase.approval", timelinePhase3.getMessageCode());
        TimelinePhase timelinePhase4 = (TimelinePhase) objects.get(2);
        assertEquals("timeline.phase.approval", timelinePhase4.getMessageCode());
        TimelinePhase timelinePhase1 = (TimelinePhase) objects.get(3);
        assertEquals("timeline.phase.not_submitted", timelinePhase1.getMessageCode());
    }

    @Before
    public void setup() {
        format = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
        userServiceMock = EasyMock.createMock(UserService.class);
        timelineService = new TimelineService(userServiceMock);

        currentUser = new RegisteredUserBuilder().id(2).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.replay(userServiceMock);

    }

}
