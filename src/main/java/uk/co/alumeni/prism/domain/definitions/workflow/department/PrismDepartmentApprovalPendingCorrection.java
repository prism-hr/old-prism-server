package uk.co.alumeni.prism.domain.definitions.workflow.department;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.DEPARTMENT_CORRECT_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentApproval.departmentCompleteApproval;

import uk.co.alumeni.prism.domain.definitions.workflow.*;

public class PrismDepartmentApprovalPendingCorrection extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(departmentCompleteApproval());

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.DEPARTMENT_CORRECT) //
                .withRaisesUrgentFlag() //
                .withNotification(DEPARTMENT_CORRECT_REQUEST) //
                .withAssignments(PrismRole.DEPARTMENT_ADMINISTRATOR) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.DEPARTMENT_APPROVAL) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_DEPARTMENT_LIST) //
                        .withRoleTransitions(PrismRoleTransitionGroup.DEPARTMENT_REVIVE_ADMINISTRATOR_GROUP))); //

        stateActions.add(PrismDepartmentWorkflow.departmentCreateProgram()); //
        stateActions.add(PrismDepartmentWorkflow.departmentCreateProject()); //
        stateActions.add(PrismDepartmentWorkflow.departmentEmailCreatorUnnapproved()); //
        stateActions.add(PrismDepartmentWorkflow.departmentEscalateUnapproved()); //
        stateActions.add(PrismDepartmentWorkflow.departmentTerminateUnapproved()); //
        stateActions.add(PrismDepartmentWorkflow.departmentViewEditApproval(state)); //
        stateActions.add(PrismDepartmentWorkflow.departmentWithdraw());
    }

}
