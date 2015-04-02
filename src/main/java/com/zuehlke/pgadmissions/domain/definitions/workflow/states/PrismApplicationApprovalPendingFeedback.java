package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_PRIMARY_SUPERVISION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_SECONDARY_SUPERVISION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_WITHDRAW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_SECONDARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CONFIRM_PRIMARY_SUPERVISION_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CONFIRM_SECONDARY_SUPERVISION_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_SUPERVISOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVAL_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_COMPLETE_STATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CONFIRM_SUPERVISION_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_WITHDRAW_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApproval.applicationCompleteApprovalStage;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationCommentReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationEmailCreatorReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationViewEditReview;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationApprovalPendingFeedback extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentReview()); //

		stateActions.add(applicationCompleteApprovalStagePendingFeedback()); //

		stateActions.add(applicationConfirmPrimarySupervision() //
		        .withTransitions(APPLICATION_CONFIRM_SUPERVISION_TRANSITION //
		                .withRoleTransitions(APPLICATION_CONFIRM_PRIMARY_SUPERVISION_GROUP)));

		stateActions.add(applicationConfirmSecondarySupervision() //
		        .withTransitions(APPLICATION_CONFIRM_SUPERVISION_TRANSITION //
		                .withRoleTransitions(APPLICATION_CONFIRM_SECONDARY_SUPERVISION_GROUP)));

		stateActions.add(applicationEmailCreatorReview()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_ESCALATE) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_APPROVAL_PENDING_COMPLETION) //
		                .withTransitionAction(APPLICATION_ESCALATE))); //

		stateActions.add(applicationViewEditApprovalPendingFeedback(state)); //

		stateActions.add(applicationWithdrawApprovalPendingFeedback());
	}

	public static PrismStateAction applicationCompleteApprovalStagePendingFeedback() {
		return applicationCompleteApprovalStage() //
		        .withTransitions(APPLICATION_COMPLETE_STATE_TRANSITION //
		                .withRoleTransitions(APPLICATION_DELETE_ADMINISTRATOR_GROUP, //
		                        APPLICATION_DELETE_SUPERVISOR_GROUP));
	}

	public static PrismStateAction applicationConfirmPrimarySupervision() {
	    return new PrismStateAction() //
		        .withAction(APPLICATION_CONFIRM_PRIMARY_SUPERVISION) //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST) //
		        .withAssignments(APPLICATION_PRIMARY_SUPERVISOR) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withNotifications(PROGRAM_APPROVER, SYSTEM_APPLICATION_UPDATE_NOTIFICATION);
    }
	
	public static PrismStateAction applicationConfirmSecondarySupervision() {
	    return new PrismStateAction() //
		        .withAction(APPLICATION_CONFIRM_SECONDARY_SUPERVISION) //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST) //
		        .withAssignments(APPLICATION_SECONDARY_SUPERVISOR) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withNotifications(PROGRAM_APPROVER, SYSTEM_APPLICATION_UPDATE_NOTIFICATION);
    }
	
	public static PrismStateAction applicationViewEditApprovalPendingFeedback(PrismState state) {
	    return applicationViewEditReview(state) //
		        .withAssignments(APPLICATION_PRIMARY_SUPERVISOR, APPLICATION_VIEW_AS_RECRUITER)
		        .withAssignments(APPLICATION_SECONDARY_SUPERVISOR, APPLICATION_VIEW_AS_RECRUITER);
    }

	public static PrismStateAction applicationWithdrawApprovalPendingFeedback() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_WITHDRAW) //
		        .withAssignments(APPLICATION_CREATOR) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withTransitions(APPLICATION_WITHDRAW_TRANSITION //
		                .withRoleTransitions(APPLICATION_DELETE_ADMINISTRATOR_GROUP, //
		                        APPLICATION_DELETE_REFEREE_GROUP, //
		                        APPLICATION_DELETE_SUPERVISOR_GROUP));
	}

}
