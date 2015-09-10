package com.zuehlke.pgadmissions.domain.definitions.workflow.project;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_COMPLETE_APPROVAL_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROJECT_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.PROJECT_COMPLETE_APPROVAL_STAGE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVAL_PENDING_CORRECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROJECT_APPROVED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_APPROVE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectEmailCreatorUnnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectEscalateUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectTerminateUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectViewEditApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectWithdraw;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProjectApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(projectCompleteApproval() //
                .withRaisesUrgentFlag() //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_APPROVAL_PENDING_CORRECTION) //
                        .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST) //
                        .withTransitionEvaluation(PROJECT_APPROVED_OUTCOME)));

        stateActions.add(projectEmailCreatorUnnapproved());
        stateActions.add(projectEscalateUnapproved());
        stateActions.add(projectTerminateUnapproved());
        stateActions.add(projectViewEditApproval(state));
        stateActions.add(projectWithdraw());
    }

    public static PrismStateAction projectCompleteApproval() {
        return new PrismStateAction() //
                .withAction(PROJECT_COMPLETE_APPROVAL_STAGE) //
                .withAssignments(PROJECT_PARENT_ADMINISTRATOR_GROUP) //
                .withNotification(PROJECT_COMPLETE_APPROVAL_STAGE_NOTIFICATION) //
                .withTransitions(PROJECT_APPROVE_TRANSITION);
    }

}
