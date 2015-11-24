package uk.co.alumeni.prism.domain.definitions.workflow.department;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;
import uk.co.alumeni.prism.domain.definitions.workflow.*;

public class PrismDepartmentApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(departmentCompleteApproval()
                .withRaisesUrgentFlag() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.DEPARTMENT_APPROVAL_PENDING_CORRECTION) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_DEPARTMENT_LIST) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.DEPARTMENT_APPROVED_OUTCOME))); //

        stateActions.add(PrismDepartmentWorkflow.departmentCreateProgram()); //
        stateActions.add(PrismDepartmentWorkflow.departmentCreateProject()); //
        stateActions.add(PrismDepartmentWorkflow.departmentEmailCreatorUnnapproved()); //
        stateActions.add(PrismDepartmentWorkflow.departmentEscalateUnapproved()); //
        stateActions.add(PrismDepartmentWorkflow.departmentTerminateUnapproved()); //
        stateActions.add(PrismDepartmentWorkflow.departmentViewEditApproval(state)); //
        stateActions.add(PrismDepartmentWorkflow.departmentWithdraw());
    }

    public static PrismStateAction departmentCompleteApproval() {
        return new PrismStateAction() //
                .withAction(PrismAction.DEPARTMENT_COMPLETE_APPROVAL_STAGE) //
                .withAssignments(PrismRoleGroup.DEPARTMENT_PARENT_ADMINISTRATOR_GROUP) //
                .withNotifications(PrismRole.DEPARTMENT_ADMINISTRATOR, PrismNotificationDefinition.DEPARTMENT_COMPLETE_APPROVAL_STAGE_NOTIFICATION) //
                .withStateTransitions(PrismStateTransitionGroup.DEPARTMENT_APPROVE_TRANSITION);
    }

}
