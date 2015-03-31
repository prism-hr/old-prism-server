package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_REFERENCE_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_REFERENCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_PROVIDE_REFERENCE_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleTransitionGroup.APPLICATION_PROVIDE_REFERENCE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleTransitionGroup.APPLICATION_REVIVE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PrismStateTransitionGroup.APPLICATION_COMPLETE_STATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTerminationEvaluation.APPLICATION_REFERENCED_TERMINATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_PROVIDED_REFERENCE_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationCommentVerification;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationEmailCreatorVerification;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationViewEditVerification;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationWithdrawVerification;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionNotification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTermination;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationReference extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentReference()); //

		stateActions.add(applicationCompleteReference()); //

		stateActions.add(applicationEmailCreatorReference()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(PrismAction.APPLICATION_ESCALATE) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_REFERENCE_PENDING_COMPLETION) //
		                .withTransitionAction(APPLICATION_ESCALATE))); //

		stateActions.add(applicationProvideReference() //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_REFERENCE) //
		                .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
		                .withTransitionEvaluation(APPLICATION_PROVIDED_REFERENCE_OUTCOME) //
		                .withRoleTransitions(APPLICATION_PROVIDE_REFERENCE_GROUP),
		                new PrismStateTransition() //
		                        .withTransitionState(APPLICATION_REFERENCE_PENDING_COMPLETION) //
		                        .withTransitionAction(APPLICATION_COMPLETE_REFERENCE_STAGE) //
		                        .withTransitionEvaluation(APPLICATION_PROVIDED_REFERENCE_OUTCOME) //
		                        .withRoleTransitions(APPLICATION_PROVIDE_REFERENCE_GROUP),
		                new PrismStateTransition() //
		                        .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
		                        .withTransitionEvaluation(APPLICATION_PROVIDED_REFERENCE_OUTCOME) //
		                        .withRoleTransitions(APPLICATION_PROVIDE_REFERENCE_GROUP) //
		                        .withStateTerminations(new PrismStateTermination() //
		                                .withTerminationState(APPLICATION_REFERENCE) //
		                                .withStateTerminationEvaluation(APPLICATION_REFERENCED_TERMINATION)))); //

		stateActions.add(applicationViewEditReference()); //

		stateActions.add(applicationWithdrawReference());
	}

	public static PrismStateAction applicationCommentReference() {
		return applicationCommentVerification();
	}

	public static PrismStateAction applicationCompleteReference() {
		return new PrismStateAction() //
		        .withAction(PrismAction.APPLICATION_COMPLETE_REFERENCE_STAGE) //
		        .withAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
		        .withNotifications(APPLICATION_PARENT_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withTransitions(APPLICATION_COMPLETE_STATE_TRANSITION.withRoleTransitions( //
		                APPLICATION_REVIVE_REFEREE_GROUP));
	}

	public static PrismStateAction applicationEmailCreatorReference() {
		return applicationEmailCreatorVerification();
	}

	public static PrismStateAction applicationProvideReference() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_PROVIDE_REFERENCE) //
		        .withRaisesUrgentFlag() //
		        .withNotification(APPLICATION_PROVIDE_REFERENCE_REQUEST) //
		        .withAssignments(APPLICATION_REFEREE) //
		        .withNotifications(APPLICATION_PARENT_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withNotifications(new PrismStateActionNotification() //
		                .withRole(APPLICATION_CREATOR) //
		                .withDefinition(SYSTEM_APPLICATION_UPDATE_NOTIFICATION));
	}

	public static PrismStateAction applicationViewEditReference() {
		return applicationViewEditVerification();
	}

	public static PrismStateAction applicationWithdrawReference() {
		return applicationWithdrawVerification();
	}

}
