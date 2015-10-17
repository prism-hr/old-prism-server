package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_COMPLETE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_COMPLETE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationTerminateAbstract;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdrawAbstract;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationUnsubmitted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_COMPLETE) //
                .withAssignments(APPLICATION_CREATOR, APPLICATION_VIEW_EDIT_AS_CREATOR) //
                .withNotifications(APPLICATION_CREATOR, APPLICATION_COMPLETE_NOTIFICATION) //
                .withTransitions(APPLICATION_COMPLETE_TRANSITION));

        stateActions.add(applicationEscalate(APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED));

        stateActions.add(applicationTerminateAbstract()
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED) //
                        .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST)));

        stateActions.add(applicationWithdrawAbstract()
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED) //
                        .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST)));
    }

}
