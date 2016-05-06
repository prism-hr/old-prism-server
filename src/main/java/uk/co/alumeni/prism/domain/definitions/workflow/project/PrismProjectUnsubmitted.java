package uk.co.alumeni.prism.domain.definitions.workflow.project;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROJECT_COMPLETE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.PROJECT_CREATE_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_COMPLETE_TRANSITION;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismProjectUnsubmitted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PROJECT_COMPLETE) //
                .withStateTransitions(PROJECT_COMPLETE_TRANSITION //
                        .withRoleTransitions(PROJECT_CREATE_ADMINISTRATOR_GROUP)));
    }

}
