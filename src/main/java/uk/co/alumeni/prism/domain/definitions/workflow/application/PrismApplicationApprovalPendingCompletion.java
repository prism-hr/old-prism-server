package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_APPROVAL_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_COMPLETE_APPROVAL_STAGE_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CONFIRM_APPOINTMENT_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_HIRING_MANAGER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproval.applicationCompleteApproval;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproval.applicationProvideHiringManagerApproval;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproval.applicationViewEditApproval;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproval.applicationWithdrawApproval;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationApprovalPendingCompletion extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentViewerRecruiter()); //

        stateActions.add(applicationCompleteApproval(state) //
                .withRaisesUrgentFlag() //
                .withNotificationDefinition(APPLICATION_COMPLETE_APPROVAL_STAGE_REQUEST)); //

        stateActions.add(applicationProvideHiringManagerApproval() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(APPLICATION_COMPLETE_APPROVAL_STAGE) //
                        .withRoleTransitions(APPLICATION_CONFIRM_APPOINTMENT_GROUP))); //

        stateActions.add(applicationEmailCreatorViewerRecruiter()); //

        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP, //
                APPLICATION_RETIRE_HIRING_MANAGER_GROUP));

        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditApproval(state)); //
        stateActions.add(applicationWithdrawApproval());
    }

}
