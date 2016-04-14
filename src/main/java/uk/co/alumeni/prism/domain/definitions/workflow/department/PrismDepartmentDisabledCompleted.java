package uk.co.alumeni.prism.domain.definitions.workflow.department;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.DEPARTMENT_RESTORE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.DEPARTMENT_VIEW_EDIT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.DEPARTMENT_VIEW_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.DEPARTMENT_VIEW_EDIT_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_VIEWER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.DEPARTMENT_APPROVED;
import static uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentViewEditAbstract;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismDepartmentDisabledCompleted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismDepartmentWorkflow.departmentSendMessageApproved()); //

        stateActions.add(new PrismStateAction() //
                .withAction(DEPARTMENT_RESTORE) //
                .withStateActionAssignments(DEPARTMENT_ADMINISTRATOR_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(DEPARTMENT_APPROVED) //
                        .withTransitionAction(DEPARTMENT_VIEW_EDIT)));

        stateActions.add(departmentViewEditAbstract() //
                .withStateActionAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, DEPARTMENT_VIEW_EDIT_AS_USER) //
                .withStateActionAssignments(DEPARTMENT_VIEWER_GROUP, DEPARTMENT_VIEW_AS_USER) //
                .withPartnerStateActionAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, DEPARTMENT_VIEW_AS_USER)); //
    }

}
