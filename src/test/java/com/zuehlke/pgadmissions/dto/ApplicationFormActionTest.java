package com.zuehlke.pgadmissions.dto;

import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.APPROVAL;
import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.INTERVIEW;
import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.REVIEW;
import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.VALIDATION;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.ADD_INTERVIEW_FEEDBACK;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.*;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.ADD_REVIEW;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.ASSIGN_INTERVIEWERS;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.ASSIGN_REVIEWERS;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.COMMENT;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.COMPLETE_INTERVIEW_STAGE;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.COMPLETE_REVIEW_STAGE;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.COMPLETE_VALIDATION_STAGE;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.CONFIRM_ELIGIBILITY;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.CONFIRM_INTERVIEW_TIME;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.EMAIL_APPLICANT;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.PROVIDE_INTERVIEW_AVAILABILITY;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.VIEW;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.VIEW_EDIT;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.WITHDRAW;
import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewParticipantBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.InterviewStage;

public class ApplicationFormActionTest {

    private ActionsDefinitions actionsDefinitions;

    private RegisteredUser userMock;

    private ApplicationForm applicationMock;

    @Before
    public void setup() {
        actionsDefinitions = new ActionsDefinitions();

        userMock = EasyMock.createMock(RegisteredUser.class);
        applicationMock = EasyMock.createMock(ApplicationForm.class);
    }

    @Test
    public void shouldAddViewActionIfCannotEdit() {
        EasyMock.expect(userMock.canEditAsAdministrator(applicationMock)).andReturn(false);
        EasyMock.expect(userMock.canEditAsApplicant(applicationMock)).andReturn(false);

        EasyMock.replay(userMock);
        VIEW.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock);

        assertActionsDefinitions(actionsDefinitions, false, VIEW);
    }

    @Test
    public void shouldAddViewEditActionIfCanEditAsApplicant() {
        EasyMock.expect(userMock.canEditAsAdministrator(applicationMock)).andReturn(false);
        EasyMock.expect(userMock.canEditAsApplicant(applicationMock)).andReturn(true);

        EasyMock.replay(userMock);
        VIEW_EDIT.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock);

        assertActionsDefinitions(actionsDefinitions, false, VIEW_EDIT);
    }

    @Test
    public void shouldAddViewEditActionIfCanEditAsAdministrator() {
        EasyMock.expect(userMock.canEditAsAdministrator(applicationMock)).andReturn(true);

        EasyMock.replay(userMock);
        VIEW_EDIT.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock);

        assertActionsDefinitions(actionsDefinitions, false, VIEW_EDIT);
    }

    @Test
    public void shouldAddEmailApplicantActionIfNotApplicant() {

        RegisteredUser anyUser = new RegisteredUser();

        EasyMock.expect(applicationMock.getStatus()).andReturn(ApplicationFormStatus.APPROVAL);
        EasyMock.expect(applicationMock.getApplicant()).andReturn(anyUser);

        EasyMock.replay(userMock, applicationMock);
        EMAIL_APPLICANT.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false, EMAIL_APPLICANT);
    }

    @Test
    public void shouldNotAddEmailApplicantActionIfApplicant() {
        EasyMock.expect(applicationMock.getStatus()).andReturn(ApplicationFormStatus.APPROVAL);
        EasyMock.expect(applicationMock.getApplicant()).andReturn(userMock);

        EasyMock.replay(userMock, applicationMock);
        EMAIL_APPLICANT.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldAddCommentActionIfHasAdminRights() {
        EasyMock.expect(applicationMock.getStatus()).andReturn(ApplicationFormStatus.APPROVAL);
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        COMMENT.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false, COMMENT);
    }

    @Test
    public void shouldAddCommentActionIfIsViewer() {
        EasyMock.expect(applicationMock.getStatus()).andReturn(ApplicationFormStatus.APPROVAL);
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(false);
        EasyMock.expect(userMock.isViewerOfProgramme(applicationMock)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        COMMENT.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false, COMMENT);
    }

    @Test
    public void shouldAddCommentActionIfIsInAdmitterRole() {
        EasyMock.expect(applicationMock.getStatus()).andReturn(ApplicationFormStatus.APPROVAL);
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(false);
        EasyMock.expect(userMock.isViewerOfProgramme(applicationMock)).andReturn(false);
        EasyMock.expect(userMock.isInRole(Authority.ADMITTER)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        COMMENT.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false, COMMENT);
    }

    @Test
    public void shouldNotAddCommentActionIfNotEnoughPrivilleges() {
        EasyMock.expect(applicationMock.getStatus()).andReturn(ApplicationFormStatus.APPROVAL);
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(false);
        EasyMock.expect(userMock.isViewerOfProgramme(applicationMock)).andReturn(false);
        EasyMock.expect(userMock.isInRole(Authority.ADMITTER)).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        COMMENT.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldAddWithdrawActionIfNotTerminatedAndIfApplicant() {
        EasyMock.expect(applicationMock.isTerminated()).andReturn(false);
        EasyMock.expect(applicationMock.getApplicant()).andReturn(userMock);

        EasyMock.replay(userMock, applicationMock);
        WITHDRAW.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false, WITHDRAW);
    }

    @Test
    public void shouldNotAddWithdrawActionIfTerminated() {
        EasyMock.expect(applicationMock.isTerminated()).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        WITHDRAW.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddWithdrawActionIfNotApplicant() {
        RegisteredUser anyUser = new RegisteredUser();

        EasyMock.expect(applicationMock.isTerminated()).andReturn(false);
        EasyMock.expect(applicationMock.getApplicant()).andReturn(anyUser);

        EasyMock.replay(userMock, applicationMock);
        WITHDRAW.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldAddConfirmEligibilityActionIfAdmitterAndSetRequiresAttentionIfRequested() {
        RegisteredUser anyRequester = new RegisteredUser();

        EasyMock.expect(userMock.isInRole(Authority.ADMITTER)).andReturn(true);
        EasyMock.expect(applicationMock.hasConfirmElegibilityComment()).andReturn(false);

        EasyMock.expect(applicationMock.getAdminRequestedRegistry()).andReturn(anyRequester);
        EasyMock.expect(applicationMock.isSubmitted()).andReturn(true);
        EasyMock.expect(applicationMock.isTerminated()).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        CONFIRM_ELIGIBILITY.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, true, CONFIRM_ELIGIBILITY);
    }

    @Test
    public void shouldAddConfirmEligibilityActionIfAdmitterAndDontSetRequiresAttentionIfNotRequested() {
        EasyMock.expect(userMock.isInRole(Authority.ADMITTER)).andReturn(true);
        EasyMock.expect(applicationMock.hasConfirmElegibilityComment()).andReturn(false);

        EasyMock.expect(applicationMock.getAdminRequestedRegistry()).andReturn(null);

        EasyMock.replay(userMock, applicationMock);
        CONFIRM_ELIGIBILITY.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false, CONFIRM_ELIGIBILITY);
    }

    @Test
    public void shouldNotAddConfirmEligibilityActionIfAlreadyConfirmed() {
        EasyMock.expect(userMock.isInRole(Authority.ADMITTER)).andReturn(true);
        EasyMock.expect(applicationMock.hasConfirmElegibilityComment()).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        CONFIRM_ELIGIBILITY.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddConfirmEligibilityActionIfNotAdmitter() {
        EasyMock.expect(userMock.isInRole(Authority.ADMITTER)).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        CONFIRM_ELIGIBILITY.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldAddAddReferenceAction() {
        EasyMock.expect(applicationMock.isSubmitted()).andReturn(true);
        EasyMock.expect(applicationMock.isTerminated()).andReturn(false);
        EasyMock.expect(userMock.isRefereeOfApplicationForm(applicationMock)).andReturn(true);

        EasyMock.expect(userMock.getRefereeForApplicationForm(applicationMock)).andReturn(new Referee());

        EasyMock.replay(userMock, applicationMock);
        ADD_REFERENCE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, true, ADD_REFERENCE);
    }

    @Test
    public void shouldNotAddAddReferenceActionIfAlreadyResponded() {
        EasyMock.expect(applicationMock.isSubmitted()).andReturn(true);
        EasyMock.expect(applicationMock.isTerminated()).andReturn(false);
        EasyMock.expect(userMock.isRefereeOfApplicationForm(applicationMock)).andReturn(true);

        EasyMock.expect(userMock.getRefereeForApplicationForm(applicationMock)).andReturn(new RefereeBuilder().declined(true).build());

        EasyMock.replay(userMock, applicationMock);
        ADD_REFERENCE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddAddReferenceActionIfNotRefereeOfApplication() {
        EasyMock.expect(applicationMock.isSubmitted()).andReturn(true);
        EasyMock.expect(applicationMock.isTerminated()).andReturn(false);
        EasyMock.expect(userMock.isRefereeOfApplicationForm(applicationMock)).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        ADD_REFERENCE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddAddReferenceActionIfApplicationIsTerminated() {
        EasyMock.expect(applicationMock.isSubmitted()).andReturn(true);
        EasyMock.expect(applicationMock.isTerminated()).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        ADD_REFERENCE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddAddReferenceActionIfApplicationIsNotSubmitted() {
        EasyMock.expect(applicationMock.isSubmitted()).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        ADD_REFERENCE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldAddCompleteValidationStageAction() {
        EasyMock.expect(applicationMock.getStatus()).andReturn(VALIDATION);
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_VALIDATION_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, true, COMPLETE_VALIDATION_STAGE);
    }

    @Test
    public void shouldNotAddCompleteValidationStageActionIfHasNotAdminRights() {
        EasyMock.expect(applicationMock.getStatus()).andReturn(VALIDATION);
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_VALIDATION_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddCompleteValidationStageActionIfNextStatusIsSpecified() {
        EasyMock.expect(applicationMock.getStatus()).andReturn(VALIDATION);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_VALIDATION_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, INTERVIEW);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddCompleteValidationStageActionIfStatusIsNotValidation() {
        EasyMock.expect(applicationMock.getStatus()).andReturn(REVIEW);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_VALIDATION_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldAddAssignReviewersAction() {
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        ASSIGN_REVIEWERS.applyAction(actionsDefinitions, userMock, applicationMock, REVIEW);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, true, ASSIGN_REVIEWERS);
    }

    @Test
    public void shouldNotAddAssignReviewersActionIfHasNotAdminRights() {
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        ASSIGN_REVIEWERS.applyAction(actionsDefinitions, userMock, applicationMock, REVIEW);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddAssignReviewersActionIfNextStatusIsNotReview() {
        EasyMock.replay(userMock, applicationMock);
        ASSIGN_REVIEWERS.applyAction(actionsDefinitions, userMock, applicationMock, APPROVAL);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldAddCompleteReviewStageAction() {
        Reviewer reviewer = new ReviewerBuilder().review(null).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(reviewer).build();

        EasyMock.expect(applicationMock.getStatus()).andReturn(REVIEW);
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(true);
        EasyMock.expect(applicationMock.getLatestReviewRound()).andReturn(reviewRound);
        EasyMock.expect(applicationMock.isDueDateExpired()).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_REVIEW_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false, COMPLETE_REVIEW_STAGE);
    }

    @Test
    public void shouldAddCompleteReviewStageActionAndSetAttentionFlagIfAllReviewersResponded() {
        Reviewer reviewer = new ReviewerBuilder().review(new ReviewComment()).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(reviewer).build();

        EasyMock.expect(applicationMock.getStatus()).andReturn(REVIEW);
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(true);
        EasyMock.expect(applicationMock.getLatestReviewRound()).andReturn(reviewRound);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_REVIEW_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, true, COMPLETE_REVIEW_STAGE);
    }

    @Test
    public void shouldAddCompleteReviewStageActionAndSetAttentionFlagIfDueDateHasExpired() {
        Reviewer reviewer = new ReviewerBuilder().review(null).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(reviewer).build();

        EasyMock.expect(applicationMock.getStatus()).andReturn(REVIEW);
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(true);
        EasyMock.expect(applicationMock.getLatestReviewRound()).andReturn(reviewRound);
        EasyMock.expect(applicationMock.isDueDateExpired()).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_REVIEW_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, true, COMPLETE_REVIEW_STAGE);
    }

    @Test
    public void shouldNotAddCompleteReviewStageActionIfHasNotAdminRights() {
        EasyMock.expect(applicationMock.getStatus()).andReturn(REVIEW);
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_REVIEW_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddCompleteReviewStageActionIfNextStatusSpecified() {
        EasyMock.expect(applicationMock.getStatus()).andReturn(REVIEW);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_REVIEW_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, APPROVAL);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddCompleteReviewStageActionIfNotReview() {
        EasyMock.expect(applicationMock.getStatus()).andReturn(VALIDATION);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_REVIEW_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldAddAddReviewActionIfNotReview() {
        EasyMock.expect(applicationMock.getStatus()).andReturn(REVIEW);
        EasyMock.expect(userMock.isReviewerInLatestReviewRoundOfApplicationForm(applicationMock)).andReturn(true);
        EasyMock.expect(userMock.hasRespondedToProvideReviewForApplicationLatestRound(applicationMock)).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        ADD_REVIEW.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, true, ADD_REVIEW);
    }

    @Test
    public void shouldNotAddAddReviewActionIfHasAlreadyResponded() {
        EasyMock.expect(applicationMock.getStatus()).andReturn(REVIEW);
        EasyMock.expect(userMock.isReviewerInLatestReviewRoundOfApplicationForm(applicationMock)).andReturn(true);
        EasyMock.expect(userMock.hasRespondedToProvideReviewForApplicationLatestRound(applicationMock)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        ADD_REVIEW.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddAddReviewActionIfNotReviewer() {
        EasyMock.expect(applicationMock.getStatus()).andReturn(REVIEW);
        EasyMock.expect(userMock.isReviewerInLatestReviewRoundOfApplicationForm(applicationMock)).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        ADD_REVIEW.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddAddReviewActionIfNotReview() {
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);

        EasyMock.replay(userMock, applicationMock);
        ADD_REVIEW.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldAddAssignInterviewersActionIfHasAdminRights() {
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        ASSIGN_INTERVIEWERS.applyAction(actionsDefinitions, userMock, applicationMock, INTERVIEW);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false, ASSIGN_INTERVIEWERS);
    }

    @Test
    public void shouldAddAssignInterviewersActionIfIsApplicationAdministrator() {
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(false);
        EasyMock.expect(userMock.isApplicationAdministrator(applicationMock)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        ASSIGN_INTERVIEWERS.applyAction(actionsDefinitions, userMock, applicationMock, INTERVIEW);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false, ASSIGN_INTERVIEWERS);
    }

    @Test
    public void shouldNotAddAssignInterviewersActionIfHasNoRights() {
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(false);
        EasyMock.expect(userMock.isApplicationAdministrator(applicationMock)).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        ASSIGN_INTERVIEWERS.applyAction(actionsDefinitions, userMock, applicationMock, INTERVIEW);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddAssignInterviewersActionIfNextStatusIsNotInterview() {
        EasyMock.replay(userMock, applicationMock);
        ASSIGN_INTERVIEWERS.applyAction(actionsDefinitions, userMock, applicationMock, APPROVAL);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldAddCompleteInterviewStageAction() {
        Interviewer interviewer = new InterviewerBuilder().build();
        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULED).interviewers(interviewer).build();

        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);
        EasyMock.expect(applicationMock.isDueDateExpired()).andReturn(false);
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_INTERVIEW_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false, COMPLETE_INTERVIEW_STAGE);
    }

    @Test
    public void shouldAddCompleteInterviewStageActionAndSetAttentionFlagIfAllInterviewersResponded() {
        InterviewComment comment = new InterviewComment();
        Interviewer interviewer = new InterviewerBuilder().interviewComment(comment).build();
        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULED).interviewers(interviewer).build();

        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_INTERVIEW_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, true, COMPLETE_INTERVIEW_STAGE);
    }

    @Test
    public void shouldAddCompleteInterviewStageActionAndSetAttentionFlagIfDueDateIsExpired() {
        Interviewer interviewer = new InterviewerBuilder().build();
        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULED).interviewers(interviewer).build();

        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);
        EasyMock.expect(applicationMock.isDueDateExpired()).andReturn(true);
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_INTERVIEW_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, true, COMPLETE_INTERVIEW_STAGE);
    }

    @Test
    public void shouldNotAddCompleteInterviewStageActionIfNoAdminRights() {
        Interviewer interviewer = new InterviewerBuilder().build();
        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULED).interviewers(interviewer).build();

        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_INTERVIEW_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddCompleteInterviewStageActionIfNextStatusSpecified() {
        Interviewer interviewer = new InterviewerBuilder().build();
        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULED).interviewers(interviewer).build();

        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_INTERVIEW_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, APPROVAL);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddCompleteInterviewStageActionIfStatusNotInterview() {
        Interviewer interviewer = new InterviewerBuilder().build();
        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULED).interviewers(interviewer).build();

        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(VALIDATION);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_INTERVIEW_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldAddConfirmInterviewTimeAction() {
        InterviewParticipant participant = new InterviewParticipantBuilder().responded(false).build();
        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULING).participants(participant).build();

        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);
        EasyMock.expect(applicationMock.isDueDateExpired()).andReturn(false);
        EasyMock.expect(userMock.isApplicationAdministrator(applicationMock)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        CONFIRM_INTERVIEW_TIME.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false, CONFIRM_INTERVIEW_TIME);
    }

    @Test
    public void shouldAddConfirmInterviewTimeActionAndSetAttentionFlagIfAllParticipantsHaveResponded() {
        InterviewParticipant participant = new InterviewParticipantBuilder().responded(true).build();
        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULING).participants(participant).build();

        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);
        EasyMock.expect(userMock.isApplicationAdministrator(applicationMock)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        CONFIRM_INTERVIEW_TIME.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, true, CONFIRM_INTERVIEW_TIME);
    }

    @Test
    public void shouldAddConfirmInterviewTimeActionAndSetAttentionFlagIfApplicationDueDateHasExpired() {
        InterviewParticipant participant = new InterviewParticipantBuilder().responded(false).build();
        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULING).participants(participant).build();

        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);
        EasyMock.expect(applicationMock.isDueDateExpired()).andReturn(true);
        EasyMock.expect(userMock.isApplicationAdministrator(applicationMock)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        CONFIRM_INTERVIEW_TIME.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, true, CONFIRM_INTERVIEW_TIME);
    }

    @Test
    public void shouldNotAddConfirmInterviewTimeActionIfNotRights() {
        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULING).build();

        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);
        EasyMock.expect(userMock.isApplicationAdministrator(applicationMock)).andReturn(false);
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        CONFIRM_INTERVIEW_TIME.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddConfirmInterviewTimeActionIfInterviewNotScheduling() {
        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULED).build();

        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);

        EasyMock.replay(userMock, applicationMock);
        CONFIRM_INTERVIEW_TIME.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddConfirmInterviewTimeActionIfNextStatusSpecified() {
        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULING).build();

        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);

        EasyMock.replay(userMock, applicationMock);
        CONFIRM_INTERVIEW_TIME.applyAction(actionsDefinitions, userMock, applicationMock, VALIDATION);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddConfirmInterviewTimeActionIfNotInterviewStatus() {
        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULING).build();

        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(VALIDATION);

        EasyMock.replay(userMock, applicationMock);
        CONFIRM_INTERVIEW_TIME.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldAddProvideInterviewAvailabilityAction() {
        InterviewParticipant participant = new InterviewParticipantBuilder().user(userMock).build();
        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULING).participants(participant).build();

        EasyMock.expect(userMock.getId()).andReturn(8).anyTimes();
        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);

        EasyMock.replay(userMock, applicationMock);
        PROVIDE_INTERVIEW_AVAILABILITY.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, true, PROVIDE_INTERVIEW_AVAILABILITY);
    }

    @Test
    public void shouldNotAddProvideInterviewAvailabilityActionIfParticipantResponded() {
        InterviewParticipant participant = new InterviewParticipantBuilder().user(userMock).responded(true).build();
        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULING).participants(participant).build();

        EasyMock.expect(userMock.getId()).andReturn(8).anyTimes();
        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);

        EasyMock.replay(userMock, applicationMock);
        PROVIDE_INTERVIEW_AVAILABILITY.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddProvideInterviewAvailabilityActionIfNotParticipant() {
        RegisteredUser anyUser = new RegisteredUserBuilder().id(7).build();

        InterviewParticipant participant = new InterviewParticipantBuilder().user(anyUser).build();
        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULING).participants(participant).build();

        EasyMock.expect(userMock.getId()).andReturn(8).anyTimes();
        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);

        EasyMock.replay(userMock, applicationMock);
        PROVIDE_INTERVIEW_AVAILABILITY.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddProvideInterviewAvailabilityActionIfNotScheduling() {

        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULED).build();

        EasyMock.expect(userMock.getId()).andReturn(8).anyTimes();
        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);

        EasyMock.replay(userMock, applicationMock);
        PROVIDE_INTERVIEW_AVAILABILITY.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddProvideInterviewAvailabilityActionIfNextStatusSpecified() {

        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULING).build();

        EasyMock.expect(userMock.getId()).andReturn(8).anyTimes();
        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);

        EasyMock.replay(userMock, applicationMock);
        PROVIDE_INTERVIEW_AVAILABILITY.applyAction(actionsDefinitions, userMock, applicationMock, APPROVAL);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddProvideInterviewAvailabilityActionIfStatusNotInterview() {

        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULING).build();

        EasyMock.expect(userMock.getId()).andReturn(8).anyTimes();
        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(REVIEW);

        EasyMock.replay(userMock, applicationMock);
        PROVIDE_INTERVIEW_AVAILABILITY.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldAddAddInterviewFeedbackAction() {
        Date today = DateUtils.truncate(new Date(), Calendar.DATE);

        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULED).dueDate(today).build();

        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);
        EasyMock.expect(userMock.isInterviewerOfApplicationForm(applicationMock)).andReturn(true);
        EasyMock.expect(userMock.hasRespondedToProvideInterviewFeedbackForApplicationLatestRound(applicationMock)).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        ADD_INTERVIEW_FEEDBACK.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, true, ADD_INTERVIEW_FEEDBACK);
    }

    @Test
    public void shouldNotAddAddInterviewFeedbackActionIfHasAlreadyResponded() {
        Date today = DateUtils.truncate(new Date(), Calendar.DATE);
        Date yesterday = DateUtils.addDays(today, -1);

        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULED).dueDate(yesterday).build();

        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);
        EasyMock.expect(userMock.isInterviewerOfApplicationForm(applicationMock)).andReturn(true);
        EasyMock.expect(userMock.hasRespondedToProvideInterviewFeedbackForApplicationLatestRound(applicationMock)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        ADD_INTERVIEW_FEEDBACK.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddAddInterviewFeedbackActionIfInterviewNotScheduled() {
        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULING).build();

        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);
        EasyMock.expect(userMock.isInterviewerOfApplicationForm(applicationMock)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        ADD_INTERVIEW_FEEDBACK.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddAddInterviewFeedbackActionIfNotInterviewer() {
        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULED).build();

        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);
        EasyMock.expect(userMock.isInterviewerOfApplicationForm(applicationMock)).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        ADD_INTERVIEW_FEEDBACK.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddAddInterviewFeedbackActionIfNextStatusSpecified() {
        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULED).build();

        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);

        EasyMock.replay(userMock, applicationMock);
        ADD_INTERVIEW_FEEDBACK.applyAction(actionsDefinitions, userMock, applicationMock, REVIEW);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddAddInterviewFeedbackActionIfStatusNotInterview() {
        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULED).build();

        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(REVIEW);

        EasyMock.replay(userMock, applicationMock);
        ADD_INTERVIEW_FEEDBACK.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddAddInterviewFeedbackActionIfInterviewHasNotTakenPlaceYet() {
        Date today = DateUtils.truncate(new Date(), Calendar.DATE);
        Date tomorrow = DateUtils.addDays(today, 1);

        Interview interview = new InterviewBuilder().stage(InterviewStage.SCHEDULED).dueDate(tomorrow).build();

        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(interview);
        EasyMock.expect(applicationMock.getStatus()).andReturn(INTERVIEW);
        EasyMock.expect(userMock.isInterviewerOfApplicationForm(applicationMock)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        ADD_INTERVIEW_FEEDBACK.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldAddReviseApprovalAction() {
        EasyMock.expect(applicationMock.isPendingApprovalRestart()).andReturn(true);
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        REVISE_APPROVAL.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, true, REVISE_APPROVAL);
    }

    @Test
    public void shouldNotAddReviseApprovalActionIfNoAdminRights() {
        EasyMock.expect(applicationMock.isPendingApprovalRestart()).andReturn(true);
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        REVISE_APPROVAL.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddReviseApprovalActionIfNoApprovalRestartNoPending() {
        EasyMock.expect(applicationMock.isPendingApprovalRestart()).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        REVISE_APPROVAL.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldAddApproveActionIfApprover() {
        Program program = new Program();
        ApprovalRound approvalRound = new ApprovalRound();

        EasyMock.expect(applicationMock.getStatus()).andReturn(APPROVAL);
        EasyMock.expect(applicationMock.getProgram()).andReturn(program);
        EasyMock.expect(applicationMock.getLatestApprovalRound()).andReturn(approvalRound);
        EasyMock.expect(applicationMock.isDueDateExpired()).andReturn(false);
        EasyMock.expect(userMock.isApproverInProgram(program)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_APPROVAL_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false, COMPLETE_APPROVAL_STAGE);
    }

    @Test
    public void shouldAddApproveActionIfSuperadministrator() {
        Program program = new Program();
        ApprovalRound approvalRound = new ApprovalRound();

        EasyMock.expect(applicationMock.getStatus()).andReturn(APPROVAL);
        EasyMock.expect(applicationMock.getProgram()).andReturn(program);
        EasyMock.expect(applicationMock.getLatestApprovalRound()).andReturn(approvalRound);
        EasyMock.expect(applicationMock.isDueDateExpired()).andReturn(false);
        EasyMock.expect(userMock.isApproverInProgram(program)).andReturn(false);
        EasyMock.expect(userMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_APPROVAL_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false, COMPLETE_APPROVAL_STAGE);
    }

    @Test
    public void shouldAddApproveActionIfApproverAndSetAttentionFlagIfPrimarySupervisorResponded() {
        Program program = new Program();
        Supervisor primarySupervisor = new SupervisorBuilder().isPrimary(true).confirmedSupervision(true).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(primarySupervisor).build();

        EasyMock.expect(applicationMock.getStatus()).andReturn(APPROVAL);
        EasyMock.expect(applicationMock.getProgram()).andReturn(program);
        EasyMock.expect(applicationMock.getLatestApprovalRound()).andReturn(approvalRound);
        EasyMock.expect(userMock.isApproverInProgram(program)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_APPROVAL_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, true, COMPLETE_APPROVAL_STAGE);
    }

    @Test
    public void shouldAddApproveActionIfApproverAndSetAttentionFlagIfDueDateHasExpired() {
        Program program = new Program();
        Supervisor primarySupervisor = new SupervisorBuilder().isPrimary(true).confirmedSupervision(false).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(primarySupervisor).build();

        EasyMock.expect(applicationMock.getStatus()).andReturn(APPROVAL);
        EasyMock.expect(applicationMock.getProgram()).andReturn(program);
        EasyMock.expect(applicationMock.getLatestApprovalRound()).andReturn(approvalRound);
        EasyMock.expect(applicationMock.isDueDateExpired()).andReturn(true);
        EasyMock.expect(userMock.isApproverInProgram(program)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_APPROVAL_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, true, COMPLETE_APPROVAL_STAGE);
    }

    @Test
    public void shouldNotAddApproveActionIfNoRights() {
        Program program = new Program();

        EasyMock.expect(applicationMock.getStatus()).andReturn(APPROVAL);
        EasyMock.expect(applicationMock.getProgram()).andReturn(program);
        EasyMock.expect(userMock.isApproverInProgram(program)).andReturn(false);
        EasyMock.expect(userMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_APPROVAL_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddApproveActionIfStatusNotApproval() {
        EasyMock.expect(applicationMock.getStatus()).andReturn(VALIDATION);

        EasyMock.replay(userMock, applicationMock);
        COMPLETE_APPROVAL_STAGE.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldAddAssignSupervisorsAction() {
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        ASSIGN_SUPERVISORS.applyAction(actionsDefinitions, userMock, applicationMock, APPROVAL);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, true, ASSIGN_SUPERVISORS);
    }

    @Test
    public void shouldNotAddAssignSupervisorsActionIfNoRights() {
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        ASSIGN_SUPERVISORS.applyAction(actionsDefinitions, userMock, applicationMock, APPROVAL);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddAssignSupervisorsActionIfNextStatusNotApproval() {
        EasyMock.replay(userMock, applicationMock);
        ASSIGN_SUPERVISORS.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldAddReviseApprovalAsAdministratorAction() {
        Program program = new Program();

        EasyMock.expect(applicationMock.getStatus()).andReturn(APPROVAL);
        EasyMock.expect(applicationMock.isPendingApprovalRestart()).andReturn(false);
        EasyMock.expect(applicationMock.getProgram()).andReturn(program).anyTimes();
        EasyMock.expect(userMock.isInRoleInProgram(Authority.ADMINISTRATOR, program)).andReturn(true);
        EasyMock.expect(userMock.isNotInRoleInProgram(Authority.APPROVER, program)).andReturn(true);
        EasyMock.expect(userMock.isNotInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        REVISE_APPROVAL_AS_ADMINISTRATOR.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, true, REVISE_APPROVAL_AS_ADMINISTRATOR);
    }

    @Test
    public void shouldNotAddReviseApprovalAsAdministratorActionIfSuperadministrator() {
        Program program = new Program();

        EasyMock.expect(applicationMock.getStatus()).andReturn(APPROVAL);
        EasyMock.expect(applicationMock.isPendingApprovalRestart()).andReturn(false);
        EasyMock.expect(applicationMock.getProgram()).andReturn(program).anyTimes();
        EasyMock.expect(userMock.isInRoleInProgram(Authority.ADMINISTRATOR, program)).andReturn(true);
        EasyMock.expect(userMock.isNotInRoleInProgram(Authority.APPROVER, program)).andReturn(true);
        EasyMock.expect(userMock.isNotInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        REVISE_APPROVAL_AS_ADMINISTRATOR.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddReviseApprovalAsAdministratorActionIfNotProgramAdministrator() {
        Program program = new Program();

        EasyMock.expect(applicationMock.getStatus()).andReturn(APPROVAL);
        EasyMock.expect(applicationMock.isPendingApprovalRestart()).andReturn(false);
        EasyMock.expect(applicationMock.getProgram()).andReturn(program).anyTimes();
        EasyMock.expect(userMock.isInRoleInProgram(Authority.ADMINISTRATOR, program)).andReturn(false);

        EasyMock.replay(userMock, applicationMock);
        REVISE_APPROVAL_AS_ADMINISTRATOR.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddReviseApprovalAsAdministratorActionIfPendingApprovalRestart() {
        EasyMock.expect(applicationMock.getStatus()).andReturn(APPROVAL);
        EasyMock.expect(applicationMock.isPendingApprovalRestart()).andReturn(true);

        EasyMock.replay(userMock, applicationMock);
        REVISE_APPROVAL_AS_ADMINISTRATOR.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddReviseApprovalAsAdministratorActionIfStatusNotApproval() {
        EasyMock.expect(applicationMock.getStatus()).andReturn(VALIDATION);

        EasyMock.replay(userMock, applicationMock);
        REVISE_APPROVAL_AS_ADMINISTRATOR.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldAddConfirmSupervisionAction() {
        Supervisor supervisor = new SupervisorBuilder().user(userMock).isPrimary(true).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).build();

        EasyMock.expect(applicationMock.getStatus()).andReturn(APPROVAL);
        EasyMock.expect(applicationMock.getLatestApprovalRound()).andReturn(approvalRound);

        EasyMock.replay(userMock, applicationMock);
        CONFIRM_SUPERVISION.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, true, CONFIRM_SUPERVISION);
    }

    @Test
    public void shouldNotAddConfirmSupervisionActionIfAlreadyResponded() {
        Supervisor supervisor = new SupervisorBuilder().user(userMock).isPrimary(true).confirmedSupervision(true).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).build();

        EasyMock.expect(applicationMock.getStatus()).andReturn(APPROVAL);
        EasyMock.expect(applicationMock.getLatestApprovalRound()).andReturn(approvalRound);

        EasyMock.replay(userMock, applicationMock);
        CONFIRM_SUPERVISION.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddConfirmSupervisionActionIfNotSupervisor() {
        RegisteredUser anyUser = new RegisteredUser();

        Supervisor supervisor = new SupervisorBuilder().user(anyUser).isPrimary(true).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).build();

        EasyMock.expect(applicationMock.getStatus()).andReturn(APPROVAL);
        EasyMock.expect(applicationMock.getLatestApprovalRound()).andReturn(approvalRound);

        EasyMock.replay(userMock, applicationMock);
        CONFIRM_SUPERVISION.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddConfirmSupervisionActionIfNoPrimarySupervisor() {
        Supervisor supervisor = new SupervisorBuilder().build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).build();

        EasyMock.expect(applicationMock.getStatus()).andReturn(APPROVAL);
        EasyMock.expect(applicationMock.getLatestApprovalRound()).andReturn(approvalRound);

        EasyMock.replay(userMock, applicationMock);
        CONFIRM_SUPERVISION.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    @Test
    public void shouldNotAddConfirmSupervisionActionIfStatusNotApproval() {
        EasyMock.expect(applicationMock.getStatus()).andReturn(VALIDATION);

        EasyMock.replay(userMock, applicationMock);
        CONFIRM_SUPERVISION.applyAction(actionsDefinitions, userMock, applicationMock, null);
        EasyMock.verify(userMock, applicationMock);

        assertActionsDefinitions(actionsDefinitions, false);
    }

    private void assertActionsDefinitions(ActionsDefinitions actionsDefinitions, boolean requiresAttention, ApplicationFormAction... actions) {
        assertEquals(requiresAttention, actionsDefinitions.isRequiresAttention());
        Assert.assertThat(actionsDefinitions.getActions(), CoreMatchers.hasItems(actions));

    }

}
