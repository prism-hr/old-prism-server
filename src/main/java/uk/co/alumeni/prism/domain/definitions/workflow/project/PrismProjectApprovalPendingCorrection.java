package uk.co.alumeni.prism.domain.definitions.workflow.project;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROJECT_CORRECT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROJECT_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.PROJECT_CORRECT_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PROJECT_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.PROJECT_REVIVE_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROJECT_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.project.PrismProjectApproval.projectCompleteApproval;
import static uk.co.alumeni.prism.domain.definitions.workflow.project.PrismProjectWorkflow.*;

public class PrismProjectApprovalPendingCorrection extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(projectCompleteApproval());

        stateActions.add(new PrismStateAction() //
                .withAction(PROJECT_CORRECT) //
                .withRaisesUrgentFlag() //
                .withNotificationDefinition(PROJECT_CORRECT_REQUEST) //
                .withStateActionAssignments(PROJECT_ADMINISTRATOR) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_APPROVAL) //
                        .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST) //
                        .withRoleTransitions(PROJECT_REVIVE_ADMINISTRATOR_GROUP))); //

        stateActions.add(projectSendMessageUnnapproved()); //
        stateActions.add(projectEscalateUnapproved());
        stateActions.add(projectTerminateUnapproved());
        stateActions.add(projectViewEditApproval(state));
        stateActions.add(projectWithdraw());
    }

}
