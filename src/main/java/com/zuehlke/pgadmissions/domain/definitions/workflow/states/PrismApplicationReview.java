package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_REVIEWERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_REVIEW_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_REVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_WITHDRAW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_TERMINATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_REVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_FEEDBACK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_ASSIGNED_REVIEWER_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_COMPLETE_STATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_ESCALATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_WITHDRAW_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationCommentVerification;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationEmailCreatorVerification;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationViewEditVerification;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationReview extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_ASSIGN_REVIEWERS) //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST) //
		        .withAssignments(APPLICATION_ADMINISTRATOR_GROUP) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withNotifications(APPLICATION_CREATOR, SYSTEM_APPLICATION_UPDATE_NOTIFICATION)
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_REVIEW) //
		                .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
		                .withTransitionEvaluation(APPLICATION_ASSIGNED_REVIEWER_OUTCOME) //
		                .withRoleTransitions(APPLICATION_CREATE_ADMINISTRATOR_GROUP), //
		                new PrismStateTransition() //
		                        .withTransitionState(APPLICATION_REVIEW_PENDING_FEEDBACK) //
		                        .withTransitionAction(APPLICATION_PROVIDE_REVIEW) //
		                        .withTransitionEvaluation(APPLICATION_ASSIGNED_REVIEWER_OUTCOME) //
		                        .withRoleTransitions(APPLICATION_CREATE_REVIEWER_GROUP))); //

		stateActions.add(applicationCommentReview()); //

		stateActions.add(applicationCompleteReviewStage()
		        .withTransitions(APPLICATION_COMPLETE_STATE_TRANSITION //
		                .withRoleTransitions(APPLICATION_DELETE_ADMINISTRATOR_GROUP))); //

		stateActions.add(applicationEmailCreatorReview()); //

		stateActions.add(applicationEscalateReview()); //

		stateActions.add(applicationViewEditReview(state)); //

		stateActions.add(applicationWithdrawReview());
	}

	public static PrismStateAction applicationCommentReview() {
		return applicationCommentVerification() //
		        .withAssignments(APPLICATION_ADMINISTRATOR);
	}

	public static PrismStateAction applicationCompleteReviewStage() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_COMPLETE_REVIEW_STAGE) //
		        .withAssignments(APPLICATION_ADMINISTRATOR_GROUP) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION);
	}

	public static PrismStateAction applicationEmailCreatorReview() {
		return applicationEmailCreatorVerification() //
		        .withAssignments(APPLICATION_ADMINISTRATOR);
	}

	public static PrismStateAction applicationEscalateReview() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_ESCALATE) //
		        .withNotifications(APPLICATION_CREATOR, APPLICATION_TERMINATE_NOTIFICATION) //
		        .withTransitions(APPLICATION_ESCALATE_TRANSITION //
		                .withRoleTransitionsAndStateTerminations( //
		                        Lists.newArrayList(APPLICATION_DELETE_ADMINISTRATOR_GROUP, //
		                                APPLICATION_DELETE_REFEREE_GROUP), //
		                        APPLICATION_TERMINATE_GROUP));
	}

	public static PrismStateAction applicationViewEditReview(PrismState state) {
		return applicationViewEditVerification(state) //
		        .withAssignments(APPLICATION_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER);
	}

	public static PrismStateAction applicationWithdrawReview() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_WITHDRAW) //
		        .withAssignments(APPLICATION_CREATOR) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withTransitions(APPLICATION_WITHDRAW_TRANSITION.withRoleTransitions( //
		                APPLICATION_DELETE_ADMINISTRATOR_GROUP, //
		                APPLICATION_DELETE_REFEREE_GROUP));
	}

}
