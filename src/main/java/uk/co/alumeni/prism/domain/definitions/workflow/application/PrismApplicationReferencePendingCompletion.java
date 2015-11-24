package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationReferencePendingCompletion extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationCommentWithViewerRecruiter()); //

        stateActions.add(PrismApplicationReference.applicationCompleteReference(state) //
                .withRaisesUrgentFlag());

        stateActions.add(PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter()); //
        stateActions.add(PrismApplicationWorkflow.applicationEscalate(PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP));

        stateActions.add(PrismApplicationReference.applicationProvideReference()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_REFERENCE_PENDING_COMPLETION) //
                        .withTransitionAction(PrismAction.APPLICATION_COMPLETE_REFERENCE_STAGE) //
                        .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_PROVIDE_REFERENCE_GROUP))); //

        stateActions.add(PrismApplicationReference.applicationTerminateReference());
        stateActions.add(PrismApplicationWorkflow.applicationUploadReference(state));
        stateActions.add(PrismApplicationReference.applicationViewEditReference(state)); //
        stateActions.add(PrismApplicationReference.applicationWithdrawReference());
    }

}
