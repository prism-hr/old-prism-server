package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CONFIRM_APPOINTMENT_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVAL_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproval.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.*;

public class PrismApplicationApprovalPendingFeedback extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentViewerRefereeViewerRecruiter()); //
        stateActions.add(applicationCompleteApproval(state)); //

        stateActions.add(applicationProvideHiringManagerApproval() //
                .withStateTransitions(APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL_TRANSITION //
                        .withRoleTransitions(APPLICATION_CONFIRM_APPOINTMENT_GROUP)));

        stateActions.add(applicationSendMessageApproval()); //
        stateActions.add(applicationEscalate(APPLICATION_APPROVAL_PENDING_COMPLETION)); //
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditApproval(state)); //
        stateActions.add(applicationWithdrawApproval());
    }

}
