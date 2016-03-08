package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationInterview.applicationCompleteInterviewScheduled;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationInterview.applicationProvideInterviewFeedback;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationInterview.applicationViewEditInterviewScheduled;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationInterview.applicationWithdrawInterviewScheduled;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationInterviewPendingFeedback extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentWithViewerRecruiter()); //
        stateActions.add(applicationCompleteInterviewScheduled(state)); //
        stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
        stateActions.add(applicationEscalate(APPLICATION_INTERVIEW_PENDING_COMPLETION)); //

        stateActions.add(applicationProvideInterviewFeedback() //
                .withStateTransitions(APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_TRANSITION //
                        .withRoleTransitions(APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_GROUP)));

        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditInterviewScheduled(state)); //
        stateActions.add(applicationWithdrawInterviewScheduled());
    }

}
