package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_CREATE_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_CREATE_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_TERMINATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.PROGRAM_VIEW_EDIT_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_PROGRAM_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROGRAM_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_CREATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.PROGRAM_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.PROGRAM_MANAGE_USERS_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_UNSUBMITTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_DISABLED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROGRAM_UPDATED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_ESCALATE_APPROVED_TRANSITION_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_VIEW_EDIT_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_CREATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectApproval.projectEmailCreatorApproval;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismProgramApproved extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(projectEmailCreatorApproval()); //

		stateActions.add(programViewEditApproved()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(PROGRAM_CREATE_APPLICATION) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_UNSUBMITTED) //
		                .withTransitionAction(APPLICATION_COMPLETE) //
		                .withRoleTransitions(APPLICATION_CREATE_CREATOR_GROUP))); //

		stateActions.add(programCreateProjectApproved()); //

		stateActions.add(programEscalateApproved()); //
	}

	public static PrismStateAction programCreateProjectApproved() {
		return new PrismStateAction() //
		        .withAction(PROGRAM_CREATE_PROJECT) //
		        .withTransitions(PROJECT_CREATE_TRANSITION //
		                .withRoleTransitions(PROGRAM_CREATE_ADMINISTRATOR_GROUP));
	}

	public static PrismStateAction programEscalateApproved() {
		return new PrismStateAction() //
		        .withAction(PROGRAM_ESCALATE) //
		        .withNotifications(PROGRAM_ADMINISTRATOR_GROUP, SYSTEM_PROGRAM_UPDATE_NOTIFICATION)
		        .withTransitions(PROGRAM_ESCALATE_APPROVED_TRANSITION_GROUP);
	}

	public static PrismStateAction programViewEditApproved() {
		return new PrismStateAction() //
		        .withAction(PROGRAM_VIEW_EDIT) //
		        .withActionEnhancement(PROGRAM_VIEW_EDIT_AS_USER) //
		        .withAssignments(PROGRAM_ADMINISTRATOR_GROUP) //
		        .withNotifications(PROGRAM_ADMINISTRATOR_GROUP, SYSTEM_PROGRAM_UPDATE_NOTIFICATION) //
		        .withTransitions(PROGRAM_VIEW_EDIT_TRANSITION //
		                .withRoleTransitions(PROGRAM_MANAGE_USERS_GROUP)) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(PROGRAM_DISABLED_COMPLETED) //
		                .withTransitionAction(PROGRAM_VIEW_EDIT) //
		                .withTransitionEvaluation(PROGRAM_UPDATED_OUTCOME) //
		                .withPropagatedActions(PROJECT_TERMINATE));
	}

}
