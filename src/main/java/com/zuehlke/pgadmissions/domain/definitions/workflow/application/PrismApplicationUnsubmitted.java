package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_COMPLETE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_UNSUBMITTED_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_COMPLETED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_COMPLETE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationTerminateUnsubmitted;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdrawUnsubmitted;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationUnsubmitted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCompleteUnsubmitted() //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_UNSUBMITTED) //
                        .withTransitionAction(APPLICATION_COMPLETE) //
                        .withTransitionEvaluation(APPLICATION_COMPLETED_OUTCOME))); //
        stateActions.add(applicationEscalate(APPLICATION_UNSUBMITTED_PENDING_COMPLETION)); //
        stateActions.add(applicationTerminateUnsubmitted());
        stateActions.add(applicationWithdrawUnsubmitted());
    }

    public static PrismStateAction applicationCompleteUnsubmitted() {
        return new PrismStateAction() //
                .withAction(APPLICATION_COMPLETE) //
                .withAssignments(APPLICATION_CREATOR, APPLICATION_VIEW_EDIT_AS_CREATOR) //
                .withNotifications(APPLICATION_CREATOR, APPLICATION_COMPLETE_NOTIFICATION) //
                .withTransitions(APPLICATION_COMPLETE_TRANSITION);
    }

}
