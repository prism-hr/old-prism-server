package uk.co.alumeni.prism.domain.definitions.workflow.department;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_VIEWER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.DEPARTMENT_APPROVED;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement;

public class PrismDepartmentDisabledCompleted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismDepartmentWorkflow.departmentEmailCreatorApproved()); //

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.DEPARTMENT_RESTORE) //
                .withAssignments(DEPARTMENT_ADMINISTRATOR_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(DEPARTMENT_APPROVED) //
                        .withTransitionAction(PrismAction.DEPARTMENT_VIEW_EDIT)));

        stateActions.add(PrismDepartmentWorkflow.departmentViewEditAbstract() //
                .withAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, PrismActionEnhancement.DEPARTMENT_VIEW_AS_USER) //
                .withAssignments(DEPARTMENT_VIEWER_GROUP, PrismActionEnhancement.DEPARTMENT_VIEW_AS_USER) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, PrismActionEnhancement.DEPARTMENT_VIEW_AS_USER)); //
    }

}
