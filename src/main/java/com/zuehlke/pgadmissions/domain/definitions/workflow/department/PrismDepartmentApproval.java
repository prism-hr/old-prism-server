package com.zuehlke.pgadmissions.domain.definitions.workflow.department;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_COMPLETE_APPROVAL_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROGRAM_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_PROGRAM_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_PROGRAM_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROGRAM_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL_PENDING_CORRECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROGRAM_APPROVED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_APPROVE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programEscalateUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programWithdraw;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismDepartmentApproval extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(programCompleteApproval()
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_PROGRAM_TASK_REQUEST) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(PROGRAM_APPROVAL_PENDING_CORRECTION) //
		                .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST) //
		                .withTransitionEvaluation(PROGRAM_APPROVED_OUTCOME))); //

		stateActions.add(programEmailCreator()); //
		stateActions.add(programEscalateUnapproved()); //
		stateActions.add(programViewEditUnapproved()); //
		stateActions.add(programWithdraw());
	}

	public static PrismStateAction programCompleteApproval() {
		return new PrismStateAction() //
		        .withAction(PROGRAM_COMPLETE_APPROVAL_STAGE) //
		        .withAssignments(PROGRAM_PARENT_ADMINISTRATOR_GROUP) //
		        .withNotifications(PROGRAM_PARENT_ADMINISTRATOR_GROUP, SYSTEM_PROGRAM_UPDATE_NOTIFICATION) //
		        .withNotifications(PROGRAM_ADMINISTRATOR, PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION) //
		        .withTransitions(PROGRAM_APPROVE_TRANSITION);
	}

}
