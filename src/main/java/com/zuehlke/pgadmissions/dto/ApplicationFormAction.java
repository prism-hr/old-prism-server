package com.zuehlke.pgadmissions.dto;

import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.APPROVAL;
import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.INTERVIEW;
import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.REVIEW;
import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.UNSUBMITTED;
import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.VALIDATION;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public enum ApplicationFormAction {

    // GENERAL ACTIONS
    VIEW_EDIT("view", "View / Edit", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (user.canEditAsAdministrator(application) || user.canEditAsApplicant(application)) {
                actions.addAction(VIEW_EDIT);
            }
        }
    }), //
    VIEW("view", "View", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (!user.canEditAsAdministrator(application) && !user.canEditAsApplicant(application)) {
                actions.addAction(VIEW);
            }
        }
    }), //
    EMAIL_APPLICANT("emailApplicant", "Email applicant", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (application.getStatus() != UNSUBMITTED && user != application.getApplicant()) {
                actions.addAction(EMAIL_APPLICANT);
            }
        }
    }), //
    COMMENT("comment", "Comment", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (application.getStatus() != ApplicationFormStatus.UNSUBMITTED
                    && (user.hasAdminRightsOnApplication(application) || user.isViewerOfProgramme(application) || user.isInRole(Authority.ADMITTER))) {
                actions.addAction(COMMENT);
            }
        }
    }), //
    WITHDRAW("withdraw", "Withdraw", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (!application.isTerminated() && user == application.getApplicant()) {
                actions.addAction(WITHDRAW);
            }
        }
    }), //
    CONFIRM_ELIGIBILITY("validate", "Confirm Eligibility", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (user.isInRole(Authority.ADMITTER) && !application.hasConfirmElegibilityComment()) {
                actions.addAction(CONFIRM_ELIGIBILITY);
                if (application.getAdminRequestedRegistry() != null && (application.isSubmitted() && !application.isTerminated())) {
                    actions.setRequiresAttention(true);
                }
            }
        }
    }), //
    ADD_REFERENCE("reference", "Provide Reference", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (application.isSubmitted() && !application.isTerminated() && user.isRefereeOfApplicationForm(application)
                    && !user.getRefereeForApplicationForm(application).hasResponded()) {
                actions.addAction(ADD_REFERENCE);
                actions.setRequiresAttention(true);
            }
        }
    }), //

    // VALIDATION STAGE ACTIONS

    COMPLETE_VALIDATION_STAGE("validate", "Complete Validation Stage", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (application.getStatus() == VALIDATION && nextStatus == null && user.hasAdminRightsOnApplication(application)) {
                actions.addAction(COMPLETE_VALIDATION_STAGE);
                actions.setRequiresAttention(true);
            }
        }
    }), //

    // REVIEW STAGE ACTIONS

    ASSIGN_REVIEWERS("validate", "Assign Reviewers", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (nextStatus == REVIEW && user.hasAdminRightsOnApplication(application)) {
                actions.addAction(ASSIGN_REVIEWERS);
                actions.setRequiresAttention(true);
            }
        }
    }), //
    COMPLETE_REVIEW_STAGE("validate", "Complete Review Stage", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (application.getStatus() == REVIEW && nextStatus == null && user.hasAdminRightsOnApplication(application)) {
                actions.addAction(COMPLETE_REVIEW_STAGE);
                ReviewRound reviewRound = application.getLatestReviewRound();
                if (reviewRound.hasAllReviewersResponded() || application.isDueDateExpired()) {
                    actions.setRequiresAttention(true);
                }
            }
        }
    }), //
    ADD_REVIEW("review", "Provide Review", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (application.getStatus() == REVIEW && user.isReviewerInLatestReviewRoundOfApplicationForm(application)
                    && !user.hasRespondedToProvideReviewForApplicationLatestRound(application)) {
                actions.addAction(ADD_REVIEW);
                actions.setRequiresAttention(true);
            }
        }
    }), //

    // INTERVIEW STAGE ACTIONS

    ASSIGN_INTERVIEWERS("validate", "Assign Interviewers", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (nextStatus == INTERVIEW && (user.hasAdminRightsOnApplication(application) || user.isApplicationAdministrator(application))) {
                actions.addAction(ASSIGN_INTERVIEWERS);
            }
        }
    }), //
    COMPLETE_INTERVIEW_STAGE("validate", "Complete Interview Stage", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            Interview interview = application.getLatestInterview();
            if (application.getStatus() == INTERVIEW && nextStatus == null && (user.hasAdminRightsOnApplication(application))) {
                actions.addAction(COMPLETE_INTERVIEW_STAGE);
                if (interview.hasAllInterviewersProvidedFeedback() || application.isDueDateExpired()) {
                    actions.setRequiresAttention(true);
                }
            }
        }
    }), //
    CONFIRM_INTERVIEW_TIME("interviewConfirm", "Confirm Interview Arrangements", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            Interview interview = application.getLatestInterview();
            if (application.getStatus() == INTERVIEW && nextStatus == null && interview.isScheduling()
                    && (user.isApplicationAdministrator(application) || user.hasAdminRightsOnApplication(application))) {
                actions.addAction(CONFIRM_INTERVIEW_TIME);
                if (interview.hasAllParticipantsProvidedAvailability() || application.isDueDateExpired()) {
                    actions.setRequiresAttention(true);
                }
            }
        }
    }), //
    PROVIDE_INTERVIEW_AVAILABILITY("interviewVote", "Provide Interview Availability", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            Interview interview = application.getLatestInterview();
            if (application.getStatus() == INTERVIEW && nextStatus == null && interview.isScheduling() && interview.isParticipant(user)
                    && !interview.getParticipant(user).getResponded()) {
                actions.addAction(PROVIDE_INTERVIEW_AVAILABILITY);
                actions.setRequiresAttention(true);
            }
        }
    }), //
    ADD_INTERVIEW_FEEDBACK("interviewFeedback", "Provide interview feedback", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            Interview interview = application.getLatestInterview();
            if (application.getStatus() == INTERVIEW && nextStatus == null && user.isInterviewerOfApplicationForm(application) && interview.isScheduled()
                    && interview.isDateExpired() && !user.hasRespondedToProvideInterviewFeedbackForApplicationLatestRound(application)) {
                actions.addAction(ADD_INTERVIEW_FEEDBACK);
                actions.setRequiresAttention(true);
            }
        }
    }), //

    // APPROVAL STAGE ACTIONS

    REVISE_APPROVAL("restartApproval", "Revise Approval", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (application.isPendingApprovalRestart() && user.hasAdminRightsOnApplication(application)) {
                actions.addAction(REVISE_APPROVAL);
                actions.setRequiresAttention(true);
            }
        }
    }), //
    COMPLETE_APPROVAL_STAGE("validate", "Complete Approval Stage", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (application.getStatus() == APPROVAL) {
                if (user.isApproverInProgram(application.getProgram()) || user.isInRole(Authority.SUPERADMINISTRATOR)) {
                    actions.addAction(COMPLETE_APPROVAL_STAGE);

                    ApprovalRound approvalRound = application.getLatestApprovalRound();
                    if (approvalRound.hasPrimarySupervisorResponded() || application.isDueDateExpired()) {
                        actions.setRequiresAttention(true);
                    }
                }
            }
        }
    }), //
    ASSIGN_SUPERVISORS("validate", "Assign Supervisors", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (nextStatus == APPROVAL && user.hasAdminRightsOnApplication(application)) {
                actions.addAction(ASSIGN_SUPERVISORS);
                actions.setRequiresAttention(true);
            }
        }
    }), //
    REVISE_APPROVAL_AS_ADMINISTRATOR("restartApprovalAsAdministrator", "Revise Approval", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (application.getStatus() == APPROVAL && !application.isPendingApprovalRestart()
                    && user.isInRoleInProgram(Authority.ADMINISTRATOR, application.getProgram())
                    && user.isNotInRoleInProgram(Authority.APPROVER, application.getProgram()) && user.isNotInRole(Authority.SUPERADMINISTRATOR)) {
                actions.addAction(REVISE_APPROVAL_AS_ADMINISTRATOR);
                actions.setRequiresAttention(true);
            }
        }
    }), //
    CONFIRM_SUPERVISION("confirmSupervision", "Confirm supervision", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (application.getStatus() == APPROVAL) {
                Supervisor primarySupervisor = application.getLatestApprovalRound().getPrimarySupervisor();
                if (primarySupervisor != null && user == primarySupervisor.getUser() && !primarySupervisor.hasResponded()) {
                    actions.addAction(CONFIRM_SUPERVISION);
                    actions.setRequiresAttention(true);
                }
            }
        }
    }), //
    ;

    private final String id;

    private final String displayName;

    private final ActionPredicate predicate;

    private ApplicationFormAction(String id, String displayName, ActionPredicate predicate) {
        this.id = id;
        this.displayName = displayName;
        this.predicate = predicate;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void applyAction(final ActionsDefinitions actions, final RegisteredUser user, final ApplicationForm application,
            final ApplicationFormStatus nextStatus) {
        predicate.apply(actions, user, application, nextStatus);
    }

    public static interface ActionPredicate {
        public void apply(final ActionsDefinitions actions, final RegisteredUser user, final ApplicationForm application, final ApplicationFormStatus nextStatus);
    }

}
