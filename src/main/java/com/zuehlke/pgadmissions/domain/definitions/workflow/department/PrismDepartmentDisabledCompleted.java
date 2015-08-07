package com.zuehlke.pgadmissions.domain.definitions.workflow.department;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_RESTORE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.DEPARTMENT_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentEmailCreatorApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentViewEditActive;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismDepartmentDisabledCompleted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(departmentEmailCreatorApproved()); //

        stateActions.add(new PrismStateAction() //
                .withAction(DEPARTMENT_RESTORE) //
                .withAssignments(DEPARTMENT_ADMINISTRATOR_GROUP) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(DEPARTMENT_APPROVED) //
                        .withTransitionAction(DEPARTMENT_VIEW_EDIT)));

        stateActions.add(departmentViewEditActive()); //
    }

}
