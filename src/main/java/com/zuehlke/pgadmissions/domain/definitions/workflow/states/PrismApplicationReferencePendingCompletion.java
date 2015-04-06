package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_REFERENCE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReference.applicationCompleteReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReference.applicationProvideReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReference.applicationViewEditReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReference.applicationWithdrawReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationEscalate;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationReferencePendingCompletion extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentWithViewerRecruiter()); //

		stateActions.add(applicationCompleteReference() //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST));

		stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
		stateActions.add(applicationEscalate(APPLICATION_DELETE_REFEREE_GROUP));

		stateActions.add(applicationProvideReference()
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_REFERENCE_PENDING_COMPLETION) //
		                .withTransitionAction(APPLICATION_COMPLETE_STAGE) //
		                .withRoleTransitions(APPLICATION_PROVIDE_REFERENCE_GROUP))); //

		stateActions.add(applicationViewEditReference(state)); //
		stateActions.add(applicationWithdrawReference());
	}

}
