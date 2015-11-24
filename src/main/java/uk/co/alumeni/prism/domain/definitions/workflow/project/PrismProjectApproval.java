package uk.co.alumeni.prism.domain.definitions.workflow.project;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;
import uk.co.alumeni.prism.domain.definitions.workflow.*;

public class PrismProjectApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(projectCompleteApproval() //
                .withRaisesUrgentFlag() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_APPROVAL_PENDING_CORRECTION) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_APPROVED_OUTCOME)));

        stateActions.add(PrismProjectWorkflow.projectEmailCreatorUnnapproved());
        stateActions.add(PrismProjectWorkflow.projectEscalateUnapproved());
        stateActions.add(PrismProjectWorkflow.projectTerminateUnapproved());
        stateActions.add(PrismProjectWorkflow.projectViewEditApproval(state));
        stateActions.add(PrismProjectWorkflow.projectWithdraw());
    }

    public static PrismStateAction projectCompleteApproval() {
        return new PrismStateAction() //
                .withAction(PrismAction.PROJECT_COMPLETE_APPROVAL_STAGE) //
                .withAssignments(PrismRoleGroup.PROJECT_PARENT_ADMINISTRATOR_GROUP) //
                .withNotifications(PrismRole.PROJECT_ADMINISTRATOR, PrismNotificationDefinition.PROJECT_COMPLETE_APPROVAL_STAGE_NOTIFICATION) //
                .withStateTransitions(PrismStateTransitionGroup.PROJECT_APPROVE_TRANSITION);
    }

}
