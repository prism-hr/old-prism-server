package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_RESERVE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_RESERVE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_RESERVED_WAITING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationCompleteState;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationWithdraw;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationReserved extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentWithViewerRecruiter()); //
		stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
		stateActions.add(applicationEscalateReserved());

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_RESERVE) //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST) //
		        .withAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
		        .withAssignments(PROGRAM_APPROVER) //
		        .withNotifications(APPLICATION_CREATOR, APPLICATION_RESERVE_NOTIFICATION) //
		        .withNotifications(APPLICATION_PARENT_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withNotifications(PROGRAM_APPROVER, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_RESERVED_WAITING) //
		                .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST)));

		stateActions.add(applicationCompleteReserved());
		stateActions.add(applicationViewEditWithViewerRecruiter(state)); //
		stateActions.add(applicationWithdrawnReserved());
	}

	public static PrismStateAction applicationCompleteReserved() {
		return applicationCompleteState(APPLICATION_PARENT_ADMINISTRATOR_GROUP);
	}
	
	public static PrismStateAction applicationEscalateReserved() {
	    return applicationEscalate(APPLICATION_DELETE_REFEREE_GROUP);
    }

	public static PrismStateAction applicationWithdrawnReserved() {
		return applicationWithdraw(APPLICATION_PARENT_ADMINISTRATOR_GROUP, //
		        APPLICATION_DELETE_REFEREE_GROUP);
	}

}
