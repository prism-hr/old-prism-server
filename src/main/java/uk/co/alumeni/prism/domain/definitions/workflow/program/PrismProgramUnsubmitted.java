package uk.co.alumeni.prism.domain.definitions.workflow.program;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROGRAM_COMPLETE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.PROGRAM_CREATE_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_COMPLETE_TRANSITION;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramUnsubmitted extends PrismWorkflowState {

    protected void setStateActions() {
        stateActions.add(PrismProgramWorkflow.programCreateProject());

        stateActions.add(new PrismStateAction() //
                .withAction(PROGRAM_COMPLETE) //
                .withStateTransitions(PROGRAM_COMPLETE_TRANSITION //
                        .withRoleTransitions(PROGRAM_CREATE_ADMINISTRATOR_GROUP)));
    }

}
