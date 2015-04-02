package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_TERMINATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_SUPERVISOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_COMPLETE_STATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CONFIRM_OFFER_RECOMMENDATION_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_ESCALATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationValidation.applicationWithdrawValidation;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationCommentVerification;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationEmailCreatorVerification;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationViewEditVerification;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;

public class PrismApplicationApproved extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentVerification()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_CONFIRM_OFFER_RECOMMENDATION) //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST) //
		        .withAssignments(APPLICATION_ADMINISTRATOR_GROUP) //
		        .withAssignments(PROGRAM_APPROVER) //
		        .withNotifications(APPLICATION_CREATOR, APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withNotifications(PROGRAM_APPROVER, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withTransitions(APPLICATION_CONFIRM_OFFER_RECOMMENDATION_TRANSITION //
		                .withRoleTransitionsAndStateTerminations(Lists.newArrayList( //
		                        APPLICATION_CREATE_SUPERVISOR_GROUP, //
		                        APPLICATION_DELETE_REFEREE_GROUP), //
		                        APPLICATION_TERMINATE_GROUP))); //

		stateActions.add(applicationEmailCreatorVerification()); //

		stateActions.add(applicationEscalateApproved()); //

		stateActions.add(applicationMoveToDifferentState());

		stateActions.add(applicationViewEditVerification(state)); //

		stateActions.add(applicationWithdrawValidation());
	}

	public static PrismStateAction applicationEscalateApproved() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_ESCALATE) //
		        .withNotifications(APPLICATION_CREATOR, APPLICATION_TERMINATE_NOTIFICATION) //
		        .withTransitions(APPLICATION_ESCALATE_TRANSITION.withRoleTransitionsAndStateTerminations( //
		                APPLICATION_DELETE_REFEREE_GROUP, //
		                APPLICATION_TERMINATE_GROUP));
	}
	
	public static PrismStateAction applicationMoveToDifferentState() {
	    return new PrismStateAction() //
		        .withAction(PrismAction.APPLICATION_MOVE_TO_DIFFERENT_STAGE) //
		        .withAssignments(APPLICATION_ADMINISTRATOR_GROUP) //
		        .withAssignments(PROGRAM_APPROVER) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withNotifications(PROGRAM_APPROVER, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withTransitions(APPLICATION_COMPLETE_STATE_TRANSITION);
    }

}
