package uk.co.alumeni.prism.domain.definitions.workflow.department;

import static uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentEmailCreatorUnnapproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentViewEditInactive;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismDepartmentRejected extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(departmentEmailCreatorUnnapproved()); //
        stateActions.add(departmentViewEditInactive()); //
    }

}
