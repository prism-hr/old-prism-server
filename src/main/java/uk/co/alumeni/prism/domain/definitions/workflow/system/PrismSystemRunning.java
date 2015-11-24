package uk.co.alumeni.prism.domain.definitions.workflow.system;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;
import uk.co.alumeni.prism.domain.definitions.workflow.*;

public class PrismSystemRunning extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.SYSTEM_VIEW_EDIT) //
                .withActionEnhancement(PrismActionEnhancement.SYSTEM_VIEW_EDIT_AS_USER)
                .withAssignments(PrismRole.SYSTEM_ADMINISTRATOR)
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
