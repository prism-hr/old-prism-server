package com.zuehlke.pgadmissions.components;

import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.ADD_INTERVIEW_FEEDBACK;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.ADD_REFERENCE;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.ADD_REVIEW;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.APPROVE;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.ASSIGN_INTERVIEWERS;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.ASSIGN_REVIEWERS;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.ASSIGN_SUPERVISORS;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.COMMENT;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.COMPLETE_INTERVIEW_STAGE;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.COMPLETE_REVIEW_STAGE;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.COMPLETE_VALIDATION_STAGE;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.CONFIRM_ELIGIBILITY;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.CONFIRM_INTERVIEW_TIME;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.CONFIRM_SUPERVISION;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.EMAIL_APPLICANT;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.PROVIDE_INTERVIEW_AVAILABILITY;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.REVISE_APPROVAL;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.REVISE_APPROVAL_AS_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.VIEW;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.VIEW_EDIT;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.WITHDRAW;

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

        // GENERAL ACTIONS
        if (availabilityProvider.canEdit(user, application)) {
            actions.addAction(VIEW_EDIT);
        } else {
            actions.addAction(VIEW);
        }

        if (availabilityProvider.canEmailApplicant(user, application)) {
            actions.addAction(EMAIL_APPLICANT);
        }

        if (availabilityProvider.canPostComment(user, application)) {
            actions.addAction(COMMENT);
        }

        if (availabilityProvider.canWithdraw(user, application)) {
            actions.addAction(WITHDRAW);
        }

        if (availabilityProvider.canConfirmEligibility(user, application)) {
            actions.addAction(CONFIRM_ELIGIBILITY);
            if (availabilityProvider.isEligibilityConfirmationAwaiting(application)) {
                actions.setRequiresAttention(true);
            } else {
                actions.setRequiresAttention(false);
            }
        }

        if (availabilityProvider.canAddReference(user, application)) {
            actions.addAction(ADD_REFERENCE);
            actions.setRequiresAttention(true);
        }

        // VALIDATION STAGE ACTIONS

        if (availabilityProvider.canCompleteValidationStage(user, application, nextStatus)) {
            actions.addAction(COMPLETE_VALIDATION_STAGE);
        }
        

        // REVIEW STAGE ACTIONS

        if (availabilityProvider.canAssignReviewers(user, application, nextStatus)) {
            actions.addAction(ASSIGN_REVIEWERS);
        }

        if (availabilityProvider.canCompleteReviewStage(user, application, nextStatus)) {
            actions.addAction(COMPLETE_REVIEW_STAGE);
        }

        if (availabilityProvider.canAddReview(user, application)) {
            actions.addAction(ADD_REVIEW);
            actions.setRequiresAttention(true);
        }

        // INTERVIEW STAGE ACTIONS

        if (availabilityProvider.canAssignInterviewers(user, application, nextStatus)) {
            actions.addAction(ASSIGN_INTERVIEWERS);
        }

        if (availabilityProvider.canCompleteInterviewStage(user, application, nextStatus)) {
            actions.addAction(COMPLETE_INTERVIEW_STAGE);
        }


        if (availabilityProvider.canConfirmInterviewTime(user, application, nextStatus)) {
            actions.addAction(CONFIRM_INTERVIEW_TIME);
            actions.setRequiresAttention(true);
        }

        if (availabilityProvider.canProvideInterviewAvailability(user, application, nextStatus)) {
            actions.addAction(PROVIDE_INTERVIEW_AVAILABILITY);
            actions.setRequiresAttention(true);
        }

        if (availabilityProvider.canAddInterviewFeedback(user, application, nextStatus)) {
            actions.addAction(ADD_INTERVIEW_FEEDBACK);
            actions.setRequiresAttention(true);
        }

        // APPROVAL STAGE ACTIONS

        if(availabilityProvider.canAssignSupervisors(user, application, nextStatus)){
            actions.addAction(ASSIGN_SUPERVISORS);
        }

        if (availabilityProvider.canApproveAsApprover(user, application)) {
            actions.addAction(APPROVE);
            actions.setRequiresAttention(true);
        } else if (availabilityProvider.canApproveAsSuperadministrator(user, application)) {
            actions.addAction(APPROVE);
        }

        
        if (availabilityProvider.canReviseApproval(user, application)) {
            actions.addAction(REVISE_APPROVAL);
            actions.setRequiresAttention(true);
        }


        if (availabilityProvider.canReviseApprovalAsAdministrator(user, application)) {
            actions.addAction(REVISE_APPROVAL_AS_ADMINISTRATOR);
            actions.setRequiresAttention(true);
        }

        if (availabilityProvider.canConfirmSupervision(user, application)) {
            actions.addAction(CONFIRM_SUPERVISION);
            actions.setRequiresAttention(true);
        }

        return actions;
    }

}
