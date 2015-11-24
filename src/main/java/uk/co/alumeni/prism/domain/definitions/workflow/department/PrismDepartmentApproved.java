package uk.co.alumeni.prism.domain.definitions.workflow.department;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;
import uk.co.alumeni.prism.domain.definitions.workflow.*;

public class PrismDepartmentApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.DEPARTMENT_CREATE_APPLICATION) //
                .withActionCondition(PrismActionCondition.ACCEPT_APPLICATION) //
                .withStateTransitions(PrismStateTransitionGroup.APPLICATION_CREATE_TRANSITION //
                        .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_CREATE_CREATOR_GROUP))); //

        stateActions.add(PrismDepartmentWorkflow.departmentCreateProgram()); //
        stateActions.add(PrismDepartmentWorkflow.departmentCreateProject()); //
        stateActions.add(PrismDepartmentWorkflow.departmentEmailCreatorApproved());

        stateActions.add(PrismDepartmentWorkflow.departmentTerminateApproved()); //
        stateActions.add(PrismDepartmentWorkflow.departmentViewEditApproved()); //
    }

}
