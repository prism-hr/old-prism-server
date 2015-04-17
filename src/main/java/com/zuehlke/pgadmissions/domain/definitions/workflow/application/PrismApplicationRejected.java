package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_APPROVER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_EXHUME_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REJECTED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CONFIRM_REJECTION_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.*;

public class PrismApplicationRejected extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentWithViewerRecruiter()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_CONFIRM_REJECTION) //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST) //
		        .withAssignments(APPLICATION_PARENT_APPROVER_GROUP) //
		        .withNotifications(APPLICATION_CREATOR, APPLICATION_CONFIRM_REJECTION_NOTIFICATION) //
		        .withNotifications(APPLICATION_PARENT_APPROVER_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withTransitions(APPLICATION_CONFIRM_REJECTION_TRANSITION //
		                .withRoleTransitionsAndStateTerminations(APPLICATION_TERMINATE_GROUP, //
		                        APPLICATION_RETIRE_REFEREE_GROUP)));

		stateActions.add(applicationEmailCreatorWithViewerRecruiter());
		stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP));
		stateActions.add(applicationCompleteState(APPLICATION_COMPLETE_REJECTED_STAGE, state, APPLICATION_PARENT_APPROVER_GROUP));
		stateActions.add(applicationViewEditWithViewerRecruiter(state));
		stateActions.add(applicationWithdraw(APPLICATION_PARENT_APPROVER_GROUP, APPLICATION_RETIRE_REFEREE_GROUP));
	}

	public static PrismStateAction applicationEscalateRejected() {
		return applicationEscalate(APPLICATION_REJECTED_COMPLETED);
	}

	public static PrismStateAction applicationReverseRejection() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_REVERSE_REJECTION) //
		        .withAssignments(APPLICATION_PARENT_APPROVER_GROUP) //
		        .withNotifications(APPLICATION_CREATOR, APPLICATION_REVERSE_REJECTION_NOTIFICATION) //
		        .withNotifications(APPLICATION_PARENT_APPROVER_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_REJECTED) //
		                .withTransitionAction(APPLICATION_COMPLETE_REJECTED_STAGE) //
		                .withRoleTransitions(APPLICATION_EXHUME_REFEREE_GROUP));
	}

}
