package uk.co.alumeni.prism.domain.definitions.workflow.project;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup;

public class PrismProjectUnsubmitted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.PROJECT_COMPLETE) //
                .withStateTransitions(PrismStateTransitionGroup.PROJECT_COMPLETE_TRANSITION //
                        .withRoleTransitions(PrismRoleTransitionGroup.PROJECT_CREATE_ADMINISTRATOR_GROUP)));
    }

}
