package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup;

public class PrismApplicationApprovalPendingFeedback extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationCommentWithViewerRecruiter()); //
        stateActions.add(PrismApplicationApproval.applicationCompleteApproval(state)); //

        stateActions.add(PrismApplicationApproval.applicationProvideHiringManagerApproval() //
                .withStateTransitions(PrismStateTransitionGroup.APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL_TRANSITION //
                        .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_CONFIRM_APPOINTMENT_GROUP)));

        stateActions.add(PrismApplicationApproval.applicationTerminateApproval());
        stateActions.add(PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter()); //
        stateActions.add(PrismApplicationWorkflow.applicationEscalate(PrismState.APPLICATION_APPROVAL_PENDING_COMPLETION)); //
        stateActions.add(PrismApplicationWorkflow.applicationUploadReference(state));
        stateActions.add(PrismApplicationApproval.applicationViewEditApproval(state)); //
        stateActions.add(PrismApplicationApproval.applicationWithdrawApproval());
    }

}
