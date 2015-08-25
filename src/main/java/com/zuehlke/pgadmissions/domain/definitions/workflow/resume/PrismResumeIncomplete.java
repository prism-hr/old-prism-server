package com.zuehlke.pgadmissions.domain.definitions.workflow.resume;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.RESUME_COMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.RESUME_VIEW_EDIT_AS_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.RESUME_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.RESUME_COMPLETE_TRANSITION;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismResumeIncomplete extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(RESUME_COMPLETE) //
                .withAssignments(RESUME_CREATOR, RESUME_VIEW_EDIT_AS_CREATOR) //
                .withTransitions(RESUME_COMPLETE_TRANSITION)); //
    }

}
