package com.zuehlke.pgadmissions.domain.definitions.workflow.department;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_COMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.DEPARTMENT_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.DEPARTMENT_COMPLETE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentCreateProgram;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentCreateProject;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismDepartmentUnsubmitted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(DEPARTMENT_COMPLETE) //
                .withStateTransitions(DEPARTMENT_COMPLETE_TRANSITION //
                        .withRoleTransitions(DEPARTMENT_CREATE_ADMINISTRATOR_GROUP)));

        stateActions.add(departmentCreateProgram()); //
        stateActions.add(departmentCreateProject()); //
    }

}
