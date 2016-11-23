package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_REVERSE_REJECTION_NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_APPROVER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_EXHUME_REFEREE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REJECTED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CONFIRM_REJECTION_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.*;

public class PrismApplicationRejected extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationComment()); //

        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_CONFIRM_REJECTION) //
                .withRaisesUrgentFlag() //
                .withStateActionAssignments(APPLICATION_PARENT_APPROVER_GROUP) //
                .withStateTransitions(APPLICATION_CONFIRM_REJECTION_TRANSITION));

        stateActions.add(PrismApplicationWorkflow.applicationSendMessage());
        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP));
        stateActions.add(applicationCompleteState(APPLICATION_COMPLETE_REJECTED_STAGE, state, APPLICATION_PARENT_APPROVER_GROUP));
        stateActions.add(applicationUploadReference(state));
        stateActions.add(PrismApplicationWorkflow.applicationViewEdit(state));
        stateActions.add(applicationWithdrawSubmitted(APPLICATION_PARENT_APPROVER_GROUP, APPLICATION_TERMINATE_REFERENCE_GROUP,
                APPLICATION_RETIRE_REFEREE_GROUP));
    }

    public static PrismStateAction applicationReverseRejection() {
        return new PrismStateAction() //
                .withAction(APPLICATION_REVERSE_REJECTION) //
                .withStateActionAssignments(APPLICATION_PARENT_APPROVER_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_REJECTED) //
                        .withTransitionAction(APPLICATION_COMPLETE_REJECTED_STAGE) //
                        .withStateTransitionNotification(APPLICATION_CREATOR, APPLICATION_REVERSE_REJECTION_NOTIFICATION) //
                        .withRoleTransitions(APPLICATION_EXHUME_REFEREE_GROUP));
    }

}
