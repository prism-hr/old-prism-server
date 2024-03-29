package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_HIRING_MANAGER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_APPROVER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_HIRING_MANAGER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_ASSIGN_HIRING_MANAGERS_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.*;

public class PrismApplicationApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_ASSIGN_HIRING_MANAGERS) //
                .withRaisesUrgentFlag() //
                .withStateActionAssignments(APPLICATION_APPROVER_GROUP) //
                .withStateTransitions(APPLICATION_ASSIGN_HIRING_MANAGERS_TRANSITION)); //

        stateActions.add(applicationCommentViewerRefereeViewerRecruiter()); //
        stateActions.add(applicationCompleteState(APPLICATION_COMPLETE_APPROVAL_STAGE, state, APPLICATION_APPROVER_GROUP));
        stateActions.add(applicationSendMessageViewerRefereeViewerRecruiter()); //
        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP)); //
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditViewerRefereeViewerRecruiter(state)); //
        stateActions.add(applicationWithdrawSubmitted(APPLICATION_APPROVER_GROUP, APPLICATION_TERMINATE_REFERENCE_GROUP, APPLICATION_RETIRE_REFEREE_GROUP));
    }

    public static PrismStateAction applicationCompleteApproval(PrismState state) {
        return applicationCompleteState(APPLICATION_COMPLETE_APPROVAL_STAGE, state, APPLICATION_APPROVER_GROUP, APPLICATION_RETIRE_HIRING_MANAGER_GROUP); //
    }

    public static PrismStateAction applicationProvideHiringManagerApproval() {
        return new PrismStateAction() //
                .withAction(APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL) //
                .withRaisesUrgentFlag() //
                .withNotificationDefinition(APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL_REQUEST) //
                .withStateActionAssignments(APPLICATION_HIRING_MANAGER);
    }

    public static PrismStateAction applicationSendMessageApproval() {
        return applicationSendMessageViewerRefereeViewerRecruiter() //
                .withStateActionAssignment(APPLICATION_HIRING_MANAGER, APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withStateActionAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_HIRING_MANAGER);
    }

    public static PrismStateAction applicationViewEditApproval(PrismState state) {
        return applicationViewEditViewerRefereeViewerRecruiter(state) //
                .withStateActionAssignments(APPLICATION_HIRING_MANAGER, APPLICATION_VIEW_AS_RECRUITER);
    }

    public static PrismStateAction applicationWithdrawApproval() {
        return applicationWithdrawSubmitted(APPLICATION_APPROVER_GROUP, APPLICATION_TERMINATE_REFERENCE_GROUP, APPLICATION_RETIRE_REFEREE_GROUP,
                APPLICATION_RETIRE_HIRING_MANAGER_GROUP);
    }

}
