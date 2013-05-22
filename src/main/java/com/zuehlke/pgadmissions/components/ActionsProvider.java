package com.zuehlke.pgadmissions.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;
import com.zuehlke.pgadmissions.services.StateTransitionViewResolver;

@Component
public class ActionsProvider {

    private StateTransitionViewResolver stateTransitionViewResolver;

    private ActionsAvailabilityProvider availabilityProvider;

    public ActionsProvider() {
    }

    @Autowired
    public ActionsProvider(StateTransitionViewResolver stateTransitionViewResolver, ActionsAvailabilityProvider availabilityProvider) {
        this.stateTransitionViewResolver = stateTransitionViewResolver;
        this.availabilityProvider = availabilityProvider;
    }

    public ActionsDefinitions calculateActions(final RegisteredUser user, final ApplicationForm application) {
        ApplicationFormStatus nextStatus = stateTransitionViewResolver.getNextStatus(application);

        ActionsDefinitions actions = new ActionsDefinitions();

        if (user.canEditAsAdministrator(application) || user.canEditAsApplicant(application)) {
            actions.addAction("view", "View / Edit");
        } else {
            actions.addAction("view", "View");
        }

        if (availabilityProvider.canValidate(user, application)) {
            actions.addAction("validate", "Validate");
        }

        if (availabilityProvider.canEvaluateReviews(user, application)) {
            actions.addAction("validate", "Evaluate reviews");
        }

        if (availabilityProvider.canEvaluateInterviewFeedback(user, application, nextStatus)) {
            actions.addAction("validate", "Evaluate interview feedback");
        }
        if (availabilityProvider.canConfirmInterviewTime(user, application, nextStatus)) {
            actions.addAction("interviewConfirm", "Confirm interview time");
            actions.setRequiresAttention(true);
        }

        if (availabilityProvider.canAdministerInterview(user, application, nextStatus)) {
            actions.addAction("validate", "Administer Interview");
        }

        if (availabilityProvider.canConfirmEligibility(user, application)) {
            actions.addAction("validate", "Confirm Eligibility");
            if (availabilityProvider.isEligibilityConfirmationAwaiting(application)) {
                actions.setRequiresAttention(true);
            } else {
                actions.setRequiresAttention(false);
            }
        }

        if (availabilityProvider.canPostComment(user, application)) {
            actions.addAction("comment", "Comment");
        }

        if (availabilityProvider.canAddReview(user, application)) {
            actions.addAction("review", "Add review");
            actions.setRequiresAttention(true);
        }

        if (availabilityProvider.canProvideInterviewAvailability(user, application, nextStatus)) {
            actions.addAction("interviewVote", "Provide Availability For Interview");
            actions.setRequiresAttention(true);
        }

        if (availabilityProvider.canAddInterviewFeedback(user, application)) {
            actions.addAction("interviewFeedback", "Add interview feedback");
            actions.setRequiresAttention(true);
        }

        if (availabilityProvider.canAddReference(user, application)) {
            actions.addAction("reference", "Add reference");
            actions.setRequiresAttention(true);
        }

        if (availabilityProvider.canWithdraw(user, application)) {
            actions.addAction("withdraw", "Withdraw");
        }

        if (availabilityProvider.canReviseApproval(user, application)) {
            actions.addAction("restartApproval", "Revise Approval");
            actions.setRequiresAttention(true);
        }

        if (availabilityProvider.canApproveAsApprover(user, application)) {
            actions.addAction("validate", "Approve");
            actions.setRequiresAttention(true);
        } else if (availabilityProvider.canApproveAsSuperadministrator(user, application)) {
            actions.addAction("validate", "Approve");
        }

        if (availabilityProvider.canReviseApprovalAsAdministrator(user, application)) {
            actions.addAction("restartApprovalAsAdministrator", "Revise Approval");
            actions.setRequiresAttention(true);
        }

        if (availabilityProvider.canConfirmSupervision(user, application)) {
            actions.addAction("confirmSupervision", "Confirm supervision");
            actions.setRequiresAttention(true);
        }

        actions.addAction("emailApplicant", "Email applicant");

        return actions.sort();
    }

}
