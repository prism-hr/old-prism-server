package com.zuehlke.pgadmissions.domain.definitions.workflow.institution;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_COMPLETE_APPROVAL_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_EMAIL_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_WITHDRAW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_INSTITUTION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.INSTITUTION_VIEW_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.INSTITUTION_COMPLETE_APPROVAL_STAGE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_INSTITUTION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_INSTITUTION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.SYSTEM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.INSTITUTION_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVAL_PENDING_CORRECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_WITHDRAWN;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.INSTITUTION_APPROVED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.INSTITUTION_APPROVE_TRANSITION;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismInstitutionApproval extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(institutionCompleteApprovalStage() //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_INSTITUTION_TASK_REQUEST) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(INSTITUTION_APPROVAL_PENDING_CORRECTION) //
		                .withTransitionAction(SYSTEM_VIEW_INSTITUTION_LIST)
		                .withTransitionEvaluation(INSTITUTION_APPROVED_OUTCOME))); //

		stateActions.add(institutionEmailCreatorApproval()); //

		stateActions.add(institutionEscalateApproval()); //

		stateActions.add(institutionViewEditApproval()); //

		stateActions.add(institutionWithdrawApproval());
	}

	public static PrismStateAction institutionCompleteApprovalStage() {
		return new PrismStateAction() //
		        .withAction(INSTITUTION_COMPLETE_APPROVAL_STAGE) //
		        .withAssignments(SYSTEM_ADMINISTRATOR) //
		        .withNotifications(SYSTEM_ADMINISTRATOR, SYSTEM_INSTITUTION_UPDATE_NOTIFICATION)
		        .withNotifications(INSTITUTION_ADMINISTRATOR, INSTITUTION_COMPLETE_APPROVAL_STAGE_NOTIFICATION) //
		        .withTransitions(INSTITUTION_APPROVE_TRANSITION);
	}

	public static PrismStateAction institutionEmailCreatorApproval() {
		return new PrismStateAction() //
		        .withAction(INSTITUTION_EMAIL_CREATOR) //
		        .withAssignments(SYSTEM_ADMINISTRATOR);
	}

	public static PrismStateAction institutionEscalateApproval() {
		return new PrismStateAction() //
		        .withAction(INSTITUTION_ESCALATE) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(INSTITUTION_REJECTED) //
		                .withTransitionAction(INSTITUTION_ESCALATE));
	}

	public static PrismStateAction institutionViewEditApproval() {
		return new PrismStateAction() //
		        .withAction(INSTITUTION_VIEW_EDIT) //
		        .withActionEnhancement(INSTITUTION_VIEW_AS_USER)
		        .withAssignments(INSTITUTION_ADMINISTRATOR_GROUP);
	}

	public static PrismStateAction institutionWithdrawApproval() {
		return new PrismStateAction() //
		        .withAction(INSTITUTION_WITHDRAW) //
		        .withAssignments(INSTITUTION_ADMINISTRATOR) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(INSTITUTION_WITHDRAWN) //
		                .withTransitionAction(SYSTEM_VIEW_INSTITUTION_LIST));
	}

}
