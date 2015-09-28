package com.zuehlke.pgadmissions.domain.definitions.workflow.program;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programCreateProject;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programEmailCreatorApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programTerminateApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditApproved;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(programCreateProject());
        stateActions.add(programEmailCreatorApproved());
        stateActions.add(programTerminateApproved()); //
        stateActions.add(programViewEditApproved()); //
    }

}
