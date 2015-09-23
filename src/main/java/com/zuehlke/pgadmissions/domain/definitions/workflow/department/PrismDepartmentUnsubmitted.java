package com.zuehlke.pgadmissions.domain.definitions.workflow.department;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_COMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.DEPARTMENT_CREATE_TRANSITION;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismDepartmentUnsubmitted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(DEPARTMENT_COMPLETE) //
                .withTransitions(DEPARTMENT_CREATE_TRANSITION));
    }

}
