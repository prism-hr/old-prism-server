package uk.co.alumeni.prism.domain.definitions.workflow.department;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.DEPARTMENT_CORRECT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_DEPARTMENT_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.DEPARTMENT_CORRECT_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.DEPARTMENT_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.DEPARTMENT_REVIVE_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.DEPARTMENT_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentApproval.departmentCompleteApproval;
import static uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentWorkflow.*;

public class PrismDepartmentApprovalPendingCorrection extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(departmentCompleteApproval());

        stateActions.add(new PrismStateAction() //
                .withAction(DEPARTMENT_CORRECT) //
                .withRaisesUrgentFlag() //
                .withNotificationDefinition(DEPARTMENT_CORRECT_REQUEST) //
                .withStateActionAssignments(DEPARTMENT_ADMINISTRATOR) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(DEPARTMENT_APPROVAL) //
                        .withTransitionAction(SYSTEM_VIEW_DEPARTMENT_LIST) //
                        .withRoleTransitions(DEPARTMENT_REVIVE_ADMINISTRATOR_GROUP))); //

        stateActions.add(departmentCreateProgram()); //
        stateActions.add(departmentCreateProject()); //
        stateActions.add(departmentSendMessageUnnapproved()); //
        stateActions.add(departmentEscalateUnapproved()); //
        stateActions.add(departmentTerminateUnapproved()); //
        stateActions.add(departmentViewEditApproval(state)); //
        stateActions.add(departmentWithdraw());
    }

}
