package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_REFERENCE_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_TERMINATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleTransitionGroup.APPLICATION_DELETE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleTransitionGroup.APPLICATION_PROVIDE_REFERENCE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PrismStateTerminationGroup.APPLICATION_TERMINATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PrismStateTransitionGroup.APPLICATION_ESCALATE_SUBMITTED_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReference.applicationCommentReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReference.applicationCompleteReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReference.applicationEmailCreatorReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReference.applicationProvideReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReference.applicationViewEditReference;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionNotification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationReferencePendingCompletion extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentReference()); //

		stateActions.add(applicationCompleteReference() //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST)); //

		stateActions.add(applicationEmailCreatorReference()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_ESCALATE) //
		        .withNotifications(new PrismStateActionNotification() //
		                .withRole(APPLICATION_CREATOR) //
		                .withDefinition(APPLICATION_TERMINATE_NOTIFICATION)) //
		        .withTransitions(APPLICATION_ESCALATE_SUBMITTED_TRANSITION.withRoleTransitionsAndStateTerminations( //
		                APPLICATION_DELETE_REFEREE_GROUP, APPLICATION_TERMINATE_GROUP))); //

		stateActions.add(applicationProvideReference()
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_REFERENCE_PENDING_COMPLETION) //
		                .withTransitionAction(APPLICATION_COMPLETE_REFERENCE_STAGE) //
		                .withRoleTransitions(APPLICATION_PROVIDE_REFERENCE_GROUP))); //

		stateActions.add(applicationViewEditReference()); //

		stateActions.add(PrismApplicationReference.applicationWithdrawReference());
	}

}
