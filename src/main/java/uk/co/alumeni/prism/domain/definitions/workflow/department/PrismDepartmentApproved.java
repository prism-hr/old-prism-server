package uk.co.alumeni.prism.domain.definitions.workflow.department;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

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
        stateActions.add(PrismDepartmentWorkflow.departmentSendMessageApproved());

        stateActions.add(PrismDepartmentWorkflow.departmentTerminateApproved()); //
        stateActions.add(PrismDepartmentWorkflow.departmentViewEditApproved()); //
    }

}
