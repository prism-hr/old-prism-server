package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_COMPLETED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationUnsubmitted.applicationCompleteUnsubmitted;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationTerminateUnsubmitted;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdrawUnsubmitted;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationUnsubmittedPendingCompletion extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCompleteUnsubmitted() //
                .withRaisesUrgentFlag() //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_UNSUBMITTED) //
                        .withTransitionAction(APPLICATION_COMPLETE) //
                        .withTransitionEvaluation(APPLICATION_COMPLETED_OUTCOME))); //

        stateActions.add(applicationEscalate(APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED)); //
        stateActions.add(applicationTerminateUnsubmitted());
        stateActions.add(applicationWithdrawUnsubmitted());
    }

}
