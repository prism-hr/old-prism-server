package uk.co.alumeni.prism.domain.definitions.workflow.department;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentSendMessageUnnapproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentViewEditInactive;

public class PrismDepartmentRejected extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(departmentSendMessageUnnapproved()); //
        stateActions.add(departmentViewEditInactive()); //
    }

}
