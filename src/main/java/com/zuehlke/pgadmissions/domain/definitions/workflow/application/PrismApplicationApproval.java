package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_SUPERVISORS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_APPROVAL_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_PRIMARY_SUPERVISION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_SECONDARY_SUPERVISION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_SECONDARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_APPROVER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_SUPERVISOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_SUPERVISOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVAL_PENDING_FEEDBACK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_ALL_STATES_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_ASSIGNED_SUPERVISOR_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCompleteState;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationTerminateSubmitted;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEditWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdrawSubmitted;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_ASSIGN_SUPERVISORS) //
                .withRaisesUrgentFlag() //
                .withNotification(SYSTEM_APPLICATION_TASK_REQUEST) //
                .withAssignments(APPLICATION_APPROVER_GROUP) //
                .withNotifications(APPLICATION_APPROVER_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_APPROVAL) //
                        .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                        .withTransitionEvaluation(APPLICATION_ASSIGNED_SUPERVISOR_OUTCOME) //
                        .withRoleTransitions(APPLICATION_CREATE_ADMINISTRATOR_GROUP),
                        new PrismStateTransition() //
                                .withTransitionState(APPLICATION_APPROVAL_PENDING_FEEDBACK) //
                                .withTransitionAction(APPLICATION_CONFIRM_PRIMARY_SUPERVISION) //
                                .withTransitionEvaluation(APPLICATION_ASSIGNED_SUPERVISOR_OUTCOME) //
                                .withRoleTransitions(APPLICATION_CREATE_SUPERVISOR_GROUP))); //

        stateActions.add(applicationCommentWithViewerRecruiterAndAdministrator()); //
        stateActions.add(applicationCompleteState(APPLICATION_COMPLETE_APPROVAL_STAGE, state, APPLICATION_APPROVER_GROUP,
                APPLICATION_RETIRE_ADMINISTRATOR_GROUP));
        
        stateActions.add(applicationEmailCreatorWithViewerRecruiterAndAdministrator()); //
        
        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP, //
                APPLICATION_RETIRE_ADMINISTRATOR_GROUP)); //
        
        stateActions.add(applicationTerminateSubmitted(APPLICATION_TERMINATE_ALL_STATES_GROUP, //
                APPLICATION_RETIRE_REFEREE_GROUP, //
                APPLICATION_RETIRE_ADMINISTRATOR_GROUP));
        
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditWithViewerRecruiterAndAdministrator(state)); //
        
        stateActions.add(applicationWithdrawSubmitted(APPLICATION_APPROVER_GROUP, //
                APPLICATION_TERMINATE_ALL_STATES_GROUP, //
                APPLICATION_RETIRE_REFEREE_GROUP, //
                APPLICATION_RETIRE_ADMINISTRATOR_GROUP));
    }

    public static PrismStateAction applicationCompleteApproval(PrismState state) {
        return applicationCompleteState(APPLICATION_COMPLETE_APPROVAL_STAGE, state, //
                APPLICATION_APPROVER_GROUP, //
                APPLICATION_RETIRE_ADMINISTRATOR_GROUP, //
                APPLICATION_RETIRE_SUPERVISOR_GROUP); //
    }

    public static PrismStateAction applicationConfirmPrimarySupervision() {
        return new PrismStateAction() //
                .withAction(APPLICATION_CONFIRM_PRIMARY_SUPERVISION) //
                .withRaisesUrgentFlag() //
                .withNotification(SYSTEM_APPLICATION_TASK_REQUEST) //
                .withAssignments(APPLICATION_PRIMARY_SUPERVISOR) //
                .withNotifications(APPLICATION_APPROVER_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION);
    }

    public static PrismStateAction applicationConfirmSecondarySupervision() {
        return new PrismStateAction() //
                .withAction(APPLICATION_CONFIRM_SECONDARY_SUPERVISION) //
                .withRaisesUrgentFlag() //
                .withNotification(SYSTEM_APPLICATION_TASK_REQUEST) //
                .withAssignments(APPLICATION_SECONDARY_SUPERVISOR) //
                .withNotifications(APPLICATION_APPROVER_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION);
    }

    public static PrismStateAction applicationTerminateApproval() {
        return applicationTerminateSubmitted(APPLICATION_TERMINATE_ALL_STATES_GROUP, //
                APPLICATION_RETIRE_REFEREE_GROUP,
                APPLICATION_RETIRE_ADMINISTRATOR_GROUP, //
                APPLICATION_RETIRE_SUPERVISOR_GROUP);
    }

    public static PrismStateAction applicationViewEditApproval(PrismState state) {
        return PrismApplicationWorkflow.applicationViewEditWithViewerRecruiterAndAdministrator(state) //
                .withAssignments(APPLICATION_PRIMARY_SUPERVISOR, APPLICATION_VIEW_AS_RECRUITER)
                .withAssignments(APPLICATION_SECONDARY_SUPERVISOR, APPLICATION_VIEW_AS_RECRUITER);
    }

    public static PrismStateAction applicationWithdrawApproval() {
        return applicationWithdrawSubmitted(APPLICATION_APPROVER_GROUP, //
                APPLICATION_TERMINATE_ALL_STATES_GROUP, //
                APPLICATION_RETIRE_REFEREE_GROUP,
                APPLICATION_RETIRE_ADMINISTRATOR_GROUP, //
                APPLICATION_RETIRE_SUPERVISOR_GROUP);
    }

}
