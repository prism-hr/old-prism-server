package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_REVIEW_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_TERMINATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_REVIEW_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_ESCALATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationCommentReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationEmailCreatorReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReviewPendingFeedback.applicationCompleteReviewStagePendingFeedback;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReviewPendingFeedback.applicationProvideReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReviewPendingFeedback.applicationViewEditReviewPendingFeedback;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReviewPendingFeedback.applicationWithdrawReviewPendingFeedback;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationReviewPendingCompletion extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentReview()); //

		stateActions.add(applicationCompleteReviewStagePendingFeedback() //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST)); //

		stateActions.add(applicationEmailCreatorReview()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_ESCALATE) //
		        .withNotifications(APPLICATION_CREATOR, APPLICATION_TERMINATE_NOTIFICATION) //
		        .withTransitions(APPLICATION_ESCALATE_TRANSITION //
		                .withRoleTransitionsAndStateTerminations(Lists.newArrayList(
		                        APPLICATION_DELETE_ADMINISTRATOR_GROUP, //
		                        APPLICATION_DELETE_REVIEWER_GROUP, //
		                        APPLICATION_DELETE_REFEREE_GROUP), //
		                        APPLICATION_TERMINATE_GROUP))); //

		stateActions.add(applicationProvideReview() //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_REVIEW_PENDING_COMPLETION) //
		                .withTransitionAction(APPLICATION_COMPLETE_REVIEW_STAGE) //
		                .withRoleTransitions(APPLICATION_PROVIDE_REVIEW_GROUP))); //

		stateActions.add(applicationViewEditReviewPendingFeedback(state)); //

		stateActions.add(applicationWithdrawReviewPendingFeedback());
	}

}
