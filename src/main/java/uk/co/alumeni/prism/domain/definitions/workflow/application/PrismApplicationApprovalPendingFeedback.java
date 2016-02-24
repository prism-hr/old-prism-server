package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CONFIRM_APPOINTMENT_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVAL_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproval.applicationCompleteApproval;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproval.applicationProvideHiringManagerApproval;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproval.applicationSendMessageApproval;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproval.applicationTerminateApproval;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproval.applicationViewEditApproval;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproval.applicationWithdrawApproval;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationApprovalPendingFeedback extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentViewerRecruiter()); //
        stateActions.add(applicationCompleteApproval(state)); //

        stateActions.add(applicationProvideHiringManagerApproval() //
                .withStateTransitions(APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL_TRANSITION //
                        .withRoleTransitions(APPLICATION_CONFIRM_APPOINTMENT_GROUP)));

        stateActions.add(applicationTerminateApproval());
        stateActions.add(applicationSendMessageApproval()); //
        stateActions.add(applicationEscalate(APPLICATION_APPROVAL_PENDING_COMPLETION)); //
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditApproval(state)); //
        stateActions.add(applicationWithdrawApproval());
    }

}
