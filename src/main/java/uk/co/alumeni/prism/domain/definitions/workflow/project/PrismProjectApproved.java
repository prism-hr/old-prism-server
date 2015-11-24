package uk.co.alumeni.prism.domain.definitions.workflow.project;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;
import uk.co.alumeni.prism.domain.definitions.workflow.*;

public class PrismProjectApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.PROJECT_CREATE_APPLICATION) //
                .withActionCondition(PrismActionCondition.ACCEPT_APPLICATION) //
                .withStateTransitions(PrismStateTransitionGroup.APPLICATION_CREATE_TRANSITION //
                        .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_CREATE_CREATOR_GROUP))); //

        stateActions.add(PrismProjectWorkflow.projectEmailCreatorApproved()); //

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.PROJECT_UNENDORSE) //
                .withPartnerAssignments(PrismRoleGroup.PARTNERSHIP_MANAGER_GROUP) //
                .withStateTransitions(PrismStateTransitionGroup.PROJECT_ENDORSE_TRANSITION));

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.PROJECT_REENDORSE) //
                .withPartnerAssignments(PrismRoleGroup.PARTNERSHIP_MANAGER_GROUP) //
                .withStateTransitions(PrismStateTransitionGroup.PROJECT_ENDORSE_TRANSITION));

        stateActions.add(PrismProjectWorkflow.projectTerminateApproved()); //
        stateActions.add(PrismProjectWorkflow.projectViewEditApproved()); //
    }

}
