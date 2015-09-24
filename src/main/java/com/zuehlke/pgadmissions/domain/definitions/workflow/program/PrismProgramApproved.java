package com.zuehlke.pgadmissions.domain.definitions.workflow.program;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_CREATE_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_CREATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CREATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programEmailCreatorApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programTerminateApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditApproved;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PROGRAM_CREATE_APPLICATION) //
                .withActionCondition(ACCEPT_APPLICATION) //
                .withTransitions(APPLICATION_CREATE_TRANSITION //
                        .withRoleTransitions(APPLICATION_CREATE_CREATOR_GROUP))); //

        stateActions.add(PrismProgramWorkflow.programCreateProject()); //
        stateActions.add(programEmailCreatorApproved());
        stateActions.add(programTerminateApproved()); //
        stateActions.add(programViewEditApproved()); //
    }

}
