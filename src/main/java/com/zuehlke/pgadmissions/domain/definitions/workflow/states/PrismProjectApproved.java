package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_CREATE_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_SUSPEND;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_TERMINATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.PROJECT_VIEW_EDIT_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_PROJECT_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleGroup.PROJECT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleTransitionGroup.APPLICATION_CREATE_CREATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleTransitionGroup.PROJECT_MANAGE_USERS_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_UNSUBMITTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_DISABLED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_DISABLED_PENDING_REACTIVATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PrismStateTransitionGroup.PROJECT_VIEW_EDIT_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROJECT_UPDATED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectApproval.projectEmailCreatorApproval;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismProjectApproved extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(new PrismStateAction() //
		        .withAction(PROJECT_CREATE_APPLICATION) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_UNSUBMITTED) //
		                .withTransitionAction(APPLICATION_COMPLETE) //
		                .withRoleTransitions(APPLICATION_CREATE_CREATOR_GROUP))); //

		stateActions.add(projectEmailCreatorApproval()); //

		stateActions.add(projectEscalateApproved()); //

		stateActions.add(projectSuspendApproved()); //

		stateActions.add(projectTerminateApproved()); //

		stateActions.add(projectViewEditApproved()); //
	}

	public static PrismStateAction projectEscalateApproved() {
		return new PrismStateAction() //
		        .withAction(PROJECT_ESCALATE) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(PROJECT_DISABLED_COMPLETED) //
		                .withTransitionAction(PROJECT_ESCALATE));
	}

	public static PrismStateAction projectSuspendApproved() {
		return new PrismStateAction() //
		        .withAction(PROJECT_SUSPEND) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(PROJECT_DISABLED_PENDING_REACTIVATION) //
		                .withTransitionAction(PROJECT_SUSPEND));
	}

	public static PrismStateAction projectTerminateApproved() {
		return new PrismStateAction() //
		        .withAction(PROJECT_TERMINATE) //
		        .withNotifications(PROJECT_ADMINISTRATOR_GROUP, SYSTEM_PROJECT_UPDATE_NOTIFICATION) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(PROJECT_DISABLED_COMPLETED) //
		                .withTransitionAction(PROJECT_TERMINATE));
	}

	public static PrismStateAction projectViewEditApproved() {
		return new PrismStateAction() //
		        .withAction(PROJECT_VIEW_EDIT) //
		        .withActionEnhancement(PROJECT_VIEW_EDIT_AS_USER) //
		        .withAssignments(PROJECT_ADMINISTRATOR_GROUP) //
		        .withTransitions(PROJECT_VIEW_EDIT_TRANSITION //
		                .withRoleTransitions(PROJECT_MANAGE_USERS_GROUP))
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(PROJECT_DISABLED_COMPLETED) //
		                .withTransitionAction(PROJECT_VIEW_EDIT) //
		                .withTransitionEvaluation(PROJECT_UPDATED_OUTCOME));
	}

}
