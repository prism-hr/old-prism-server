package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_INTERVIEW_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_COMPLETE_INTERVIEW_STAGE_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationInterview.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.*;

public class PrismApplicationInterviewPendingCompletion extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentViewerRefereeViewerRecruiter()); //

        stateActions.add(applicationCompleteInterviewScheduled(state) //
                .withRaisesUrgentFlag() //
                .withNotificationDefinition(APPLICATION_COMPLETE_INTERVIEW_STAGE_REQUEST)); //

        stateActions.add(applicationSendMessageInterviewFeedback()); //
        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP,
                APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP));

        stateActions.add(applicationProvideInterviewFeedback() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_INTERVIEW_PENDING_COMPLETION) //
                        .withTransitionAction(APPLICATION_COMPLETE_INTERVIEW_STAGE) //
                        .withRoleTransitions(APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_GROUP)));

        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditInterviewScheduled(state)); //
        stateActions.add(applicationWithdrawInterviewScheduled());
    }

}
