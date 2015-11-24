package uk.co.alumeni.prism.domain.definitions.workflow.department;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup;

public class PrismDepartmentUnsubmitted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.DEPARTMENT_COMPLETE) //
                .withStateTransitions(PrismStateTransitionGroup.DEPARTMENT_COMPLETE_TRANSITION //
                        .withRoleTransitions(PrismRoleTransitionGroup.DEPARTMENT_CREATE_ADMINISTRATOR_GROUP)));

        stateActions.add(PrismDepartmentWorkflow.departmentCreateProgram()); //
        stateActions.add(PrismDepartmentWorkflow.departmentCreateProject()); //
    }

}
