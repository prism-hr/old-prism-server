package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationInterview.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.*;

public class PrismApplicationInterviewPendingFeedback extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentViewerRefereeViewerRecruiter()); //
        stateActions.add(applicationCompleteInterviewScheduled(state)); //
        stateActions.add(applicationSendMessageInterviewFeedback()); //
        stateActions.add(applicationEscalate(APPLICATION_INTERVIEW_PENDING_COMPLETION)); //

        stateActions.add(applicationProvideInterviewFeedback() //
                .withStateTransitions(APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_TRANSITION //
                        .withRoleTransitions(APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_GROUP)));

        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditInterviewScheduled(state)); //
        stateActions.add(applicationWithdrawInterviewScheduled());
    }

}
