package uk.co.alumeni.prism.domain.definitions.workflow.system;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismSystemRunning extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.SYSTEM_VIEW_EDIT) //
                .withActionEnhancement(PrismActionEnhancement.SYSTEM_VIEW_EDIT_AS_USER)
                .withStateActionAssignments(PrismRole.SYSTEM_ADMINISTRATOR)
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.SYSTEM_RUNNING) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_EDIT)
                        .withRoleTransitions(PrismRoleTransitionGroup.SYSTEM_MANAGE_USER_GROUP)));

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.SYSTEM_CREATE_INSTITUTION) //
                .withStateTransitions(PrismStateTransitionGroup.INSTITUTION_CREATE_TRANSITION //
                        .withRoleTransitions(PrismRoleTransitionGroup.INSTITUTION_CREATE_ADMINISTRATOR_GROUP)));

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.SYSTEM_MANAGE_ACCOUNT));

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.SYSTEM_STARTUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.SYSTEM_RUNNING) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_EDIT)
                        .withRoleTransitions(PrismRoleTransitionGroup.SYSTEM_CREATE_ADMINISTRATOR_GROUP)));

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST));

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.SYSTEM_VIEW_INSTITUTION_LIST));

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST));

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST));

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.SYSTEM_VIEW_TASK_LIST));

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.SYSTEM_VIEW_APPOINTMENT_LIST));

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.SYSTEM_VIEW_CONNECTION_LIST));

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.SYSTEM_VIEW_JOIN_LIST)); //
    }

}
