package com.zuehlke.pgadmissions.domain.definitions.workflow.institution;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_STARTUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_STARTUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_STARTUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.INSTITUTION_STARTUP_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionViewEditUnapproved;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismInstitutionApproved extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(institutionEmailCreator()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(INSTITUTION_STARTUP) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(INSTITUTION_APPROVED_COMPLETED) //
		                .withTransitionAction(INSTITUTION_STARTUP) //
		                .withPropagatedActions(PROGRAM_STARTUP, PROJECT_STARTUP)) //
		        .withNotifications(INSTITUTION_ADMINISTRATOR, INSTITUTION_STARTUP_NOTIFICATION)); //

		stateActions.add(institutionViewEditUnapproved()); //
	}

}
