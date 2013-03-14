package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RequestRestartCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;

public class ApplicationFormTest {

    @Test
    public void shouldReturnLatestCommentOfTypeRequestRestart() {
        Date now = new Date();
        Date yesterDay = DateUtils.addDays(now, -1);
        Date twoDaysAgo = DateUtils.addDays(now, -2);

        RequestRestartComment commentOne = new RequestRestartCommentBuilder().id(1).date(twoDaysAgo).build();
        RequestRestartComment commentTwo = new RequestRestartCommentBuilder().id(3).date(yesterDay).build();
        Comment commentThree = new CommentBuilder().date(now).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().comments(commentOne, commentTwo, commentThree).build();
        assertEquals(commentTwo, applicationForm.getLatestsRequestRestartComment());
    }

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
    public void shouldReturnNotificationOfCorrectType() {
        NotificationRecord validationReminder = new NotificationRecordBuilder().id(1).notificationType(NotificationType.VALIDATION_REMINDER).build();
        NotificationRecord submissionUpdateNotification = new NotificationRecordBuilder().id(2).notificationType(NotificationType.UPDATED_NOTIFICATION).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().notificationRecords(validationReminder).build();
        assertEquals(validationReminder, applicationForm.getNotificationForType(NotificationType.VALIDATION_REMINDER));
        assertNull(applicationForm.getNotificationForType(NotificationType.UPDATED_NOTIFICATION));
        applicationForm.addNotificationRecord(submissionUpdateNotification);
        assertEquals(submissionUpdateNotification, applicationForm.getNotificationForType(NotificationType.UPDATED_NOTIFICATION));
    }

    @Test
    public void shouldNotAddDuplicateNotificationTypeButUpdateExistingOne() throws ParseException {
        NotificationRecord updatedNotification = new NotificationRecordBuilder().notificationType(NotificationType.UPDATED_NOTIFICATION)
                .notificationDate(DateUtils.parseDate("2012-09-09T00:03:00", new String[] { "yyyy-MM-dd'T'HH:mm:ss" })).build();

        NotificationRecord duplicateNpdatedNotification = new NotificationRecordBuilder().notificationType(NotificationType.UPDATED_NOTIFICATION)
                .notificationDate(new Date()).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().notificationRecords(updatedNotification).build();

        applicationForm.addNotificationRecord(duplicateNpdatedNotification);

        assertEquals(1, applicationForm.getNotificationRecords().size());
        assertEquals(updatedNotification.getDate(), applicationForm.getNotificationRecords().get(0).getDate());
    }

    @Test
    public void shouldReturnTrueIfInStateByString() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.UNSUBMITTED).build();
        assertTrue(applicationForm.isInState("UNSUBMITTED"));
        assertFalse(applicationForm.isInState("VALIDATION"));
        assertFalse(applicationForm.isInState("BOB"));
    }

    @Test
    public void shouldSeeNoCommentsApplicant() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(new ProgramBuilder().build()).comments(new CommentBuilder().id(4).build()).build();
        RegisteredUser user = new RegisteredUserBuilder().id(6).role(new RoleBuilder().authorityEnum(Authority.APPLICANT).build()).build();
        assertTrue(applicationForm.getVisibleComments(user).isEmpty());
    }

    @Test
    public void shouldSeeOwnCommentOnlyfRefereeOnly() {
        Comment comment = new CommentBuilder().id(4).build();
        RegisteredUser user = new RegisteredUserBuilder().id(6).roles(new RoleBuilder().authorityEnum(Authority.REFEREE).build()).build();
        Referee referee = new RefereeBuilder().user(user).toReferee();
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
                .roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).build(), new RoleBuilder().authorityEnum(Authority.REFEREE).build()).id(7).build();

        Comment commentOne = new CommentBuilder().date(format.parse("01 01 2011")).id(4).user(reviewerUserTwo).build();
        Comment commentTwo = new CommentBuilder().date(format.parse("01 10 2011")).id(6).user(reviewerUserOne).build();
        Comment commentThree = new CommentBuilder().date(format.parse("01 05 2011")).id(9).user(reviewerUserTwo).build();

        ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUserOne).build(),
                new ReviewerBuilder().user(reviewerUserTwo).build()).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().reviewRounds(reviewRound).id(5).comments(commentOne, commentTwo, commentThree).build();
        Referee referee = new RefereeBuilder().application(applicationForm).toReferee();
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
                .roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).build(), new RoleBuilder().authorityEnum(Authority.REFEREE).build(),
                        new RoleBuilder().authorityEnum(Authority.APPLICANT).build()).id(7).build();

        Comment commentOne = new CommentBuilder().date(format.parse("01 01 2011")).id(4).user(reviewerUserTwo).build();
        Comment commentTwo = new CommentBuilder().date(format.parse("01 10 2011")).id(6).user(reviewerUserOne).build();
        Comment commentThree = new CommentBuilder().date(format.parse("01 05 2011")).id(9).user(reviewerUserTwo).build();

        ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUserOne).build(),
                new ReviewerBuilder().user(reviewerUserTwo).build()).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().reviewRounds(reviewRound).id(5).comments(commentOne, commentTwo, commentThree).build();
        Referee referee = new RefereeBuilder().application(applicationForm).toReferee();
        reviewerUserTwo.getReferees().add(referee);
        List<Comment> visibleComments = applicationForm.getVisibleComments(reviewerUserTwo);
        assertEquals(3, visibleComments.size());
    }
    
    @Test
    public void shouldSeeAllCommentsIfViewerOfProgram() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
        
        RegisteredUser viewer = new RegisteredUserBuilder().id(9).build();
        Program program = new ProgramBuilder().id(2).viewers(viewer).build();

        RegisteredUser reviewerUserOne = new RegisteredUserBuilder().id(6).build();
        RegisteredUser reviewerUserTwo = new RegisteredUserBuilder()
                .referees()
                .roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).build(),
                        new RoleBuilder().authorityEnum(Authority.REFEREE).build(),
                        new RoleBuilder().authorityEnum(Authority.APPLICANT).build()).id(7).build();

        Comment commentOne = new CommentBuilder().date(format.parse("01 01 2011")).id(4).user(reviewerUserTwo).build();
        Comment commentTwo = new CommentBuilder().date(format.parse("01 10 2011")).id(6).user(reviewerUserOne).build();
        Comment commentThree = new CommentBuilder().date(format.parse("01 05 2011")).id(9).user(reviewerUserTwo)
                .build();

        ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(
                new ReviewerBuilder().user(reviewerUserOne).build(),
                new ReviewerBuilder().user(reviewerUserTwo).build()).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().reviewRounds(reviewRound).id(5)
                .comments(commentOne, commentTwo, commentThree).program(program).build();
        
        Referee referee = new RefereeBuilder().application(applicationForm).toReferee();
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
    public void shouldReturnUsersWilingToSupervise() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
        RegisteredUser reviewerUserOne = new RegisteredUserBuilder().id(6).build();
        RegisteredUser reviewerUserTwo = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).build()).id(7).build();

        Comment commentOne = new CommentBuilder().date(format.parse("01 01 2011")).id(4).user(reviewerUserTwo).build();
        Comment commentTwo = new CommentBuilder().date(format.parse("01 10 2011")).id(6).user(reviewerUserOne).build();
        ReviewComment review1 = new ReviewCommentBuilder().willingToInterview(true).id(10).user(reviewerUserTwo).build();
        ReviewComment review2 = new ReviewCommentBuilder().willingToInterview(false).id(11).user(reviewerUserTwo).build();
        ReviewComment review3 = new ReviewCommentBuilder().willingToInterview(true).id(12).user(reviewerUserOne).build();
        InterviewComment interviewComment = new InterviewCommentBuilder().willingToSupervise(true).id(12).user(reviewerUserTwo).build();
        InterviewComment interviewComment1 = new InterviewCommentBuilder().willingToSupervise(false).id(12).user(reviewerUserOne).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5)
                .comments(commentOne, commentTwo, review1, review2, review3, interviewComment, interviewComment1).build();

        List<RegisteredUser> users = applicationForm.getUsersWillingToSupervise();
        assertEquals(1, users.size());
    }

    @Test
    public void shouldReturnEmptyListIfNoUsersWillingToSupervise() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
        RegisteredUser reviewerUserOne = new RegisteredUserBuilder().id(6).build();
        RegisteredUser reviewerUserTwo = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).build()).id(7).build();

        Comment commentOne = new CommentBuilder().date(format.parse("01 01 2011")).id(4).user(reviewerUserTwo).build();
        Comment commentTwo = new CommentBuilder().date(format.parse("01 10 2011")).id(6).user(reviewerUserOne).build();
        ReviewComment review1 = new ReviewCommentBuilder().willingToInterview(true).id(10).user(reviewerUserTwo).build();
        ReviewComment review2 = new ReviewCommentBuilder().willingToInterview(false).id(11).user(reviewerUserTwo).build();
        ReviewComment review3 = new ReviewCommentBuilder().willingToInterview(true).id(12).user(reviewerUserOne).build();
        InterviewComment interviewComment1 = new InterviewCommentBuilder().willingToSupervise(false).id(12).user(reviewerUserOne).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).comments(commentOne, commentTwo, review1, review2, review3, interviewComment1)
                .build();

        List<RegisteredUser> users = applicationForm.getUsersWillingToSupervise();
        assertEquals(Collections.EMPTY_LIST, users);
    }

    @Test
    public void shouldReturnUsersWilingTointerview() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
        RegisteredUser reviewerUserOne = new RegisteredUserBuilder().id(6).build();
        RegisteredUser reviewerUserTwo = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).build()).id(7).build();

        Comment commentOne = new CommentBuilder().date(format.parse("01 01 2011")).id(4).user(reviewerUserTwo).build();
        Comment commentTwo = new CommentBuilder().date(format.parse("01 10 2011")).id(6).user(reviewerUserOne).build();
        ReviewComment review1 = new ReviewCommentBuilder().willingToInterview(true).id(10).user(reviewerUserTwo).build();
        ReviewComment review2 = new ReviewCommentBuilder().willingToInterview(false).id(11).user(reviewerUserTwo).build();
        ReviewComment review3 = new ReviewCommentBuilder().willingToInterview(true).id(12).user(reviewerUserOne).build();
        InterviewComment interviewComment = new InterviewCommentBuilder().willingToSupervise(true).id(12).user(reviewerUserTwo).build();
        InterviewComment interviewComment1 = new InterviewCommentBuilder().willingToSupervise(false).id(12).user(reviewerUserOne).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5)
                .comments(commentOne, commentTwo, review1, review2, review3, interviewComment, interviewComment1).build();

        List<RegisteredUser> users = applicationForm.getReviewersWillingToInterview();
        assertEquals(2, users.size());
    }

    @Test
    public void shouldReturnEmptyListIfNoUsersWillingToInterview() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
        RegisteredUser reviewerUserOne = new RegisteredUserBuilder().id(6).build();
        RegisteredUser reviewerUserTwo = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).build()).id(7).build();

        Comment commentOne = new CommentBuilder().date(format.parse("01 01 2011")).id(4).user(reviewerUserTwo).build();
        Comment commentTwo = new CommentBuilder().date(format.parse("01 10 2011")).id(6).user(reviewerUserOne).build();
        ReviewComment review2 = new ReviewCommentBuilder().willingToInterview(false).id(11).user(reviewerUserTwo).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).comments(commentOne, commentTwo, review2).build();

        List<RegisteredUser> users = applicationForm.getReviewersWillingToInterview();
        assertEquals(Collections.EMPTY_LIST, users);
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
    public void shouldBeCompleteForSendingToPorticoWithTwoReferencesAndTwoQualifications() {
        RegisteredUser user1 = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.REFEREE).build()).build();
        RegisteredUser user2 = new RegisteredUserBuilder().id(2).roles(new RoleBuilder().authorityEnum(Authority.REFEREE).build()).build();

        Referee referee1 = new RefereeBuilder().user(user1).sendToUCL(true).toReferee();
        Referee referee2 = new RefereeBuilder().user(user2).sendToUCL(true).toReferee();

        user1.getReferees().add(referee1);
        user2.getReferees().add(referee2);

        Document document1 = new DocumentBuilder().id(1).build();

        Qualification qualification1 = new QualificationBuilder().id(1).sendToUCL(true).proofOfAward(document1).build();
        Qualification qualification2 = new QualificationBuilder().id(1).sendToUCL(true).proofOfAward(document1).build();

        ReferenceComment referenceComment1 = new ReferenceCommentBuilder().id(1).referee(referee1).build();
        ReferenceComment referenceComment2 = new ReferenceCommentBuilder().id(2).referee(referee2).build();

        referee1.setReference(referenceComment1);
        referee2.setReference(referenceComment2);

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).referees(referee1, referee2).comments(referenceComment1, referenceComment2)
                .qualification(qualification1, qualification2).build();

        assertTrue(applicationForm.isCompleteForSendingToPortico(false));
    }

    @Test
    public void shouldBeCompleteForSendingToPorticoWithTwoReferencesAndExplanation() {
        RegisteredUser user1 = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.REFEREE).build()).build();
        RegisteredUser user2 = new RegisteredUserBuilder().id(2).roles(new RoleBuilder().authorityEnum(Authority.REFEREE).build()).build();

        Referee referee1 = new RefereeBuilder().user(user1).sendToUCL(true).toReferee();
        Referee referee2 = new RefereeBuilder().user(user2).sendToUCL(true).toReferee();

        user1.getReferees().add(referee1);
        user2.getReferees().add(referee2);

        ReferenceComment referenceComment1 = new ReferenceCommentBuilder().id(1).referee(referee1).build();
        ReferenceComment referenceComment2 = new ReferenceCommentBuilder().id(2).referee(referee2).build();

        referee1.setReference(referenceComment1);
        referee2.setReference(referenceComment2);

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).referees(referee1, referee2).comments(referenceComment1, referenceComment2)
                .build();

        assertTrue("Two reference comments have been selected for sending to Portico but function returned false.",
                applicationForm.isCompleteForSendingToPortico(true));
    }

    @Test
    public void shouldNotBeCompleteForSendingToPorticoWithOnlyOneReference() {
        RegisteredUser user1 = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.REFEREE).build()).build();
        RegisteredUser user2 = new RegisteredUserBuilder().id(2).roles(new RoleBuilder().authorityEnum(Authority.REFEREE).build()).build();

        Referee referee1 = new RefereeBuilder().user(user1).sendToUCL(true).toReferee();
        Referee referee2 = new RefereeBuilder().user(user2).sendToUCL(false).toReferee();

        user1.getReferees().add(referee1);
        user2.getReferees().add(referee2);

        ReferenceComment referenceComment1 = new ReferenceCommentBuilder().id(1).referee(referee1).build();
        ReferenceComment referenceComment2 = new ReferenceCommentBuilder().id(2).referee(referee2).build();

        referee1.setReference(referenceComment1);
        referee2.setReference(referenceComment2);

        Document document1 = new DocumentBuilder().id(1).build();
        Qualification qualification1 = new QualificationBuilder().id(1).sendToUCL(true).proofOfAward(document1).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).referees(referee1, referee2).comments(referenceComment1, referenceComment2)
                .qualification(qualification1).build();

        assertFalse("Less than two reference comments have been selected for sending to Portico but function returned true.",
                applicationForm.isCompleteForSendingToPortico(false));
    }

    @Test
    public void shouldNotBeCompleteForSendingToPorticoWithNoQualificationsAndNoExplanation() {
        RegisteredUser user1 = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.REFEREE).build()).build();
        RegisteredUser user2 = new RegisteredUserBuilder().id(2).roles(new RoleBuilder().authorityEnum(Authority.REFEREE).build()).build();

        Referee referee1 = new RefereeBuilder().user(user1).sendToUCL(true).toReferee();
        Referee referee2 = new RefereeBuilder().user(user2).sendToUCL(true).toReferee();

        user1.getReferees().add(referee1);
        user2.getReferees().add(referee2);

        ReferenceComment referenceComment1 = new ReferenceCommentBuilder().id(1).referee(referee1).build();
        ReferenceComment referenceComment2 = new ReferenceCommentBuilder().id(2).referee(referee2).build();

        referee1.setReference(referenceComment1);
        referee2.setReference(referenceComment2);

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).referees(referee1, referee2).comments(referenceComment1, referenceComment2)
                .build();

        assertFalse(applicationForm.isCompleteForSendingToPortico(false));
    }

    @Test
    public void shouldNotBeCompleteForSendingToPorticoWithMoreThanTwoQualifications() {
        RegisteredUser user1 = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.REFEREE).build()).build();
        RegisteredUser user2 = new RegisteredUserBuilder().id(2).roles(new RoleBuilder().authorityEnum(Authority.REFEREE).build()).build();

        Referee referee1 = new RefereeBuilder().user(user1).sendToUCL(true).toReferee();
        Referee referee2 = new RefereeBuilder().user(user2).sendToUCL(true).toReferee();

        user1.getReferees().add(referee1);
        user2.getReferees().add(referee2);

        Document document1 = new DocumentBuilder().id(1).build();

        Qualification qualification1 = new QualificationBuilder().id(1).sendToUCL(true).proofOfAward(document1).build();
        Qualification qualification2 = new QualificationBuilder().id(1).sendToUCL(true).proofOfAward(document1).build();
        Qualification qualification3 = new QualificationBuilder().id(1).sendToUCL(true).proofOfAward(document1).build();

        ReferenceComment referenceComment1 = new ReferenceCommentBuilder().id(1).referee(referee1).build();
        ReferenceComment referenceComment2 = new ReferenceCommentBuilder().id(2).referee(referee2).build();

        referee1.setReference(referenceComment1);
        referee2.setReference(referenceComment2);

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).referees(referee1, referee2).comments(referenceComment1, referenceComment2)
                .qualification(qualification1, qualification2, qualification3).build();

        assertFalse(applicationForm.isCompleteForSendingToPortico(false));
    }

    @Test
    public void shouldReturnFalseForIsProgrammeStillAvailableIfLargestEndDateIsBeforeToday() {
        DateTime instance1StartDate = new DateTime(2011, 1, 1, 8, 0);
        DateTime instance1EndDate = new DateTime(2011, 8, 1, 8, 0);

        DateTime instance2StartDate = new DateTime(2012, 10, 1, 8, 0);
        DateTime instance2EndDate = new DateTime(2012, 6, 1, 8, 0);

        ProgramInstance instance1 = new ProgramInstanceBuilder().id(Integer.MAX_VALUE).academicYear("2013").applicationStartDate(instance1StartDate.toDate())
                .enabled(true).applicationDeadline(instance1EndDate.toDate()).studyOption("F+++++", "Full-time").identifier("0009").build();

        ProgramInstance instance2 = new ProgramInstanceBuilder().id(Integer.MAX_VALUE).academicYear("2013").applicationStartDate(instance2StartDate.toDate())
                .enabled(true).applicationDeadline(instance2EndDate.toDate()).studyOption("F+++++", "Full-time").identifier("0009").build();

        Program program = new ProgramBuilder().id(Integer.MAX_VALUE).code("TMRMBISING99").enabled(true).instances(instance1, instance2)
                .title("MRes Medical and Biomedical Imaging").build();

        ProgrammeDetails programDetails = new ProgrammeDetailsBuilder().id(Integer.MAX_VALUE).programmeName("MRes Medical and Biomedical Imaging")
                .projectName("Project Title").startDate(org.apache.commons.lang.time.DateUtils.addDays(new Date(), 1)).studyOption("F+++++", "Full-time")
                .build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(Integer.MAX_VALUE).program(program).programmeDetails(programDetails).build();

        assertFalse("Should have returned false because the largest possible end date is " + instance2EndDate.toString() + " which is before today",
                applicationForm.isProgrammeStillAvailable());
    }

    @Test
    public void shouldAllowSuperAdministratorToEdit() {
        RegisteredUser superAdmin = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).build()).id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.INTERVIEW).build();
        assertTrue(applicationForm.isUserAllowedToSeeAndEditAsAdministrator(superAdmin));
    }

    @Test
    public void shouldAllowProgramAdministratorToEdit() {
        RegisteredUser admin = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build()).id(7).build();
        Program program = new ProgramBuilder().id(1).administrators(admin).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.INTERVIEW).program(program).build();
        assertTrue(applicationForm.isUserAllowedToSeeAndEditAsAdministrator(admin));
    }

    @Test
    public void shouldAllowApplicationInterviewerToEdit() {
        RegisteredUser interviewerUser = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).build()).id(7).build();
        Interviewer interviewer = new InterviewerBuilder().user(interviewerUser).build();
        Interview interview = new InterviewBuilder().interviewers(interviewer).build();
        Program program = new ProgramBuilder().id(1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.INTERVIEW).program(program)
                .latestInterview(interview).build();
        assertTrue(applicationForm.isUserAllowedToSeeAndEditAsAdministrator(interviewerUser));
    }

    @Test
    public void shouldNotAllowApplicantToEditAsAdministrator() {
        RegisteredUser applicant = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).build()).id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.INTERVIEW).applicant(applicant).build();
        assertFalse(applicationForm.isUserAllowedToSeeAndEditAsAdministrator(applicant));
    }

    @Test
    public void shouldNotAllowAdministratorToEditApplicationInValidationStage() {
        RegisteredUser admin = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build()).id(7).build();
        Program program = new ProgramBuilder().id(1).administrators(admin).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).program(program).build();
        assertFalse(applicationForm.isUserAllowedToSeeAndEditAsAdministrator(admin));
    }

    @Test
    public void shouldNotAllowAdministratorToEditUnsubmittedApplication() {
        RegisteredUser admin = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build()).id(7).build();
        Program program = new ProgramBuilder().id(1).administrators(admin).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.UNSUBMITTED).program(program).build();
        assertFalse(applicationForm.isUserAllowedToSeeAndEditAsAdministrator(admin));
    }

    @Test
    public void shouldNotAllowAdministratorToEditWithdrawnApplication() {
        RegisteredUser admin = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build()).id(7).build();
        Program program = new ProgramBuilder().id(1).administrators(admin).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.WITHDRAWN).program(program).build();
        assertFalse(applicationForm.isUserAllowedToSeeAndEditAsAdministrator(admin));
    }

    @Test
    public void shouldNotAllowAdministratorToEditRejectedApplication() {
        RegisteredUser admin = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build()).id(7).build();
        Program program = new ProgramBuilder().id(1).administrators(admin).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REJECTED).program(program).build();
        assertFalse(applicationForm.isUserAllowedToSeeAndEditAsAdministrator(admin));
    }

    @Test
    public void shouldNotAllowAdministratorToEditApplicationInApprovalState() {
        RegisteredUser admin = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build()).id(7).build();
        Program program = new ProgramBuilder().id(1).administrators(admin).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.APPROVAL).program(program).build();
        assertFalse(applicationForm.isUserAllowedToSeeAndEditAsAdministrator(admin));
    }

    @Test
    public void shouldReturnTrueForIsProgrammeStillAvailableIfLargestEndDateIsAfterToday() {
        DateTime instance1StartDate = new DateTime(2013, 1, 1, 8, 0);
        DateTime instance1EndDate = new DateTime(2013, 8, 1, 8, 0);

        DateTime instance2StartDate = new DateTime(2014, 10, 1, 8, 0);
        DateTime instance2EndDate = new DateTime(2014, 6, 1, 8, 0);

        ProgramInstance instance1 = new ProgramInstanceBuilder().id(Integer.MAX_VALUE).academicYear("2013").applicationStartDate(instance1StartDate.toDate())
                .enabled(true).applicationDeadline(instance1EndDate.toDate()).studyOption("F+++++", "Full-time").identifier("0009").build();

        ProgramInstance instance2 = new ProgramInstanceBuilder().id(Integer.MAX_VALUE).academicYear("2013").applicationStartDate(instance2StartDate.toDate())
                .enabled(true).applicationDeadline(instance2EndDate.toDate()).studyOption("F+++++", "Full-time").identifier("0009").build();

        Program program = new ProgramBuilder().id(Integer.MAX_VALUE).code("TMRMBISING99").enabled(true).instances(instance1, instance2)
                .title("MRes Medical and Biomedical Imaging").build();

        ProgrammeDetails programDetails = new ProgrammeDetailsBuilder().id(Integer.MAX_VALUE).programmeName("MRes Medical and Biomedical Imaging")
                .projectName("Project Title").startDate(org.apache.commons.lang.time.DateUtils.addDays(new Date(), 1)).studyOption("F+++++", "Full-time")
                .build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(Integer.MAX_VALUE).program(program).programmeDetails(programDetails).build();

        assertTrue("Should have returned true because the largest possible end date is " + instance2EndDate.toString() + " which is after today",
                applicationForm.isProgrammeStillAvailable());
    }

    @Test
    public void shouldReturnFalseForIsProgrammeStillAvailableIfProgrammeIsNotEnabled() {
        DateTime instance1StartDate = new DateTime(2013, 1, 1, 8, 0);
        DateTime instance1EndDate = new DateTime(2013, 8, 1, 8, 0);

        DateTime instance2StartDate = new DateTime(2014, 10, 1, 8, 0);
        DateTime instance2EndDate = new DateTime(2014, 6, 1, 8, 0);

        ProgramInstance instance1 = new ProgramInstanceBuilder().id(Integer.MAX_VALUE).academicYear("2013").applicationStartDate(instance1StartDate.toDate())
                .enabled(true).applicationDeadline(instance1EndDate.toDate()).studyOption("F+++++", "Full-time").identifier("0009").build();

        ProgramInstance instance2 = new ProgramInstanceBuilder().id(Integer.MAX_VALUE).academicYear("2013").applicationStartDate(instance2StartDate.toDate())
                .enabled(true).applicationDeadline(instance2EndDate.toDate()).studyOption("F+++++", "Full-time").identifier("0009").build();

        Program program = new ProgramBuilder().id(Integer.MAX_VALUE).code("TMRMBISING99").enabled(false).instances(instance1, instance2)
                .title("MRes Medical and Biomedical Imaging").build();

        ProgrammeDetails programDetails = new ProgrammeDetailsBuilder().id(Integer.MAX_VALUE).programmeName("MRes Medical and Biomedical Imaging")
                .projectName("Project Title").startDate(org.apache.commons.lang.time.DateUtils.addDays(new Date(), 1)).studyOption("F+++++", "Full-time")
                .build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(Integer.MAX_VALUE).program(program).programmeDetails(programDetails).build();

        assertFalse("Should have returned false because the programme is not enabled", applicationForm.isProgrammeStillAvailable());
    }

    @Test
    public void shouldReturnFalseForIsProgrammeStillAvailableIfNoInstancesAreEnabled() {
        DateTime instance1StartDate = new DateTime(2013, 1, 1, 8, 0);
        DateTime instance1EndDate = new DateTime(2013, 8, 1, 8, 0);

        DateTime instance2StartDate = new DateTime(2014, 10, 1, 8, 0);
        DateTime instance2EndDate = new DateTime(2014, 6, 1, 8, 0);

        ProgramInstance instance1 = new ProgramInstanceBuilder().id(Integer.MAX_VALUE).academicYear("2013").applicationStartDate(instance1StartDate.toDate())
                .enabled(false).applicationDeadline(instance1EndDate.toDate()).studyOption("F+++++", "Full-time").identifier("0009").build();

        ProgramInstance instance2 = new ProgramInstanceBuilder().id(Integer.MAX_VALUE).academicYear("2013").applicationStartDate(instance2StartDate.toDate())
                .enabled(false).applicationDeadline(instance2EndDate.toDate()).studyOption("F+++++", "Full-time").identifier("0009").build();

        Program program = new ProgramBuilder().id(Integer.MAX_VALUE).code("TMRMBISING99").enabled(false).instances(instance1, instance2)
                .title("MRes Medical and Biomedical Imaging").build();

        ProgrammeDetails programDetails = new ProgrammeDetailsBuilder().id(Integer.MAX_VALUE).programmeName("MRes Medical and Biomedical Imaging")
                .projectName("Project Title").startDate(org.apache.commons.lang.time.DateUtils.addDays(new Date(), 1)).studyOption("F+++++", "Full-time")
                .build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(Integer.MAX_VALUE).program(program).programmeDetails(programDetails).build();

        assertFalse("Should have returned false because the programme instances are not enabled", applicationForm.isProgrammeStillAvailable());
    }

    @Test
    public void shouldReturnFalseForIsProgrammeStillAvailableIfStudyOptionsDoNotMatch() {
        DateTime instance1StartDate = new DateTime(2013, 1, 1, 8, 0);
        DateTime instance1EndDate = new DateTime(2013, 8, 1, 8, 0);

        DateTime instance2StartDate = new DateTime(2014, 10, 1, 8, 0);
        DateTime instance2EndDate = new DateTime(2014, 6, 1, 8, 0);

        ProgramInstance instance1 = new ProgramInstanceBuilder().id(Integer.MAX_VALUE).academicYear("2013").applicationStartDate(instance1StartDate.toDate())
                .enabled(false).applicationDeadline(instance1EndDate.toDate()).studyOption("F+++++", "Full-time").identifier("0009").build();

        ProgramInstance instance2 = new ProgramInstanceBuilder().id(Integer.MAX_VALUE).academicYear("2013").applicationStartDate(instance2StartDate.toDate())
                .enabled(false).applicationDeadline(instance2EndDate.toDate()).studyOption("F+++++", "Full-time").identifier("0009").build();

        Program program = new ProgramBuilder().id(Integer.MAX_VALUE).code("TMRMBISING99").enabled(false).instances(instance1, instance2)
                .title("MRes Medical and Biomedical Imaging").build();

        ProgrammeDetails programDetails = new ProgrammeDetailsBuilder().id(Integer.MAX_VALUE).programmeName("MRes Medical and Biomedical Imaging")
                .projectName("Project Title").startDate(org.apache.commons.lang.time.DateUtils.addDays(new Date(), 1)).studyOption("H+++++", "Part-time")
                .build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(Integer.MAX_VALUE).program(program).programmeDetails(programDetails).build();

        assertFalse("Should have returned false because the study options do not match the programme instances", applicationForm.isProgrammeStillAvailable());
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
