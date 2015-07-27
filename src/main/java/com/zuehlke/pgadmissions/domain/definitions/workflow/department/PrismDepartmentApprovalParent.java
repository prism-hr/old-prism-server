package com.zuehlke.pgadmissions.domain.definitions.workflow.department;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_STARTUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.DEPARTMENT_STARTUP_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.DEPARTMENT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.DEPARTMENT_STARTUP_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentEscalateUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentViewEditUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentWithdraw;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismDepartmentApprovalParent extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(departmentEscalateUnapproved()); //

        stateActions.add(new PrismStateAction() //
                .withAction(DEPARTMENT_STARTUP) //
                .withNotifications(DEPARTMENT_ADMINISTRATOR, DEPARTMENT_STARTUP_NOTIFICATION) //
                .withTransitions(DEPARTMENT_STARTUP_TRANSITION));

        stateActions.add(departmentViewEditUnapproved()); //
        stateActions.add(departmentWithdraw());
    }
}
