package uk.co.alumeni.prism.domain.definitions.workflow.project;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROJECT_COMPLETE_APPROVAL_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROJECT_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.PROJECT_COMPLETE_APPROVAL_STAGE_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PROJECT_PARENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROJECT_APPROVAL_PENDING_CORRECTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.PROJECT_APPROVED_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_APPROVE_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.project.PrismProjectWorkflow.*;

public class PrismProjectApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(projectCompleteApproval() //
                .withRaisesUrgentFlag() //
                .withNotificationDefinition(PROJECT_COMPLETE_APPROVAL_STAGE_REQUEST) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_APPROVAL_PENDING_CORRECTION) //
                        .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST) //
                        .withStateTransitionEvaluation(PROJECT_APPROVED_OUTCOME)));

        stateActions.add(projectSendMessageUnnapproved());
        stateActions.add(projectEscalateUnapproved());
        stateActions.add(projectTerminateUnapproved());
        stateActions.add(projectViewEditApproval(state));
        stateActions.add(projectWithdraw());
    }

    public static PrismStateAction projectCompleteApproval() {
        return new PrismStateAction() //
                .withAction(PROJECT_COMPLETE_APPROVAL_STAGE) //
                .withStateActionAssignments(PROJECT_PARENT_ADMINISTRATOR_GROUP) //
                .withStateTransitions(PROJECT_APPROVE_TRANSITION);
    }

}
