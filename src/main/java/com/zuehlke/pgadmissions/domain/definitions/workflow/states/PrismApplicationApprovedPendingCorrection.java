package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CORRECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_CORRECT_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_PENDING_EXPORT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovedPendingExport.applicationEscalateApprovedPendingExport;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovedPendingExport.applicationViewEditApprovedPendingExport;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationCommentVerification;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationEmailCreatorVerification;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationApprovedPendingCorrection extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentVerification()); //

		stateActions.add(applicationCorrect(APPLICATION_APPROVED_PENDING_EXPORT)); //

		stateActions.add(applicationEmailCreatorVerification()); //

		stateActions.add(applicationEscalateApprovedPendingExport()); //

		stateActions.add(applicationViewEditApprovedPendingExport());
	}

	public static PrismStateAction applicationCorrect(PrismState state) {
		return new PrismStateAction() //
		        .withAction(APPLICATION_CORRECT) //
		        .withRaisesUrgentFlag() //
		        .withNotification(APPLICATION_CORRECT_REQUEST) //
		        .withAssignments(INSTITUTION_ADMINISTRATOR) //
		        .withNotifications(INSTITUTION_ADMINISTRATOR, SYSTEM_APPLICATION_UPDATE_NOTIFICATION)
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(state) //
		                .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST));
	}
}
