package com.zuehlke.pgadmissions.domain.definitions.workflow.project;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_STARTUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.PROJECT_STARTUP_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectEscalateUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectViewEditUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectWithdraw;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProjectApprovalPartnerInstitution extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(projectEscalateUnapproved()); //

        stateActions.add(new PrismStateAction() //
                .withAction(PROJECT_STARTUP) //
                .withNotifications(PROJECT_ADMINISTRATOR, PROJECT_STARTUP_NOTIFICATION) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_APPROVAL) //
                        .withTransitionAction(PROJECT_STARTUP)));
        
        stateActions.add(projectViewEditUnapproved()); //
        stateActions.add(projectWithdraw());
    }

}