package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_COMPLETE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_UNSUBMITTED_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_COMPLETE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationWithdraw;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;

public class PrismApplicationUnsubmitted extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCompleteUnsubmitted()); //
		stateActions.add(applicationEscalate(APPLICATION_UNSUBMITTED_PENDING_COMPLETION)); //
		stateActions.add(applicationWithdraw());
	}

	public static PrismStateAction applicationCompleteUnsubmitted() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_COMPLETE) //
		        .withAssignments(APPLICATION_CREATOR, APPLICATION_VIEW_EDIT_AS_CREATOR) //
		        .withNotifications(APPLICATION_CREATOR, APPLICATION_COMPLETE_NOTIFICATION) //
		        .withTransitions(APPLICATION_COMPLETE_TRANSITION //
		                .withRoleTransitions(APPLICATION_CREATE_REFEREE_GROUP));
	}

}
