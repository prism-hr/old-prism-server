package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_INTERVIEW_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationInterview.applicationCompleteInterviewScheduled;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationInterview.applicationProvideInterviewFeedback;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationInterview.applicationTerminateInterviewScheduled;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationInterview.applicationViewEditInterviewScheduled;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationInterview.applicationWithdrawInterviewScheduled;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationInterviewPendingCompletion extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentWithViewerRecruiter()); //

        stateActions.add(applicationCompleteInterviewScheduled(state) //
                .withRaisesUrgentFlag()); //

        stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
        stateActions.add(PrismApplicationWorkflow.applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP,
                APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP));

        stateActions.add(applicationProvideInterviewFeedback() //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_INTERVIEW_PENDING_COMPLETION) //
                        .withTransitionAction(APPLICATION_COMPLETE_INTERVIEW_STAGE) //
                        .withRoleTransitions(APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_GROUP)));

        stateActions.add(applicationTerminateInterviewScheduled());
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditInterviewScheduled(state)); //
        stateActions.add(applicationWithdrawInterviewScheduled());
    }

}
