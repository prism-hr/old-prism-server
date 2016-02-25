package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_APPROVED_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_OFFER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_APPROVER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CONFIRM_OFFER_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCompleteState;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationSendMessageViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationTerminateSubmitted;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdrawSubmitted;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentViewerRecruiter()); //

        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_CONFIRM_OFFER) //
                .withRaisesUrgentFlag() //
                .withAssignments(APPLICATION_PARENT_APPROVER_GROUP) //
                .withStateTransitions(APPLICATION_CONFIRM_OFFER_TRANSITION)); //

        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP)); //
        stateActions.add(applicationCompleteState(APPLICATION_COMPLETE_APPROVED_STAGE, state, APPLICATION_PARENT_APPROVER_GROUP));
        stateActions.add(applicationTerminateSubmitted(APPLICATION_TERMINATE_REFERENCE_GROUP, APPLICATION_RETIRE_REFEREE_GROUP));
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditWithViewerRecruiter(state)); //
        stateActions.add(applicationWithdrawSubmitted(APPLICATION_PARENT_APPROVER_GROUP, APPLICATION_TERMINATE_REFERENCE_GROUP,
                APPLICATION_RETIRE_REFEREE_GROUP));
    }

    public static PrismStateAction applicationSendMessageApproved() {
        return applicationSendMessageViewerRecruiter() //
                .withAssignment(APPLICATION_CREATOR, APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_CREATOR);
    }

}
