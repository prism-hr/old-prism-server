package com.zuehlke.pgadmissions.domain.definitions.workflow.program;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_COMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_CREATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programCreateProject;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramUnsubmitted extends PrismWorkflowState {

    protected void setStateActions() {
        stateActions.add(programCreateProject());
        
        stateActions.add(new PrismStateAction() //
                .withAction(PROGRAM_COMPLETE) //
                .withTransitions(PROGRAM_CREATE_TRANSITION));
    }

}
