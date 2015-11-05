package com.zuehlke.pgadmissions.domain.definitions.workflow.program;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_COMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.PROGRAM_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_COMPLETE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programCreateProject;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramUnsubmitted extends PrismWorkflowState {

    protected void setStateActions() {
        stateActions.add(programCreateProject());

        stateActions.add(new PrismStateAction() //
                .withAction(PROGRAM_COMPLETE) //
                .withStateTransitions(PROGRAM_COMPLETE_TRANSITION //
                        .withRoleTransitions(PROGRAM_CREATE_ADMINISTRATOR_GROUP)));
    }

}
