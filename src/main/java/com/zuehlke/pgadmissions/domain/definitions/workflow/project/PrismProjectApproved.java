package com.zuehlke.pgadmissions.domain.definitions.workflow.project;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_CREATE_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_ENDORSE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_UNENDORSE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_ENDORSER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_CREATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CREATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_ENDORSE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectEmailCreatorApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectTerminateApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectViewEditApproved;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProjectApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PROJECT_CREATE_APPLICATION) //
                .withActionCondition(ACCEPT_APPLICATION) //
                .withTransitions(APPLICATION_CREATE_TRANSITION //
                        .withRoleTransitions(APPLICATION_CREATE_CREATOR_GROUP))); //

        stateActions.add(projectEmailCreatorApproved()); //

        stateActions.add(new PrismStateAction() //
                .withAction(PROJECT_ENDORSE) //
                .withRaisesUrgentFlag() //
                .withPartnerAssignments(PROJECT_ENDORSER_GROUP) //
                .withTransitions(PROJECT_ENDORSE_TRANSITION));

        stateActions.add(new PrismStateAction() //
                .withAction(PROJECT_UNENDORSE) //
                .withPartnerAssignments(PROJECT_ENDORSER_GROUP) //
                .withTransitions(PROJECT_ENDORSE_TRANSITION));

        stateActions.add(new PrismStateAction() //
                .withAction(PROJECT_ENDORSE) //
                .withPartnerAssignments(PROJECT_ENDORSER_GROUP) //
                .withTransitions(PROJECT_ENDORSE_TRANSITION));

        stateActions.add(projectTerminateApproved()); //
        stateActions.add(projectViewEditApproved()); //
    }

}
