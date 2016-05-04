package uk.co.alumeni.prism.domain.definitions.workflow.project;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROJECT_RESTORE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.PROJECT_VIEW_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.PROJECT_VIEW_EDIT_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PROJECT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PROJECT_STAFF_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROJECT_APPROVED;
import static uk.co.alumeni.prism.domain.definitions.workflow.project.PrismProjectWorkflow.projectSendMessageApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.project.PrismProjectWorkflow.projectViewEditAbstract;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismProjectDisabledCompleted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(projectSendMessageApproved());

        stateActions.add(new PrismStateAction() //
                .withAction(PROJECT_RESTORE) //
                .withStateActionAssignments(PROJECT_ADMINISTRATOR_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_APPROVED) //
                        .withTransitionAction(PrismAction.PROJECT_VIEW_EDIT)));

        stateActions.add(projectViewEditAbstract()
                .withStateActionAssignments(PROJECT_ADMINISTRATOR_GROUP, PROJECT_VIEW_EDIT_AS_USER) //
                .withStateActionAssignments(PROJECT_STAFF_GROUP, PROJECT_VIEW_AS_USER) //
                .withPartnerStateActionAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, PROJECT_VIEW_AS_USER));
    }

}
