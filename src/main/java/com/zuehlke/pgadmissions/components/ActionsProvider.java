package com.zuehlke.pgadmissions.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;
import com.zuehlke.pgadmissions.services.StateTransitionViewResolver;

@Component
public class ActionsProvider {

    private StateTransitionViewResolver stateTransitionViewResolver;

    public ActionsProvider() {
    }

    @Autowired
    public ActionsProvider(StateTransitionViewResolver stateTransitionViewResolver) {
        this.stateTransitionViewResolver = stateTransitionViewResolver;
    }

    public ActionsDefinitions calculateActions(final RegisteredUser user, final ApplicationForm application) {
        Interview interview = application.getLatestInterview();
        ApplicationFormStatus nextStatus = stateTransitionViewResolver.getNextStatus(application);

        ActionsDefinitions actions = new ActionsDefinitions();

        if (user.canEditAsAdministrator(application) || user.canEditAsApplicant(application)) {
            actions.addAction("view", "View / Edit");
        } else if (user.canSee(application)) {
            actions.addAction("view", "View");
        }

        if (application.isInState(ApplicationFormStatus.VALIDATION)) {
            if (user.hasAdminRightsOnApplication(application)) {
                actions.addAction("validate", "Validate");
            }
        }

        if (application.isInState(ApplicationFormStatus.REVIEW)) {
            if (user.hasAdminRightsOnApplication(application)) {
                actions.addAction("validate", "Evaluate reviews");
            }
        }

        if (application.isInState(ApplicationFormStatus.INTERVIEW) && nextStatus == null) {
            if (user.hasAdminRightsOnApplication(application)) {
                actions.addAction("validate", "Evaluate interview feedback");
            }
            if (interview.isScheduling() && (user.isApplicationAdministrator(application) || user.hasAdminRightsOnApplication(application))) {
                actions.addAction("interviewConfirm", "Confirm interview time");
                actions.setRequiresAttention(true);
            }
        }

        if (user.isApplicationAdministrator(application) && nextStatus == ApplicationFormStatus.INTERVIEW) {
            // application not yet in interview stage, interview is next
            actions.addAction("validate", "Administer Interview");
        }

        if (user.isInRole(Authority.ADMITTER) && !application.hasConfirmElegibilityComment()) {
            actions.addAction("validate", "Confirm Eligibility");
            if (application.getAdminRequestedRegistry() != null
                    && (application.isNotInState(ApplicationFormStatus.WITHDRAWN) && application.isNotInState(ApplicationFormStatus.REJECTED))) {
                actions.setRequiresAttention(true);
            } else {
                actions.setRequiresAttention(false);
            }
        }

        if (user.hasAdminRightsOnApplication(application) || user.isViewerOfProgramme(application) || user.isInRole(Authority.ADMITTER)) {
            actions.addAction("comment", "Comment");
        }

        if (user.isReviewerInLatestReviewRoundOfApplicationForm(application) && application.isInState(ApplicationFormStatus.REVIEW)
                && !user.hasRespondedToProvideReviewForApplicationLatestRound(application)) {
            actions.addAction("review", "Add review");
            actions.setRequiresAttention(true);
        }

        if (application.isInState(ApplicationFormStatus.INTERVIEW) && nextStatus == null && interview.isScheduling() && interview.isParticipant(user)
                && !interview.getParticipant(user).getResponded()) {
            actions.addAction("interviewVote", "Provide Availability For Interview");
            actions.setRequiresAttention(true);
        }

        if (user.isInterviewerOfApplicationForm(application) && application.isInState(ApplicationFormStatus.INTERVIEW) && interview.isScheduled()
                && !user.hasRespondedToProvideInterviewFeedbackForApplicationLatestRound(application)) {
            actions.addAction("interviewFeedback", "Add interview feedback");
            actions.setRequiresAttention(true);
        }

        if (user.isRefereeOfApplicationForm(application) && application.isSubmitted() && !application.isTerminated()
                && !user.getRefereeForApplicationForm(application).hasResponded()) {
            actions.addAction("reference", "Add reference");
            actions.setRequiresAttention(true);
        }

        if (user == application.getApplicant() && !application.isTerminated()) {
            actions.addAction("withdraw", "Withdraw");
        }

        if (user.hasAdminRightsOnApplication(application) && application.isPendingApprovalRestart()) {
            actions.addAction("restartApproval", "Revise Approval");
            actions.setRequiresAttention(true);
        }

        if (application.isInState(ApplicationFormStatus.APPROVAL)
                && (user.isInRoleInProgram(Authority.APPROVER, application.getProgram()) || user.isInRole(Authority.SUPERADMINISTRATOR))) {
            actions.addAction("validate", "Approve");
            if (user.isNotInRole(Authority.SUPERADMINISTRATOR) && !application.isPendingApprovalRestart()) {
                actions.setRequiresAttention(true);
            }
        }

        if (application.isInState(ApplicationFormStatus.APPROVAL) && !application.isPendingApprovalRestart()
                && user.isInRoleInProgram(Authority.ADMINISTRATOR, application.getProgram())
                && user.isNotInRoleInProgram(Authority.APPROVER, application.getProgram()) && user.isNotInRole(Authority.SUPERADMINISTRATOR)) {
            actions.addAction("restartApprovalAsAdministrator", "Revise Approval");
            actions.setRequiresAttention(true);
        }

        if (application.isInState(ApplicationFormStatus.APPROVAL)) {
            Supervisor primarySupervisor = application.getLatestApprovalRound().getPrimarySupervisor();
            if (primarySupervisor != null && user == primarySupervisor.getUser() && !primarySupervisor.hasResponded()) {
                actions.addAction("confirmSupervision", "Confirm supervision");
                actions.setRequiresAttention(true);
            }
        }

        actions.addAction("emailApplicant", "Email applicant");

        return actions.sort();
    }

}
