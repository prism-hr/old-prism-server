package com.zuehlke.pgadmissions.domain.definitions.workflow.department;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_COMPLETE_APPROVAL_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_DEPARTMENT_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.DEPARTMENT_COMPLETE_APPROVAL_STAGE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_DEPARTMENT_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_DEPARTMENT_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.DEPARTMENT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.DEPARTMENT_APPROVAL_PENDING_CORRECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.DEPARTMENT_APPROVED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.DEPARTMENT_APPROVE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentEmailCreatorUnnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentEscalateUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentViewEditActive;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentWithdraw;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismDepartmentApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(departmentCompleteApproval()
                .withRaisesUrgentFlag() //
                .withNotification(SYSTEM_DEPARTMENT_TASK_REQUEST) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(DEPARTMENT_APPROVAL_PENDING_CORRECTION) //
                        .withTransitionAction(SYSTEM_VIEW_DEPARTMENT_LIST) //
                        .withTransitionEvaluation(DEPARTMENT_APPROVED_OUTCOME))); //

        stateActions.add(departmentEmailCreatorUnnapproved()); //
        stateActions.add(departmentEscalateUnapproved()); //
        stateActions.add(departmentViewEditActive()); //
        stateActions.add(departmentWithdraw());
    }

    public static PrismStateAction departmentCompleteApproval() {
        return new PrismStateAction() //
                .withAction(DEPARTMENT_COMPLETE_APPROVAL_STAGE) //
                .withAssignments(DEPARTMENT_PARENT_ADMINISTRATOR_GROUP) //
                .withNotifications(DEPARTMENT_PARENT_ADMINISTRATOR_GROUP, SYSTEM_DEPARTMENT_UPDATE_NOTIFICATION) //
                .withNotifications(DEPARTMENT_ADMINISTRATOR, DEPARTMENT_COMPLETE_APPROVAL_STAGE_NOTIFICATION) //
                .withTransitions(DEPARTMENT_APPROVE_TRANSITION);
    }

}
