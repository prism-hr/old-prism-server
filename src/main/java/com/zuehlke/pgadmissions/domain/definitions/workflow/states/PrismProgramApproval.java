package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_COMPLETE_APPROVAL_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_EMAIL_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_WITHDRAW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROGRAM_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.PROGRAM_VIEW_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_PROGRAM_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_PROGRAM_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROGRAM_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL_PENDING_CORRECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_WITHDRAWN;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROGRAM_APPROVED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_APPROVE_TRANSITION;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismProgramApproval extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(projectCompleteApprovalStage()
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_PROGRAM_TASK_REQUEST) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(PROGRAM_APPROVAL_PENDING_CORRECTION) //
		                .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST) //
		                .withTransitionEvaluation(PROGRAM_APPROVED_OUTCOME))); //

		stateActions.add(programEmailCreatorApproval()); //

		stateActions.add(programEscalateApproval()); //

		stateActions.add(programViewEditApproval()); //

		stateActions.add(programWithdrawApproval());
	}

	public static PrismStateAction projectCompleteApprovalStage() {
		return new PrismStateAction() //
		        .withAction(PROGRAM_COMPLETE_APPROVAL_STAGE) //
		        .withAssignments(PROGRAM_PARENT_ADMINISTRATOR_GROUP) //
		        .withNotifications(PROGRAM_PARENT_ADMINISTRATOR_GROUP, SYSTEM_PROGRAM_UPDATE_NOTIFICATION) //
		        .withNotifications(PROGRAM_ADMINISTRATOR, PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION) //
		        .withTransitions(PROGRAM_APPROVE_TRANSITION);
	}

	public static PrismStateAction programEmailCreatorApproval() {
		return new PrismStateAction() //
		        .withAction(PROGRAM_EMAIL_CREATOR) //
		        .withAssignments(PROGRAM_PARENT_ADMINISTRATOR_GROUP);
	}

	public static PrismStateAction programEscalateApproval() {
		return new PrismStateAction() //
		        .withAction(PROGRAM_ESCALATE) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(PROGRAM_REJECTED) //
		                .withTransitionAction(PROGRAM_ESCALATE));
	}

	public static PrismStateAction programViewEditApproval() {
		return new PrismStateAction() //
		        .withAction(PROGRAM_VIEW_EDIT) //
		        .withActionEnhancement(PROGRAM_VIEW_AS_USER) //
		        .withAssignments(PROGRAM_PARENT_ADMINISTRATOR_GROUP);
	}

	public static PrismStateAction programWithdrawApproval() {
		return new PrismStateAction() //
		        .withAction(PROGRAM_WITHDRAW) //
		        .withAssignments(PROGRAM_ADMINISTRATOR) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(PROGRAM_WITHDRAWN) //
		                .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST));
	}

}
