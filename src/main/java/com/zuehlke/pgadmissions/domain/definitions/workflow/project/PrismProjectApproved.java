package com.zuehlke.pgadmissions.domain.definitions.workflow.project;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_CREATE_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_PROVIDE_SPONSORSHIP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_SPONSOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.PROJECT_SPONSOR_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_SPONSOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_SPONSOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_CREATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.PROJECT_CREATE_SPONSOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CREATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_SPONSOR_TRANSITON;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectEscalateApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectSuspendApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectTerminateApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectViewEditApproved;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProjectApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PROJECT_CREATE_APPLICATION) //
                .withCondition(ACCEPT_APPLICATION) //
                .withTransitions(APPLICATION_CREATE_TRANSITION //
                        .withRoleTransitions(APPLICATION_CREATE_CREATOR_GROUP))); //

        stateActions.add(projectEmailCreator()); //
        stateActions.add(projectEscalateApproved()); //

        stateActions.add(new PrismStateAction() //
                .withAction(PROJECT_PROVIDE_SPONSORSHIP) //
                .withCondition(ACCEPT_SPONSOR) //
                .withAssignments(PROJECT_SPONSOR_GROUP)
                .withNotifications(PROJECT_SPONSOR, PROJECT_SPONSOR_NOTIFICATION) //
                .withTransitions(PROJECT_SPONSOR_TRANSITON //
                        .withRoleTransitions(PROJECT_CREATE_SPONSOR_GROUP))); //

        stateActions.add(projectSuspendApproved()); //
        stateActions.add(projectTerminateApproved()); //
        stateActions.add(projectViewEditApproved()); //
    }

}
