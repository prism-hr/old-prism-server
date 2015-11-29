package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationApprovalPendingCompletion extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationCommentWithViewerRecruiter()); //

        stateActions.add(PrismApplicationApproval.applicationCompleteApproval(state) //
                .withRaisesUrgentFlag()); //

        stateActions.add(PrismApplicationApproval.applicationProvideHiringManagerApproval() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(PrismAction.APPLICATION_COMPLETE_APPROVAL_STAGE) //
                        .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_CONFIRM_APPOINTMENT_GROUP))); //

        stateActions.add(PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter()); //

        stateActions.add(PrismApplicationWorkflow.applicationEscalate(PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_HIRING_MANAGER_GROUP));

        stateActions.add(PrismApplicationApproval.applicationTerminateApproval());
        stateActions.add(PrismApplicationWorkflow.applicationUploadReference(state));
        stateActions.add(PrismApplicationApproval.applicationViewEditApproval(state)); //
        stateActions.add(PrismApplicationApproval.applicationWithdrawApproval());
    }

}
