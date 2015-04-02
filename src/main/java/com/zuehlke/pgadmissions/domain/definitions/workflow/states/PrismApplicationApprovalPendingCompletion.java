package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_APPROVAL_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_TERMINATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CONFIRM_PRIMARY_SUPERVISION_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CONFIRM_SECONDARY_SUPERVISION_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_SUPERVISOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_ESCALATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovalPendingFeedback.applicationCompleteApprovalStagePendingFeedback;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovalPendingFeedback.applicationConfirmPrimarySupervision;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovalPendingFeedback.applicationConfirmSecondarySupervision;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovalPendingFeedback.applicationViewEditApprovalPendingFeedback;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovalPendingFeedback.applicationWithdrawApprovalPendingFeedback;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationCommentReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationEmailCreatorReview;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationApprovalPendingCompletion extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentReview()); //

		stateActions.add(applicationCompleteApprovalStagePendingFeedback() //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST)); //

		stateActions.add(applicationConfirmPrimarySupervision() //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(state) //
		                .withTransitionAction(APPLICATION_COMPLETE_APPROVAL_STAGE) //
		                .withRoleTransitions(APPLICATION_CONFIRM_PRIMARY_SUPERVISION_GROUP))); //

		stateActions.add(applicationConfirmSecondarySupervision() //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(state) //
		                .withTransitionAction(APPLICATION_COMPLETE_APPROVAL_STAGE) //
		                .withRoleTransitions(APPLICATION_CONFIRM_SECONDARY_SUPERVISION_GROUP))); //

		stateActions.add(applicationEmailCreatorReview()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_ESCALATE) //
		        .withNotifications(APPLICATION_CREATOR, APPLICATION_TERMINATE_NOTIFICATION) //
		        .withTransitions(APPLICATION_ESCALATE_TRANSITION //
		                .withRoleTransitionsAndStateTerminations( //
		                        Lists.newArrayList(APPLICATION_DELETE_ADMINISTRATOR_GROUP, //
		                                APPLICATION_DELETE_REFEREE_GROUP,
		                                APPLICATION_DELETE_SUPERVISOR_GROUP), //
		                        APPLICATION_TERMINATE_GROUP)));

		stateActions.add(applicationViewEditApprovalPendingFeedback(state)); //

		stateActions.add(applicationWithdrawApprovalPendingFeedback());
	}
}
