package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCompleteState;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationTerminateSubmitted;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEdit;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdrawSubmitted;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup;

public class PrismApplicationValidation extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationComment()); //
        stateActions.add(applicationCompleteValidation(state)); //
        stateActions.add(PrismApplicationWorkflow.applicationEmailCreator()); //
        stateActions.add(PrismApplicationWorkflow.applicationEscalate(PrismState.APPLICATION_VALIDATION_PENDING_COMPLETION)); //
        stateActions.add(PrismApplicationWorkflow.applicationTerminateSubmitted());
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEdit(state)); //
        stateActions.add(applicationWithdrawValidation());
    }

    public static PrismStateAction applicationCompleteValidation(PrismState state) {
        return PrismApplicationWorkflow.applicationCompleteState(PrismAction.APPLICATION_COMPLETE_VALIDATION_STAGE, state, PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction applicationWithdrawValidation() {
        return PrismApplicationWorkflow.applicationWithdrawSubmitted(PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction applicationUploadReference(PrismState state) {
        return new PrismStateAction() //
                .withAction(PrismAction.APPLICATION_UPLOAD_REFERENCE) //
                .withAssignments(PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(PrismAction.APPLICATION_UPLOAD_REFERENCE));
    }

}
