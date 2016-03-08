package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_HIRING_MANAGERS;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_APPROVAL_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_HIRING_MANAGER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_APPROVER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_HIRING_MANAGER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_ASSIGN_HIRING_MANAGERS_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCompleteState;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdrawSubmitted;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_ASSIGN_HIRING_MANAGERS) //
                .withRaisesUrgentFlag() //
                .withAssignments(APPLICATION_APPROVER_GROUP) //
                .withStateTransitions(APPLICATION_ASSIGN_HIRING_MANAGERS_TRANSITION)); //

        stateActions.add(applicationCommentWithViewerRecruiter()); //
        stateActions.add(applicationCompleteState(APPLICATION_COMPLETE_APPROVAL_STAGE, state, APPLICATION_APPROVER_GROUP));
        stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP)); //
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditWithViewerRecruiter(state)); //
        stateActions.add(applicationWithdrawSubmitted(APPLICATION_APPROVER_GROUP, APPLICATION_TERMINATE_REFERENCE_GROUP, APPLICATION_RETIRE_REFEREE_GROUP));
    }

    public static PrismStateAction applicationCompleteApproval(PrismState state) {
        return applicationCompleteState(APPLICATION_COMPLETE_APPROVAL_STAGE, state, APPLICATION_APPROVER_GROUP, APPLICATION_RETIRE_HIRING_MANAGER_GROUP); //
    }

    public static PrismStateAction applicationProvideHiringManagerApproval() {
        return new PrismStateAction() //
                .withAction(APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL) //
                .withRaisesUrgentFlag() //
                .withNotification(APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL_REQUEST) //
                .withAssignments(APPLICATION_HIRING_MANAGER);
    }

    public static PrismStateAction applicationViewEditApproval(PrismState state) {
        return applicationViewEditWithViewerRecruiter(state) //
                .withAssignments(APPLICATION_HIRING_MANAGER, APPLICATION_VIEW_AS_RECRUITER);
    }

    public static PrismStateAction applicationWithdrawApproval() {
        return applicationWithdrawSubmitted(APPLICATION_APPROVER_GROUP, APPLICATION_TERMINATE_REFERENCE_GROUP, APPLICATION_RETIRE_REFEREE_GROUP,
                APPLICATION_RETIRE_HIRING_MANAGER_GROUP);
    }

}
