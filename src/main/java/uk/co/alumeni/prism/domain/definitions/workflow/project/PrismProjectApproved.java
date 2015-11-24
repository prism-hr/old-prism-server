package uk.co.alumeni.prism.domain.definitions.workflow.project;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

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
