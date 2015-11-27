package uk.co.alumeni.prism.domain.definitions.workflow.program;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramUnsubmitted extends PrismWorkflowState {

    protected void setStateActions() {
        stateActions.add(PrismProgramWorkflow.programCreateProject());

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.PROGRAM_COMPLETE) //
                .withStateTransitions(PrismStateTransitionGroup.PROGRAM_COMPLETE_TRANSITION //
                        .withRoleTransitions(PrismRoleTransitionGroup.PROGRAM_CREATE_ADMINISTRATOR_GROUP)));
    }

}
