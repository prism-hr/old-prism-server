package uk.co.alumeni.prism.domain.definitions.workflow.project;

import uk.co.alumeni.prism.domain.definitions.workflow.*;

public class PrismProjectParentApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.PROJECT_COMPLETE_PARENT_APPROVAL_STAGE) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_APPROVED) //
                        .withTransitionAction(PrismAction.PROJECT_COMPLETE_PARENT_APPROVAL_STAGE)));

        stateActions.add(PrismProjectWorkflow.projectSendMessageUnnapproved());
        stateActions.add(PrismProjectWorkflow.projectEscalateUnapproved());
        stateActions.add(PrismProjectWorkflow.projectTerminateUnapproved());
        stateActions.add(PrismProjectWorkflow.projectViewEditApproval(state));
        stateActions.add(PrismProjectWorkflow.projectWithdraw());
    }

}
