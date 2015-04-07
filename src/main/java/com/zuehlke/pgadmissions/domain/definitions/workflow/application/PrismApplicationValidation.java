package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_VALIDATION_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationComment;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCompleteState;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEdit;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdraw;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationValidation extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationComment()); //
		stateActions.add(applicationCompleteValidation()); //
		stateActions.add(applicationEmailCreator()); //
		stateActions.add(applicationEscalate(APPLICATION_VALIDATION_PENDING_COMPLETION)); //
		stateActions.add(applicationViewEdit(state)); //
		stateActions.add(applicationWithdrawValidation());
	}

	public static PrismStateAction applicationCompleteValidation() {
		return applicationCompleteState(APPLICATION_PARENT_ADMINISTRATOR_GROUP);
	}

	public static PrismStateAction applicationWithdrawValidation() {
		return applicationWithdraw(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_DELETE_REFEREE_GROUP);
	}

}
