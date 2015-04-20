package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_APPROVAL_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CONFIRM_PRIMARY_SUPERVISION_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CONFIRM_SECONDARY_SUPERVISION_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_SUPERVISOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationCompleteApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationConfirmPrimarySupervision;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationConfirmSecondarySupervision;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationViewEditApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationWithdrawApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiterAndAdministrator;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationApprovalPendingCompletion extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentWithViewerRecruiterAndAdministrator()); //

		stateActions.add(applicationCompleteApproval(state) //
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

		stateActions.add(applicationEmailCreatorWithViewerRecruiterAndAdministrator()); //

		stateActions.add(PrismApplicationWorkflow.applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP, //
		        APPLICATION_RETIRE_ADMINISTRATOR_GROUP, //
		        APPLICATION_RETIRE_SUPERVISOR_GROUP));

		stateActions.add(applicationViewEditApproval(state)); //
		stateActions.add(applicationWithdrawApproval());
	}

}