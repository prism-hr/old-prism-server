package uk.co.alumeni.prism.domain.definitions.workflow.department;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismDepartmentWithdrawn extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismDepartmentWorkflow.departmentSendMessageUnnapproved()); //
        stateActions.add(PrismDepartmentWorkflow.departmentViewEditInactive()); //
    }

}
