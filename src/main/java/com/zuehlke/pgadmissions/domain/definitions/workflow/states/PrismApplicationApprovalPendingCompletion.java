package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CONFIRM_PRIMARY_SUPERVISION_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CONFIRM_SECONDARY_SUPERVISION_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_SUPERVISOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApproval.applicationCompleteApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApproval.applicationConfirmPrimarySupervision;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApproval.applicationConfirmSecondarySupervision;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApproval.applicationViewEditApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApproval.applicationWithdrawApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationCommentWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiterAndAdministrator;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationApprovalPendingCompletion extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentWithViewerRecruiterAndAdministrator()); //

		stateActions.add(applicationCompleteApproval() //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST)); //

		stateActions.add(applicationConfirmPrimarySupervision() //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(state) //
		                .withTransitionAction(APPLICATION_COMPLETE_STAGE) //
		                .withRoleTransitions(APPLICATION_CONFIRM_PRIMARY_SUPERVISION_GROUP))); //

		stateActions.add(applicationConfirmSecondarySupervision() //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(state) //
		                .withTransitionAction(APPLICATION_COMPLETE_STAGE) //
		                .withRoleTransitions(APPLICATION_CONFIRM_SECONDARY_SUPERVISION_GROUP))); //

		stateActions.add(applicationEmailCreatorWithViewerRecruiterAndAdministrator()); //

		stateActions.add(PrismApplicationWorkflow.applicationEscalate(APPLICATION_DELETE_REFEREE_GROUP, //
		        APPLICATION_DELETE_ADMINISTRATOR_GROUP, //
		        APPLICATION_DELETE_SUPERVISOR_GROUP));

		stateActions.add(applicationViewEditApproval(state)); //
		stateActions.add(applicationWithdrawApproval());
	}

}
