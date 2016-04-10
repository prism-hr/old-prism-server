package uk.co.alumeni.prism.domain.definitions.workflow.department;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.DEPARTMENT_CREATE_APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.DEPARTMENT_SEND_MESSAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition.ACCEPT_APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.DEPARTMENT_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.DEPARTMENT_ENQUIRER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_CREATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CREATE_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentCreateProgram;
import static uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentCreateProject;
import static uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentSendMessageApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentTerminateApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentViewEditApproved;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismDepartmentApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(DEPARTMENT_CREATE_APPLICATION) //
                .withActionCondition(ACCEPT_APPLICATION) //
                .withStateTransitions(APPLICATION_CREATE_TRANSITION //
                        .withRoleTransitions(APPLICATION_CREATE_CREATOR_GROUP))); //

        stateActions.add(departmentCreateProgram()); //
        stateActions.add(departmentCreateProject()); //

        stateActions.add(departmentSendMessageApproved() //
                .withStateActionAssignment(DEPARTMENT_ENQUIRER, DEPARTMENT_ADMINISTRATOR) //
                .withStateActionAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, DEPARTMENT_ENQUIRER) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(DEPARTMENT_SEND_MESSAGE) //
                        .withRoleTransitions(new PrismRoleTransition() //
                                .withRole(DEPARTMENT_ENQUIRER) //
                                .withTransitionType(CREATE) //
                                .withTransitionRole(DEPARTMENT_ENQUIRER) //
                                .withRestrictToOwner() //
                                .withMinimumPermitted(0) //
                                .withMaximumPermitted(1))));

        stateActions.add(departmentTerminateApproved()); //
        stateActions.add(departmentViewEditApproved()); //
    }

}
