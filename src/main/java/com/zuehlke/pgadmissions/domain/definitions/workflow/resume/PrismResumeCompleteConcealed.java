package com.zuehlke.pgadmissions.domain.definitions.workflow.resume;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.resume.PrismResumeCompleted.resumeStateActions;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismResumeCompleteConcealed extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.addAll(resumeStateActions(state));
    }

}
