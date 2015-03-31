package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_COMPLETE_APPROVAL_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_EMAIL_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_SUSPEND;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_TERMINATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_WITHDRAW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROJECT_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.PROJECT_VIEW_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.PROJECT_COMPLETE_APPROVAL_STAGE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_PROJECT_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_PROJECT_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVAL_PENDING_CORRECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_WITHDRAWN;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_APPROVE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROJECT_APPROVED_OUTCOME;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionNotification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismProjectApproval extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(projectCompleteApprovalStage() //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_PROJECT_TASK_REQUEST) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(PROJECT_APPROVAL_PENDING_CORRECTION) //
		                .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST) //
		                .withTransitionEvaluation(PROJECT_APPROVED_OUTCOME)));

		stateActions.add(projectEmailCreatorApproval());

		stateActions.add(projectEscalateApproval());

		stateActions.add(projectSuspendApproval());

		stateActions.add(projectTerminateApproval());

		stateActions.add(projectViewEditApproval());

		stateActions.add(projectWithdrawApproval());
	}

	public static PrismStateAction projectCompleteApprovalStage() {
		return new PrismStateAction() //
		        .withAction(PROJECT_COMPLETE_APPROVAL_STAGE) //
		        .withAssignments(PROJECT_PARENT_ADMINISTRATOR_GROUP) //
		        .withNotifications(PROJECT_PARENT_ADMINISTRATOR_GROUP, SYSTEM_PROJECT_UPDATE_NOTIFICATION) //
		        .withNotifications(new PrismStateActionNotification() //
		                .withRole(PROJECT_ADMINISTRATOR) //
		                .withDefinition(PROJECT_COMPLETE_APPROVAL_STAGE_NOTIFICATION)) //
		        .withTransitions(PROJECT_APPROVE_TRANSITION);
	}

	public static PrismStateAction projectEmailCreatorApproval() {
		return new PrismStateAction() //
		        .withAction(PROJECT_EMAIL_CREATOR) //
		        .withAssignments(PROJECT_PARENT_ADMINISTRATOR_GROUP);
	}

	public static PrismStateAction projectEscalateApproval() {
		return new PrismStateAction() //
		        .withAction(PROJECT_ESCALATE) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(PROJECT_REJECTED) //
		                .withTransitionAction(PROJECT_ESCALATE));
	}

	public static PrismStateAction projectSuspendApproval() {
		return new PrismStateAction() //
		        .withAction(PROJECT_SUSPEND) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(PROJECT_REJECTED) //
		                .withTransitionAction(PROJECT_SUSPEND));
	}

	public static PrismStateAction projectTerminateApproval() {
		return new PrismStateAction() //
		        .withAction(PROJECT_TERMINATE) //
		        .withNotifications(PROJECT_ADMINISTRATOR_GROUP, SYSTEM_PROJECT_UPDATE_NOTIFICATION) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(PROJECT_REJECTED) //
		                .withTransitionAction(PROJECT_TERMINATE));
	}

	public static PrismStateAction projectViewEditApproval() {
		return new PrismStateAction() //
		        .withAction(PROJECT_VIEW_EDIT) //
		        .withActionEnhancement(PROJECT_VIEW_AS_USER) //
		        .withAssignments(PROJECT_ADMINISTRATOR_GROUP);
	}

	public static PrismStateAction projectWithdrawApproval() {
		return new PrismStateAction() //
		        .withAction(PROJECT_WITHDRAW) //
		        .withAssignments(PROJECT_ADMINISTRATOR)
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(PROJECT_WITHDRAWN) //
		                .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST));
	}

}
