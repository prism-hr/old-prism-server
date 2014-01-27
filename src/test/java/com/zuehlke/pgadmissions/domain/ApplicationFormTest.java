package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewVoteCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class ApplicationFormTest {

    @Test
    public void shouldReturnReviewableFalseIfApplicationFormRejected() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REJECTED).build();
        assertFalse(applicationForm.isModifiable());
    }

    @Test
    public void shouldReturnReviewableFalseIfApplicationFormAccepted() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).build();
        assertFalse(applicationForm.isModifiable());
    }

    @Test
    public void shouldReturnReviewableFalseIfApplicationFormAcceptedRejecteOrwitdrawn() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.UNSUBMITTED).build();
        assertTrue(applicationForm.isModifiable());
        applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).build();
        assertTrue(applicationForm.isModifiable());
        applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REJECTED).build();
        assertFalse(applicationForm.isModifiable());
        applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).build();
        assertFalse(applicationForm.isModifiable());
        applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.WITHDRAWN).build();
        assertFalse(applicationForm.isModifiable());
    }

    @Test
    public void shouldReturnDecidedTrueIfRejectedOrApproved() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).build();
        assertTrue(applicationForm.isDecided());
        applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REJECTED).build();
        assertTrue(applicationForm.isDecided());
    }

    @Test
    public void shouldReturnDecidedFalseIfNeitherUnsubmitterOrValidation() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.UNSUBMITTED).build();
        assertFalse(applicationForm.isDecided());
        applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).build();
        assertFalse(applicationForm.isDecided());
    }

    @Test
    public void shouldReturnTrueIfInStateByString() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.UNSUBMITTED).build();
        assertTrue(applicationForm.isInState("UNSUBMITTED"));
        assertFalse(applicationForm.isInState("VALIDATION"));
        assertFalse(applicationForm.isInState("BOB"));
    }

    @Test
    public void shouldSeeOnlyOwnInterviewVoteCommentsAndInterviewScheduleCommentsIfApplicant() {
        RegisteredUser user = new RegisteredUserBuilder().id(6).role(new RoleBuilder().id(Authority.APPLICANT).build()).build();
        RegisteredUser interviewer = new RegisteredUserBuilder().id(8).build();

        Comment genericComment = new CommentBuilder().id(4).build();
        InterviewComment interviewComment = new InterviewCommentBuilder().id(1).build();
        InterviewScheduleComment scheduleComment = new InterviewScheduleComment();

        InterviewVoteComment applicantVoteComment = new InterviewVoteComment();
        applicantVoteComment.setUser(user);

        InterviewVoteComment interviewerVoteComment = new InterviewVoteComment();
        interviewerVoteComment.setUser(interviewer);

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(new ProgramBuilder().build())
                .comments(genericComment, interviewComment, scheduleComment, applicantVoteComment, interviewerVoteComment).build();

        List<Comment> visibleComments = applicationForm.getVisibleComments(user);
        assertThat(visibleComments, CoreMatchers.hasItems(scheduleComment, applicantVoteComment));
    }

    @Test
    public void shouldSeeOwnCommentOnlyfRefereeOnly() {
        Comment comment = new CommentBuilder().id(4).build();
        RegisteredUser user = new RegisteredUserBuilder().id(6).roles(new RoleBuilder().id(Authority.REFEREE).build()).build();
        Referee referee = new RefereeBuilder().user(user).build();
        user.getReferees().add(referee);
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).comments(comment).referees(referee).build();
        referee.setApplication(applicationForm);
        ReferenceComment referenceComment = new ReferenceCommentBuilder().id(5).referee(referee).build();
        applicationForm.getApplicationComments().add(referenceComment);

        assertEquals(1, applicationForm.getVisibleComments(user).size());
        assertTrue(applicationForm.getVisibleComments(user).contains(referenceComment));
    }

    @Test
    public void shouldSeeAllCommentsIfNotApplicantOrRefereeOnly() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
        RegisteredUser reviewerUserOne = new RegisteredUserBuilder().id(6).build();
        RegisteredUser reviewerUserTwo = new RegisteredUserBuilder().referees()
                .roles(new RoleBuilder().id(Authority.REVIEWER).build(), new RoleBuilder().id(Authority.REFEREE).build()).id(7).build();

        Comment commentOne = new CommentBuilder().date(format.parse("01 01 2011")).id(4).user(reviewerUserTwo).build();
        Comment commentTwo = new CommentBuilder().date(format.parse("01 10 2011")).id(6).user(reviewerUserOne).build();
        Comment commentThree = new CommentBuilder().date(format.parse("01 05 2011")).id(9).user(reviewerUserTwo).build();

        ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUserOne).build(),
                new ReviewerBuilder().user(reviewerUserTwo).build()).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().reviewRounds(reviewRound).id(5).comments(commentOne, commentTwo, commentThree).build();
        Referee referee = new RefereeBuilder().application(applicationForm).build();
        reviewerUserTwo.getReferees().add(referee);
        List<Comment> visibleComments = applicationForm.getVisibleComments(reviewerUserTwo);
        assertEquals(3, visibleComments.size());
    }

    @Test
    public void shouldSeeAllCommentsIfApplicantAndAdministrator() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
        RegisteredUser reviewerUserOne = new RegisteredUserBuilder().id(6).build();
        RegisteredUser reviewerUserTwo = new RegisteredUserBuilder()
                .referees()
                .roles(new RoleBuilder().id(Authority.REVIEWER).build(), new RoleBuilder().id(Authority.REFEREE).build(),
                        new RoleBuilder().id(Authority.APPLICANT).build()).id(7).build();

        Comment commentOne = new CommentBuilder().date(format.parse("01 01 2011")).id(4).user(reviewerUserTwo).build();
        Comment commentTwo = new CommentBuilder().date(format.parse("01 10 2011")).id(6).user(reviewerUserOne).build();
        Comment commentThree = new CommentBuilder().date(format.parse("01 05 2011")).id(9).user(reviewerUserTwo).build();

        ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUserOne).build(),
                new ReviewerBuilder().user(reviewerUserTwo).build()).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().reviewRounds(reviewRound).id(5).comments(commentOne, commentTwo, commentThree).build();
        Referee referee = new RefereeBuilder().application(applicationForm).build();
        reviewerUserTwo.getReferees().add(referee);
        List<Comment> visibleComments = applicationForm.getVisibleComments(reviewerUserTwo);
        assertEquals(3, visibleComments.size());
    }

    @Test
    public void shouldDisplayInterviewCommentsSubmittedByApplicantToApplicantAndInterviewer() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
        RegisteredUser interviewerUser = new RegisteredUserBuilder().role(new RoleBuilder().id(Authority.INTERVIEWER).build()).id(6).build();
        RegisteredUser applicant = new RegisteredUserBuilder().role(new RoleBuilder().id(Authority.APPLICANT).build()).id(7).build();
        Interviewer interviewer = new InterviewerBuilder().user(interviewerUser).id(1).build();
        Interview interview = new InterviewBuilder().interviewers(interviewer).id(1).build();

        InterviewVoteComment interviewVoteCommentByInterviewer = new InterviewVoteCommentBuilder().date(format.parse("01 01 2011")).id(1).user(interviewerUser)
                .build();
        InterviewVoteComment interviewVoteCommentByApplicant = new InterviewVoteCommentBuilder().date(format.parse("01 01 2011")).id(2).user(applicant).build();

        Program program = new ProgramBuilder().id(1).viewers(interviewerUser).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(applicant).interviews(interview).id(5)
                .comments(interviewVoteCommentByInterviewer, interviewVoteCommentByApplicant).build();
        List<Comment> visibleComments = applicationForm.getVisibleComments(applicant);
        assertEquals(1, visibleComments.size());
        List<Comment> visibleCommentsForInterviewer = applicationForm.getVisibleComments(interviewerUser);
        assertEquals(2, visibleCommentsForInterviewer.size());
    }

    @Test
    public void shouldDisplayInterviewCommentsSubmittedByInterviewerToInterviewerOnly() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
        RegisteredUser interviewerUser = new RegisteredUserBuilder().role(new RoleBuilder().id(Authority.INTERVIEWER).build()).id(6).build();
        RegisteredUser applicant = new RegisteredUserBuilder().role(new RoleBuilder().id(Authority.APPLICANT).build()).id(7).build();
        Interviewer interviewer = new InterviewerBuilder().user(interviewerUser).id(1).build();
        Interview interview = new InterviewBuilder().interviewers(interviewer).id(1).build();

        InterviewVoteComment interviewVoteCommentByInterviewer = new InterviewVoteCommentBuilder().date(format.parse("01 01 2011")).id(1).user(interviewerUser)
                .build();
        InterviewVoteComment interviewVoteCommentByApplicant = new InterviewVoteCommentBuilder().date(format.parse("01 01 2011")).id(2).user(applicant).build();

        Program program = new ProgramBuilder().id(1).viewers(interviewerUser).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(applicant).interviews(interview).id(5)
                .comments(interviewVoteCommentByInterviewer, interviewVoteCommentByApplicant).build();
        List<Comment> visibleCommentsForInterviewer = applicationForm.getVisibleComments(interviewerUser);
        assertEquals(2, visibleCommentsForInterviewer.size());
    }

    @Test
    public void shouldSeeAllCommentsIfViewerOfProgram() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");

        RegisteredUser viewer = new RegisteredUserBuilder().id(9).build();
        Program program = new ProgramBuilder().id(2).viewers(viewer).build();

        RegisteredUser reviewerUserOne = new RegisteredUserBuilder().id(6).build();
        RegisteredUser reviewerUserTwo = new RegisteredUserBuilder()
                .referees()
                .roles(new RoleBuilder().id(Authority.REVIEWER).build(), new RoleBuilder().id(Authority.REFEREE).build(),
                        new RoleBuilder().id(Authority.APPLICANT).build()).id(7).build();

        Comment commentOne = new CommentBuilder().date(format.parse("01 01 2011")).id(4).user(reviewerUserTwo).build();
        Comment commentTwo = new CommentBuilder().date(format.parse("01 10 2011")).id(6).user(reviewerUserOne).build();
        Comment commentThree = new CommentBuilder().date(format.parse("01 05 2011")).id(9).user(reviewerUserTwo).build();

        ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUserOne).build(),
                new ReviewerBuilder().user(reviewerUserTwo).build()).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().reviewRounds(reviewRound).id(5).comments(commentOne, commentTwo, commentThree)
                .program(program).build();

        Referee referee = new RefereeBuilder().application(applicationForm).build();
        reviewerUserTwo.getReferees().add(referee);

        List<Comment> visibleComments = applicationForm.getVisibleComments(viewer);
        assertEquals(3, visibleComments.size());
    }

    @Test
    public void shouldReturnStateChangeEventsEventsSortedByDate() throws ParseException {
        Event validationEvent = new StateChangeEventBuilder().id(1).date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/01/01"))
                .newStatus(ApplicationFormStatus.VALIDATION).build();
        Event reviewEvent = new StateChangeEventBuilder().id(2).date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/02/02"))
                .newStatus(ApplicationFormStatus.REVIEW).build();
        StateChangeEvent approvalEvent = new StateChangeEventBuilder().id(3).date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/04"))
                .newStatus(ApplicationFormStatus.APPROVAL).build();
        Event rejectedEvent = new StateChangeEventBuilder().id(40).date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04"))
                .newStatus(ApplicationFormStatus.REJECTED).build();
        ApplicationForm application = new ApplicationFormBuilder().id(1).events(approvalEvent, rejectedEvent, reviewEvent, validationEvent).build();
        List<StateChangeEvent> eventsSortedByDate = application.getStateChangeEventsSortedByDate();
        Assert.assertEquals(validationEvent, eventsSortedByDate.get(0));
        Assert.assertEquals(reviewEvent, eventsSortedByDate.get(1));
        Assert.assertEquals(approvalEvent, eventsSortedByDate.get(2));
        Assert.assertEquals(rejectedEvent, eventsSortedByDate.get(3));
        // fail("re-implement when other event types created");
    }

    @Test
    public void shouldReturnValidationifCurretnStateFirstReviewRound() {
        ReviewRound reviewRound = new ReviewRoundBuilder().id(3).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(8).reviewRounds(reviewRound).status(ApplicationFormStatus.REVIEW).build();
        assertEquals(ApplicationFormStatus.VALIDATION, applicationForm.getOutcomeOfStage());
    }

    @Test
    public void shouldReturnReviewifCurretnStateReviewButNotFfirstReviewRound() {
        ReviewRound reviewRoundOne = new ReviewRoundBuilder().id(3).build();
        ReviewRound reviewRoundTwo = new ReviewRoundBuilder().id(4).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(8).reviewRounds(reviewRoundOne, reviewRoundTwo).status(ApplicationFormStatus.REVIEW)
                .build();
        assertEquals(ApplicationFormStatus.REVIEW, applicationForm.getOutcomeOfStage());
    }

    @Test
    public void shouldReturValidationIfStateInterviewAndOneInteriewAndNoReviewRounds() {
        Interview interviewOne = new InterviewBuilder().id(3).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(8).interviews(interviewOne).status(ApplicationFormStatus.INTERVIEW).build();
        assertEquals(ApplicationFormStatus.VALIDATION, applicationForm.getOutcomeOfStage());
    }

    @Test
    public void shouldReturReviewIfStateInterviewAndOneInteriewAndSomeeviewRounds() {
        Interview interviewOne = new InterviewBuilder().id(3).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().id(3).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(8).interviews(interviewOne).reviewRounds(reviewRound)
                .status(ApplicationFormStatus.INTERVIEW).build();
        assertEquals(ApplicationFormStatus.REVIEW, applicationForm.getOutcomeOfStage());
    }

    @Test
    public void shouldReturnInterviewIfStateInterviewAndMoreThanOneInteriew() {
        Interview interviewOne = new InterviewBuilder().id(3).build();
        Interview interviewTwo = new InterviewBuilder().id(5).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(8).interviews(interviewOne, interviewTwo).status(ApplicationFormStatus.INTERVIEW)
                .build();
        assertEquals(ApplicationFormStatus.INTERVIEW, applicationForm.getOutcomeOfStage());
    }

    @Test
    public void shouldReturnApprovalIfStatusApprovalAndMoreThanOneApprovalRound() {
        ApprovalRound approvalRoundOne = new ApprovalRoundBuilder().id(3).build();
        ApprovalRound approvalRoundTwo = new ApprovalRoundBuilder().id(5).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(8).approvalRounds(approvalRoundOne, approvalRoundTwo)
                .status(ApplicationFormStatus.APPROVAL).build();
        assertEquals(ApplicationFormStatus.APPROVAL, applicationForm.getOutcomeOfStage());
    }

    @Test
    public void shouldReturnValidationIfStatusApprovalAndoneApprovalRoundAndNoInterviewsOrReviews() {
        ApprovalRound approvalRoundOne = new ApprovalRoundBuilder().id(3).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(8).approvalRounds(approvalRoundOne).status(ApplicationFormStatus.APPROVAL).build();
        assertEquals(ApplicationFormStatus.VALIDATION, applicationForm.getOutcomeOfStage());
    }

    @Test
    public void shouldReturReviewIfStateApprovalAndOneApprovalRoundAndSomeeviewRoundsbutNoInterviews() {
        ApprovalRound approvalRoundOne = new ApprovalRoundBuilder().id(3).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().id(3).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(8).approvalRounds(approvalRoundOne).reviewRounds(reviewRound)
                .status(ApplicationFormStatus.APPROVAL).build();
        assertEquals(ApplicationFormStatus.REVIEW, applicationForm.getOutcomeOfStage());
    }

    @Test
    public void shouldReturInterviewIfStateApprovalAndOneApprovalRoundAndSomeInterviewRounds() {
        ApprovalRound approvalRoundOne = new ApprovalRoundBuilder().id(3).build();
        Interview interviewOne = new InterviewBuilder().id(3).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(8).approvalRounds(approvalRoundOne).interviews(interviewOne)
                .status(ApplicationFormStatus.APPROVAL).build();
        assertEquals(ApplicationFormStatus.INTERVIEW, applicationForm.getOutcomeOfStage());
    }

    @Test
    public void shouldReturnValidationForStatusRejectedIfNoReviewRoundsItnerviewsOrApprovalRounds() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(8).status(ApplicationFormStatus.REJECTED).build();
        assertEquals(ApplicationFormStatus.VALIDATION, applicationForm.getOutcomeOfStage());
    }

    @Test
    public void shouldReturnInterviewForStatusRejectedIfNoApprovalRoundsButSomeInterviews() {
        Interview interviewOne = new InterviewBuilder().id(3).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(8).status(ApplicationFormStatus.REJECTED).interviews(interviewOne).build();
        assertEquals(ApplicationFormStatus.INTERVIEW, applicationForm.getOutcomeOfStage());
    }

    @Test
    public void shouldReturnIReviewForStatusRejectedIfNoApprovalRoundsorinterviewButSomereviewRounds() {
        ReviewRound reviewRound = new ReviewRoundBuilder().id(3).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(8).status(ApplicationFormStatus.REJECTED).reviewRounds(reviewRound).build();
        assertEquals(ApplicationFormStatus.REVIEW, applicationForm.getOutcomeOfStage());
    }

    @Test
    public void shouldReturnApprovalForStatusRejectedIfSomApprovalRounds() {
        ApprovalRound approvalRoundOne = new ApprovalRoundBuilder().id(3).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(8).approvalRounds(approvalRoundOne).status(ApplicationFormStatus.REJECTED).build();
        assertEquals(ApplicationFormStatus.APPROVAL, applicationForm.getOutcomeOfStage());
    }

    @Test
    public void shouldHaveEnoughReferencesToSendToPortico() {
        RegisteredUser user1 = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().id(Authority.REFEREE).build()).build();
        RegisteredUser user2 = new RegisteredUserBuilder().id(2).roles(new RoleBuilder().id(Authority.REFEREE).build()).build();

        Referee referee1 = new RefereeBuilder().user(user1).sendToUCL(true).build();
        Referee referee2 = new RefereeBuilder().user(user2).sendToUCL(true).build();

        user1.getReferees().add(referee1);
        user2.getReferees().add(referee2);

        ReferenceComment referenceComment1 = new ReferenceCommentBuilder().id(1).referee(referee1).build();
        ReferenceComment referenceComment2 = new ReferenceCommentBuilder().id(2).referee(referee2).build();

        referee1.setReference(referenceComment1);
        referee2.setReference(referenceComment2);

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).referees(referee1, referee2).comments(referenceComment1, referenceComment2)
                .build();

        assertTrue(applicationForm.hasEnoughReferencesToSendToPortico());
    }

    @Test
    public void shouldHaveEnoughQualificationsToSendToPortico() {
        Document document1 = new DocumentBuilder().id(1).build();

        Qualification qualification1 = new QualificationBuilder().id(1).sendToUCL(true).proofOfAward(document1).build();
        Qualification qualification2 = new QualificationBuilder().id(1).sendToUCL(true).proofOfAward(document1).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).qualification(qualification1, qualification2).build();

        assertTrue(applicationForm.hasEnoughQualificationsToSendToPortico());
    }

    @Test
    public void shouldNotBeCompleteForSendingToPorticoWithOnlyOneReference() {
        RegisteredUser user1 = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().id(Authority.REFEREE).build()).build();
        RegisteredUser user2 = new RegisteredUserBuilder().id(2).roles(new RoleBuilder().id(Authority.REFEREE).build()).build();

        Referee referee1 = new RefereeBuilder().user(user1).sendToUCL(true).build();
        Referee referee2 = new RefereeBuilder().user(user2).sendToUCL(false).build();

        user1.getReferees().add(referee1);
        user2.getReferees().add(referee2);

        ReferenceComment referenceComment1 = new ReferenceCommentBuilder().id(1).referee(referee1).build();
        ReferenceComment referenceComment2 = new ReferenceCommentBuilder().id(2).referee(referee2).build();

        referee1.setReference(referenceComment1);
        referee2.setReference(referenceComment2);

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).referees(referee1, referee2).comments(referenceComment1, referenceComment2)
                .build();

        assertFalse("Less than two reference comments have been selected for sending to Portico but function returned true.",
                applicationForm.hasEnoughReferencesToSendToPortico());
    }

    @Test
    public void shouldNotBeCompleteForSendingToPorticoWithNoQualifications() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();

        assertFalse(applicationForm.hasEnoughQualificationsToSendToPortico());
    }

    @Test
    public void shouldNotBeCompleteForSendingToPorticoWithMoreThanTwoQualifications() {
        Document document1 = new DocumentBuilder().id(1).build();

        Qualification qualification1 = new QualificationBuilder().id(1).sendToUCL(true).proofOfAward(document1).build();
        Qualification qualification2 = new QualificationBuilder().id(1).sendToUCL(true).proofOfAward(document1).build();
        Qualification qualification3 = new QualificationBuilder().id(1).sendToUCL(true).proofOfAward(document1).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).qualification(qualification1, qualification2, qualification3).build();

        assertFalse(applicationForm.hasEnoughQualificationsToSendToPortico());
    }

    @Test
    public void shouldGetProgramAndProjectTitle() {
        ApplicationForm application = new ApplicationFormBuilder().program(new ProgramBuilder().title("Ppp").build()).projectTitle("Rrr").build();
        assertEquals("Ppp (project: Rrr)", application.getProgramAndProjectTitle());
    }

    @Test
    public void shouldGetProgramAndProjectTitleWhenProjectTitleIsNull() {
        ApplicationForm application = new ApplicationFormBuilder().program(new ProgramBuilder().title("Ppp").build()).build();
        assertEquals("Ppp", application.getProgramAndProjectTitle());
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
}