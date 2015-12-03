package uk.co.alumeni.prism.domain.definitions.workflow.department;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismDepartmentParentApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.DEPARTMENT_COMPLETE_PARENT_APPROVAL_STAGE) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.DEPARTMENT_APPROVED) //
                        .withTransitionAction(PrismAction.DEPARTMENT_COMPLETE_PARENT_APPROVAL_STAGE)));

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.DEPARTMENT_CREATE_PROGRAM) //
                .withActionCondition(PrismActionCondition.ACCEPT_PROGRAM) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.PROGRAM_APPROVAL_PARENT_APPROVAL) //
                        .withTransitionAction(PrismAction.PROGRAM_VIEW_EDIT)
                        .withRoleTransitions(PrismRoleTransitionGroup.PROGRAM_CREATE_ADMINISTRATOR_GROUP)));

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.DEPARTMENT_CREATE_PROJECT) //
                .withActionCondition(PrismActionCondition.ACCEPT_PROJECT) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_APPROVAL_PARENT_APPROVAL) //
                        .withTransitionAction(PrismAction.PROJECT_VIEW_EDIT)
                        .withRoleTransitions(PrismRoleTransitionGroup.PROJECT_CREATE_ADMINISTRATOR_GROUP)));

        stateActions.add(PrismDepartmentWorkflow.departmentEmailCreatorUnnapproved()); //
        stateActions.add(PrismDepartmentWorkflow.departmentEscalateUnapproved()); //
        stateActions.add(PrismDepartmentWorkflow.departmentTerminateUnapproved()); //
        stateActions.add(PrismDepartmentWorkflow.departmentViewEditApproval(state)); //
        stateActions.add(PrismDepartmentWorkflow.departmentWithdraw());
    }

}
