package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationInterview.applicationCompleteInterviewScheduled;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationInterview.applicationProvideInterviewFeedback;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationInterview.applicationViewEditInterviewScheduled;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationInterview.applicationWithdrawInterviewScheduled;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationInterviewPendingFeedback extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentWithViewerRecruiterAndAdministrator()); //
		stateActions.add(applicationCompleteInterviewScheduled(state)); //
		stateActions.add(applicationEmailCreatorWithViewerRecruiterAndAdministrator()); //
		stateActions.add(applicationEscalate(APPLICATION_INTERVIEW_PENDING_COMPLETION)); //

		stateActions.add(applicationProvideInterviewFeedback() //
		        .withTransitions(APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_TRANSITION //
		                .withRoleTransitions(APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_GROUP)));

        stateActions.add(applicationUploadReference(state));
		stateActions.add(applicationViewEditInterviewScheduled(state)); //
		stateActions.add(applicationWithdrawInterviewScheduled());
	}

}
