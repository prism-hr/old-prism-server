package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_VALIDATION_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_REFERENCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_WITHDRAW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_ADMITTER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_VIEWER_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMITTER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_VIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_VIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_VALIDATION_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_COMPLETE_STATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_WITHDRAW_TRANSITION;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationValidation extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentValidation()); //

		stateActions.add(applicationCompleteValidation()); //

		stateActions.add(applicationEmailCreatorValidation()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_ESCALATE) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_VALIDATION_PENDING_COMPLETION) //
		                .withTransitionAction(APPLICATION_ESCALATE))); //

		stateActions.add(applicationViewEditValidation(state)); //

		stateActions.add(applicationWithdrawValidation());
	}

	public static PrismStateAction applicationCommentValidation() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_COMMENT) //
		        .withAssignments(APPLICATION_PARENT_VIEWER_GROUP) //
		        .withAssignments(APPLICATION_VIEWER_REFEREE)
		        .withNotifications(APPLICATION_PARENT_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION);
	}

	public static PrismStateAction applicationCompleteValidation() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_COMPLETE_VALIDATION_STAGE) //
		        .withAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
		        .withNotifications(APPLICATION_PARENT_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION)
		        .withTransitions(APPLICATION_COMPLETE_STATE_TRANSITION);
	}

	public static PrismStateAction applicationEmailCreatorValidation() {
		return new PrismStateAction() //
		        .withAction(PrismAction.APPLICATION_EMAIL_CREATOR) //
		        .withAssignments(APPLICATION_PARENT_VIEWER_GROUP) //
		        .withAssignments(APPLICATION_VIEWER_REFEREE);
	}

	public static PrismStateAction applicationViewEditValidation(PrismState state) {
		return new PrismStateAction() //
		        .withAction(APPLICATION_VIEW_EDIT) //
		        .withAssignments(INSTITUTION_ADMINISTRATOR, APPLICATION_VIEW_EDIT_AS_ADMITTER, APPLICATION_PROVIDE_REFERENCE) //
		        .withAssignments(INSTITUTION_ADMITTER, APPLICATION_VIEW_EDIT_AS_ADMITTER, APPLICATION_PROVIDE_REFERENCE) //
		        .withAssignments(PROGRAM_ADMINISTRATOR, APPLICATION_VIEW_EDIT_AS_RECRUITER, APPLICATION_PROVIDE_REFERENCE) //
		        .withAssignments(PROGRAM_APPROVER, APPLICATION_VIEW_AS_RECRUITER) //
		        .withAssignments(PROGRAM_VIEWER, APPLICATION_VIEW_AS_RECRUITER) //
		        .withAssignments(PROJECT_ADMINISTRATOR, APPLICATION_VIEW_EDIT_AS_RECRUITER, APPLICATION_PROVIDE_REFERENCE) //
		        .withAssignments(PROJECT_PRIMARY_SUPERVISOR, APPLICATION_VIEW_AS_RECRUITER) //
		        .withAssignments(APPLICATION_CREATOR, APPLICATION_VIEW_EDIT_AS_CREATOR) //
		        .withAssignments(APPLICATION_VIEWER_REFEREE, APPLICATION_VIEW_AS_REFEREE) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(state) //
		                .withTransitionAction(APPLICATION_VIEW_EDIT) //
		                .withRoleTransitions(APPLICATION_CREATE_REFEREE_GROUP, APPLICATION_DELETE_REFEREE_GROUP)); //
	}

	public static PrismStateAction applicationWithdrawValidation() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_WITHDRAW) //
		        .withAssignments(APPLICATION_CREATOR) //
		        .withNotifications(APPLICATION_PARENT_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withTransitions(APPLICATION_WITHDRAW_TRANSITION //
		                .withRoleTransitions(APPLICATION_DELETE_REFEREE_GROUP));
	}

}
