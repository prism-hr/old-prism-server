package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_REJECTED_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_REJECTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_REVERSE_REJECTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_CONFIRM_REJECTION_NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_REVERSE_REJECTION_NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_APPROVER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_EXHUME_REFEREE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REJECTED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REJECTED_COMPLETED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CONFIRM_REJECTION_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCompleteState;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationTerminateSubmitted;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdrawSubmitted;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;

public class PrismApplicationRejected extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentWithViewerRecruiter()); //

        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_CONFIRM_REJECTION) //
                .withRaisesUrgentFlag() //
                .withAssignments(APPLICATION_PARENT_APPROVER_GROUP) //
                .withNotifications(PrismRole.APPLICATION_CREATOR, APPLICATION_CONFIRM_REJECTION_NOTIFICATION) //
                .withStateTransitions(APPLICATION_CONFIRM_REJECTION_TRANSITION //
                        .withStateTerminationsAndRoleTransitions(APPLICATION_TERMINATE_REFERENCE_GROUP, //
                                APPLICATION_RETIRE_REFEREE_GROUP)));

        stateActions.add(applicationEmailCreatorWithViewerRecruiter());
        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP));
        stateActions.add(applicationCompleteState(APPLICATION_COMPLETE_REJECTED_STAGE, state, APPLICATION_PARENT_APPROVER_GROUP));

        stateActions.add(applicationTerminateSubmitted(APPLICATION_TERMINATE_REFERENCE_GROUP, //
                APPLICATION_RETIRE_REFEREE_GROUP));

        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditWithViewerRecruiter(state));

        stateActions.add(applicationWithdrawSubmitted(APPLICATION_PARENT_APPROVER_GROUP, //
                APPLICATION_TERMINATE_REFERENCE_GROUP, //
                APPLICATION_RETIRE_REFEREE_GROUP));
    }

    public static PrismStateAction applicationEscalateRejected() {
        return applicationEscalate(APPLICATION_REJECTED_COMPLETED);
    }

    public static PrismStateAction applicationReverseRejection() {
        return new PrismStateAction() //
                .withAction(APPLICATION_REVERSE_REJECTION) //
                .withAssignments(APPLICATION_PARENT_APPROVER_GROUP) //
                .withNotifications(PrismRole.APPLICATION_CREATOR, APPLICATION_REVERSE_REJECTION_NOTIFICATION) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_REJECTED) //
                        .withTransitionAction(APPLICATION_COMPLETE_REJECTED_STAGE) //
                        .withRoleTransitions(APPLICATION_EXHUME_REFEREE_GROUP));
    }

}
