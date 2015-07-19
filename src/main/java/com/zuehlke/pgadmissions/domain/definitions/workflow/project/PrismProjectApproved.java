package com.zuehlke.pgadmissions.domain.definitions.workflow.project;

import static com.zuehlke.pgadmissions.domain.definitions.PrismActionResolution.RESOLVE_ENDORSEMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_CREATE_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_ENDORSE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_PROJECT_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_CREATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CREATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectEscalateApproved;
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

        stateActions.add(new PrismStateAction() //
                .withAction(PROJECT_ENDORSE) //
                .withResolution(RESOLVE_ENDORSEMENT) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP) //
                .withNotifications(PROJECT_ADMINISTRATOR_GROUP, SYSTEM_PROJECT_UPDATE_NOTIFICATION));

        stateActions.add(projectEscalateApproved()); //
        stateActions.add(projectTerminateApproved()); //
        stateActions.add(projectViewEditApproved()); //
    }

}
