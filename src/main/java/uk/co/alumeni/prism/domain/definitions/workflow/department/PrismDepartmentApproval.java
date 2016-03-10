package uk.co.alumeni.prism.domain.definitions.workflow.department;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.DEPARTMENT_COMPLETE_APPROVAL_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_DEPARTMENT_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_PARENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.DEPARTMENT_APPROVAL_PENDING_CORRECTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.DEPARTMENT_APPROVED_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.DEPARTMENT_APPROVE_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentCreateProgram;
import static uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentCreateProject;
import static uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentEmailCreatorUnnapproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentEscalateUnapproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentTerminateUnapproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentSendMessageUnnapproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentViewEditApproval;
import static uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentWithdraw;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismDepartmentApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(departmentCompleteApproval()
                .withRaisesUrgentFlag() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(DEPARTMENT_APPROVAL_PENDING_CORRECTION) //
                        .withTransitionAction(SYSTEM_VIEW_DEPARTMENT_LIST) //
                        .withStateTransitionEvaluation(DEPARTMENT_APPROVED_OUTCOME))); //

        stateActions.add(departmentCreateProgram()); //
        stateActions.add(departmentCreateProject()); //
        stateActions.add(departmentSendMessageUnnapproved()); //
        stateActions.add(departmentEscalateUnapproved()); //
        stateActions.add(departmentTerminateUnapproved()); //
        stateActions.add(departmentViewEditApproval(state)); //
        stateActions.add(departmentWithdraw());
    }

    public static PrismStateAction departmentCompleteApproval() {
        return new PrismStateAction() //
                .withAction(DEPARTMENT_COMPLETE_APPROVAL_STAGE) //
                .withStateActionAssignments(DEPARTMENT_PARENT_ADMINISTRATOR_GROUP) //
                .withStateTransitions(DEPARTMENT_APPROVE_TRANSITION);
    }

}
