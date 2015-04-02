package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_MOVE_TO_DIFFERENT_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_REVERSE_REJECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_REVERSE_REJECTION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_EXHUME_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REJECTED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_REJECTED_EXPORT_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovedPendingExport.applicationViewEditApprovedPendingExport;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationCommentVerification;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationEmailCreatorVerification;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationRejectedPendingExport extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentVerification()); //

		stateActions.add(applicationEmailCreatorVerification()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_ESCALATE) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_REJECTED_COMPLETED) //
		                .withTransitionAction(APPLICATION_ESCALATE))); //

		stateActions.add(new PrismStateAction() //
		        .withAction(PrismAction.APPLICATION_EXPORT) //
		        .withTransitions(APPLICATION_REJECTED_EXPORT_TRANSITION)); //

		stateActions.add(applicationReverseRejection()); //

		stateActions.add(applicationViewEditApprovedPendingExport()); //
	}

	public static PrismStateAction applicationReverseRejection() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_REVERSE_REJECTION) //
		        .withAssignments(APPLICATION_ADMINISTRATOR_GROUP) //
		        .withAssignments(PROGRAM_APPROVER) //
		        .withNotifications(APPLICATION_CREATOR, APPLICATION_REVERSE_REJECTION_NOTIFICATION) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withNotifications(PROGRAM_APPROVER, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_REJECTED) //
		                .withTransitionAction(APPLICATION_MOVE_TO_DIFFERENT_STAGE) //
		                .withRoleTransitions(APPLICATION_EXHUME_REFEREE_GROUP));
	}

}
