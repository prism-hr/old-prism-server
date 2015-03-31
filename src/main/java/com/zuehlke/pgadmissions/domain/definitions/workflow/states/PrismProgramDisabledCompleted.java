package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramApproval.programEmailCreatorApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramApproved.programViewEditApproved;

public class PrismProgramDisabledCompleted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(programEmailCreatorApproval()); //

        stateActions.add(programViewEditApproved()); //
    }

}
