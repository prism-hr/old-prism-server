package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCompleteState;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationTerminateSubmitted;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdrawSubmitted;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.APPLICATION_ASSIGN_HIRING_MANAGERS) //
                .withRaisesUrgentFlag() //
                .withAssignments(PrismRoleGroup.APPLICATION_APPROVER_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_APPROVAL_PENDING_FEEDBACK) //
                        .withTransitionAction(PrismAction.APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL) //
                        .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_CREATE_HIRING_MANAGER_GROUP))); //

        stateActions.add(PrismApplicationWorkflow.applicationCommentWithViewerRecruiter()); //
        stateActions.add(applicationCompleteState(PrismAction.APPLICATION_COMPLETE_APPROVAL_STAGE, state, PrismRoleGroup.APPLICATION_APPROVER_GROUP));

        stateActions.add(PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter()); //

        stateActions.add(PrismApplicationWorkflow.applicationEscalate(PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP)); //

        stateActions.add(applicationTerminateSubmitted(APPLICATION_TERMINATE_REFERENCE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP));

        stateActions.add(PrismApplicationWorkflow.applicationUploadReference(state));
        stateActions.add(PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter(state)); //

        stateActions.add(applicationWithdrawSubmitted(PrismRoleGroup.APPLICATION_APPROVER_GROUP, //
                APPLICATION_TERMINATE_REFERENCE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP));
    }

    public static PrismStateAction applicationCompleteApproval(PrismState state) {
        return PrismApplicationWorkflow.applicationCompleteState(PrismAction.APPLICATION_COMPLETE_APPROVAL_STAGE, state, //
                PrismRoleGroup.APPLICATION_APPROVER_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_HIRING_MANAGER_GROUP); //
    }

    public static PrismStateAction applicationProvideHiringManagerApproval() {
        return new PrismStateAction() //
                .withAction(PrismAction.APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL) //
                .withRaisesUrgentFlag() //
                .withNotification(APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL_REQUEST) //
                .withAssignments(PrismRole.APPLICATION_HIRING_MANAGER);
    }

    public static PrismStateAction applicationTerminateApproval() {
        return applicationTerminateSubmitted(APPLICATION_TERMINATE_REFERENCE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP,
                PrismRoleTransitionGroup.APPLICATION_RETIRE_HIRING_MANAGER_GROUP);
    }

    public static PrismStateAction applicationViewEditApproval(PrismState state) {
        return PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter(state) //
                .withAssignments(PrismRole.APPLICATION_HIRING_MANAGER, PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER);
    }

    public static PrismStateAction applicationWithdrawApproval() {
        return applicationWithdrawSubmitted(PrismRoleGroup.APPLICATION_APPROVER_GROUP, //
                APPLICATION_TERMINATE_REFERENCE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_HIRING_MANAGER_GROUP);
    }

}
