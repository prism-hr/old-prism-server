package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_APPROVED_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_APPROVER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_HIRING_MANAGER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CONFIRM_OFFER_RECOMMENDATION_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCompleteState;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationTerminateSubmitted;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdrawSubmitted;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentWithViewerRecruiter()); //

        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_CONFIRM_OFFER_RECOMMENDATION) //
                .withRaisesUrgentFlag() //
                .withAssignments(APPLICATION_PARENT_APPROVER_GROUP) //
                .withNotifications(APPLICATION_CREATOR, APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION) //
                .withTransitions(APPLICATION_CONFIRM_OFFER_RECOMMENDATION_TRANSITION //
                        .withStateTerminationsAndRoleTransitions(APPLICATION_TERMINATE_REFERENCE_GROUP, //
                                APPLICATION_CREATE_HIRING_MANAGER_GROUP, //
                                APPLICATION_RETIRE_REFEREE_GROUP))); //

        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP)); //
        stateActions.add(applicationCompleteState(APPLICATION_COMPLETE_APPROVED_STAGE, state, APPLICATION_PARENT_APPROVER_GROUP));

        stateActions.add(applicationTerminateSubmitted(APPLICATION_TERMINATE_REFERENCE_GROUP, //
                APPLICATION_RETIRE_REFEREE_GROUP));

        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditWithViewerRecruiter(state)); //

        stateActions.add(applicationWithdrawSubmitted(APPLICATION_PARENT_APPROVER_GROUP, //
                APPLICATION_TERMINATE_REFERENCE_GROUP, //
                APPLICATION_RETIRE_REFEREE_GROUP));
    }

    public static PrismStateAction applicationEscalateApproved() {
        return applicationEscalate(APPLICATION_APPROVED_COMPLETED);
    }

}
