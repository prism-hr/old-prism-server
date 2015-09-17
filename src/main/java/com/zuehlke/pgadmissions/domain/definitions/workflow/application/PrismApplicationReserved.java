package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_RESERVED_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_RESERVE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_RESERVE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_APPROVER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_RESERVED_WAITING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_ALL_STATES_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCompleteState;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationTerminateSubmitted;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdrawSubmitted;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationReserved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentWithViewerRecruiter()); //
        stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
        stateActions.add(applicationEscalateReserved());

        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_RESERVE) //
                .withRaisesUrgentFlag() //
                .withAssignments(APPLICATION_PARENT_APPROVER_GROUP) //
                .withNotifications(APPLICATION_CREATOR, APPLICATION_RESERVE_NOTIFICATION) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_RESERVED_WAITING) //
                        .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST)));

        stateActions.add(applicationCompleteReserved(state));
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditWithViewerRecruiter(state)); //
        stateActions.add(applicationWithdrawnReserved());
    }

    public static PrismStateAction applicationCompleteReserved(PrismState state) {
        return applicationCompleteState(APPLICATION_COMPLETE_RESERVED_STAGE, state, APPLICATION_PARENT_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction applicationEscalateReserved() {
        return applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP);
    }

    public static PrismStateAction applicationTerminateReserved() {
        return applicationTerminateSubmitted(APPLICATION_TERMINATE_ALL_STATES_GROUP,
                APPLICATION_RETIRE_REFEREE_GROUP);
    }

    public static PrismStateAction applicationWithdrawnReserved() {
        return applicationWithdrawSubmitted(APPLICATION_PARENT_ADMINISTRATOR_GROUP, //
                APPLICATION_TERMINATE_ALL_STATES_GROUP,
                APPLICATION_RETIRE_REFEREE_GROUP);
    }

}
