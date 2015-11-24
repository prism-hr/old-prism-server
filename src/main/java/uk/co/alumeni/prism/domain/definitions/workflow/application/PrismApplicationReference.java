package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCompleteState;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationTerminateSubmitted;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdrawSubmitted;

import uk.co.alumeni.prism.domain.definitions.workflow.*;

public class PrismApplicationReference extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationCommentWithViewerRecruiter()); //

        stateActions.add(applicationCompleteReference(state) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionAction(PrismAction.APPLICATION_VIEW_EDIT) //
                        .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP) //
                        .withStateTerminations(new PrismStateTermination() //
                                .withTerminationState(PrismState.APPLICATION_REFERENCE)))); //

        stateActions.add(PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter()); //
        stateActions.add(PrismApplicationWorkflow.applicationEscalate(PrismState.APPLICATION_REFERENCE_PENDING_COMPLETION));

        stateActions.add(applicationProvideReference() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_REFERENCE) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_PROVIDED_REFERENCE_OUTCOME) //
                        .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_PROVIDE_REFERENCE_GROUP),
                        new PrismStateTransition() //
                                .withTransitionState(PrismState.APPLICATION_REFERENCE_PENDING_COMPLETION) //
                                .withTransitionAction(PrismAction.APPLICATION_COMPLETE_REFERENCE_STAGE) //
                                .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_PROVIDED_REFERENCE_OUTCOME) //
                                .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_PROVIDE_REFERENCE_GROUP),
                        new PrismStateTransition() //
                                .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) //
                                .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_PROVIDED_REFERENCE_OUTCOME) //
                                .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_PROVIDE_REFERENCE_GROUP) //
                                .withStateTerminations(new PrismStateTermination() //
                                        .withTerminationState(PrismState.APPLICATION_REFERENCE) //
                                        .withStateTerminationEvaluation(PrismStateTerminationEvaluation.APPLICATION_REFERENCED_TERMINATION)))); //

        stateActions.add(applicationTerminateReference());
        stateActions.add(PrismApplicationWorkflow.applicationUploadReference(state));
        stateActions.add(applicationViewEditReference(state)); //
        stateActions.add(applicationWithdrawReference());
    }

    public static PrismStateAction applicationCompleteReference(PrismState state) {
        return PrismApplicationWorkflow.applicationCompleteState(PrismAction.APPLICATION_COMPLETE_REFERENCE_STAGE, state, //
                PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP, PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP);
    }

    public static PrismStateAction applicationProvideReference() {
        return new PrismStateAction() //
                .withAction(PrismAction.APPLICATION_PROVIDE_REFERENCE) //
                .withRaisesUrgentFlag() //
                .withNotification(PrismNotificationDefinition.APPLICATION_PROVIDE_REFERENCE_REQUEST) //
                .withAssignments(PrismRole.APPLICATION_REFEREE);
    }

    public static PrismStateAction applicationTerminateReference() {
        return PrismApplicationWorkflow.applicationTerminateSubmitted(PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP);
    }

    public static PrismStateAction applicationViewEditReference(PrismState state) {
        return PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter(state) //
                .withAssignments(PrismRole.APPLICATION_REFEREE, PrismActionEnhancement.APPLICATION_VIEW_AS_REFEREE);
    }

    public static PrismStateAction applicationWithdrawReference() {
        return PrismApplicationWorkflow.applicationWithdrawSubmitted(PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP, //
                PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP);
    }

}
