package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_COMPLETE_NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;

import uk.co.alumeni.prism.domain.definitions.workflow.*;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationUnsubmitted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.APPLICATION_COMPLETE) //
                .withAssignments(PrismRole.APPLICATION_CREATOR, PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_CREATOR) //
                .withNotifications(PrismRole.APPLICATION_CREATOR, APPLICATION_COMPLETE_NOTIFICATION) //
                .withStateTransitions(PrismStateTransitionGroup.APPLICATION_COMPLETE_TRANSITION));

        stateActions.add(PrismApplicationWorkflow.applicationEscalate(PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED));

        stateActions.add(PrismApplicationWorkflow.applicationTerminateAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST)));

        stateActions.add(PrismApplicationWorkflow.applicationWithdrawAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST)));
    }

}
