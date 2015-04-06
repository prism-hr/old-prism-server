package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationValidation.applicationCompleteValidation;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationValidation.applicationWithdrawValidation;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationComment;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationViewEdit;

public class PrismApplicationValidationPendingCompletion extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationComment()); //

		stateActions.add(applicationCompleteValidation() //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST)); //

		stateActions.add(applicationEmailCreator()); //
		stateActions.add(applicationEscalate(APPLICATION_DELETE_REFEREE_GROUP)); //
		stateActions.add(applicationViewEdit(state));
		stateActions.add(applicationWithdrawValidation());
	}

}
