package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_SUPERVISORS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_APPROVAL_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_PRIMARY_SUPERVISION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_SUPERVISOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVAL_PENDING_FEEDBACK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_ASSIGNED_SUPERVISOR_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_COMPLETE_STATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationCommentReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationEmailCreatorReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationEscalateReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationViewEditReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationWithdrawReview;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationApproval extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_ASSIGN_SUPERVISORS) //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST) //
		        .withAssignments(APPLICATION_ADMINISTRATOR_GROUP) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withNotifications(PROGRAM_APPROVER, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_APPROVAL) //
		                .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
		                .withTransitionEvaluation(APPLICATION_ASSIGNED_SUPERVISOR_OUTCOME) //
		                .withRoleTransitions(APPLICATION_CREATE_ADMINISTRATOR_GROUP),
		                new PrismStateTransition() //
		                        .withTransitionState(APPLICATION_APPROVAL_PENDING_FEEDBACK) //
		                        .withTransitionAction(APPLICATION_CONFIRM_PRIMARY_SUPERVISION) //
		                        .withTransitionEvaluation(APPLICATION_ASSIGNED_SUPERVISOR_OUTCOME) //
		                        .withRoleTransitions(APPLICATION_CREATE_SUPERVISOR_GROUP))); //

		stateActions.add(applicationCommentReview()); //

		stateActions.add(applicationEmailCreatorReview()); //

		stateActions.add(applicationEscalateReview()); //

		stateActions.add(applicationCompleteApprovalStage() //
		        .withTransitions(APPLICATION_COMPLETE_STATE_TRANSITION //
		                .withRoleTransitions(APPLICATION_DELETE_ADMINISTRATOR_GROUP))); //

		stateActions.add(applicationViewEditReview(state)); //

		stateActions.add(applicationWithdrawReview());
	}

	public static PrismStateAction applicationCompleteApprovalStage() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_COMPLETE_APPROVAL_STAGE) //
		        .withAssignments(APPLICATION_ADMINISTRATOR_GROUP) //
		        .withAssignments(PROGRAM_APPROVER) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION);
	}

}
