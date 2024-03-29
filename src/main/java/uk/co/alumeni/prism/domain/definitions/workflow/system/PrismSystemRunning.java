package uk.co.alumeni.prism.domain.definitions.workflow.system;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.SYSTEM_VIEW_EDIT_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.SYSTEM_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.SYSTEM_RUNNING;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.INSTITUTION_CREATE_TRANSITION;

public class PrismSystemRunning extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(SYSTEM_VIEW_EDIT) //
                .withActionEnhancement(SYSTEM_VIEW_EDIT_AS_USER)
                .withStateActionAssignments(SYSTEM_ADMINISTRATOR)
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(SYSTEM_RUNNING) //
                        .withTransitionAction(SYSTEM_VIEW_EDIT)
                        .withRoleTransitions(SYSTEM_MANAGE_USER_GROUP)));

        stateActions.add(new PrismStateAction() //
                .withAction(SYSTEM_CREATE_INSTITUTION) //
                .withStateTransitions(INSTITUTION_CREATE_TRANSITION //
                        .withRoleTransitions(INSTITUTION_CREATE_ADMINISTRATOR_GROUP)));

        stateActions.add(new PrismStateAction() //
                .withAction(SYSTEM_MANAGE_ACCOUNT));

        stateActions.add(new PrismStateAction() //
                .withAction(SYSTEM_STARTUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(SYSTEM_RUNNING) //
                        .withTransitionAction(SYSTEM_VIEW_EDIT)
                        .withRoleTransitions(SYSTEM_CREATE_ADMINISTRATOR_GROUP)));

        stateActions.add(new PrismStateAction() //
                .withAction(SYSTEM_VIEW_APPLICATION_LIST));

        stateActions.add(new PrismStateAction() //
                .withAction(SYSTEM_VIEW_INSTITUTION_LIST));

        stateActions.add(new PrismStateAction() //
                .withAction(SYSTEM_VIEW_PROGRAM_LIST));

        stateActions.add(new PrismStateAction() //
                .withAction(SYSTEM_VIEW_PROJECT_LIST));

        stateActions.add(new PrismStateAction() //
                .withAction(SYSTEM_VIEW_TASK_LIST));

        stateActions.add(new PrismStateAction() //
                .withAction(SYSTEM_VIEW_APPOINTMENT_LIST));

        stateActions.add(new PrismStateAction() //
                .withAction(SYSTEM_VIEW_CONNECTION_LIST));

        stateActions.add(new PrismStateAction() //
                .withAction(SYSTEM_VIEW_JOIN_LIST)); //
    }

}
