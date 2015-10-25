package com.zuehlke.pgadmissions.domain.definitions.workflow.system;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_CREATE_INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_MANAGE_ACCOUNT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_STARTUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPOINTMENT_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_CONNECTION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_INSTITUTION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_JOIN_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROGRAM_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROJECT_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_TASK_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.SYSTEM_VIEW_EDIT_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.SYSTEM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.INSTITUTION_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.SYSTEM_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.SYSTEM_MANAGE_USER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.SYSTEM_RUNNING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.INSTITUTION_CREATE_TRANSITION;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismSystemRunning extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(SYSTEM_VIEW_EDIT) //
                .withActionEnhancement(SYSTEM_VIEW_EDIT_AS_USER)
                .withAssignments(SYSTEM_ADMINISTRATOR)
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
