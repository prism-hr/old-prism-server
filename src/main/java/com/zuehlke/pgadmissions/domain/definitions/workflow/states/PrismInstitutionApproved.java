package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_STARTUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.INSTITUTION_STARTUP_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismInstitutionApproval.institutionEmailCreatorApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismInstitutionApproval.institutionViewEditApproval;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionNotification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismInstitutionApproved extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(institutionEmailCreatorApproved()); //

		stateActions.add(institutionViewEditApproved()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(INSTITUTION_STARTUP) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(INSTITUTION_APPROVED_COMPLETED) //
		                .withTransitionAction(INSTITUTION_STARTUP))
		        .withNotifications(new PrismStateActionNotification() //
		                .withRole(INSTITUTION_ADMINISTRATOR) //
		                .withDefinition(INSTITUTION_STARTUP_NOTIFICATION))); //
	}

	public static PrismStateAction institutionEmailCreatorApproved() {
		return institutionEmailCreatorApproval();
	}

	public static PrismStateAction institutionViewEditApproved() {
		return institutionViewEditApproval();
	}

}
