package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCompleteState;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationCommentWithViewerRecruiter()); //

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.APPLICATION_CONFIRM_OFFER) //
                .withRaisesUrgentFlag() //
                .withNotification(PrismNotificationDefinition.APPLICATION_CONFIRM_OFFER_ACCEPTANCE_REQUEST) //
                .withAssignments(PrismRoleGroup.APPLICATION_PARENT_APPROVER_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_APPROVED_PENDING_PARTNER_APPROVAL)
                        .withTransitionAction(PrismAction.APPLICATION_PROVIDE_PARTNER_APPROVAL) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_CONFIRMED_OFFER_OUTCOME) //
                        .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP) //
                        .withStateTerminations(PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP.getStateTerminations()), //
                        new PrismStateTransition() //
                                .withTransitionState(PrismState.APPLICATION_APPROVED_PENDING_OFFER_ACCEPTANCE) //
                                .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) //
                                .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_CONFIRMED_OFFER_OUTCOME) //
                                .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_CREATE_APPOINTEE_GROUP, //
                                        PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP)
                                .withStateTerminations(PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP.getStateTerminations()))); //

        stateActions.add(PrismApplicationWorkflow.applicationEscalate(PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP)); //
        stateActions.add(applicationCompleteState(PrismAction.APPLICATION_COMPLETE_APPROVED_STAGE, state, PrismRoleGroup.APPLICATION_PARENT_APPROVER_GROUP));

        stateActions.add(PrismApplicationWorkflow.applicationTerminateSubmitted(PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP));

        stateActions.add(PrismApplicationWorkflow.applicationUploadReference(state));
        stateActions.add(PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter(state)); //

        stateActions.add(PrismApplicationWorkflow.applicationWithdrawSubmitted(PrismRoleGroup.APPLICATION_PARENT_APPROVER_GROUP, //
                PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP));
    }

    public static PrismStateAction applicationEscalateApproved() {
        return PrismApplicationWorkflow.applicationEscalate(PrismState.APPLICATION_APPROVED_COMPLETED);
    }

}
