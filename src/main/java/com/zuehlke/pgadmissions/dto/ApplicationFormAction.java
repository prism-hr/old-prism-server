package com.zuehlke.pgadmissions.dto;

import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.APPROVAL;
import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.INTERVIEW;
import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.REJECTED;
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
    EMAIL_APPLICANT("emailApplicant", "Email Applicant", new ActionPredicate() {
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
            if (application.getStatus() != UNSUBMITTED && user != application.getApplicant()) {
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
    CONFIRM_ELIGIBILITY("confirmEligibility", "Confirm Eligibility", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if ((user.isInRole(Authority.ADMITTER) || user.isInRole(Authority.SUPERADMINISTRATOR)) && !application.hasConfirmElegibilityComment()
                            && application.isSubmitted() && !application.isTerminated()) {
                actions.addAction(CONFIRM_ELIGIBILITY);
                if (application.getAdminRequestedRegistry() != null) {
                    actions.addActionRequiringAttention(CONFIRM_ELIGIBILITY);

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
                actions.addActionRequiringAttention(ADD_REFERENCE);

            }
        }
    }), //

    // VALIDATION STAGE ACTIONS

    COMPLETE_VALIDATION_STAGE("validate", "Complete Validation Stage", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (application.getStatus() == VALIDATION && nextStatus == null && user.hasAdminRightsOnApplication(application)) {
                actions.addAction(COMPLETE_VALIDATION_STAGE);
                actions.addActionRequiringAttention(COMPLETE_VALIDATION_STAGE);

            }
        }
    }), //

    // REVIEW STAGE ACTIONS
    ABORT_REVIEW_STAGE("abort", "Complete Review Stage", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (application.getNextStatus() == REVIEW && 
            	user.hasAdminRightsOnApplication(application)
            	&& 1 == 2) {
                actions.addAction(ABORT_REVIEW_STAGE);
            }
        }
    }), //
    ASSIGN_REVIEWERS("validate", "Assign Reviewers", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (nextStatus == REVIEW && user.hasAdminRightsOnApplication(application)) {
                actions.addAction(ASSIGN_REVIEWERS);
                actions.addActionRequiringAttention(ASSIGN_REVIEWERS);

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
                    actions.addActionRequiringAttention(COMPLETE_REVIEW_STAGE);

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
                actions.addActionRequiringAttention(ADD_REVIEW);

            }
        }
    }), //

    // INTERVIEW STAGE ACTIONS
    ABORT_INTERVIEW_STAGE("abort", "Complete Interview Stage", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (application.getNextStatus() == INTERVIEW && 
            	(user.hasAdminRightsOnApplication(application) ||
            	user.isApplicationAdministrator(application)) && 
            	1 == 2) {
                actions.addAction(ABORT_INTERVIEW_STAGE);
            }
        }
    }), //
    ASSIGN_INTERVIEWERS("validate", "Assign Interviewers", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (nextStatus == INTERVIEW && (user.hasAdminRightsOnApplication(application) || user.isApplicationAdministrator(application))) {
                actions.addAction(ASSIGN_INTERVIEWERS);
                actions.addActionRequiringAttention(ASSIGN_INTERVIEWERS);

            }
        }
    }), //
    COMPLETE_INTERVIEW_STAGE("validate", "Complete Interview Stage", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            Interview interview = application.getLatestInterview();
            if (application.getStatus() == INTERVIEW && nextStatus == null
                            && (user.hasAdminRightsOnApplication(application) || user.isApplicationAdministrator(application))) {
                actions.addAction(COMPLETE_INTERVIEW_STAGE);
                if (interview.hasAllInterviewersProvidedFeedback() || application.isDueDateExpired()) {
                    actions.addActionRequiringAttention(COMPLETE_INTERVIEW_STAGE);

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
                    actions.addActionRequiringAttention(CONFIRM_INTERVIEW_TIME);

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
                actions.addActionRequiringAttention(PROVIDE_INTERVIEW_AVAILABILITY);

            }
        }
    }), //
    ADD_INTERVIEW_FEEDBACK("interviewFeedback", "Provide Interview Feedback", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            Interview interview = application.getLatestInterview();
            if (application.getStatus() == INTERVIEW && nextStatus == null && user.isInterviewerOfApplicationForm(application) && interview.isScheduled()
                            && !user.hasRespondedToProvideInterviewFeedbackForApplicationLatestRound(application)) {
                actions.addAction(ADD_INTERVIEW_FEEDBACK);
                if (interview.isDateExpired()) {
                    actions.addActionRequiringAttention(ADD_INTERVIEW_FEEDBACK);

                }
            }
        }
    }), //

    // APPROVAL STAGE ACTIONS
    ABORT_APPROVAL_STAGE("abort", "Complete Interview Stage", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (application.getNextStatus() == APPROVAL && 
            	user.hasAdminRightsOnApplication(application)
            	&& 1 == 2) {
                actions.addAction(ABORT_INTERVIEW_STAGE);
            }
        }
    }), //
    REVISE_APPROVAL("restartApproval", "Assign Supervisors", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (application.isPendingApprovalRestart() && application.isInApprovalStage() && nextStatus == null
                            && user.hasAdminRightsOnApplication(application)) {
                actions.addAction(REVISE_APPROVAL);
                actions.addActionRequiringAttention(REVISE_APPROVAL);

            }
        }
    }), //
    COMPLETE_APPROVAL_STAGE("validate", "Complete Approval Stage", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (application.getStatus() == APPROVAL && nextStatus == null) {
                if (user.isApproverInProgram(application.getProgram()) || user.isInRole(Authority.SUPERADMINISTRATOR)) {
                    actions.addAction(COMPLETE_APPROVAL_STAGE);

                    ApprovalRound approvalRound = application.getLatestApprovalRound();
                    if (approvalRound.hasPrimarySupervisorResponded() || application.isDueDateExpired()) {
                        actions.addActionRequiringAttention(COMPLETE_APPROVAL_STAGE);

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
                actions.addActionRequiringAttention(ASSIGN_SUPERVISORS);

            }
        }
    }), //
    REVISE_APPROVAL_AS_ADMINISTRATOR("restartApprovalAsAdministrator", "Complete Approval Stage", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (application.getStatus() == APPROVAL && !application.isPendingApprovalRestart()
                            && user.isInRoleInProgram(Authority.ADMINISTRATOR, application.getProgram())
                            && user.isNotInRoleInProgram(Authority.APPROVER, application.getProgram()) && user.isNotInRole(Authority.SUPERADMINISTRATOR)) {
                actions.addAction(REVISE_APPROVAL_AS_ADMINISTRATOR);
            }
        }
    }), //
    CONFIRM_SUPERVISION("confirmSupervision", "Confirm Supervision", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (application.getStatus() == APPROVAL) {
                Supervisor primarySupervisor = application.getLatestApprovalRound().getPrimarySupervisor();
                if (!application.isPendingApprovalRestart() && primarySupervisor != null && user == primarySupervisor.getUser()
                                && !primarySupervisor.hasResponded()) {
                    actions.addAction(CONFIRM_SUPERVISION);
                    actions.addActionRequiringAttention(CONFIRM_SUPERVISION);

                }
            }
        }
    }), //
    COMPLETE_REJECTION("completeRejection", "Complete Rejection", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (nextStatus == REJECTED && user.hasAdminRightsOnApplication(application)) {
                actions.addAction(COMPLETE_REJECTION);
                actions.addActionRequiringAttention(COMPLETE_REJECTION);

            }
        }
    }),
    CONFIRM_OFFER_RECOMMENDATION("confirmOfferRecommendation", "Confirm Offer Recommendation", new ActionPredicate() {
        @Override
        public void apply(ActionsDefinitions actions, RegisteredUser user, ApplicationForm application, ApplicationFormStatus nextStatus) {
            if (nextStatus == ApplicationFormStatus.APPROVED && user.hasAdminRightsOnApplication(application)) {
                actions.addAction(CONFIRM_OFFER_RECOMMENDATION);
                actions.addActionRequiringAttention(CONFIRM_OFFER_RECOMMENDATION);

            }
        }
    });

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
