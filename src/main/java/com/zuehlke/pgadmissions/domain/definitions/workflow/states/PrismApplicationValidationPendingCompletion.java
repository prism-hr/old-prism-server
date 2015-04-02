package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_TERMINATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_ESCALATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationValidation.applicationCommentValidation;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationValidation.applicationCompleteValidation;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationValidation.applicationEmailCreatorValidation;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationValidation.applicationViewEditValidation;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationValidation.applicationWithdrawValidation;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;

public class PrismApplicationValidationPendingCompletion extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentValidation()); //

		stateActions.add(applicationCompleteValidation() //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST)); //

		stateActions.add(applicationEmailCreatorValidation()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_ESCALATE) //
		        .withNotifications(APPLICATION_CREATOR, APPLICATION_TERMINATE_NOTIFICATION) //
		        .withTransitions(APPLICATION_ESCALATE_TRANSITION.withRoleTransitions( //
		                APPLICATION_DELETE_REFEREE_GROUP))); //

		stateActions.add(applicationViewEditValidation(state));

		stateActions.add(applicationWithdrawValidation());
	}

}
