package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_REJECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_REVERSE_REJECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_REVERSE_REJECTION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_APPROVER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_EXHUME_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REJECTED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CONFIRM_REJECTION_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCompleteState;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdraw;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationRejected extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentWithViewerRecruiter()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_CONFIRM_REJECTION) //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST) //
		        .withAssignments(APPLICATION_PARENT_APPROVER_GROUP) //
		        .withAssignments(PROGRAM_APPROVER) //
		        .withNotifications(APPLICATION_CREATOR, APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION) //
		        .withNotifications(APPLICATION_PARENT_APPROVER_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withTransitions(APPLICATION_CONFIRM_REJECTION_TRANSITION //
		                .withRoleTransitionsAndStateTerminations(APPLICATION_TERMINATE_GROUP, //
		                        APPLICATION_DELETE_REFEREE_GROUP)));

		stateActions.add(applicationEmailCreatorWithViewerRecruiter());
		stateActions.add(applicationEscalate(APPLICATION_DELETE_REFEREE_GROUP));
		stateActions.add(applicationCompleteState(APPLICATION_PARENT_APPROVER_GROUP));
		stateActions.add(applicationViewEditWithViewerRecruiter(state));
		stateActions.add(applicationWithdraw(APPLICATION_PARENT_APPROVER_GROUP, APPLICATION_DELETE_REFEREE_GROUP));
	}

	public static PrismStateAction applicationEscalateRejected() {
		return applicationEscalate(APPLICATION_REJECTED_COMPLETED);
	}

	public static PrismStateAction applicationReverseRejection() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_REVERSE_REJECTION) //
		        .withAssignments(APPLICATION_ADMINISTRATOR_GROUP) //
		        .withAssignments(PROGRAM_APPROVER) //
		        .withNotifications(APPLICATION_CREATOR, APPLICATION_REVERSE_REJECTION_NOTIFICATION) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withNotifications(PROGRAM_APPROVER, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_REJECTED) //
		                .withTransitionAction(APPLICATION_COMPLETE_STAGE) //
		                .withRoleTransitions(APPLICATION_EXHUME_REFEREE_GROUP));
	}

}
