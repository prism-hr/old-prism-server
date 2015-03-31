package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_REVIEW_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_REVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_WITHDRAW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_REVIEW_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_COMPLETE_STATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_PROVIDE_REVIEW_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_WITHDRAW_SUBMITTED_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationCommentReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationEmailCreatorReview;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionAssignment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationReviewPendingFeedback extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentReview()); //

		stateActions.add(applicationCompleteReviewStage());

		stateActions.add(applicationEmailCreatorReview()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_ESCALATE) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_REVIEW_PENDING_COMPLETION) //
		                .withTransitionAction(APPLICATION_ESCALATE))); //

		stateActions.add(applicationProvideReviewReview()
		        .withTransitions(APPLICATION_PROVIDE_REVIEW_TRANSITION //
		                .withRoleTransitions(APPLICATION_PROVIDE_REVIEW_GROUP)));

		stateActions.add(applicationViewEditReviewPendingFeedback(state)); //

		stateActions.add(applicationWithdrawReviewPendingFeedback());
	}

	public static PrismStateAction applicationCompleteReviewStage() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_COMPLETE_REVIEW_STAGE) //
		        .withAssignments(APPLICATION_ADMINISTRATOR_GROUP) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withTransitions(APPLICATION_COMPLETE_STATE_TRANSITION //
		                .withRoleTransitions(APPLICATION_DELETE_ADMINISTRATOR_GROUP, APPLICATION_DELETE_REVIEWER_GROUP));
	}

	public static PrismStateAction applicationProvideReviewReview() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_PROVIDE_REVIEW) //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST) //
		        .withAssignments(APPLICATION_REVIEWER) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION);
	}

	public static PrismStateAction applicationViewEditReviewPendingFeedback(PrismState state) {
		return PrismApplicationVerification.applicationViewEditVerification(state) //
		        .withAssignments(new PrismStateActionAssignment() //
		                .withRole(APPLICATION_REVIEWER) //
		                .withActionEnhancement(APPLICATION_VIEW_AS_RECRUITER));
	}

	public static PrismStateAction applicationWithdrawReviewPendingFeedback() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_WITHDRAW) //
		        .withAssignments(APPLICATION_CREATOR) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withTransitions(APPLICATION_WITHDRAW_SUBMITTED_TRANSITION //
		                .withRoleTransitions(APPLICATION_DELETE_ADMINISTRATOR_GROUP, APPLICATION_DELETE_REVIEWER_GROUP, //
		                        APPLICATION_DELETE_REFEREE_GROUP));
	}

}
