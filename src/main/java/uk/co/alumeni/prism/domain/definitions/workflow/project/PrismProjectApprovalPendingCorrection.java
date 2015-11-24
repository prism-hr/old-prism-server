package uk.co.alumeni.prism.domain.definitions.workflow.project;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.PROJECT_CORRECT_REQUEST;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;
import uk.co.alumeni.prism.domain.definitions.workflow.*;

public class PrismProjectApprovalPendingCorrection extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismProjectApproval.projectCompleteApproval());

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.PROJECT_CORRECT) //
                .withRaisesUrgentFlag() //
                .withNotification(PROJECT_CORRECT_REQUEST) //
                .withAssignments(PrismRole.PROJECT_ADMINISTRATOR) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_APPROVAL) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST) //
                        .withRoleTransitions(PrismRoleTransitionGroup.PROJECT_REVIVE_ADMINISTRATOR_GROUP))); //

        stateActions.add(PrismProjectWorkflow.projectEmailCreatorUnnapproved()); //
        stateActions.add(PrismProjectWorkflow.projectEscalateUnapproved());
        stateActions.add(PrismProjectWorkflow.projectTerminateUnapproved());
        stateActions.add(PrismProjectWorkflow.projectViewEditApproval(state));
        stateActions.add(PrismProjectWorkflow.projectWithdraw());
    }

}
