package com.zuehlke.pgadmissions.domain.definitions.workflow.department;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentEmailCreatorUnnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentViewEditInactive;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismDepartmentWithdrawn extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(departmentEmailCreatorUnnapproved()); //
        stateActions.add(departmentViewEditInactive()); //
    }

}
