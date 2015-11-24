package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;

import uk.co.alumeni.prism.domain.definitions.workflow.*;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationInterviewPendingAvailability extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationCommentWithViewerRecruiter()); //
        stateActions.add(PrismApplicationInterview.applicationCompleteInterviewScheduling(state));
        stateActions.add(PrismApplicationInterview.applicationConfirmInterviewArrangements()); //
        stateActions.add(PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter()); //
        stateActions.add(PrismApplicationWorkflow.applicationEscalate(PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING)); //

        stateActions.add(PrismApplicationInterview.applicationProvideInterviewAvailability() //
                .withStateTransitions(PrismStateTransitionGroup.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_TRANSITION //
                        .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_GROUP))); //

        stateActions.add(PrismApplicationInterview.applicationTerminateInterviewScheduling());

        stateActions.add(PrismApplicationInterview.applicationUpdateInterviewAvailability(PrismRoleGroup.APPLICATION_SCHEDULED_INTERVIEW_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST)));

        stateActions.add(PrismApplicationWorkflow.applicationUploadReference(state));
        stateActions.add(PrismApplicationInterview.applicationViewEditInterviewScheduling(state)); //
        stateActions.add(PrismApplicationInterview.applicationWithdrawInterviewScheduling());
    }

}
