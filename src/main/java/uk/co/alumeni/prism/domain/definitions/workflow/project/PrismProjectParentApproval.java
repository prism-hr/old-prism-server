package uk.co.alumeni.prism.domain.definitions.workflow.project;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;

public class PrismProjectParentApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.PROJECT_COMPLETE_PARENT_APPROVAL_STAGE) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_APPROVED) //
                        .withTransitionAction(PrismAction.PROJECT_COMPLETE_PARENT_APPROVAL_STAGE)));

        stateActions.add(PrismProjectWorkflow.projectEmailCreatorUnnapproved());
        stateActions.add(PrismProjectWorkflow.projectEscalateUnapproved());
        stateActions.add(PrismProjectWorkflow.projectTerminateUnapproved());
        stateActions.add(PrismProjectWorkflow.projectViewEditApproval(state));
        stateActions.add(PrismProjectWorkflow.projectWithdraw());
    }

}
